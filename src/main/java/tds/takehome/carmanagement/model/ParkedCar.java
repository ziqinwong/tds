package tds.takehome.carmanagement.model;

import java.time.Instant;

public record ParkedCar(
        String vehicleReg,
        int vehicleType,
        Instant timeIn
) {}