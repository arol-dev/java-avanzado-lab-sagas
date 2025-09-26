package com.example.flightservice.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private static final Logger log = LoggerFactory.getLogger(FlightController.class);
    //Mapa para controlar idempotencia
    private final HashMap<String,FlightBookingResponse> mapSagaIds = new HashMap<>();

    public record FlightBookingRequest(
            @NotBlank String customerId,
            @NotBlank String origin,
            @NotBlank String destination,
            @NotNull LocalDate departureDate,
            @NotNull LocalDate returnDate,
            @Min(1) int guests
    ) {}

    public record FlightBookingResponse(
            String flightBookingId,
            boolean confirmed,
            String message
    ) {}

    @PostMapping("/book")
    public ResponseEntity<FlightBookingResponse> book(@RequestBody FlightBookingRequest request, @RequestHeader(value = "sagaId", required = true) String sagaId) {
        // Simulación simple de reserva de vuelo
        log.warn("[SAGA :{}] Inicio book para  {}", sagaId, request.customerId);
        //Control de idempodencia
        if(mapSagaIds.get(sagaId) != null) {
            return ResponseEntity.ok(mapSagaIds.get(sagaId));
        }
        String id = UUID.randomUUID().toString();
        FlightBookingResponse response = new FlightBookingResponse(id, true, "Vuelo reservado");

        //Añadir control de idempodencia
        mapSagaIds.put(sagaId, response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<FlightBookingResponse> cancel(@RequestBody FlightBookingRequest request) {
        log.info("Peticion de compensacion para FlightController request: {}]", request.customerId);
        return ResponseEntity.ok(new FlightBookingResponse(request.customerId,false, "Vuelo cancelado"));
    }

}
