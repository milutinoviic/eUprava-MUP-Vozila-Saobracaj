package com.example.mupvehicles.repository;

import com.example.mupvehicles.model.DriverId;
import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.model.OwnershipTransfer;
import com.example.mupvehicles.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnershipTransferRepository extends JpaRepository<OwnershipTransfer, String> {

    Optional<OwnershipTransfer> findByOldOwner(Owner oldOwner);

    Optional<OwnershipTransfer> findByNewOwner(Owner newOwner);

    List<OwnershipTransfer> findByVehicle(Vehicle vehicle);

}
