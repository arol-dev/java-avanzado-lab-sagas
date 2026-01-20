package com.example.billingservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "charges")
public class Charge {

    @Id
    private String id;
    private String customerId;
    private BigDecimal amount;
    private String reason;
    private LocalDateTime createdAt;

    public Charge() {
    }

    public Charge(String id, String customerId, BigDecimal amount, String reason) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
