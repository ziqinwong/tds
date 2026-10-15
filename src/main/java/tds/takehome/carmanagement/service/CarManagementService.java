package tds.takehome.carmanagement.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tds.takehome.carmanagement.model.CarSizes;
import tds.takehome.carmanagement.model.ParkingCarResponse;
import tds.takehome.carmanagement.model.ParkedCar;
import tds.takehome.carmanagement.model.ParkingCarRequest;
import tds.takehome.carmanagement.model.ParkingBillResponse;
import tds.takehome.carmanagement.model.ParkingSpaces;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class CarManagementService {

    private static final int TOTAL_SPACES = 100;
    private final Map<String, ParkedCar> parkedCars = new HashMap<>();

    public ParkingSpaces getParkingSpaces() {
        int occupiedSpaces = parkedCars.size();
        int availableSpaces = TOTAL_SPACES - occupiedSpaces;

        return new ParkingSpaces(availableSpaces, occupiedSpaces);
    }

    public ParkingBillResponse processCarCharges(String vehicleReg) {
        Instant timeOut = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        ParkedCar car = parkedCars.get(vehicleReg);

        if (car == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Car does not exist in this car park.");
        }

        double charge = calculateBill(car, timeOut);

        parkedCars.remove(vehicleReg);
        return new ParkingBillResponse(vehicleReg, charge, car.timeIn(), timeOut);
    }

    public ParkingCarResponse processParkingCar(ParkingCarRequest newRequest) {
        if (parkedCars.size() == TOTAL_SPACES) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Car park is full.");
        }

        if (parkedCars.containsKey(newRequest.vehicleReg())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Car is already parked.");
        }

        ParkedCar newCar = new ParkedCar(newRequest.vehicleReg(), newRequest.vehicleType(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS));
        parkedCars.put(newRequest.vehicleReg(), newCar);

        return new ParkingCarResponse(newCar.vehicleReg(), newCar.timeIn());
    }

    double calculateBill(ParkedCar parkedCar, Instant timeOut) {
        int numberOfMinutes = (int) Duration.between(parkedCar.timeIn(), timeOut).toMinutes();

        if (numberOfMinutes < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative");
        }

        CarSizes carSize = CarSizes.fromId(parkedCar.vehicleType());

        int baseCharge = carSize.getPricePerMinute() * numberOfMinutes;
        int additionalCharge = (numberOfMinutes / 5) * 100;

        return (baseCharge + additionalCharge) / 100.0;
    }

}
