package com.example.traffic_police.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String jmbg;
    private String email;
}
