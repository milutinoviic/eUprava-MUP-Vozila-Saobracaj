package model

import (
	"encoding/json"
	"io"
	"time"
)

type Violation struct {
	Id              string          `bson:"_id,omitempty" json:"id"`
	TypeOfViolation TypeOfViolation `bson:"type_of_violation" json:"type_of_violation"`
	Date            time.Time       `bson:"date" json:"date"`
	Location        string          `bson:"location" json:"location"`
	DriverId        string          `bson:"driver_id" json:"driver_id"`
	VehicleId       string          `bson:"vehicle_id" json:"vehicle_id"`
	PoliceId        string          `bson:"police_id,omitempty" json:"police_id,omitempty"`
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
	d.DisallowUnknownFields()
	return d.Decode(v)
}

func (v *Violations) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(v)
}
func (v *Violations) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	d.DisallowUnknownFields()
	return d.Decode(v)
}
