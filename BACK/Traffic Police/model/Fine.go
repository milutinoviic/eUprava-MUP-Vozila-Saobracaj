package model

import (
	"encoding/json"
	"io"
	"time"
)

type Fine struct {
	Id          string    `bson:"_id,omitempty" json:"id"`
	Amount      float64   `bson:"amount" json:"amount"`
	IsPaid      bool      `bson:"is_paid" json:"isPaid"`
	Date        time.Time `bson:"date" json:"date"`
	ViolationID string    `bson:"violation_id" json:"violationID"`
}

type Fines []*Fine

func (f *Fine) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(f)
}
func (f *Fine) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	e.DisallowUnknownFields()
	return e.Decode(f)

}

func (f *Fines) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	e.DisallowUnknownFields()
	return e.Decode(f)
}

func (f *Fines) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(f)
}
