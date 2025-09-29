package com.example.billingservice.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    public record ChargeRequest(
            @NotBlank String sagaId,
            @NotBlank String customerId,
            @NotNull BigDecimal amount,
            String reason
    ) {}

    public record ChargeResponse(
            @NotBlank String sagaId,
            String chargeId,
            boolean charged,
            String message
    ) {}

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> charge(@RequestBody ChargeRequest request, @RequestParam(name = "fail", required = false) Integer failCode) {
        // Simulación simple de cobro
        String id = UUID.randomUUID().toString();

        if (failCode != null) {
            if (failCode == 409) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ChargeResponse(request.sagaId(), id, false, "Forzado error 409"));
            }
        }

        return ResponseEntity.ok(new ChargeResponse(request.sagaId(), id, true, "Cobro realizado"));
    }
}
