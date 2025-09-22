package Traffic_Police

import (
	"context"
	"eUprava/trafficPolice/handler"
	"eUprava/trafficPolice/repo"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/gorilla/mux"
	"github.com/rs/cors"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/exporters/jaeger"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/sdk/resource"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
	semconv "go.opentelemetry.io/otel/semconv/v1.10.0"
	"io"
	"log"
	"net/http"
	"os"
	"os/signal"
	"strings"
	"time"
)

type VerifyResponse struct {
	Ok    bool   `json:"ok"`
	Email string `json:"email,omitempty"`
	Role  string `json:"role,omitempty"`
}

type contextKey string

const (
	ContextEmail contextKey = "email"
	ContextRole  contextKey = "role"
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

	router.Use(func(next http.Handler) http.Handler {
		return AuthMiddleware("http://auth-service:8080")(next)
	})
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
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatal(err)
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

func AuthMiddleware(authServiceURL string) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			authHeader := r.Header.Get("Authorization")
			if authHeader == "" || !strings.HasPrefix(authHeader, "Bearer ") {
				writeJSONError(w, http.StatusUnauthorized, "Missing or invalid Authorization header")
				return
			}
			token := strings.TrimPrefix(authHeader, "Bearer ")

			client := &http.Client{Timeout: 10 * time.Second}
			req, err := http.NewRequest(http.MethodGet, authServiceURL+"/auth/verify", nil)
			if err != nil {
				writeJSONError(w, http.StatusInternalServerError, "Internal error creating request")
				return
			}
			req.Header.Set("Authorization", "Bearer "+token)

			resp, err := client.Do(req)
			if err != nil {
				writeJSONError(w, http.StatusUnauthorized, "Auth service unreachable: "+err.Error())
				return
			}
			defer resp.Body.Close()

			if resp.StatusCode != http.StatusOK {
				w.Header().Set("Content-Type", "application/json")
				w.WriteHeader(resp.StatusCode)
				io.Copy(w, resp.Body)
				return
			}

			var verify VerifyResponse
			if err := json.NewDecoder(resp.Body).Decode(&verify); err != nil {
				writeJSONError(w, http.StatusUnauthorized, "Invalid response from auth service")
				return
			}

			if !verify.Ok {
				writeJSONError(w, http.StatusUnauthorized, "Token not valid")
				return
			}

			ctx := context.WithValue(r.Context(), ContextEmail, verify.Email)
			ctx = context.WithValue(ctx, ContextRole, verify.Role)
			next.ServeHTTP(w, r.WithContext(ctx))
		})
	}
}

func writeJSONError(w http.ResponseWriter, status int, msg string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(map[string]string{"error": msg})
}
