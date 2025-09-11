package model

import (
	"encoding/json"
	"io"
)

type OwnerDTO struct {
	Jmbg      string `json:"jmbg"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
	Email     string `json:"email"`
}

type Owners []*OwnerDTO

func (o *Owners) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(o)
}

func (o *OwnerDTO) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(o)
}

func (o *OwnerDTO) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(o)
}

func (o *Owners) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(o)
}
