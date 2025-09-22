package com.example.hotelservice.api;

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
@RequestMapping("/api/hotels")
public class HotelController {

    public record HotelBookingRequest(
            @NotBlank String customerId,
            @NotBlank String destination,
            @NotNull LocalDate checkIn,
            @NotNull LocalDate checkOut,
            @Min(1) int guests
    ) {}

    public record HotelBookingResponse(
            String hotelBookingId,
            boolean confirmed,
            String message
    ) {}

    @PostMapping("/book")
    public ResponseEntity<HotelBookingResponse> book(@RequestBody HotelBookingRequest request) {
        // Simulación simple de reserva de hotel
        String id = UUID.randomUUID().toString();
        return ResponseEntity.ok(new HotelBookingResponse(id, true, "Hotel reservado"));
    }

    // TODO: implementar endpoint de cancelación para soportar compensación en la SAGA
    @DeleteMapping("/cancel")
    public ResponseEntity<HotelBookingResponse> cancel(@RequestBody HotelBookingRequest request) {
        return ResponseEntity.ok(new HotelBookingResponse(request.customerId, false, "Hotel cancelado"));
    }
}
