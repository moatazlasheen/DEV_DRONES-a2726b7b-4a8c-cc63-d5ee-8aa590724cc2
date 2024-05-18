package com.example.demo.services;

import com.example.demo.converter.Base64ToByteArrayConverter;
import com.example.demo.dto.MedicationDto;
import com.example.demo.entities.MedicationEntity;
import com.example.demo.ropositories.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    @Override
    public MedicationDto registerMedication(final MedicationDto medicationDto) {
        final ModelMapper modelMapper = new ModelMapper();
        final Base64ToByteArrayConverter base64ToByteArrayConverter = new Base64ToByteArrayConverter();
        modelMapper.addConverter(base64ToByteArrayConverter);
        modelMapper.typeMap(MedicationDto.class, MedicationEntity.class)
                .addMappings(mapper -> mapper.using(base64ToByteArrayConverter).map(MedicationDto::getBase64Image, MedicationEntity::setImage));
        final MedicationEntity medicationEntity = modelMapper.map(medicationDto, MedicationEntity.class);
        medicationRepository.save(medicationEntity);
        medicationDto.setId(medicationEntity.getId());
        return medicationDto;
    }
}
