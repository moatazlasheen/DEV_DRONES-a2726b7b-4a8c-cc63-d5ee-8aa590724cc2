package com.example.demo.ropositories;

import com.example.demo.entities.MedicationEntity;
import org.springframework.data.repository.CrudRepository;


public interface MedicationRepository extends CrudRepository<MedicationEntity, Long> {
}
