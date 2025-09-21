# Build stage
FROM golang:1.21-alpine AS build
WORKDIR /app

# Install git (needed if your modules require it)
RUN apk add --no-cache git

# Cache dependencies
COPY go.mod go.sum ./
RUN go mod download

# Copy source and build
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o server .

# Final stage
FROM alpine:latest
WORKDIR /app

# Copy the binary
COPY --from=build /app/server .

# Expose port
EXPOSE 8090

# Run the binary
ENTRYPOINT ["./server"]
