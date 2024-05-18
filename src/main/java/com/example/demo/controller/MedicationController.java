package com.example.demo.controller;


import com.example.demo.dto.APIError;
import com.example.demo.dto.MedicationDto;
import com.example.demo.services.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
public class MedicationController {

    private final MedicationService medicationService;

    @Operation(summary = "Register a medication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medication was successfully registered",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MedicationDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIError.class)) })})
    @PostMapping("/medication")
    public MedicationDto registerMedication(@Valid @RequestBody final MedicationDto medicationDto) {
        return medicationService.registerMedication(medicationDto);
    }

}
