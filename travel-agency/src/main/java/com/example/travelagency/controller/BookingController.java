package com.example.travelagency.controller;

import com.example.travelagency.dto.BookingDtos.TravelBookingRequest;
import com.example.travelagency.dto.BookingDtos.TravelBookingResponse;
import com.example.travelagency.service.TravelOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<TravelBookingResponse> book(@RequestBody @Valid TravelBookingRequest request,  @RequestHeader(value = "fakeSagaId", required = false) String fakeSagaId,  @RequestHeader(value = "X-Fail", required = false) String failHeader) {
        var result = orchestrator.bookTrip(request, fakeSagaId, failHeader);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<Object> get(@PathVariable("id") String id) {
        var result = orchestrator.get(id);
        if(result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Reserva no encontrada", "id", id));
        }
    }
}
