package model

import (
	"encoding/json"
	"io"
)

type DriverIDDTO struct {
	Id                 string `json:"id"`
	OwnerID            string `json:"ownerID"`
	IsSuspended        bool   `json:"isSuspended"`
	NumberOfViolations int    `json:"numberOfViolations"`
}

type DriverIDs []*DriverIDDTO

func (d *DriverIDDTO) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(d)
}

func (d *DriverIDDTO) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	return e.Decode(d)
}

func (d *DriverIDs) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(d)
}

func (d *DriverIDs) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	return e.Decode(d)
}
