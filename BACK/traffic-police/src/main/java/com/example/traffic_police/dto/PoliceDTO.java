package com.example.traffic_police.dto;

import com.example.traffic_police.model.PolicePerson;
import lombok.Getter;

// PoliceDTO.java
@Getter
public class PoliceDTO {

    private final String id;            // MongoDB _id
    private final String firstName;
    private final String lastName;
    private final boolean isSuspended;
    private final PolicePerson.Rank rank;
    private final String email;


    public PoliceDTO(PolicePerson person) {
        this.id = person.getId() != null ? person.getId() : "";
        this.firstName = person.getFirstName() != null ? person.getFirstName() : "";
        this.lastName = person.getLastName() != null ? person.getLastName() : "";
        this.isSuspended = person.isSuspended();
        this.rank = person.getRank() != null ? person.getRank() : PolicePerson.Rank.LOW;
        this.email = person.getEmail() != null ? person.getEmail() : "";
    }

}
