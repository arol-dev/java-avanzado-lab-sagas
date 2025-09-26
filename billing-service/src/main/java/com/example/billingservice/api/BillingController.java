package com.example.billingservice.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {
    //Mapa para controlar idempotencia
    private final HashMap<String,ChargeResponse> setSagaIds = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);

    public record ChargeRequest(
            @NotBlank String customerId,
            @NotNull BigDecimal amount,
            String reason
    ) {}

    public record ChargeResponse(
            String chargeId,
            boolean charged,
            String message
    ) {}

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> charge(@RequestBody ChargeRequest request, @RequestHeader(value = "sagaId", required = true) String sagaId, @RequestHeader(value = "X-Fail", required = false) String failHeader) {
        log.warn("[SAGA :{}] Inicio charge para  {}", sagaId, request.customerId);
        //Control de idempodencia
        if(setSagaIds.get(sagaId) != null) {
            return ResponseEntity.ok(setSagaIds.get(sagaId));
        }

        if(failHeader != null) {
            log.info("[Se fuerza un error manual en BillingController /Charge error: {}]", failHeader);
            if("500".equals(failHeader)){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ChargeResponse(null, false, "Error en el cobro"));
            }
        }
        // Simulación simple de cobro
        String id = UUID.randomUUID().toString();
        ChargeResponse response = new ChargeResponse(id, true, "Cobro realizado");

        //Añadir control de idempodencia
        setSagaIds.put(sagaId, response);

        return ResponseEntity.ok(response);
    }
}
