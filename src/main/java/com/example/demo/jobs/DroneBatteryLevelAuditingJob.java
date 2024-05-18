package com.example.demo.jobs;

import com.example.demo.entities.DroneBatteryLevelAuditEntity;
import com.example.demo.entities.DroneEntity;
import com.example.demo.ropositories.DroneBatteryLevelAuditRepository;
import com.example.demo.ropositories.DroneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class DroneBatteryLevelAuditingJob {

    private final DroneRepository droneRepository;
    private final DroneBatteryLevelAuditRepository droneBatteryLevelAuditRepository;

    @Scheduled(fixedDelayString = "${battery.level.audit.period.in.ms}")
    public void logBatteryLevels(){
        final Iterable<DroneEntity> drones = droneRepository.findAll();
        final List<DroneBatteryLevelAuditEntity> auditEntities =
                StreamSupport.stream(drones.spliterator(), false)
                        .map( droneEntity -> new DroneBatteryLevelAuditEntity(null, droneEntity.getId(), droneEntity.getBatteryCapacityPercentage(), LocalDateTime.now()))
                        .toList();
        droneBatteryLevelAuditRepository.saveAll(auditEntities);
    }
}
