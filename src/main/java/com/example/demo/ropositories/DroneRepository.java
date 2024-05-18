package com.example.demo.ropositories;

import com.example.demo.entities.DroneEntity;
import com.example.demo.enums.DroneState;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DroneRepository extends CrudRepository<DroneEntity, Long> {
    List<DroneEntity> findAllByDroneStateAndBatteryCapacityPercentageGreaterThanEqual(DroneState idle, byte batteryPercentage);
}
