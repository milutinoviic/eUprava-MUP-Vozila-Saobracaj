package dto

import (
	"encoding/json"
	"io"
)

type SuspendDriverIdRequest struct {
	DriverId                string `json:"driverId"`
	NumberOfViolationPoints int    `json:"numberOfViolationPoints"`
}

func (s *SuspendDriverIdRequest) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(s)
}

func (s *SuspendDriverIdRequest) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	return e.Decode(s)
}
