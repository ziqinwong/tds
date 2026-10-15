package tds.takehome.carmanagement.model;

import java.time.Instant;

public record ParkingBillResponse(
        String vehicleReg,
        double vehicleCharge,
        Instant timeIn,
        Instant timeOut
) {}