package handler

import (
	"bytes"
	"context"
	"eUprava/trafficPolice/domain"
	dto2 "eUprava/trafficPolice/dto"
	"eUprava/trafficPolice/model"
	"eUprava/trafficPolice/repo"
	"encoding/json"
	"fmt"
	"github.com/eapache/go-resiliency/retrier"
	"github.com/gorilla/mux"
	"github.com/sony/gobreaker"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/codes"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/trace"
	"io"
	"log"
	"net/http"
	"os"
	"strings"
	"time"
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

func NewTrafficPoliceHandler(logger *log.Logger, repo *repo.TrafficPoliceRepo, tracer trace.Tracer) *TrafficPoliceHandler {
	return &TrafficPoliceHandler{
		logger: logger,
		repo:   repo,
		tracer: tracer,
	}
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
	b := struct {
		violation model.Violation
		owner     model.OwnerDTO
		driverId  model.DriverIDDTO
	}{}
	err := json.NewDecoder(r.Body).Decode(&b)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
	}

	_, err = tp.repo.InsertViolation(ctx, b.violation)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
	}

	err = tp.repo.NotifyPersonOfViolation(ctx, b.violation, b.owner)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
	}

	var points int
	switch b.violation.TypeOfViolation {
	case model.Minor:
		points = 1
	case model.Major:
		points = 2
	case model.Critical:
		points = 3
	}

	ans := dto2.SuspendDriverIdRequest{
		DriverId:                b.driverId.Id,
		NumberOfViolationPoints: points,
	}
	v, err := tp.HandleDriverSuspension(ctx, ans)

	rw.Header().Set("Content-Type", "application/json")
	rw.WriteHeader(http.StatusCreated)
	err = v.ToJSON(rw)
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

	vars := mux.Vars(r)
	id := vars["officerId"]

	v, err := tp.repo.GetAssignedViolations(ctx, id)
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
	vars := mux.Vars(r)
	res := vars["jmbg"]

	f, err := tp.repo.FindUnpaidFinesByDriverID(ctx, res)
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
	vars := mux.Vars(r)
	res := vars["registration"]

	v, err := tp.repo.CheckVehicleViolations(ctx, res)
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
	vars := mux.Vars(r)
	res := vars["police"]

	d, err := tp.repo.GetDailyStatistics(ctx, res)
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
	ctx, span := tp.tracer.Start(r.Context(), "HandleOfficerPromotion")
	defer span.End()

	vars := mux.Vars(r)
	id := vars["policeId"]

	err := tp.repo.PromoteOfficer(ctx, id)
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

	vars := mux.Vars(r)
	driverId := vars["driverId"]

	d, err := tp.repo.GetViolationHistory(ctx, driverId)
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
	ctx, span := tp.tracer.Start(r.Context(), "HandleOfficerSuspension")
	defer span.End()
	vars := mux.Vars(r)
	policeId := vars["policeId"]

	err := tp.repo.SuspendOfficer(ctx, policeId)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	rw.WriteHeader(http.StatusOK)
}

func (tp *TrafficPoliceHandler) HandleGettingPolice(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleGettingPolice")
	defer span.End()

	v, err := tp.repo.GetAllPolice(ctx)
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

func (tp *TrafficPoliceHandler) fetchAllDrivers(ctx context.Context) (model.Owners, error) {
	client := &http.Client{}

	projectUrl := os.Getenv("LINK_TO_MUP_SERVICE")
	ownerService := fmt.Sprintf("%s/owners", projectUrl)

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, ownerService, nil)
	if err != nil {
		return nil, fmt.Errorf("failed to build request: %w", err)
	}

	resp, err := tp.executeReqToMUP(ctx, req, client, "fetchAllDrivers")
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var drivers model.Owners
	if err = drivers.FromJSON(resp.Body); err != nil {
		return nil, fmt.Errorf("failed to decode drivers: %w", err)
	}
	return drivers, nil
}

func (tp *TrafficPoliceHandler) BuildRequestForAllDrivers(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "BuildRequestForAllDrivers")
	defer span.End()

	drivers, err := tp.fetchAllDrivers(ctx)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

	rw.Header().Set(contentType, appJson)
	rw.WriteHeader(http.StatusOK)
	if err = drivers.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
	}
}

func (tp *TrafficPoliceHandler) executeReqToMUP(ctx context.Context, req *http.Request, clientToDo *http.Client, name string) (*http.Response, error) {
	circuit := tp.createCircuitBreaker(name)
	retryAgain := retrier.New(retrier.ConstantBackoff(3, time.Second), retrier.WhitelistClassifier{domain.ErrRespTmp{}})
	var resp *http.Response
	err := retryAgain.RunCtx(ctx, func(ctx context.Context) (err error) {
		_, err = circuit.Execute(func() (interface{}, error) {
			var err error
			resp, err = clientToDo.Do(req)
			if err != nil {
				return nil, err
			}

			if resp.StatusCode == http.StatusGatewayTimeout || resp.StatusCode == http.StatusServiceUnavailable {
				return nil, domain.ErrRespTmp{
					URL:        resp.Request.URL.String(),
					Method:     resp.Request.Method,
					StatusCode: resp.StatusCode,
				}
			}
			if resp.StatusCode != http.StatusOK && resp.StatusCode != http.StatusNoContent {
				return nil, domain.ErrResp{
					URL:        resp.Request.URL.String(),
					Method:     resp.Request.Method,
					StatusCode: resp.StatusCode,
				}
			}

			return resp, nil
		})
		return err
	})

	return resp, err
}

func (tp *TrafficPoliceHandler) createCircuitBreaker(name string) *gobreaker.CircuitBreaker {
	return gobreaker.NewCircuitBreaker(
		gobreaker.Settings{
			Name:        name,
			MaxRequests: 10,
			Timeout:     10 * time.Second,
			Interval:    0,
			ReadyToTrip: func(counts gobreaker.Counts) bool {
				return counts.ConsecutiveFailures > 2
			},
			OnStateChange: func(name string, from, to gobreaker.State) {
				tp.logger.Printf("Circuit Breaker '%s' changed from '%s' to '%s'\n", name, from, to)
			},
		},
	)
}

func (tp *TrafficPoliceHandler) HandleDriverSuspension(ctx context.Context, dto dto2.SuspendDriverIdRequest) (model.Owners, error) {
	ctx, span := tp.tracer.Start(ctx, "HandleDriverSuspension")
	defer span.End()

	payload, err := json.Marshal(dto)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}

	projectUrl := os.Getenv("LINK_TO_MUP_SERVICE")
	idService := fmt.Sprintf("%s/driverIds/suspendDriverId", projectUrl)

	req, err := http.NewRequestWithContext(ctx, http.MethodPatch, idService, bytes.NewReader(payload))
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}
	req.Header.Set(contentType, appJson)

	client := &http.Client{}
	_, err = tp.executeReqToMUP(ctx, req, client, "HandleDriverSuspension")
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return nil, err
	}

	return tp.fetchAllDrivers(ctx)
}
func (tp *TrafficPoliceHandler) HandleQuestionAboutVehicle(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "HandleQuestionAboutVehicle")
	defer span.End()

	vars := mux.Vars(r)
	registration := vars["registration"]

	projectUrl := os.Getenv("LINK_TO_MUP_SERVICE")
	url := fmt.Sprintf("%s/vehicles/isStolen/%s", projectUrl, registration)

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

	client := &http.Client{}
	resp, err := tp.executeReqToMUP(ctx, req, client, "HandleQuestionAboutVehicle")
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()

	bodyBytes, err := io.ReadAll(resp.Body)
	if err != nil {
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	raw := strings.TrimSpace(string(bodyBytes))

	var message string
	switch raw {
	case "True":
		message = "Vehicle is stolen"
	case "False":
		message = "Vehicle is not stolen"
	case "Vehicle does not exist":
		message = "Vehicle does not exist"
	default:
		message = "Unknown response from vehicle service"
	}

	rw.Header().Set("Content-Type", "application/json")
	rw.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(rw).Encode(map[string]string{"message": message}); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
}

func (tp *TrafficPoliceHandler) ReportVehicleAsStolen(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "ReportVehicleAsStolen")
	defer span.End()
	vars := mux.Vars(r)
	registration := vars["registration"]
	projectUrl := os.Getenv("LINK_TO_MUP_SERVICE")
	url := fmt.Sprintf("%s/vehicles/%s/report-stolen", projectUrl, registration)
	req, err := http.NewRequestWithContext(ctx, http.MethodPost, url, nil)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	client := &http.Client{}
	resp, err := tp.executeReqToMUP(ctx, req, client, "ReportVehicleAsStolen")
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			span.RecordError(err)
			span.SetStatus(codes.Error, err.Error())
			http.Error(rw, err.Error(), http.StatusInternalServerError)
			return
		}
	}(resp.Body)
	var dto model.VehicleDTO
	if err := json.NewDecoder(resp.Body).Decode(&dto); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

	rw.Header().Set(contentType, appJson)
	rw.WriteHeader(http.StatusOK)
	if err = dto.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
	}

}

func (tp *TrafficPoliceHandler) VerifyVehicleWithOwner(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "VerifyVehicleWithOwner")
	defer span.End()
	st := struct {
		Registration string `json:"registration"`
		Jmbg         string `json:"jmbg"`
	}{}
	if err := json.NewDecoder(r.Body).Decode(&st); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	projectUrl := os.Getenv("LINK_TO_MUP_SERVICE")
	url := fmt.Sprintf("%s/vehicles/verify", projectUrl)
	v, err := json.Marshal(st)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	req, err := http.NewRequestWithContext(ctx, http.MethodPost, url, bytes.NewReader(v))
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	client := &http.Client{}
	resp, err := tp.executeReqToMUP(ctx, req, client, "VerifyVehicleWithOwner")
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()
	bodyBytes, err := io.ReadAll(resp.Body)
	if err != nil {
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	raw := strings.TrimSpace(string(bodyBytes))

	var message string
	switch raw {
	case "Vehicle is reported as stolen":
		message = "Vehicle is reported as stolen"
	case "Vehicle does not belong to the owner, it may have been stolen":
		message = "Vehicle does not belong to the owner, it may have been stolen"
	case "Vehicle does not exist":
		message = "Vehicle does not exist"
	case "All good, the vehicle belongs to the owner":
		message = "All good, the vehicle belongs to the owner"
	default:
		message = "Unknown response from vehicle service"
	}

	rw.Header().Set("Content-Type", "application/json")
	rw.WriteHeader(http.StatusOK)
	err = json.NewEncoder(rw).Encode(map[string]string{
		"message": message,
	})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

}

func (tp *TrafficPoliceHandler) GetOwnershipHistoryForInvestigation(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "GetOwnershipHistoryForInvitation")
	defer span.End()
	vars := mux.Vars(r)
	registration := vars["registration"]
	projectUrl := os.Getenv("LINK_TO_MUP_SERVICE")
	url := fmt.Sprintf("%s/ownerTransfers/getOwnershipTransferForVehicle/%s", projectUrl, registration)
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	client := &http.Client{}

	resp, err := tp.executeReqToMUP(ctx, req, client, "GetOwnershipHistoryForInvitation")
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()
	var d dto2.OwnershipTransferDto
	if err := d.FromJSON(resp.Body); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	rw.Header().Set(contentType, appJson)
	rw.WriteHeader(http.StatusOK)
	if err := d.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}

}

func (tp *TrafficPoliceHandler) SearchVehicleByOptional(rw http.ResponseWriter, r *http.Request) {
	ctx, span := tp.tracer.Start(r.Context(), "SearchVehicleByOptional")
	defer span.End()
	st := struct {
		Mark         string `json:"mark,omitempty"`
		Model        string `json:"model,omitempty"`
		Color        string `json:"color,omitempty"`
		Registration string `json:"registration,omitempty"`
	}{}
	if err := json.NewDecoder(r.Body).Decode(&st); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}

	projectUrl := os.Getenv("LINK_TO_MUP_SERVICE")
	url := fmt.Sprintf("%s/vehicles/search", projectUrl)
	client := &http.Client{}
	v, err := json.Marshal(st)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}
	req, err := http.NewRequestWithContext(ctx, http.MethodPost, url, bytes.NewReader(v))
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	resp, err := tp.executeReqToMUP(ctx, req, client, "SearchVehicleByOptional")
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()
	var d model.Vehicles
	if err = d.FromJSON(resp.Body); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusBadRequest)
		return
	}
	rw.Header().Set("Content-Type", "application/json")
	rw.WriteHeader(http.StatusOK)
	if err = d.ToJSON(rw); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		http.Error(rw, err.Error(), http.StatusInternalServerError)
		return
	}
}
