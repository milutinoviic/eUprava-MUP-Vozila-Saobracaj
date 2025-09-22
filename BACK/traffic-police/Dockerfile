# Build stage
FROM golang:1.23.11-alpine AS build

WORKDIR /app

# Install git (needed for module dependencies)
RUN apk add --no-cache git

# Cache Go modules
COPY go.mod go.sum ./
RUN go mod download

# Copy source code and build the binary
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o traffic-police .

# Final stage (small production image)
FROM alpine:latest
WORKDIR /app

# Copy the binary from the build stage
COPY --from=build /app/traffic-police .

RUN chmod +x /app/traffic-police

# Expose the port your service listens on (example: 8081)
EXPOSE 8081

# Run the binary
ENTRYPOINT ["./traffic-police"]
