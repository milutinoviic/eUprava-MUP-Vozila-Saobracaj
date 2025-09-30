package com.example.mupvehicles.repository;

import com.example.mupvehicles.model.Owner;
import com.example.mupvehicles.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    @Query("SELECT v.owner FROM Vehicle v WHERE v.registration = :registration")
    Owner findOwnerByRegistration(String registration);

    List<Vehicle> findByOwnerJmbg(String jmbg);

    @Query("""
    SELECT v FROM Vehicle v
    WHERE (:mark IS NULL OR v.mark = :mark)
      AND (:model IS NULL OR v.model = :model)
      AND (:color IS NULL OR v.color = :color)
      AND (:registration IS NULL OR v.registration ILIKE %:registration%)
    """)
    List<Vehicle> searchVehicles(
            @Param("mark") String mark,
            @Param("model") String model,
            @Param("color") String color,
            @Param("registration") String registration
    );

    Vehicle findVehicleByRegistration(String registration);

    boolean existsByRegistration(String registration);
}
