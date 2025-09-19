package Traffic_Police

import (
	"context"
	"eUprava/trafficPolice/handler"
	"eUprava/trafficPolice/repo"
	"fmt"
	"github.com/gorilla/mux"
	"github.com/rs/cors"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/exporters/jaeger"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/sdk/resource"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
	semconv "go.opentelemetry.io/otel/semconv/v1.10.0"
	"log"
	"net/http"
	"os"
	"os/signal"
	"time"
)

func main() {
	config := loadConfig()
	fmt.Println("Traffic police service is starting...")
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	timeoutCtx, cancel := context.WithTimeout(context.Background(), 60*time.Second)
	defer cancel()
	logger := log.New(os.Stdout, "[police-api] ", log.LstdFlags)
	cfg := os.Getenv("JAEGER_ADDRESS")
	exp, err := newExporter(cfg)
	if err != nil {
		log.Fatal(err)
	}

	tp := newTraceProvider(exp)
	defer func() { _ = tp.Shutdown(timeoutCtx) }()
	otel.SetTracerProvider(tp)
	otel.SetTextMapPropagator(propagation.TraceContext{})
	tracer := tp.Tracer("traffic-police-service")
	pr, err := repo.NewTrafficPoliceRepo(logger, tracer, timeoutCtx)
	if err != nil {
		logger.Fatal(err)
	}
	ph := handler.NewTrafficPoliceHandler(logger, pr, tracer)

	router := mux.NewRouter()
	router.Use(handler.ExtractTraceInfoMiddleware)

	// GET
	router.HandleFunc("/violations/{id}", ph.HandleGettingViolationsByOfficer).Methods(http.MethodGet)
	router.HandleFunc("/fines/unpaid/{jmbg}", ph.HandleGettingUnpaidFines).Methods(http.MethodGet)
	router.HandleFunc("/vehicles/history/{registration}", ph.GetVehicleHistory).Methods(http.MethodGet)
	router.HandleFunc("/police/statistics/{police}", ph.HandleDailyStatistics).Methods(http.MethodGet)
	router.HandleFunc("/violations/history/{driverId}", ph.HandleViolationHistory).Methods(http.MethodGet)
	router.HandleFunc("/violations/{format}/{period}", ph.HandleExportViolations).Methods(http.MethodGet)
	router.HandleFunc("/police", ph.HandleGettingPolice).Methods(http.MethodGet)
	router.HandleFunc("/owners", ph.BuildRequestForAllDrivers).Methods(http.MethodGet)
	router.HandleFunc("/vehicles/stolen/{registration}", ph.HandleQuestionAboutVehicle).Methods(http.MethodGet)
	router.HandleFunc("/owners/history/{registration}", ph.GetOwnershipHistoryForInvestigation).Methods(http.MethodGet)

	// POST
	router.HandleFunc("/violations/new", ph.HandleNewViolation).Methods(http.MethodPost)
	router.HandleFunc("/vehicles/verify", ph.VerifyVehicleWithOwner).Methods(http.MethodPost)
	router.HandleFunc("/vehicles/stolen/{registration}", ph.ReportVehicleAsStolen).Methods(http.MethodPost)
	router.HandleFunc("/vehicles/search", ph.SearchVehicleByOptional).Methods(http.MethodPost)

	// PATCH
	router.HandleFunc("/violation/assign", ph.HandleAssignOfViolation).Methods(http.MethodPatch)
	router.HandleFunc("/police/promotion/{policeId}", ph.HandleOfficerPromotion).Methods(http.MethodPatch)
	router.HandleFunc("/police/suspend/{policeId}", ph.HandleOfficerSuspension).Methods(http.MethodPatch)

	corsHandler := cors.New(cors.Options{
		AllowedOrigins:   []string{"*"},
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"*"},
		AllowCredentials: true,
	})

	h := corsHandler.Handler(router)

	server := http.Server{
		Addr:         config["address"],
		Handler:      h,
		IdleTimeout:  120 * time.Second,
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 10 * time.Second,
	}
	logger.Println("Starting server on port", config["address"])
	go func() {
		err := server.ListenAndServeTLS("/app/cert.crt", "/app/privat.key")
		if err != nil {
			logger.Fatal(err)
		}
	}()

	sigCh := make(chan os.Signal)
	signal.Notify(sigCh, os.Interrupt)
	signal.Notify(sigCh, os.Kill)

	sig := <-sigCh
	logger.Println("Got signal:", sig)
	if server.Shutdown(timeoutCtx) != nil {
		logger.Fatal("Server Shutdown")
	}
	logger.Println("Server gracefully shutdown")
}
func newExporter(address string) (*jaeger.Exporter, error) {
	if address == "" {
		return nil, fmt.Errorf("jaeger collector endpoint address is empty")
	}
	exp, err := jaeger.New(jaeger.WithCollectorEndpoint(jaeger.WithEndpoint(address)))
	if err != nil {
		return nil, fmt.Errorf("failed to create Jaeger exporter: %w", err)
	}
	return exp, nil
}

func newTraceProvider(exp sdktrace.SpanExporter) *sdktrace.TracerProvider {
	r, err := resource.Merge(
		resource.Default(),
		resource.NewWithAttributes(
			semconv.SchemaURL,
			semconv.ServiceNameKey.String("traffic-police-service"),
		),
	)
	if err != nil {
		log.Fatalf("failed to create resource %v", err)
	}
	return sdktrace.NewTracerProvider(
		sdktrace.WithSampler(sdktrace.AlwaysSample()),
		sdktrace.WithBatcher(exp),
		sdktrace.WithResource(r),
	)
}
func loadConfig() map[string]string {
	config := make(map[string]string)
	config["address"] = fmt.Sprintf(":%s", os.Getenv("PORT"))
	return config
}
