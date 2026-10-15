package tds.takehome.carmanagement.model;

import java.time.Instant;

public record ParkingCarResponse(String vehicleReg,
                                 Instant timeIn) {}
