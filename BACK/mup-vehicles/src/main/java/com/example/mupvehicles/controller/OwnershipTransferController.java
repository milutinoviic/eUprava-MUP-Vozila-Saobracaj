package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.CreateOwnershipTransferDto;
import com.example.mupvehicles.dto.OwnershipTransferDto;
import com.example.mupvehicles.service.OwnershipTransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ownerTransfers")
public class OwnershipTransferController {

    private OwnershipTransferService ownershipTransferService;

    @Autowired
    public OwnershipTransferController(OwnershipTransferService ownershipTransferService) {
        this.ownershipTransferService = ownershipTransferService;
    }

    @PostMapping("/create/OwnershipTransfer")
    public ResponseEntity<OwnershipTransferDto> transferOwnership(@Valid @RequestBody CreateOwnershipTransferDto createOwnershipTransferDto) {
        return new ResponseEntity<>(ownershipTransferService.transferOwnership(createOwnershipTransferDto), HttpStatus.CREATED);

    }

}
