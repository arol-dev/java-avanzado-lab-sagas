package com.example.flightservice.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private static final Logger log = LoggerFactory.getLogger(FlightController.class);

    private final Map<String, FlightBookingResponse> inMemory = new HashMap<>();
    private final Map<String, CancelResponse> canceled = new HashMap<>();

    public record FlightBookingRequest(
            String sagaId,
            String customerId,
            String origin,
            String destination,
            String departureDate,
            String returnDate,
            int guests,
            String failFlag
    ) {}

    public record FlightBookingResponse(
            String flightBookingId,
            boolean confirmed,
            String message
    ) {}

    public record FlightBookingCancel(
            String sagaId,
            String flightBookingId
    ) {}

    public record CancelResponse(
            String flightBookingId,
            boolean canceled,
            String message
    ) {}

    @PostMapping("/book")
    public ResponseEntity<FlightBookingResponse> book(@RequestBody FlightBookingRequest request) {

        if (inMemory.containsKey(request.sagaId())) {
            log.info("[FlightService] Reutilizando booking sagaId={}", request.sagaId());
            return ResponseEntity.ok(inMemory.get(request.sagaId()));
        }

        if ("true".equalsIgnoreCase(request.failFlag())) {
            FlightBookingResponse resp = new FlightBookingResponse(null, false, "Fallo simulado en vuelo");
            inMemory.put(request.sagaId(), resp);
            return ResponseEntity.badRequest().body(resp);
        }

        String bookingId = UUID.randomUUID().toString();
        FlightBookingResponse resp = new FlightBookingResponse(bookingId, true, "Vuelo reservado");
        inMemory.put(request.sagaId(), resp);

        log.info("[FlightService] Vuelo reservado sagaId={} flightId={}", request.sagaId(), bookingId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelResponse> cancel(@RequestBody FlightBookingCancel request) {
        if (canceled.containsKey(request.sagaId())) {
            log.info("[FlightService] Cancel ya realizada sagaId={}", request.sagaId());
            return ResponseEntity.ok(canceled.get(request.sagaId()));
        }

        CancelResponse resp = new CancelResponse(request.flightBookingId(), true, "Vuelo cancelado");
        canceled.put(request.sagaId(), resp);

        log.info("[FlightService] Vuelo cancelado sagaId={} flightId={}", request.sagaId(), request.flightBookingId());
        return ResponseEntity.ok(resp);
    }
}
