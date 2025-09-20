package com.example.flightservice.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

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
    public ResponseEntity<FlightBookingResponse> book(@RequestBody FlightBookingRequest request) {
        // Simulación simple de reserva de vuelo
        String id = UUID.randomUUID().toString();
        return ResponseEntity.ok(new FlightBookingResponse(id, true, "Vuelo reservado"));
    }

    // TODO: implementar endpoint de cancelación para soportar compensación en la SAGA
    @DeleteMapping("/cancel")
    public ResponseEntity<FlightBookingResponse> cancel(@RequestBody FlightBookingRequest request) {
        return ResponseEntity.ok(new FlightBookingResponse(request.customerId, false, "Vuelo cancelado"));
    }
}
