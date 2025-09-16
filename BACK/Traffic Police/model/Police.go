package model

import (
	"encoding/json"
	"io"
)

type PolicePerson struct {
	Id        string `bson:"_id,omitempty" json:"id"`
	FirstName string `bson:"first_name" json:"firstName"`
	LastName  string `bson:"last_name" json:"lastName"`
	Rank      Rank   `bson:"rank" json:"rank"`
	Email     string `bson:"email" json:"email"`
	Password  string `bson:"password" json:"password"`
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
	d.DisallowUnknownFields()
	return d.Decode(p)
}

func (p *Police) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(p)
}
func (p *Police) FromJSON(r io.Reader) error {
	d := json.NewDecoder(r)
	d.DisallowUnknownFields()
	return d.Decode(p)
}
