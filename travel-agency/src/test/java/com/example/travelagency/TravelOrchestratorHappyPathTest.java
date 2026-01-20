package com.example.travelagency;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.client.MockRestServiceServer;

import com.example.travelagency.dto.BookingDtos.TravelBookingRequest;
import com.example.travelagency.service.TravelOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockRestServiceServer
public class TravelOrchestratorHappyPathTest {

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
    void shouldCompleteFullBookingSuccessfully() throws Exception {
        TravelBookingRequest request = new TravelBookingRequest(
                "cust-123", "MAD", "NYC",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(17),
                2, new BigDecimal("1500.00"));

        // TODO: Paso 1 - Configurar Mock para Flight Service
        // Tip: Usa
        // mockServer.expect(requestTo("http://flight-service/api/flights/book"))...

        // TODO: Paso 2 - Configurar Mock para Hotel Service
        // Tip: Usa
        // mockServer.expect(requestTo("http://hotel-service/api/hotels/book"))...

        // TODO: Paso 3 - Configurar Mock para Billing Service
        // Tip: Usa
        // mockServer.expect(requestTo("http://billing-service/api/billing/charge"))...

        // TODO: Paso 4 - Ejecutar la reserva y verificar que los booleanos de
        // confirmación en el response sean true
        // orchestration.bookTrip(request)...

        // fail("Ejercicio no completado - Sigue las instrucciones en
        // docs/ejercicio-1-happy-path.md");
    }
}
