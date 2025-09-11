package model

import (
	"encoding/json"
	"io"
	"time"
)

type Fine struct {
	Id          string    `json:"id"`
	Amount      float64   `json:"amount"`
	IsPaid      bool      `json:"isPaid"`
	Date        time.Time `json:"date"`
	ViolationID string    `json:"violationID"`
}

type Fines []*Fine

func (f *Fine) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(f)
}
func (f *Fine) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	return e.Decode(f)

}

func (f *Fines) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	return e.Decode(f)
}

func (f *Fines) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(f)
}
