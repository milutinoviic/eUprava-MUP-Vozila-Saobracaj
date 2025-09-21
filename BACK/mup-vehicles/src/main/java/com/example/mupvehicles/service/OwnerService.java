package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.CreateOwnerDto;
import com.example.mupvehicles.dto.OwnerDto;

import java.util.List;

public interface OwnerService {

    OwnerDto getOwnerByRegistration(String registration);

    OwnerDto createOwner(CreateOwnerDto createOwnerDto);

    void deleteOwner(String ownerId);

    List<OwnerDto> getAllOwners();
}
