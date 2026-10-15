package tds.takehome.carmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tds.takehome.carmanagement.model.*;
import tds.takehome.carmanagement.service.CarManagementService;

@RestController
public class CarManagementController {

    private final CarManagementService managementService;

    public CarManagementController(CarManagementService managementService) {
        this.managementService = managementService;
    }

    @GetMapping("/parking")
    public ParkingSpaces getParkingSpaces() {
        return managementService.getParkingSpaces();
    }

    @PostMapping("/parking")
    public ParkingCarResponse parkCar(@Valid @RequestBody ParkingCarRequest request) {
        return managementService.processParkingCar(request);
    }

    @PostMapping("/parking/bill")
    public ParkingBillResponse calculateCarBill(@Valid @RequestBody ParkingBillRequest request) {
        return managementService.processCarCharges(request.vehicleReg());
    }
}
