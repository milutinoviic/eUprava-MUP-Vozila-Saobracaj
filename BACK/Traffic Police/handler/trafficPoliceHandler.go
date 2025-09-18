package handler

import (
	"eUprava/trafficPolice/model"
	"eUprava/trafficPolice/repo"
	"encoding/json"
	"fmt"
	"github.com/gorilla/mux"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/codes"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/trace"
	"log"
	"net/http"
)

const (
	receivedReq     = "Received %s request for %s"
	contentType     = "Content-Type"
	appJson         = "application/json"
	responseErr     = "Error writing response: "
	jsonConvert     = "Unable to convert to json. "
	tokenValidation = "Token validation failed: "
	encodeErr       = "Error encoding response: "
	reqBodyErr      = "Invalid request body"
	emailRequired   = "Email is required"
)

type TrafficPoliceHandler struct {
	logger *log.Logger
	repo   *repo.TrafficPoliceRepo
	tracer trace.Tracer
}

func ExtractTraceInfoMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ctx := otel.GetTextMapPropagator().Extract(r.Context(), propagation.HeaderCarrier(r.Header))
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}

func (tp *TrafficPoliceHandler) HandleNewViolation(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleNewViolation")
	defer span.End()
	violation := &model.Violation{}
	err := violation.FromJSON(r.Body)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
	}

	res, err := tp.repo.InsertViolation(ctx, violation)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
	}
	// TODO Kada Stefan zavrsi svoje funkcije, za sada stoji prazan objekat
	err = tp.repo.NotifyPersonOfViolation(ctx, violation, model.OwnerDTO{})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
	}
	response := map[string]string{"id": res.Hex()}

	rw.Header().Set("Content-Type", "application/json")
	rw.WriteHeader(http.StatusCreated)
	err = json.NewEncoder(rw).Encode(response)
	if err != nil {
		return
	}
}

func (tp *TrafficPoliceHandler) HandleAssignOfViolation(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleAssignOfViolation")
	defer span.End()
	res := struct {
		ViolationId string `json:"violationId"`
		OfficerId   string `json:"officerId"`
	}{}
	err := json.NewDecoder(r.Body).Decode(&res)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, "Invalid request body", http.StatusBadRequest)
		return
	}

	err = tp.repo.AssignOfficerToViolation(ctx, res.ViolationId, res.ViolationId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

	span.SetStatus(codes.Ok, "")
	rw.WriteHeader(http.StatusOK)
}

func (tp *TrafficPoliceHandler) HandleGettingViolationsByOfficer(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleGettingViolationsByOfficer")
	defer span.End()

	res := struct {
		OfficerId string `json:"officerId"`
	}{}
	err := json.NewDecoder(r.Body).Decode(&res)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	v, err := tp.repo.GetAssignedViolations(ctx, res.OfficerId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

	rw.Header().Set(contentType, appJson)
	rw.WriteHeader(http.StatusOK)

	if err := v.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
}

func (tp *TrafficPoliceHandler) HandleGettingUnpaidFines(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleGettingUnpaidFines")
	defer span.End()
	res := struct {
		DriverId string `json:"driverId"`
	}{}
	err := json.NewDecoder(r.Body).Decode(&res)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	f, err := tp.repo.FindUnpaidFinesByDriverID(ctx, res.DriverId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	rw.Header().Set(contentType, appJson)
	rw.WriteHeader(http.StatusOK)

	if err := f.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
}

func (tp *TrafficPoliceHandler) GetVehicleHistory(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "GetVehicleHistory")
	defer span.End()
	res := struct {
		VehicleId string `json:"vehicleId"`
	}{}
	err := json.NewDecoder(r.Body).Decode(&res)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	v, err := tp.repo.CheckVehicleViolations(ctx, res.VehicleId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	rw.Header().Set(contentType, appJson)
	rw.WriteHeader(http.StatusOK)
	if err = v.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
}

func (tp *TrafficPoliceHandler) HandleDailyStatistics(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleDailyStatistics")
	defer span.End()
	res := struct {
		PoliceId string `json:"policeId"`
	}{}
	err := json.NewDecoder(r.Body).Decode(&res)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	d, err := tp.repo.GetDailyStatistics(ctx, res.PoliceId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

	rw.Header().Set(contentType, appJson)
	rw.WriteHeader(http.StatusOK)
	if err = d.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

}

func (tp *TrafficPoliceHandler) HandleOfficerPromotion(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleDailyStatistics")
	defer span.End()
	res := struct {
		PoliceId string `json:"policeId"`
	}{}
	err := json.NewDecoder(r.Body).Decode(&res)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	err = tp.repo.PromoteOfficer(ctx, res.PoliceId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	rw.WriteHeader(http.StatusOK)
}
func (tp *TrafficPoliceHandler) HandleViolationHistory(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleViolationHistory")
	defer span.End()
	res := struct {
		DriverId string `json:"driverId"`
	}{}
	err := json.NewDecoder(r.Body).Decode(&res)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	d, err := tp.repo.GetViolationHistory(ctx, res.DriverId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

	rw.Header().Set(contentType, appJson)
	rw.WriteHeader(http.StatusOK)
	if err = d.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

}
func (tp *TrafficPoliceHandler) HandleExportViolations(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleExportViolations")
	defer span.End()

	vars := mux.Vars(r)
	format := vars["format"]
	period := vars["period"]

	data, err := tp.repo.ExportViolationData(ctx, format, period)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

	var contentTypes, fileName string
	switch format {
	case "csv":
		contentTypes = "text/csv"
		fileName = fmt.Sprintf("violations_%s.csv", period)
	case "pdf":
		contentTypes = "application/pdf"
		fileName = fmt.Sprintf("violations_%s.pdf", period)
	default:
		http.Error(rw, "unsupported format", http.StatusBadRequest)
		return
	}

	rw.Header().Set(contentType, contentTypes)
	rw.Header().Set("Content-Disposition", fmt.Sprintf("attachment; filename=%s", fileName))
	rw.WriteHeader(http.StatusOK)

	if _, err = rw.Write(data); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
	}
}

func (tp *TrafficPoliceHandler) HandleOfficerSuspension(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleDailyStatistics")
	defer span.End()
	res := struct {
		PoliceId string `json:"policeId"`
	}{}
	err := json.NewDecoder(r.Body).Decode(&res)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	err = tp.repo.SuspendOfficer(ctx, res.PoliceId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	rw.WriteHeader(http.StatusOK)
}
