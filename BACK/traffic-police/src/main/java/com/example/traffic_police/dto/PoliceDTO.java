package com.example.traffic_police.dto;

import com.example.traffic_police.model.PolicePerson;

public class PoliceDTO {
    private String id;            // MongoDB _id
    private String firstName;
    private String lastName;
    private PolicePerson.Rank rank;

    public PoliceDTO(PolicePerson person) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.rank = person.getRank();
    }
}
