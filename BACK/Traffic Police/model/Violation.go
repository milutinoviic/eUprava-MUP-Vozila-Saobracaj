package model

import (
	"encoding/json"
	"io"
	"time"
)

type Violation struct {
	Id              string          `json:"id"`
	TypeOfViolation TypeOfViolation `json:"type_of_violation"`
	Date            time.Time       `json:"date"`
	Location        string          `json:"location"`
	DriverId        string          `json:"driver_id"`
	VehicleId       string          `json:"vehicle_id"`
	PoliceId        string          `json:"police_id"`
}

type Violations []*Violation

type TypeOfViolation string

const (
	Minor    TypeOfViolation = "MINOR"
	Major    TypeOfViolation = "MAJOR"
	Critical TypeOfViolation = "CRITICAL"
)

func (v *Violation) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(v)
}

func (v *Violation) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(v)
}

func (v *Violations) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(v)
}
func (v *Violations) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(v)
}
