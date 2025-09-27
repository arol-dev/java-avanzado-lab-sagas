package com.example.billingservice.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);

    private final Map<String, ChargeResponse> inMemory = new HashMap<>();

    public record ChargeRequest(
            @NotBlank String sagaId,
            @NotBlank String customerId,
            @NotNull Integer amount,
            String reason,
            @Nullable String failFlag
    ) {}

    public record ChargeResponse(
            String chargeId,
            boolean charged,
            String message
    ) {}



    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> charge(@RequestBody ChargeRequest request)
    {
        if (inMemory.containsKey(request.sagaId())) {
            log.info("[BillingService] Reutilizando respuesta idempotente sagaId={}", request.sagaId());
            return ResponseEntity.ok(inMemory.get(request.sagaId()));
        }

        if ("true".equalsIgnoreCase(request.failFlag())) {
            ChargeResponse resp = new ChargeResponse(null, false, "Simulación de fallo en cobro");
            inMemory.put(request.sagaId(), resp);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }

        String chargeId = UUID.randomUUID().toString();
        ChargeResponse resp = new ChargeResponse(chargeId, true, "Cobro realizado");
        inMemory.put(request.sagaId(), resp);
        log.info("[BillingService] Cobro registrado sagaId={} chargeId={}", request.sagaId(), chargeId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refund")
    public ResponseEntity<ChargeResponse> refund(@RequestBody ChargeRequest request) {
        // Simulación simple de reverso
        return ResponseEntity.ok(
                new ChargeResponse(request.customerId, true, "Refund realizado")
        );
    }
}
