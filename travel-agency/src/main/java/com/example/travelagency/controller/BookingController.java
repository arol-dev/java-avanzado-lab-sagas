package com.example.travelagency.controller;

import com.example.travelagency.dto.BookingDtos.TravelBookingRequest;
import com.example.travelagency.dto.BookingDtos.TravelBookingResponse;
import com.example.travelagency.model.BookingState;
import com.example.travelagency.service.TravelOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TravelBookingResponse> book(@RequestBody TravelBookingRequest request) {
        return ResponseEntity.ok(orchestrator.bookTrip(request));
    }

    @GetMapping("/state/{sagaId}")
    public ResponseEntity<String> getState(@PathVariable("sagaId") String sagaId) {
        BookingState state = orchestrator.getState(sagaId);
        if (state == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(state.name());
    }


}
