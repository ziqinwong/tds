package tds.takehome.carmanagement.model;

import jakarta.validation.constraints.NotEmpty;

public record ParkingBillRequest(
        @NotEmpty
        String vehicleReg
) {}
