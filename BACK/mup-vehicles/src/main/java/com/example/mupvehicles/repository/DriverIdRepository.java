package com.example.mupvehicles.repository;

import com.example.mupvehicles.model.DriverID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverIdRepository extends JpaRepository<DriverID,String> {
}
