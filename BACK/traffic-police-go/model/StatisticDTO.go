package model

import (
	"encoding/json"
	"io"
	"time"
)

type StatisticDTO struct {
	Date               time.Time `bson:"date" json:"date"`
	NumberOfViolations int       `bson:"number_of_violations" json:"number_of_violations"`
}

type StatisticsDTO []*StatisticDTO

func (s *StatisticDTO) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(s)
}

func (s *StatisticDTO) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	e.DisallowUnknownFields()
	return e.Decode(s)
}

func (s *StatisticsDTO) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(s)
}

func (s *StatisticsDTO) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	e.DisallowUnknownFields()
	return e.Decode(s)
}
