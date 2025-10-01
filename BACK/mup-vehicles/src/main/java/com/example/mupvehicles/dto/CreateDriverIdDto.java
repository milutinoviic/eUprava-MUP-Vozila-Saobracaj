package com.example.mupvehicles.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDriverIdDto {

    @NotNull(message = "File cannot be null")
    private MultipartFile picture;

    @NotNull(message = "Owner JMBG cannot be null")
    private String ownerJmbg;

}
