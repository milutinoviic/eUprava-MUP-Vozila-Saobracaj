package com.example.mupvehicles.controller;

import com.example.mupvehicles.dto.CreateDriverIdDto;
import com.example.mupvehicles.dto.DriverIdDto;
import com.example.mupvehicles.dto.SuspendDriverIdRequest;
import com.example.mupvehicles.service.DriverIdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;



@RestController
@RequestMapping("/api/driverIds")
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
public class DriverIdController {

    private final DriverIdService driverIdService;

    @Autowired
    public DriverIdController(DriverIdService driverIdService) {
        this.driverIdService = driverIdService;
    }


    @GetMapping("/{jmbg}")
    public DriverIdDto getDriverIdByOwner(@PathVariable String jmbg) {
        return driverIdService.getDriverId(jmbg);
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DriverIdDto createDriverId(
            @RequestParam("ownerJmbg") String ownerJmbg
    ) {
        CreateDriverIdDto dto = new CreateDriverIdDto(ownerJmbg);
        return driverIdService.createDriverId(dto);
    }


    @PatchMapping("/suspendDriverId")
    public DriverIdDto suspendDriverId(@Valid @RequestBody SuspendDriverIdRequest suspendDriverIdRequest) {
        return driverIdService.suspendDriverId(suspendDriverIdRequest);
    }

    @PatchMapping("/reactivateDriverId/{reactivateDriverId}")
    public DriverIdDto reactivateDriverId(@Valid @PathVariable String reactivateDriverId) {
        return driverIdService.reactivateDriverId(reactivateDriverId);
    }

    @DeleteMapping("/deleteDriverId/{id}")
    public ResponseEntity<Void> deleteDriverId(@PathVariable String id) {
        driverIdService.deleteDriverId(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/picture")
    public ResponseEntity<Resource> getDriverIdPicture(@RequestParam String userJmbg) {
        Resource image = driverIdService.getDriverIdPicture(userJmbg);

        String contentType = "image/jpeg";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }

}
