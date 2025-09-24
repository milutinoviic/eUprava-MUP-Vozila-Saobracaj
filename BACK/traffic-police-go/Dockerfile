FROM golang:alpine AS build_container
WORKDIR /app
COPY go.mod .
COPY go.sum .
RUN go mod download
COPY . .
RUN go build -o server

FROM alpine:3.18

RUN apk add --no-cache ca-certificates

COPY --from=build_container /app/server /usr/bin

EXPOSE 8010

ENTRYPOINT ["server"]
