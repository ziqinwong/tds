package tds.takehome.carmanagement.model;

public record ParkingSpaces(
        int availableSpaces,
        int occupiedSpaces
) {}