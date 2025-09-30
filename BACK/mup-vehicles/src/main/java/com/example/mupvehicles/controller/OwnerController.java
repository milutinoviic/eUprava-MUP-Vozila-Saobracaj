package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.CreateOwnerDto;
import com.example.mupvehicles.dto.OwnerDto;
import com.example.mupvehicles.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
public class OwnerController {

    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<List<OwnerDto>> findAllOwners() {
        return new ResponseEntity<>(ownerService.getAllOwners(),HttpStatus.OK);
    }

    @GetMapping("/{registration}")
    public ResponseEntity<OwnerDto> getOwnerByRegistration(@PathVariable String registration) {
        return new ResponseEntity<>(ownerService.getOwnerByRegistration(registration), HttpStatus.OK);
    }

    @PostMapping("/createOwner")
    public ResponseEntity<OwnerDto> createOwner(@RequestBody CreateOwnerDto createOwnerDto) {
        return new ResponseEntity<>(ownerService.createOwner(createOwnerDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@PathVariable String id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok().build();
    }


}
