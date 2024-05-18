package com.example.demo.services;

import com.example.demo.converter.ByteArrayToBase64Converter;
import com.example.demo.dto.DroneDto;
import com.example.demo.dto.MedicationDto;
import com.example.demo.entities.DroneEntity;
import com.example.demo.entities.MedicationEntity;
import com.example.demo.enums.DroneState;
import com.example.demo.exceptions.TooHeavyLoadException;
import com.example.demo.exceptions.UnknownDroneException;
import com.example.demo.exceptions.UnknownMedicationException;
import com.example.demo.exceptions.UnloadableDroneException;
import com.example.demo.ropositories.DroneRepository;
import com.example.demo.ropositories.MedicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static com.example.demo.enums.DroneState.IDLE;
import static com.example.demo.enums.DroneState.LOADED;
import static com.example.demo.enums.DroneState.LOADING;

@RequiredArgsConstructor
@Service
public class DroneServiceImpl implements DroneService{

    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;
    @Override
    public DroneDto registerDrone(final DroneDto droneDto) {
        final ModelMapper modelMapper = new ModelMapper();
        final DroneEntity droneEntity = modelMapper.map(droneDto, DroneEntity.class);
        droneRepository.save(droneEntity);
        droneDto.setId(droneEntity.getId());
        return droneDto;
    }

    @Override
    public List<DroneDto> getAvailableDrones() {
        final List<DroneEntity> droneEntities = droneRepository.findAllByDroneStateAndBatteryCapacityPercentageGreaterThanEqual(IDLE,
                MIN_BATTARY_PERCENT_FOR_LOADING);
        final ModelMapper modelMapper = new ModelMapper();
        return droneEntities.stream().map(entity -> modelMapper.map(entity, DroneDto.class)).toList();
    }

    @Override
    public DroneDto getDrone(final long droneId) {
        final DroneEntity droneEntity = droneRepository.findById(droneId).orElseThrow(UnknownDroneException::new);
        final ModelMapper modelMapper = getDroneEntityToDroneDtoModelMapper();
        return modelMapper.map(droneEntity, DroneDto.class);
    }

    @Transactional
    @Override
    public DroneDto loadDroneWithMedications(final long droneId, final Set<Long> medicationIds) {
        DroneEntity droneEntity = droneRepository.findById(droneId).orElseThrow(UnknownDroneException::new);
        if (droneEntity.getBatteryCapacityPercentage() < MIN_BATTARY_PERCENT_FOR_LOADING || droneEntity.getDroneState() != IDLE) {
            throw new UnloadableDroneException();
        }
        final Iterable<MedicationEntity> medicationEntityIterable = medicationRepository.findAllById(medicationIds);
        final List<MedicationEntity> medicationsList =
                StreamSupport.stream(medicationEntityIterable.spliterator(), false)
                        .toList();
        if (medicationsList.size() != medicationIds.size()) {
            throw new UnknownMedicationException();
        }
        double totalWeight = medicationsList.stream().map( MedicationEntity::getWeight).reduce(0.0f, Float::sum);
        if (totalWeight > droneEntity.getWeightLimitGrm()){
            throw new TooHeavyLoadException();
        }
        droneEntity = updateDroneState(droneEntity, LOADING);
        droneEntity.getMedications().addAll(medicationsList);
        droneEntity = droneRepository.save(droneEntity);
        droneEntity = updateDroneState(droneEntity, LOADED);
        final ModelMapper modelMapper = getDroneEntityToDroneDtoModelMapper();

        return modelMapper.map(droneEntity, DroneDto.class);
    }

    private DroneEntity updateDroneState(final DroneEntity droneEntity, final DroneState state) {
        droneEntity.setDroneState(state);
        return droneRepository.save(droneEntity);
    }

    private ModelMapper getDroneEntityToDroneDtoModelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        final ByteArrayToBase64Converter byteArrayToBase64Converter = new ByteArrayToBase64Converter();
        modelMapper.addConverter(byteArrayToBase64Converter);
        modelMapper.typeMap(MedicationEntity.class, MedicationDto.class)
                .addMappings(mapper -> mapper.using(byteArrayToBase64Converter).map(MedicationEntity::getImage, MedicationDto::setBase64Image));
        return modelMapper;
    }
}
