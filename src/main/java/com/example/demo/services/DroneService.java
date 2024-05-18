package com.example.demo.services;

import com.example.demo.dto.DroneDto;

import java.util.List;
import java.util.Set;

public interface DroneService {

    byte MIN_BATTARY_PERCENT_FOR_LOADING = 25;

    DroneDto registerDrone(DroneDto droneDto);

    List<DroneDto> getAvailableDrones();

    DroneDto getDrone(long droneId);

    DroneDto loadDroneWithMedications(long droneId, Set<Long> medicationIds);
}
