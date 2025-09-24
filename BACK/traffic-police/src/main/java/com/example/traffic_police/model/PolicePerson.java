package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "police")
public class PolicePerson {

    @Id
    private String id;            // MongoDB _id
    private String firstName;
    private String lastName;
    private Rank rank;
    private boolean isSuspended;
    private String email;
    private String password;

    public enum Rank {
        LOW, MEDIUM, HIGH
    }
}
