package com.example.mupvehicles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDto {

    private String id;

    private String firstName;

    private String lastName;

    private String address;

    private String jmbg;

    private String email;

}
