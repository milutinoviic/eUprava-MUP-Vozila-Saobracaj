package com.example.mupvehicles.service;

import com.example.mupvehicles.dto.CreateOwnershipTransferDto;
import com.example.mupvehicles.dto.OwnershipTransferDto;

import java.util.List;

public interface OwnershipTransferService {

    OwnershipTransferDto transferOwnership(CreateOwnershipTransferDto transferOwnershipDto);

    List<OwnershipTransferDto> getOwnershipHistoryForInvestigation(String registration);
}
