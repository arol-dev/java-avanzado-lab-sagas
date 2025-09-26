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
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    //Mapa para controlar idempotencia
    private final HashMap<String,HotelBookingResponse> setSagaIds = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);

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
    public ResponseEntity<HotelBookingResponse> book(@RequestBody HotelBookingRequest request, @RequestHeader(value = "sagaId", required = true) String sagaId, @RequestHeader(value = "X-Fail", required = false) String failHeader) {
        log.warn("[SAGA :{}] Inicio book para  {}", sagaId, request.customerId);
        //Control de idempodencia
        if(setSagaIds.get(sagaId) != null) {
            return ResponseEntity.ok(setSagaIds.get(sagaId));
        }

        if(failHeader != null) {
            log.info("[Se fuerza un error manual en HotelController /book error: {}]", failHeader);
            if("409".equals(failHeader)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new HotelBookingResponse(null, false, "Error en reserva de hotel"));
            }
        }
        // Simulación simple de reserva de hotel
        String id = UUID.randomUUID().toString();
        HotelBookingResponse response = new HotelBookingResponse(id, true, "Hotel reservado");

        //Añadir control de idempodencia
        setSagaIds.put(sagaId, response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<HotelBookingResponse> cancel(@RequestBody HotelBookingRequest request) {
        log.info("Peticion de compensacion para HotelController request: {}]", request.customerId);
        return ResponseEntity.ok(new HotelBookingResponse(request.customerId, false, "Hotel cancelado"));
    }

}
