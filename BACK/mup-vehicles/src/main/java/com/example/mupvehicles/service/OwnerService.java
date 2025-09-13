package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.CreateOwnerDto;
import com.example.mupvehicles.dto.OwnerDto;

public interface OwnerService {

    OwnerDto getOwnerByRegistration(String registration);

    OwnerDto createOwner(CreateOwnerDto createOwnerDto);

    void deleteOwner(String ownerId);
}
