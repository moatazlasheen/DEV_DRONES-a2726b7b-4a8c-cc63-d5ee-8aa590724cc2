package com.example.demo.dto;

import com.example.demo.enums.DroneModel;
import com.example.demo.enums.DroneState;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DroneDto {
    private Long id;
    @Size(min = 1, max = 100 , message = "{validation.invalid.drone.serial.length}")
    @NotNull
    @NotEmpty
    private String serial;
    @NotNull
    private DroneModel model;
    @NotNull
    @Min(value = 0, message = "{validation.drone.weightMinLimit}")
    @Max(value = 500, message = "{validation.drone.weightMaxLimit}")
    private Short weightLimitGrm;
    @NotNull
    @Min(value = 0, message = "{validation.drone.minBatteryCapacity}")
    @Max(value = 100, message = "{validation.drone.maxBatteryCapacity}")
    private Byte batteryCapacityPercentage;

    @NotNull
    private DroneState droneState;

    private Set<MedicationDto> medications;
}
