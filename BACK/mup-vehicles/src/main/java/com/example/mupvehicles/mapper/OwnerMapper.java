package com.example.mupvehicles.mapper;

import com.example.mupvehicles.dto.OwnerDto;
import com.example.mupvehicles.model.Owner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<OwnerDto> convertOwnersToOwnerDtos(List<Owner> owners) {
        return owners.stream()
                .map(this::convertOwnerToOwnerDto)
                .collect(Collectors.toList());
    }
}
