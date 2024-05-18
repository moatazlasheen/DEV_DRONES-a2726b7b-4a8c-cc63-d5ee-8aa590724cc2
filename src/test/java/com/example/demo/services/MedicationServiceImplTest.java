package com.example.demo.services;

import com.example.demo.dto.MedicationDto;
import com.example.demo.entities.MedicationEntity;
import com.example.demo.ropositories.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MedicationServiceImplTest {

    @Mock
    private MedicationRepository medicationRepository;
    @InjectMocks
    private MedicationServiceImpl underTest;

    @Test
    void test1() throws IOException {
        final MedicationDto medicationDto = new MedicationDto();
        medicationDto.setWeight(50.0f);
        medicationDto.setCode("CODE_1");
        final Path imagePath = Paths.get("src","test","resources","image.jpg");
        final byte[] imageBytes = Files.readAllBytes(imagePath);
        medicationDto.setBase64Image(Base64.getEncoder().encodeToString(imageBytes));
        underTest.registerMedication(medicationDto);
        verify(medicationRepository).save(any(MedicationEntity.class));

    }
}
