
package tds.takehome.carmanagement.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import tds.takehome.carmanagement.model.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CarManagementServiceTest {

    private final CarManagementService service = new CarManagementService();

    @Test
    void testGetParkingSpaces_Success() {
        ParkingSpaces spaces = service.getParkingSpaces();

        assertEquals(100, spaces.availableSpaces());
        assertEquals(0, spaces.occupiedSpaces());

        service.processParkingCar(new ParkingCarRequest("ABC123", 1));

        ParkingSpaces updatedSpaces = service.getParkingSpaces();

        assertEquals(99, updatedSpaces.availableSpaces());
        assertEquals(1, updatedSpaces.occupiedSpaces());
    }

    @Test
    void testProcessCarCharges_Success() {
        ParkingCarRequest request = new ParkingCarRequest("PROCESSSUCCESS", 1);

        ParkingCarResponse parkedCar = service.processParkingCar(request);

        ParkingBillResponse bill = service.processCarCharges("PROCESSSUCCESS");

        assertEquals("PROCESSSUCCESS", bill.vehicleReg());
        assertEquals(parkedCar.timeIn(), bill.timeIn());
        assertNotNull(bill.timeOut());

        assertEquals(0.0, bill.vehicleCharge());

        ParkingSpaces spaces = service.getParkingSpaces();

        assertEquals(100, spaces.availableSpaces());
        assertEquals(0, spaces.occupiedSpaces());
    }

    @Test
    void testProcessCarCharges_NonexistingVehicleReg_throwsResponseStatusException() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.processCarCharges("NOTFOUND")
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void testProcessParkingCar_Success() {
        ParkingCarRequest request = new ParkingCarRequest("TESTSUCCESS", 2);

        ParkingCarResponse response = service.processParkingCar(request);

        assertEquals("TESTSUCCESS", response.vehicleReg());
    }

    @Test
    void testProcessParkingCar_NoAvailableSpace_throwsResponseStatusException() {
        for (int i = 0; i < 100; i++) {
            service.processParkingCar(new ParkingCarRequest("CAR" + i, 1));
        }

        ParkingSpaces spaces = service.getParkingSpaces();

        assertEquals(0, spaces.availableSpaces());
        assertEquals(100, spaces.occupiedSpaces());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.processParkingCar(
                        new ParkingCarRequest("EXTRA", 1)
                )
        );

        assertEquals(409, exception.getStatusCode().value());

        assertEquals(100, service.getParkingSpaces().occupiedSpaces());
    }

    @Test
    void testProcessParkingCar_ExistingVehicleParked_throwsResponseStatusException() {
        ParkingCarRequest request = new ParkingCarRequest("DUPLICATECAR", 1);

        service.processParkingCar(request);

        ParkingSpaces existingSpaces = service.getParkingSpaces();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.processParkingCar(request));

        ParkingSpaces updatedSpaces = service.getParkingSpaces();

        assertEquals(409, exception.getStatusCode().value());
        assertEquals(existingSpaces, updatedSpaces);
    }

    @Test
    void testCalculateBill_CalculatesCorrectly() {
        ParkedCar existingCar = new ParkedCar("VEHICLEREG", 1, Instant.parse("2026-10-15T10:00:00Z"));
        double bill = service.calculateBill(existingCar, Instant.parse("2026-10-15T10:06:00Z"));

        //6 minutes charge = (0.1*6 + 1)
        assertEquals(1.6, bill);

    }
}
