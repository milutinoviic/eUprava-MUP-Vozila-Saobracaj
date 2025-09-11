package model

import (
	"encoding/json"
	"io"
)

type VehicleDTO struct {
	Registration string `json:"registration"`
	Mark         string `json:"mark"`
	Model        string `json:"model"`
	OwnerId      string `json:"ownerId"`
	IsStolen     bool   `json:"isStolen"`
}

type Vehicles []*VehicleDTO

func (v *VehicleDTO) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(v)
}

func (v *Vehicles) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(v)
}

func (v *Vehicles) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(v)
}

func (v *VehicleDTO) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(v)
}
