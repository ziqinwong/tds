package tds.takehome.carmanagement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import tds.takehome.carmanagement.model.*;
import tds.takehome.carmanagement.service.CarManagementService;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarManagementController.class)
class CarManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarManagementService service;

    private static final Instant timeIn = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    private static final String timeInResponse = String.valueOf(timeIn);

    @Test
    void testGetParkingSpaces_ReturnsOccupiedAndAvailableSpaces() throws Exception {

        when(service.getParkingSpaces()).thenReturn(new ParkingSpaces(100, 0));

        mockMvc.perform(get("/parking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSpaces").value(100))
                .andExpect(jsonPath("$.occupiedSpaces").value(0));
    }

    @Test
    void testParkCar_HappyPath_Returns200WithParkingCarResponse() throws Exception {
        when(service.processParkingCar(any(ParkingCarRequest.class)))
                .thenReturn(new ParkingCarResponse("ABC123", timeIn));

        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "vehicleReg": "ABC123",
                        "vehicleType": 1
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleReg").value("ABC123"))
                .andExpect(jsonPath("$.timeIn").value(String.valueOf(timeInResponse)));
    }

    @Test
    void testParkCar_InvalidRequestBody_Returns400() throws Exception {

        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "vehicleReg": "",
                        "vehicleType": 5
                    }
                    """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void testParkCar_DuplicateVehicleReg_Returns409() throws Exception {

        when(service.processParkingCar(any(ParkingCarRequest.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Car is already parked"
                ));

        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "vehicleReg": "ABC123",
                        "vehicleType": 1
                    }
                    """))
                .andExpect(status().isConflict());
    }

    @Test
    void testCalculateCarBill_HappyPath_Returns200WithParkingBillResponse() throws Exception {
        Instant timeOut = timeIn.plus(Duration.ofMinutes(3));

        when(service.processCarCharges("ABC123"))
                .thenReturn(new ParkingBillResponse(
                        "ABC123",
                        3.0,
                        timeIn,
                        timeOut
                ));

        mockMvc.perform(post("/parking/bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "vehicleReg": "ABC123"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleReg").value("ABC123"))
                .andExpect(jsonPath("$.vehicleCharge").value(3.0))
                .andExpect(jsonPath("$.timeIn").value(timeInResponse))
                .andExpect(jsonPath("$.timeOut").value(String.valueOf(timeOut)));
    }

    @Test
    void testCalculateCarBill_InvalidRequestBody_Returns400() throws Exception {

        mockMvc.perform(post("/parking/bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "vehicleReg": ""
                    }
                    """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void testCalculateCarBill_NonexistingVehicleReg_Returns404() throws Exception {

        when(service.processCarCharges("NOTFOUND"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Car does not exist"));

        mockMvc.perform(post("/parking/bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "vehicleReg": "NOTFOUND"
                    }
                    """))
                .andExpect(status().isNotFound());
    }
}
