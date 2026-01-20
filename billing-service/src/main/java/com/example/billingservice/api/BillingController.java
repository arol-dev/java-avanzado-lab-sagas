package com.example.billingservice.api;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    public record ChargeRequest(
            @NotBlank String customerId,
            @NotNull BigDecimal amount,
            String reason) {
    }

    public record ChargeResponse(
            String chargeId,
            boolean charged,
            String message) {
    }

    private final com.example.billingservice.repository.ChargeRepository repository;

    public BillingController(com.example.billingservice.repository.ChargeRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> charge(@RequestBody ChargeRequest request) {
        // Simulación de cobro y persistencia
        String id = UUID.randomUUID().toString();
        repository.save(new com.example.billingservice.model.Charge(id, request.customerId(), request.amount(),
                request.reason()));

        return ResponseEntity.ok(new ChargeResponse(id, true, "Cobro realizado y persistido"));
    }
}
