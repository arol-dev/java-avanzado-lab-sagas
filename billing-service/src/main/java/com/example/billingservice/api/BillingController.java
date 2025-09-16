package com.example.billingservice.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

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
    public ResponseEntity<ChargeResponse> charge(@RequestBody ChargeRequest request) {
        // Simulación simple de cobro
        String id = UUID.randomUUID().toString();
        return ResponseEntity.ok(new ChargeResponse(id, true, "Cobro realizado"));
    }
}
