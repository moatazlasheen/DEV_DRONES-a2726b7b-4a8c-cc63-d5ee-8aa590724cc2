package com.example.demo.services;

import com.example.demo.dto.DroneDto;
import com.example.demo.entities.DroneEntity;
import com.example.demo.entities.MedicationEntity;
import com.example.demo.exceptions.TooHeavyLoadException;
import com.example.demo.exceptions.UnknownDroneException;
import com.example.demo.exceptions.UnknownMedicationException;
import com.example.demo.exceptions.UnloadableDroneException;
import com.example.demo.ropositories.DroneRepository;
import com.example.demo.ropositories.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.demo.enums.DroneState.IDLE;
import static com.example.demo.enums.DroneState.LOADED;
import static com.example.demo.services.DroneService.MIN_BATTARY_PERCENT_FOR_LOADING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroneServiceImplTest {

    @Mock
    private DroneRepository droneRepository;
    @Mock
    private MedicationRepository medicationRepository;
    @Mock
    private DroneDto droneDto1;
    @Mock
    private DroneEntity droneEntity1;
    @Mock
    private DroneEntity droneEntity2;

    @Mock
    private MedicationEntity medicationEntity1;
    @Mock
    private MedicationEntity medicationEntity2;
    @InjectMocks
    private DroneServiceImpl underTest;


    @Test
    void testRegisterDrone() {
        underTest.registerDrone(droneDto1);
        verify(droneRepository).save(any(DroneEntity.class));
    }

    @Test
    void testGetAvailableDrones(){
        when(droneRepository.findAllByDroneStateAndBatteryCapacityPercentageGreaterThanEqual(IDLE,
                MIN_BATTARY_PERCENT_FOR_LOADING)).thenReturn(List.of(droneEntity1, droneEntity2));
        final List<DroneDto> actualDronesList = underTest.getAvailableDrones();
        verify(droneRepository).findAllByDroneStateAndBatteryCapacityPercentageGreaterThanEqual(IDLE, MIN_BATTARY_PERCENT_FOR_LOADING);
        assertEquals(2, actualDronesList.size());
    }

    @Test
    void testGetDrone() {
        final Long droneId = 1L;
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(droneEntity1));
        when(droneEntity1.getId()).thenReturn(droneId);
        final DroneDto droneDto = underTest.getDrone(droneId);
        verify(droneRepository).findById(droneId);
        assertEquals(droneId, droneDto.getId());
    }

    @Test
    void testLoadDroneWithMedications() {
        final long droneId = 1L;
        final long medicationId1 = 2L;
        final long medicationId2 = 3L;
        final Set<Long> medicationIdsToLoad = Set.of(medicationId1, medicationId2);
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(droneEntity1));
        when(medicationRepository.findAllById(medicationIdsToLoad)).thenReturn(Set.of(medicationEntity1, medicationEntity2));
        // total weight is less than drone max limit
        when(droneEntity1.getWeightLimitGrm()).thenReturn((short)300);
        when(medicationEntity1.getWeight()).thenReturn(100.0f);
        when(medicationEntity2.getWeight()).thenReturn(50.0f);
        // loadable drone
        when(droneEntity1.getDroneState()).thenReturn(IDLE);
        when(droneEntity1.getBatteryCapacityPercentage()).thenReturn(MIN_BATTARY_PERCENT_FOR_LOADING);
        when(droneRepository.save(any(DroneEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        underTest.loadDroneWithMedications(droneId, medicationIdsToLoad);
        verify(droneRepository).findById(droneId);
        verify(medicationRepository).findAllById(medicationIdsToLoad);
        verify(droneRepository,times(3)).save(any(DroneEntity.class));
    }

    @Test
    void testLoadDroneWithMedications_unknownDrone() {
        final long droneId = 1L;

        when(droneRepository.findById(droneId)).thenReturn(Optional.empty());
        assertThrows(UnknownDroneException.class, () -> underTest.loadDroneWithMedications(droneId, null));
        verify(droneRepository).findById(droneId);
        verify(medicationRepository, times(0)).findAllById(anyIterable());
        verify(droneRepository,times(0)).save(any(DroneEntity.class));
    }

    @Test
    void testLoadDroneWithMedications_unknownDroneMedication() {
        final long droneId = 1L;
        final long medicationId1 = 2L;
        final long medicationId2 = 3L;
        final Set<Long> medicationIdsToLoad = Set.of(medicationId1, medicationId2);
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(droneEntity1));
        when(droneEntity1.getDroneState()).thenReturn(IDLE);
        when(droneEntity1.getBatteryCapacityPercentage()).thenReturn(MIN_BATTARY_PERCENT_FOR_LOADING);
        // two id to look for, but only one medication was found
        when(medicationRepository.findAllById(medicationIdsToLoad)).thenReturn(Set.of(medicationEntity1));
        assertThrows(UnknownMedicationException.class, () -> underTest.loadDroneWithMedications(droneId, medicationIdsToLoad));
        verify(droneRepository).findById(droneId);
        verify(medicationRepository).findAllById(anyIterable());
        verify(droneRepository,times(0)).save(any(DroneEntity.class));
    }

    @Test
    void testLoadDroneWithMedications_notIdle() {
        final long droneId = 1L;
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(droneEntity1));
        when(droneEntity1.getDroneState()).thenReturn(LOADED);
        when(droneEntity1.getBatteryCapacityPercentage()).thenReturn(MIN_BATTARY_PERCENT_FOR_LOADING);
        assertThrows(UnloadableDroneException.class, () -> underTest.loadDroneWithMedications(droneId, null));
        verify(droneRepository).findById(droneId);
        verify(medicationRepository, times(0)).findAllById(anyIterable());
        verify(droneRepository,times(0)).save(any(DroneEntity.class));
    }

    @Test
    void testLoadDroneWithMedications_lowBattery() {
        final long droneId = 1L;
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(droneEntity1));
        when(droneEntity1.getBatteryCapacityPercentage()).thenReturn((byte)(MIN_BATTARY_PERCENT_FOR_LOADING-(byte)1));
        assertThrows(UnloadableDroneException.class, () -> underTest.loadDroneWithMedications(droneId, null));
        verify(droneRepository).findById(droneId);
        verify(medicationRepository, times(0)).findAllById(anyIterable());
        verify(droneRepository,times(0)).save(any(DroneEntity.class));
    }

    @Test
    void testLoadDroneWithMedications_tooHeavyLoad() {
        final long droneId = 1L;
        final long medicationId1 = 2L;
        final long medicationId2 = 3L;
        final Set<Long> medicationIdsToLoad = Set.of(medicationId1, medicationId2);
        when(droneRepository.findById(droneId)).thenReturn(Optional.of(droneEntity1));
        when(medicationRepository.findAllById(medicationIdsToLoad)).thenReturn(Set.of(medicationEntity1, medicationEntity2));
        // total weight is more than drone max limit
        when(droneEntity1.getWeightLimitGrm()).thenReturn((short)150);
        when(medicationEntity1.getWeight()).thenReturn(100.0f);
        when(medicationEntity2.getWeight()).thenReturn(51.0f);
        // loadable drone
        when(droneEntity1.getDroneState()).thenReturn(IDLE);
        when(droneEntity1.getBatteryCapacityPercentage()).thenReturn(MIN_BATTARY_PERCENT_FOR_LOADING);
        assertThrows(TooHeavyLoadException.class, () -> underTest.loadDroneWithMedications(droneId, medicationIdsToLoad));
        verify(droneRepository).findById(droneId);
        verify(medicationRepository).findAllById(anyIterable());
        verify(droneRepository,times(0)).save(any(DroneEntity.class));
    }
}
