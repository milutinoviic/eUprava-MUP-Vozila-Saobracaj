package com.example.mupvehicles.dto;

import lombok.Data;

@Data
public class PolicePersonDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String rank;
    private boolean isSuspended;
    private String email;
    private String password;
}