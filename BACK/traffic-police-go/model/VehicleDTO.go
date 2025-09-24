package model

import (
	"encoding/json"
	"io"
)

type VehicleDTO struct {
	Id           string `json:"id"`
	Mark         string `json:"mark"`
	Model        string `json:"model"`
	Registration string `json:"registration"`
	Year         int    `json:"year"`
	Color        string `json:"color"`
	IsStolen     bool   `json:"isStolen"`
	OwnerId      string `json:"ownerId"`
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
	d.DisallowUnknownFields()
	return d.Decode(v)
}

func (v *VehicleDTO) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	d.DisallowUnknownFields()
	return d.Decode(v)
}
