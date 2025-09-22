package Auth

import (
	"context"
	"eUprava/Auth/handlers"
	"eUprava/Auth/repository"
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
	"log"
	"net/http"
	"os"
	"os/signal"
	"time"
)

func main() {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	timeoutCtx, cancel := context.WithTimeout(context.Background(), 60*time.Second)
	defer cancel()
	logger := log.New(os.Stdout, "[auth-api]", log.LstdFlags)
	cfg := os.Getenv("JAEGER_ADDRESS")
	exp, err := newExporter(cfg)
	if err != nil {
		logger.Fatal(err)
	}

	tp := newTraceProvider(exp)
	defer func() { _ = tp.Shutdown(timeoutCtx) }()
	otel.SetTracerProvider(tp)
	otel.SetTextMapPropagator(propagation.TraceContext{})
	tracer := tp.Tracer("auth-service")
	ar, err := repository.NewUserRepository(logger, tracer, timeoutCtx)
	if err != nil {
		logger.Fatal(err)
	}
	secretKey := []byte(os.Getenv("SECRET_KEY"))
	au := handlers.NewUserHandler(ar, secretKey)

	router := mux.NewRouter()
	router.Use(jsonContentTypeMiddleware)

	router.HandleFunc("/login", au.Login).Methods("POST")
	router.HandleFunc("/register", au.Register).Methods("POST")
	router.HandleFunc("/auth/verify", au.Verify).Methods("GET", "HEAD")

	corsHandler := cors.New(cors.Options{
		AllowedOrigins:   []string{"*"},
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"*"},
		AllowCredentials: true,
	})

	h := corsHandler.Handler(router)

	server := &http.Server{
		Addr:         ":" + port,
		Handler:      h,
		IdleTimeout:  time.Minute,
		ReadTimeout:  5 * time.Second,
		WriteTimeout: 10 * time.Second,
	}

	go func() {
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatal(err)
		}
	}()

	// Graceful shutdown
	sigCh := make(chan os.Signal, 1)
	signal.Notify(sigCh, os.Interrupt, os.Kill)
	sig := <-sigCh
	log.Println("Received terminate, graceful shutdown", sig)
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
			semconv.ServiceNameKey.String("auth-service"),
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

func jsonContentTypeMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		next.ServeHTTP(w, r)
	})
}
