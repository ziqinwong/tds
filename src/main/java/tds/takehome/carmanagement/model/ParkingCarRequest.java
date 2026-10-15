package tds.takehome.carmanagement.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ParkingCarRequest(
        @NotBlank
        String vehicleReg,

        @Min(1)
        @Max(3)
        int vehicleType
) {}
