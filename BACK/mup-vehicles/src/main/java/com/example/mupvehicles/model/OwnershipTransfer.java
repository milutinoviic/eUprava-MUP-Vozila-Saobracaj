package com.example.mupvehicles.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OwnershipTransfer {

    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private Owner oldOwner;

    @ManyToOne
    private Owner newOwner;

    private Date dateOfTransfer;
}
