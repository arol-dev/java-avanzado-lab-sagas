package com.example.hotelservice.api;

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
@RequestMapping("/api/hotels")
public class HotelController {

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);

    public record HotelBookingRequest(
            @NotBlank String sagaId,
            @NotBlank String customerId,
            @NotBlank String destination,
            @NotNull LocalDate checkIn,
            @NotNull LocalDate checkOut,
            @Min(1) int guests
    ) {}

    public record HotelBookingResponse(
            @NotBlank String sagaId,
            String hotelBookingId,
            boolean confirmed,
            String message
    ) {}

    public record HotelCancelingRequest(
            @NotBlank String sagaId,
            @NotBlank String hotelBookingId
    ) {}

    public record HotelCancelingResponse(
            @NotBlank String sagaId,
            String hotelBookingId,
            boolean confirmed,
            String message
    ) {}

    @PostMapping("/book")
    public ResponseEntity<HotelBookingResponse> book(@RequestBody HotelBookingRequest request, @RequestParam(name = "fail", required = false) Integer failCode) {
        // Simulación simple de reserva de hotel
        String id = UUID.randomUUID().toString();

        if (failCode != null) {
            if (failCode == 500) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HotelBookingResponse(request.sagaId(), id, false, "Forzado error 500"));
            }
        }

        return ResponseEntity.ok(new HotelBookingResponse(request.sagaId(), id, true, "Hotel reservado"));
    }

    @PostMapping("/cancel")
    public ResponseEntity<HotelCancelingResponse> cancel(@RequestBody HotelCancelingRequest request) {
        return ResponseEntity.ok(new HotelCancelingResponse(request.sagaId(), request.hotelBookingId(), true, "Reserva de hotel cancelada"));
    }
}
