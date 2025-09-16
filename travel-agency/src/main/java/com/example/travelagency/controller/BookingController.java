package com.example.travelagency.controller;

import com.example.travelagency.dto.BookingDtos.TravelBookingRequest;
import com.example.travelagency.dto.BookingDtos.TravelBookingResponse;
import com.example.travelagency.service.TravelOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel")
public class BookingController {

    private final TravelOrchestrator orchestrator;

    public BookingController(TravelOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    /**
     * Endpoint de entrada al flujo SAGA.
     * Envía la solicitud a los microservicios (vuelos, hotel) y luego a billing.
     */
    @PostMapping("/book")
    public ResponseEntity<TravelBookingResponse> book(@RequestBody @Valid TravelBookingRequest request) {
        var result = orchestrator.bookTrip(request);
        return ResponseEntity.ok(result);
    }
}
