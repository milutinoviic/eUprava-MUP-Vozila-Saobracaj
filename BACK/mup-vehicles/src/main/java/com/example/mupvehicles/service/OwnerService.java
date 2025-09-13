package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.OwnerDto;

public interface OwnerService {

    OwnerDto getOwnerByRegistration(String registration);
}
