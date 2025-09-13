package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.OwnerDto;
import com.example.mupvehicles.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping("/{registration}")
    public ResponseEntity<OwnerDto> getOwnerByRegistration(@PathVariable String registration) {
        return new ResponseEntity<>(ownerService.getOwnerByRegistration(registration), HttpStatus.OK);
    }

}
