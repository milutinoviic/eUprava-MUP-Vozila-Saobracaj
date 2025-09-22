package dto

import (
	"eUprava/trafficPolice/model"
	"encoding/json"
	"io"
	"time"
)

type OwnershipTransferDto struct {
	Id             string           `json:"id"`
	Vehicle        model.VehicleDTO `json:"vehicle"`
	OldOwner       model.OwnerDTO   `json:"oldOwner"`
	NewOwner       model.OwnerDTO   `json:"newOwner"`
	DateOfTransfer time.Time        `json:"dateOfTransfer"`
}

func (o *OwnershipTransferDto) ToJSON(w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(o)
}
func (o *OwnershipTransferDto) FromJSON(r io.Reader) error {
	e := json.NewDecoder(r)
	return e.Decode(o)
}
