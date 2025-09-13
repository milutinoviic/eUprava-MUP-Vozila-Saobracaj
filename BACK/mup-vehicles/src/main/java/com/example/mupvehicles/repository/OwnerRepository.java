package com.example.mupvehicles.repository;

import com.example.mupvehicles.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner,String> {

    boolean existsByEmail(String email);

    boolean existsByJmbg(String email);

    Owner findByJmbg(String jmbg);

}
