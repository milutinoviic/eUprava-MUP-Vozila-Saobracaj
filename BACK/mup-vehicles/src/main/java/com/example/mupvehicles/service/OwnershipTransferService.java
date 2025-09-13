package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.CreateOwnershipTransferDto;
import com.example.mupvehicles.dto.OwnershipTransferDto;

public interface OwnershipTransferService {

    OwnershipTransferDto transferOwnership(CreateOwnershipTransferDto transferOwnershipDto);
}
