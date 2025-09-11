package Traffic_Police

import (
	"context"
	"fmt"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/exporters/jaeger"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/sdk/resource"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
	semconv "go.opentelemetry.io/otel/semconv/v1.10.0"
	"log"
	"os"
	"time"
)

func main() {
	fmt.Println("Traffic police service is starting...")
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	timeoutCtx, cancel := context.WithTimeout(context.Background(), 60*time.Second)
	defer cancel()

	cfg := os.Getenv("JAEGER_ADDRESS")
	exp, err := newExporter(cfg)
	if err != nil {
		log.Fatal(err)
	}

	tp := newTraceProvider(exp)
	defer func() { _ = tp.Shutdown(timeoutCtx) }()
	otel.SetTracerProvider(tp)
	otel.SetTextMapPropagator(propagation.TraceContext{})
	// tracer := tp.Tracer("traffic-police-service")
	//router := mux.NewRouter()

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
