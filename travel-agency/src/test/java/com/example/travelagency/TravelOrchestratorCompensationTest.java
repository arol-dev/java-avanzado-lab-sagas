package com.example.travelagency;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.example.travelagency.dto.BookingDtos.ChargeResponse;
import com.example.travelagency.dto.BookingDtos.FlightBookingResponse;
import com.example.travelagency.dto.BookingDtos.HotelBookingResponse;
import com.example.travelagency.dto.BookingDtos.TravelBookingRequest;
import com.example.travelagency.dto.BookingDtos.TravelBookingResponse;
import com.example.travelagency.service.TravelOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockRestServiceServer
public class TravelOrchestratorCompensationTest {

    @Autowired
    private TravelOrchestrator orchestrator;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @Test
    void shouldCompensateWhenHotelFails() throws Exception {
        TravelBookingRequest request = new TravelBookingRequest(
                "cust-123", "MAD", "NYC",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(17),
                2, new BigDecimal("1500.00"));

        // Mock Flight - Success
        mockServer.expect(requestTo("http://flight-service/api/flights/book"))
                .andRespond(
                        withSuccess(objectMapper.writeValueAsString(new FlightBookingResponse("f-1", true, "Vuelo OK")),
                                MediaType.APPLICATION_JSON));

        // Mock Hotel - Failure
        mockServer.expect(requestTo("http://hotel-service/api/hotels/book"))
                .andRespond(withSuccess(
                        objectMapper.writeValueAsString(new HotelBookingResponse(null, false, "Sin habitaciones")),
                        MediaType.APPLICATION_JSON));

        // TODO: Paso 1 - Configurar Mock para la compensación del vuelo (Cancelación)
        // mockServer.expect(requestTo("http://flight-service/api/flights/cancel"))...

        TravelBookingResponse response = orchestrator.bookTrip(request);

        assertTrue(response.flightConfirmed(), "El vuelo debería haberse intentado confirmar");
        assertFalse(response.hotelConfirmed(), "El hotel debería haber fallado");
        mockServer.verify();
    }

    @Test
    void shouldCompensateWhenBillingFails() throws Exception {
        TravelBookingRequest request = new TravelBookingRequest(
                "cust-123", "MAD", "NYC",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(17),
                2, new BigDecimal("1500.00"));

        // Mock Flight - Success
        mockServer.expect(requestTo("http://flight-service/api/flights/book"))
                .andRespond(
                        withSuccess(objectMapper.writeValueAsString(new FlightBookingResponse("f-1", true, "Vuelo OK")),
                                MediaType.APPLICATION_JSON));

        // Mock Hotel - Success
        mockServer.expect(requestTo("http://hotel-service/api/hotels/book"))
                .andRespond(
                        withSuccess(objectMapper.writeValueAsString(new HotelBookingResponse("h-1", true, "Hotel OK")),
                                MediaType.APPLICATION_JSON));

        // Mock Billing - Failure
        mockServer.expect(requestTo("http://billing-service/api/billing/charge"))
                .andRespond(withSuccess(
                        objectMapper.writeValueAsString(new ChargeResponse(null, false, "Fondos insuficientes")),
                        MediaType.APPLICATION_JSON));

        // TODO: Paso 2 - Configurar Mock para la compensación del hotel
        // TODO: Paso 3 - Configurar Mock para la compensación del vuelo

        TravelBookingResponse response = orchestrator.bookTrip(request);

        assertFalse(response.chargeConfirmed(), "El cobro debería haber fallado");
        mockServer.verify();
    }
}
