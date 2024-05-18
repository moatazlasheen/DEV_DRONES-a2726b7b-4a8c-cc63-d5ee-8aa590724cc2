package com.example.demo.controller;


import com.example.demo.dto.APIError;
import com.example.demo.dto.DroneBatteryLevelDto;
import com.example.demo.dto.DroneDto;
import com.example.demo.dto.DroneLoadingDto;
import com.example.demo.dto.DronesDto;
import com.example.demo.services.DroneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
public class DronesController {

    private final DroneService droneService;

    @Operation(summary = "Register a new drone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drone was successfully registered",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DroneDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIError.class)) })})
    @PostMapping("/drones")
    public DroneDto registerDrone(@Valid @RequestBody final DroneDto droneDto) {
        return droneService.registerDrone(droneDto);
    }

    @Operation(summary = "retrieve all drones available for loading")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "available drones were successfully loaded",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DronesDto.class)) })}
            )
    @GetMapping("/drones/available")
    public DronesDto getAvailableDrones() {
        return new DronesDto(droneService.getAvailableDrones());
    }


    @Operation(summary = "get battery level for a given drone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "battery level was successfully retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DroneBatteryLevelDto.class)) }),
            @ApiResponse(responseCode = "404", description = "unknown drone",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIError.class)) })}
    )
    @GetMapping("/drones/{id}/battery-level")
    public DroneBatteryLevelDto getDroneBatteryLevel(@PathVariable(name = "id") final long droneId) {
        return new DroneBatteryLevelDto(droneId, droneService.getDrone(droneId).getBatteryCapacityPercentage());
    }

    @Operation(summary = "get drone information including the loaded medications if any")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drone information was successfully retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DroneDto.class)) }),
            @ApiResponse(responseCode = "404", description = "unknown drone",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIError.class)) })}
    )
    @GetMapping("/drones/{id}")
    public DroneDto getDrone(@PathVariable(name = "id") final long droneId) {
        return droneService.getDrone(droneId);
    }

    @Operation(summary = "load drone with medications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drone was successfully loaded",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DroneBatteryLevelDto.class)) }),
            @ApiResponse(responseCode = "404", description = "unknown drone or medication",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIError.class)) })}
    )
    @PostMapping("/drones/{id}/load-medications")
    public DroneDto loadDroneWithMedication(@PathVariable(name = "id") final long droneId, @RequestBody final DroneLoadingDto droneLoadingDto) {
        return droneService.loadDroneWithMedications(droneId, droneLoadingDto.getMedications());
    }

}
