package com.example.mupvehicles.repository;

import com.example.mupvehicles.model.OwnershipTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnershipTransferRepository extends JpaRepository<OwnershipTransfer, String> {
}
