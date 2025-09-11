package model

import (
	"encoding/json"
	"io"
)

type PolicePerson struct {
	Id        string `json:"id"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
	Rank      Rank   `json:"rank"`
	Email     string `json:"email"`
	Password  string `json:"password"`
}

type Police []*PolicePerson

type Rank string

const (
	RankLow    Rank = "LOW"
	RankMedium Rank = "MEDIUM"
	RankHigh   Rank = "HIGH"
)

func (p *PolicePerson) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(p)
}
func (p *PolicePerson) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(p)
}

func (p *Police) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(p)
}
func (p *Police) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(p)
}
