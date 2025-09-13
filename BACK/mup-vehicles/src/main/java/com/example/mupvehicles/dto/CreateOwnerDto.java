package com.example.mupvehicles.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOwnerDto {

    @NotNull(message = "firstName cannot be null")
    @Size(min = 3, message = "firstName must be at least 3 characters")
    private String firstName;

    @NotNull(message = "lastname cannot be null")
    @Size(min = 3, message = "lastname must be at least 3 characters")
    private String lastName;

    @NotNull(message = "address cannot be null")
    @Size(min = 3, message = "address must be at least 3 characters")
    private String address;

    @NotNull(message = "jmbg cannot be null")
    @Size(min = 9, message = "jmbg must be at least 9 characters")
    @Size(max = 9, message = "jmbg max 9 characters")
    private String jmbg;

    @NotNull(message = "email cannot be null")
    @Email(message = "email must be valid")
    private String email;

}
