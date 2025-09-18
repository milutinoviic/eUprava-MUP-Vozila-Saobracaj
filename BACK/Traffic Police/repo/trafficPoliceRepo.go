package repo

import (
	"bytes"
	"context"
	"eUprava/trafficPolice/model"
	"encoding/csv"
	"errors"
	"fmt"
	"github.com/jung-kurt/gofpdf"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"go.opentelemetry.io/otel/codes"
	"go.opentelemetry.io/otel/trace"
	"log"
	"net/smtp"
	"os"
	"sort"
	"time"
)

type TrafficPoliceRepo struct {
	cli    *mongo.Client
	logger *log.Logger
	tracer trace.Tracer
}

func NewTrafficPoliceRepo(cli *mongo.Client, logger *log.Logger, tracer trace.Tracer, ctx context.Context) (*TrafficPoliceRepo, error) {
	dbUri := os.Getenv("MONGO_DB_URI")
	if dbUri == "" {
		return nil, errors.New("env MONGO_DB_URI is not set")
	}
	client, err := mongo.Connect(ctx, options.Client().ApplyURI(dbUri))
	if err != nil {
		return nil, err
	}
	err = client.Ping(ctx, nil)
	if err != nil {
		return nil, err
	}
	repo := &TrafficPoliceRepo{
		cli:    client,
		logger: logger,
		tracer: tracer,
	}
	return repo, nil
}

func (tp *TrafficPoliceRepo) Disconnect(ctx context.Context) error {
	_, span := tp.tracer.Start(ctx, "Disconnect")
	defer span.End()
	err := tp.cli.Disconnect(ctx)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return err
	}
	span.SetStatus(codes.Ok, "")
	return nil
}

func (tp *TrafficPoliceRepo) Ping() {
	ctx, span := tp.tracer.Start(context.Background(), "Ping")
	defer span.End()
	err := tp.cli.Ping(ctx, nil)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
	}
}
func (tp *TrafficPoliceRepo) getPoliceCollection() *mongo.Collection {
	projectDatabase := tp.cli.Database("mongoEGovernment")
	projectCollection := projectDatabase.Collection("police")
	return projectCollection
}
func (tp *TrafficPoliceRepo) getFinesCollection() *mongo.Collection {
	projectDatabase := tp.cli.Database("mongoEGovernment")
	projectCollection := projectDatabase.Collection("fines")
	return projectCollection
}

func (tp *TrafficPoliceRepo) getViolationCollection() *mongo.Collection {
	projectDatabase := tp.cli.Database("mongoEGovernment")
	projectCollection := projectDatabase.Collection("violations")
	return projectCollection
}

func (tp *TrafficPoliceRepo) GetAllPolice(ctx context.Context) (model.Police, error) {
	ctx, span := tp.tracer.Start(ctx, "GetAllPolice")
	defer span.End()
	tpCollection := tp.getPoliceCollection()

	var police model.Police
	cursor, err := tpCollection.Find(ctx, bson.M{})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	if err = cursor.All(ctx, &police); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	span.SetStatus(codes.Ok, "")
	return police, nil

}

func (tp *TrafficPoliceRepo) GetAllFines(ctx context.Context) (model.Fines, error) {
	ctx, span := tp.tracer.Start(ctx, "GetAllFines")
	defer span.End()
	tpCollection := tp.getFinesCollection()
	var fine model.Fines
	cursor, err := tpCollection.Find(ctx, bson.M{})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	if err = cursor.All(ctx, &fine); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	span.SetStatus(codes.Ok, "")
	return fine, nil
}

func (tp *TrafficPoliceRepo) GetAllViolations(ctx context.Context) (model.Violations, error) {
	ctx, span := tp.tracer.Start(ctx, "GetAllViolations")
	defer span.End()
	tpCollection := tp.getViolationCollection()
	var violation model.Violations
	cursor, err := tpCollection.Find(ctx, bson.M{})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	if err = cursor.All(ctx, &violation); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	span.SetStatus(codes.Ok, "")
	return violation, nil
}

func (tp *TrafficPoliceRepo) InsertPolicePerson(ctx context.Context, police *model.PolicePerson) (primitive.ObjectID, error) {
	ctx, span := tp.tracer.Start(ctx, "InsertPolicePerson")
	defer span.End()
	policeCollection := tp.getPoliceCollection()
	if len(police.Rank) == 0 {
		police.Rank = model.RankLow
	}
	result, err := policeCollection.InsertOne(ctx, police)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return primitive.NilObjectID, err
	}
	span.SetStatus(codes.Ok, "")
	return result.InsertedID.(primitive.ObjectID), nil
}

func (tp *TrafficPoliceRepo) NotifyPersonOfViolation(ctx context.Context, violation *model.Violation, driver model.OwnerDTO) error {
	ctx, span := tp.tracer.Start(ctx, "NotifyPersonOfViolation")
	defer span.End()

	from := os.Getenv("SMTP_EMAIL")
	password := os.Getenv("SMTP_PASSWORD")
	smtpHost := os.Getenv("SMTP_HOST")
	smtpPort := os.Getenv("SMTP_PORT")

	to := driver.Email

	plainTextBody := fmt.Sprintf(
		"Dear %s %s,\n\n"+
			"We are notifying you about a traffic violation.\n\n"+
			"Violation details:\n"+
			"- Type: %s\n"+
			"- Date: %s\n"+
			"- Location: %s\n\n"+
			"Please address this violation promptly.\n\n"+
			"Best regards,\nTraffic Police Department",
		driver.FirstName, driver.LastName,
		violation.TypeOfViolation,
		violation.Date.Format("2006-01-02 15:04:05"),
		violation.Location,
	)

	htmlBody := fmt.Sprintf(`<!DOCTYPE html>
	<html>
	<head>
		<style>
			body { font-family: Arial, sans-serif; color: #333; }
			.container { padding: 20px; border: 1px solid #ddd; border-radius: 8px; }
			.header { font-size: 20px; font-weight: bold; color: #d9534f; }
			.content { margin-top: 10px; }
			.footer { margin-top: 20px; font-size: 12px; color: #888; }
		</style>
	</head>
	<body>
		<div class="container">
			<div class="header">Traffic Violation Notice</div>
			<div class="content">
				<p>Dear %s %s,</p>
				<p>We are notifying you about a traffic violation.</p>
				<ul>
					<li><strong>Type:</strong> %s</li>
					<li><strong>Date:</strong> %s</li>
					<li><strong>Location:</strong> %s</li>
				</ul>
				<p>Please address this violation promptly.</p>
			</div>
			<div class="footer">
				<p>Best regards,<br>Traffic Police Department</p>
			</div>
		</div>
	</body>
	</html>`,
		driver.FirstName, driver.LastName,
		violation.TypeOfViolation,
		violation.Date.Format("2006-01-02 15:04:05"),
		violation.Location,
	)

	message := []byte("MIME-Version: 1.0\r\n" +
		"Content-Type: multipart/alternative; boundary=\"fancy-boundary\"\r\n" +
		"Subject: Traffic Violation Notice\r\n" +
		"From: " + from + "\r\n" +
		"To: " + to + "\r\n" +
		"\r\n" +
		"--fancy-boundary\r\n" +
		"Content-Type: text/plain; charset=\"utf-8\"\r\n" +
		"\r\n" +
		plainTextBody + "\r\n" +
		"--fancy-boundary\r\n" +
		"Content-Type: text/html; charset=\"utf-8\"\r\n" +
		"\r\n" +
		htmlBody + "\r\n" +
		"--fancy-boundary--")

	auth := smtp.PlainAuth("", from, password, smtpHost)
	err := smtp.SendMail(smtpHost+":"+smtpPort, auth, from, []string{to}, message)
	if err != nil {
		return fmt.Errorf("failed to send email: %w", err)
	}

	return nil
}

func (tp *TrafficPoliceRepo) InsertViolation(ctx context.Context, violation *model.Violation) (primitive.ObjectID, error) {
	ctx, span := tp.tracer.Start(ctx, "InsertViolation")
	defer span.End()
	violationCollection := tp.getViolationCollection()
	if len(violation.TypeOfViolation) == 0 {
		violation.TypeOfViolation = model.Minor
	}
	result, err := violationCollection.InsertOne(ctx, violation)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("failed to insert violation: %w", err)
	}
	span.SetStatus(codes.Ok, "")
	return result.InsertedID.(primitive.ObjectID), nil
}

func (tp *TrafficPoliceRepo) AssignOfficerToViolation(ctx context.Context, violationId, officerId string) error {
	ctx, span := tp.tracer.Start(ctx, "AssignOfficerToViolation")
	defer span.End()
	violationCollection := tp.getViolationCollection()
	objId, _ := primitive.ObjectIDFromHex(violationId)
	_, err := violationCollection.UpdateOne(ctx, bson.M{"_id": objId}, bson.M{"$set": bson.M{"police_id": officerId}})
	if err != nil {
		span.SetStatus(codes.Error, err.Error())
		span.RecordError(err)
		return err
	}
	span.SetStatus(codes.Ok, "")
	return nil

}

func (tp *TrafficPoliceRepo) GetAssignedViolations(ctx context.Context, officerId string) (model.Violations, error) {
	ctx, span := tp.tracer.Start(ctx, "GetAssignedViolations")
	defer span.End()
	violationCollection := tp.getViolationCollection()
	var violations model.Violations
	objId, _ := primitive.ObjectIDFromHex(officerId)
	filter := bson.M{"police_id": objId}
	cursor, err := violationCollection.Find(ctx, filter)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
	}
	if err = cursor.All(ctx, &violations); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	span.SetStatus(codes.Ok, "")
	return violations, nil
}

func (tp *TrafficPoliceRepo) FindUnpaidFinesByDriverID(ctx context.Context, driverId string) (model.Fines, error) {
	ctx, span := tp.tracer.Start(ctx, "FindUnpaidFinesByDriverID")
	defer span.End()

	driverObjID, err := primitive.ObjectIDFromHex(driverId)
	if err != nil {
		return nil, fmt.Errorf("invalid driverId: %w", err)
	}

	pipeline := mongo.Pipeline{
		bson.D{{"$match", bson.D{{"driver_id", driverObjID}}}}, // get violations for driver
		bson.D{{"$lookup", bson.D{
			{"from", "fines"},                // collection name
			{"localField", "_id"},            // violation _id
			{"foreignField", "violation_id"}, // fine.violation_id
			{"as", "fines"},
		}}},
		bson.D{{"$unwind", "$fines"}},
		bson.D{{"$match", bson.D{{"fines.is_paid", false}}}},
	}

	cursor, err := tp.getViolationCollection().Aggregate(ctx, pipeline)
	if err != nil {
		return nil, err
	}
	defer cursor.Close(ctx)

	var results []struct {
		Violation model.Violation `bson:",inline"`
		Fine      model.Fine      `bson:"fines"`
	}
	if err := cursor.All(ctx, &results); err != nil {
		return nil, err
	}

	fines := make(model.Fines, 0, len(results))
	for _, r := range results {
		f := r.Fine
		fines = append(fines, &f)
	}

	return fines, nil
}

func (tp *TrafficPoliceRepo) CheckVehicleViolations(ctx context.Context, vehicleId string) (model.Violations, error) {
	ctx, span := tp.tracer.Start(ctx, "CheckVehicleViolations")
	defer span.End()
	var violations model.Violations
	violationCollection := tp.getViolationCollection()
	filter := bson.M{"vehicle_id": vehicleId}
	cursor, err := violationCollection.Find(ctx, filter)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	if err = cursor.All(ctx, &violations); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	span.SetStatus(codes.Ok, "")
	return violations, nil
}

func (tp *TrafficPoliceRepo) GetDailyStatistics(ctx context.Context, policeId string) (model.StatisticsDTO, error) {
	ctx, span := tp.tracer.Start(ctx, "GetDailyStatisticsNoPipeline")
	defer span.End()

	cursor, err := tp.getViolationCollection().Find(ctx, bson.M{"police_id": policeId})
	if err != nil {
		return nil, err
	}
	defer cursor.Close(ctx)

	var violations model.Violations
	if err := cursor.All(ctx, &violations); err != nil {
		return nil, err
	}

	counts := make(map[string]int)
	for _, v := range violations {
		dateKey := v.Date.Format("2006-01-02")
		counts[dateKey]++
	}

	stats := make(model.StatisticsDTO, 0, len(counts))
	for dateStr, count := range counts {
		parsedDate, _ := time.Parse("2006-01-02", dateStr)
		stats = append(stats, &model.StatisticDTO{
			Date:               parsedDate,
			NumberOfViolations: count,
		})
	}

	sort.Slice(stats, func(i, j int) bool {
		return stats[i].Date.Before(stats[j].Date)
	})

	return stats, nil
}

func (tp *TrafficPoliceRepo) MarkFineAsPaid(ctx context.Context, fineId string) error {
	ctx, span := tp.tracer.Start(ctx, "MarkFineAsPaid")
	defer span.End()
	fines := tp.getFinesCollection()
	objId, _ := primitive.ObjectIDFromHex(fineId)
	_, err := fines.UpdateOne(ctx, bson.M{"_id": objId}, bson.M{"$set": bson.M{"is_paid": true}})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return err
	}
	span.SetStatus(codes.Ok, "")
	return nil
}

func (tp *TrafficPoliceRepo) PromoteOfficer(ctx context.Context, officerId string) error {
	ctx, span := tp.tracer.Start(ctx, "PromoteOfficer")
	defer span.End()

	objId, err := primitive.ObjectIDFromHex(officerId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, "invalid officerId")
		return err
	}

	policeCollection := tp.getPoliceCollection()
	var officer model.PolicePerson
	err = policeCollection.FindOne(ctx, bson.M{"_id": objId}).Decode(&officer)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return err
	}

	switch officer.Rank {
	case model.RankLow:
		_, err = policeCollection.UpdateOne(ctx, bson.M{"_id": objId}, bson.M{"$set": bson.M{"rank": model.RankMedium}})
	case model.RankMedium:
		_, err = policeCollection.UpdateOne(ctx, bson.M{"_id": objId}, bson.M{"$set": bson.M{"rank": model.RankHigh}})
	case model.RankHigh:
		return errors.New("you can't promote this officer since they have the highest position")
	}

	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return err
	}
	span.SetStatus(codes.Ok, "")

	return nil
}

func (tp *TrafficPoliceRepo) GetViolationHistory(ctx context.Context, driverId string) (model.Violations, error) {
	ctx, span := tp.tracer.Start(ctx, "GetViolationHistory")
	defer span.End()
	var violations model.Violations
	filter := bson.M{"driver_id": driverId}
	cursor, err := tp.getViolationCollection().Find(ctx, filter)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	if err = cursor.All(ctx, &violations); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	span.SetStatus(codes.Ok, "")
	return violations, nil
}
func (tp *TrafficPoliceRepo) ExportViolationData(ctx context.Context, format string, period string) ([]byte, error) {
	ctx, span := tp.tracer.Start(ctx, "ExportViolationData")
	defer span.End()
	var violations model.Violations
	start, end, err := parsePeriod(period)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}

	filter := bson.M{
		"date": bson.M{
			"$gte": start,
			"$lte": end,
		},
	}
	cursor, err := tp.getViolationCollection().Find(ctx, filter)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	if err = cursor.All(ctx, &violations); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}

	switch format {
	case "csv":
		return exportViolationsToCSV(violations)
	case "pdf":
		return exportViolationsToPDF(violations)
	default:
		return nil, fmt.Errorf("unsupported export format: %s", format)

	}

}

func parsePeriod(period string) (time.Time, time.Time, error) {
	now := time.Now()
	switch period {
	case "last7days":
		return now.AddDate(0, 0, -7), now, nil

	case "last30days":
		return now.AddDate(0, 0, -30), now, nil

	case "thisMonth":
		start := time.Date(now.Year(), now.Month(), 1, 0, 0, 0, 0, now.Location())
		end := start.AddDate(0, 1, -1)
		return start, end, nil

	case "last6months":
		start := now.AddDate(0, -6, 0)
		return start, now, nil

	case "1year":
		start := now.AddDate(-1, 0, 0)
		return start, now, nil

	default:

		t, err := time.Parse("2006-01", period)
		if err != nil {
			return time.Time{}, time.Time{}, fmt.Errorf("invalid period: %s", period)
		}
		start := t
		end := t.AddDate(0, 1, -1)
		return start, end, nil
	}

}

func exportViolationsToCSV(violations model.Violations) ([]byte, error) {
	buf := &bytes.Buffer{}
	writer := csv.NewWriter(buf)
	writer.Write([]string{"ID", "Type", "Date", "Location", "DriverId", "VehicleId", "PoliceId"})

	// rows
	for _, v := range violations {
		writer.Write([]string{
			v.Id,
			string(v.TypeOfViolation),
			v.Date.Format("2006-01-02"),
			v.Location,
			v.DriverId,
			v.VehicleId,
			v.PoliceId,
		})
	}
	writer.Flush()

	return buf.Bytes(), writer.Error()
}

func exportViolationsToPDF(violations model.Violations) ([]byte, error) {
	pdf := gofpdf.New("P", "mm", "A4", "")
	pdf.AddPage()
	pdf.SetFont("Arial", "B", 14)
	pdf.Cell(40, 10, "Violation Report")

	pdf.SetFont("Arial", "", 10)
	pdf.Ln(12)

	headers := []string{"ID", "Type", "Date", "Location", "DriverId", "VehicleId", "PoliceId"}
	for _, h := range headers {
		pdf.CellFormat(30, 7, h, "1", 0, "", false, 0, "")
	}
	pdf.Ln(-1)

	for _, v := range violations {
		row := []string{
			v.Id,
			string(v.TypeOfViolation),
			v.Date.Format("2006-01-02"),
			v.Location,
			v.DriverId,
			v.VehicleId,
			v.PoliceId,
		}
		for _, col := range row {
			pdf.CellFormat(30, 7, col, "1", 0, "", false, 0, "")
		}
		pdf.Ln(-1)
	}

	buf := &bytes.Buffer{}
	err := pdf.Output(buf)
	if err != nil {
		return nil, err
	}
	return buf.Bytes(), nil
}

func (tp *TrafficPoliceRepo) SuspendOfficer(ctx context.Context, officerId string) error {
	ctx, span := tp.tracer.Start(ctx, "suspendOfficer")
	defer span.End()
	policeCollection := tp.getPoliceCollection()
	objId, _ := primitive.ObjectIDFromHex(officerId)
	_, err := policeCollection.UpdateOne(ctx, bson.M{"_id": objId}, bson.M{"$set": bson.M{"is_suspended": true}})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return err
	}
	span.SetStatus(codes.Ok, "")
	return nil
}
