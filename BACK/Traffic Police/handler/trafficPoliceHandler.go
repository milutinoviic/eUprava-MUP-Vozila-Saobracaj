package handler

import (
	"eUprava/trafficPolice/repo"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/trace"
	"log"
	"net/http"
)

type TrafficPoliceHandler struct {
	logger *log.Logger
	repo *repo.TrafficPoliceRepo
	tracer trace.Tracer
}
func ExtractTraceInfoMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ctx := otel.GetTextMapPropagator().Extract(r.Context(), propagation.HeaderCarrier(r.Header))
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}