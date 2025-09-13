package com.example.mupvehicles.repository;

import com.example.mupvehicles.model.DriverId;
import com.example.mupvehicles.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverIdRepository extends JpaRepository<DriverId,String> {

    boolean existsByOwner(Owner owner);

    DriverId findByOwner_Jmbg(String jmbg);

    Optional<DriverId> findByOwner(Owner owner);

}
