package com.example.flightservice.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    public record FlightBookingRequest(
            @NotBlank String sagaId,
            @NotBlank String customerId,
            @NotBlank String origin,
            @NotBlank String destination,
            @NotNull LocalDate departureDate,
            @NotNull LocalDate returnDate,
            @Min(1) int guests
    ) {}

    public record FlightBookingResponse(
            @NotBlank String sagaId,
            String flightBookingId,
            boolean confirmed,
            String message
    ) {}

    public record FlightCancelingRequest(
            @NotBlank String sagaId,
            @NotBlank String flightBookingId
    ) {}

    public record FlightCancelingResponse(
            @NotBlank String sagaId,
            String flightBookingId,
            boolean confirmed,
            String message
    ) {}

    @PostMapping("/book")
    public ResponseEntity<FlightBookingResponse> book(@RequestBody FlightBookingRequest request) {
        // Simulación simple de reserva de vuelo
        String id = UUID.randomUUID().toString();
        return ResponseEntity.ok(new FlightBookingResponse(request.sagaId(), id, true, "Vuelo reservado"));
    }

    @PostMapping("/cancel")
    public ResponseEntity<FlightCancelingResponse> cancel(@RequestBody FlightCancelingRequest request) {
        return ResponseEntity.ok(new FlightCancelingResponse(request.sagaId(), request.flightBookingId(), true, "Reserva de vuelo cancelada"));
    }

}
