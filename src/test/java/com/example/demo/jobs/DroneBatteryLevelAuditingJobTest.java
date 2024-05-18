package com.example.demo.jobs;

import com.example.demo.entities.DroneEntity;
import com.example.demo.ropositories.DroneBatteryLevelAuditRepository;
import com.example.demo.ropositories.DroneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DroneBatteryLevelAuditingJobTest {
    @Mock
    private DroneRepository droneRepository;
    @Mock
    private DroneBatteryLevelAuditRepository droneBatteryLevelAuditRepository;

    @InjectMocks
    private DroneBatteryLevelAuditingJob underTest;

    @Mock
    private DroneEntity drone1;
    @Mock
    private DroneEntity drone2;


    @Test
    void testLogBatteryLevels() {
        final List<DroneEntity> drones = List.of(drone1, drone2);
        when(droneRepository.findAll()).thenReturn(drones);
        underTest.logBatteryLevels();
        verify(droneRepository).findAll();
        verify(droneBatteryLevelAuditRepository).saveAll(ArgumentMatchers.anyIterable());

    }
}
