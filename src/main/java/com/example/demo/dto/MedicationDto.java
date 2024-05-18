package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {
    private Long id;
    @Size(min = 1, max = 255 , message = "{validation.invalid.medication.name.length}")
    @Pattern(regexp = "([A-Za-z0-9\\-\\_]+)", message = "{validation.invalid.medication.name.regex}")
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @Min(value = 0, message = "{validation.medication.weightMinLimit}")
    @Max(value = 500, message = "{validation.medication.weightMaxLimit}")
    private Float weight;
    @Size(min = 1, max = 255 , message = "{validation.invalid.medication.code.length}")
    @Pattern(regexp = "([A-Z0-9\\_]+)", message = "{validation.invalid.medication.code.regex}")
    @NotNull
    @NotEmpty
    private String code;
    private String base64Image;
}
