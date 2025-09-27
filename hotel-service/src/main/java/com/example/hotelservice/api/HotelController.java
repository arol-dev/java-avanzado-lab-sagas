package com.example.hotelservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);

    private final Map<String, HotelBookingResponse> inMemory = new HashMap<>();
    private final Map<String, CancelResponse> canceled = new HashMap<>();

    public record HotelBookingRequest(
            String sagaId,
            String customerId,
            String destination,
            String checkInDate,
            String checkOutDate,
            int guests,
            String failFlag
    ) {}

    public record HotelBookingResponse(
            String hotelBookingId,
            boolean confirmed,
            String message
    ) {}

    public record HotelBookingCancel(
            String sagaId,
            String hotelBookingId
    ) {}

    public record CancelResponse(
            String hotelBookingId,
            boolean canceled,
            String message
    ) {}

    @PostMapping("/book")
    public ResponseEntity<HotelBookingResponse> book(@RequestBody HotelBookingRequest request) {
        if (inMemory.containsKey(request.sagaId())) {
            log.info("[HotelService] Reutilizando booking sagaId={}", request.sagaId());
            return ResponseEntity.ok(inMemory.get(request.sagaId()));
        }

        if ("true".equalsIgnoreCase(request.failFlag())) {
            HotelBookingResponse resp = new HotelBookingResponse(null, false, "Fallo simulado en hotel");
            inMemory.put(request.sagaId(), resp);
            return ResponseEntity.badRequest().body(resp);
        }

        String bookingId = UUID.randomUUID().toString();
        HotelBookingResponse resp = new HotelBookingResponse(bookingId, true, "Hotel reservado");
        inMemory.put(request.sagaId(), resp);

        log.info("[HotelService] Hotel reservado sagaId={} hotelId={}", request.sagaId(), bookingId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelResponse> cancel(@RequestBody HotelBookingCancel request) {
        if (canceled.containsKey(request.sagaId())) {
            log.info("[HotelService] Cancel ya realizada sagaId={}", request.sagaId());
            return ResponseEntity.ok(canceled.get(request.sagaId()));
        }

        CancelResponse resp = new CancelResponse(request.hotelBookingId(), true, "Hotel cancelado");
        canceled.put(request.sagaId(), resp);

        log.info("[HotelService] Hotel cancelado sagaId={} hotelId={}", request.sagaId(), request.hotelBookingId());
        return ResponseEntity.ok(resp);
    }
}