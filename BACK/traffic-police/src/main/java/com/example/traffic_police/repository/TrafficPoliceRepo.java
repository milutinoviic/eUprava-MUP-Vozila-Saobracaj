package com.example.traffic_police.repository;

import com.example.traffic_police.model.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Repository
public class TrafficPoliceRepo {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public TrafficPoliceRepo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // ------------------ POLICE ------------------

    public List<PolicePerson> getAllPolice() {
        return mongoTemplate.findAll(PolicePerson.class);
    }

    public PolicePerson getPoliceById(String id) {
        return mongoTemplate.findById(id, PolicePerson.class);
    }

    public PolicePerson insertPolicePerson(PolicePerson police) {
        if (police.getRank() == null) {
            police.setRank(PolicePerson.Rank.LOW);
        }
        return mongoTemplate.insert(police);
    }

    public void suspendOfficer(String officerId) {
        Query query = new Query(Criteria.where("id").is(officerId));
        PolicePerson officer = mongoTemplate.findOne(query, PolicePerson.class);
        if (officer == null) return;

        Update update = new Update().set("isSuspended", !officer.isSuspended());
        mongoTemplate.updateFirst(query, update, PolicePerson.class);
    }

    public void promoteOfficer(String officerId) {
        Query query = new Query(Criteria.where("id").is(officerId));
        PolicePerson officer = mongoTemplate.findOne(query, PolicePerson.class);
        if (officer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer not found");
        }

        switch (officer.getRank()) {
            case LOW -> mongoTemplate.updateFirst(query, new Update().set("rank", PolicePerson.Rank.MEDIUM), PolicePerson.class);
            case MEDIUM -> mongoTemplate.updateFirst(query, new Update().set("rank", PolicePerson.Rank.HIGH), PolicePerson.class);
            case HIGH -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Officer already has the highest rank");
        }
    }

    // ------------------ FINES ------------------

    public List<Fine> getAllFines() {
        return mongoTemplate.findAll(Fine.class);
    }

    public void markFineAsPaid(String fineId) {
        Query query = new Query(Criteria.where("id").is(fineId));
        Update update = new Update().set("isPaid", true);
        mongoTemplate.updateFirst(query, update, Fine.class);
    }

    // ------------------ VIOLATIONS ------------------

    public List<Violation> getAllViolations() {
        return mongoTemplate.findAll(Violation.class);
    }

    public Violation insertViolation(Violation violation) {
        if (violation.getTypeOfViolation() == null) {
            violation.setTypeOfViolation(Violation.TypeOfViolation.MINOR);
        }
        return mongoTemplate.insert(violation);
    }

    public void assignOfficerToViolation(String violationId, String officerId) {
        Query query = new Query(Criteria.where("id").is(violationId));
        Update update = new Update().set("policeId", officerId);
        mongoTemplate.updateFirst(query, update, Violation.class);
    }

    public List<Violation> getAssignedViolations(String officerId) {
        Query query = new Query(Criteria.where("policeId").is(officerId));
        return mongoTemplate.find(query, Violation.class);
    }

    public List<Fine> findUnpaidFinesByDriverId(String driverId) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("driverId").is(driverId)),
                Aggregation.lookup("fines", "id", "violationID", "fines"),
                Aggregation.unwind("fines"),
                Aggregation.match(Criteria.where("fines.isPaid").is(false)),
                Aggregation.project().and("fines").as("fines")
        );

        AggregationResults<FineWrapper> results = mongoTemplate.aggregate(agg, "violations", FineWrapper.class);
        return results.getMappedResults().stream().map(FineWrapper::getFine).toList();
    }

    public List<Violation> getViolationHistory(String driverId) {
        Query query = new Query(Criteria.where("driverId").is(driverId));
        return mongoTemplate.find(query, Violation.class);
    }

    public Fine insertFine(Fine fine) {
        return mongoTemplate.insert(fine);
    }

    public Fine getFineByViolationId(String violationId) {
        Query query = new Query(Criteria.where("violationID").is(violationId));
        return mongoTemplate.findOne(query, Fine.class);
    }

    // ------------------ VEHICLE ------------------

    public List<Violation> checkVehicleViolations(String vehicleId) {
        Query query = new Query(Criteria.where("vehicleId").is(vehicleId));
        return mongoTemplate.find(query, Violation.class);
    }

    // ------------------ STATISTICS ------------------

    public List<StatisticDTO> getDailyStatistics(String policeId) {
        Query query = new Query(Criteria.where("policeId").is(policeId));
        List<Violation> violations = mongoTemplate.find(query, Violation.class);

        return violations.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        v -> v.getDate().toLocalDate(),
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> new StatisticDTO(
                        e.getKey().atStartOfDay(),
                        e.getValue().intValue()
                ))
                .sorted(Comparator.comparing(StatisticDTO::getDate))
                .toList();
    }

    // ------------------ Helper Classes ------------------

    @Getter
    @Setter
    private static class FineWrapper {
        private Fine fine;
    }


    public byte[] exportViolationData(String format, String period) {
        LocalDateTime[] range = parsePeriod(period);
        Query query = new Query(Criteria.where("date")
                .gte(range[0])
                .lte(range[1]));
        List<Violation> violations = mongoTemplate.find(query, Violation.class);

        return switch (format.toLowerCase()) {
            case "csv" -> exportViolationsToCSV(violations);
            case "pdf" -> exportViolationsToPDF(violations);
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    private LocalDateTime[] parsePeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period) {
            case "last7days" -> new LocalDateTime[]{now.minusDays(7), now};
            case "last30days" -> new LocalDateTime[]{now.minusDays(30), now};
            case "thisMonth" -> {
                LocalDate start = LocalDate.now().withDayOfMonth(1);
                LocalDate end = start.plusMonths(1).minusDays(1);
                yield new LocalDateTime[]{start.atStartOfDay(), end.atTime(23, 59, 59)};
            }
            case "last6months" -> new LocalDateTime[]{now.minusMonths(6), now};
            case "1year" -> new LocalDateTime[]{now.minusYears(1), now};
            default -> {
                try {
                    LocalDate parsed = LocalDate.parse(period, DateTimeFormatter.ofPattern("yyyy-MM"));
                    LocalDate start = parsed.withDayOfMonth(1);
                    LocalDate end = start.plusMonths(1).minusDays(1);
                    yield new LocalDateTime[]{start.atStartOfDay(), end.atTime(23, 59, 59)};
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid period: " + period);
                }
            }
        };
    }

    private byte[] exportViolationsToCSV(List<Violation> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Type,Date,Location,DriverId,VehicleId,PoliceId\n");
        for (Violation v : violations) {
            sb.append(v.getId()).append(",")
                    .append(v.getTypeOfViolation()).append(",")
                    .append(v.getDate()).append(",")
                    .append(v.getLocation()).append(",")
                    .append(v.getDriverId()).append(",")
                    .append(v.getVehicleId()).append(",")
                    .append(v.getPoliceId()).append("\n");
        }
        return sb.toString().getBytes();
    }

    private byte[] exportViolationsToPDF(List<Violation> violations) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("Violation Report"));
            doc.add(new Paragraph("Generated: " + LocalDateTime.now()));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(7);
            table.addCell("ID");
            table.addCell("Type");
            table.addCell("Date");
            table.addCell("Location");
            table.addCell("DriverId");
            table.addCell("VehicleId");
            table.addCell("PoliceId");

            for (Violation v : violations) {
                table.addCell(v.getId());
                table.addCell(v.getTypeOfViolation().toString());
                table.addCell(v.getDate().toString());
                table.addCell(v.getLocation());
                table.addCell(v.getDriverId());
                table.addCell(v.getVehicleId());
                table.addCell(v.getPoliceId());
            }

            doc.add(table);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error exporting PDF", e);
        }
    }
}
