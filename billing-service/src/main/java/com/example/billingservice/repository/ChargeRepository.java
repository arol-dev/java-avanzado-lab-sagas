package com.example.billingservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.billingservice.model.Charge;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, String> {
    List<Charge> findByCustomerId(String customerId);
}
