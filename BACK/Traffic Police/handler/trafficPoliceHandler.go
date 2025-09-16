package handler

import (
	"eUprava/trafficPolice/model"
	"eUprava/trafficPolice/repo"
	"encoding/json"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/codes"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/trace"
	"log"
	"net/http"
)

type KeyViolation struct{}

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
