package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.CreateOwnershipTransferDto;
import com.example.mupvehicles.dto.OwnershipTransferDto;
import com.example.mupvehicles.service.OwnershipTransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ownerTransfers")
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
public class OwnershipTransferController {

    private final OwnershipTransferService ownershipTransferService;

    @Autowired
    public OwnershipTransferController(OwnershipTransferService ownershipTransferService) {
        this.ownershipTransferService = ownershipTransferService;
    }

    @GetMapping("/getOwnershipTransferForVehicle/{registration}")
    public ResponseEntity<List<OwnershipTransferDto>> getOwnershipHistoryForInvestigation(@PathVariable String registration) {
        return new ResponseEntity<>(ownershipTransferService.getOwnershipHistoryForInvestigation(registration), HttpStatus.OK);

    }

    @PostMapping("/create/OwnershipTransfer")
    public ResponseEntity<OwnershipTransferDto> transferOwnership(@Valid @RequestBody CreateOwnershipTransferDto createOwnershipTransferDto) {
        return new ResponseEntity<>(ownershipTransferService.transferOwnership(createOwnershipTransferDto), HttpStatus.CREATED);

    }


}
