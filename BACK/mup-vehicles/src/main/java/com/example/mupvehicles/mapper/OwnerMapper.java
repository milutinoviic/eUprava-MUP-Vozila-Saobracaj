package com.example.mupvehicles.mapper;

import com.example.mupvehicles.dto.OwnerDto;
import com.example.mupvehicles.model.Owner;
import org.springframework.stereotype.Component;

@Component
public class OwnerMapper {

    public OwnerDto convertOwnerToOwnerDto(Owner owner) {

        OwnerDto ownerDto = new OwnerDto();

        ownerDto.setId(owner.getId());
        ownerDto.setFirstName(owner.getFirstName());
        ownerDto.setLastName(owner.getLastName());
        ownerDto.setAddress(owner.getAddress());
        ownerDto.setJmbg(owner.getJmbg());
        ownerDto.setEmail(owner.getEmail());

        return ownerDto;

    }
}
