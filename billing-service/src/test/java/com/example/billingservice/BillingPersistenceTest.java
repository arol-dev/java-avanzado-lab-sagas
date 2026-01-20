package com.example.billingservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.billingservice.model.Charge;
import com.example.billingservice.repository.ChargeRepository;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BillingPersistenceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private ChargeRepository repository;

    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldSaveAndLoadCharge() {
        // TODO: Paso 1 - Crear una nueva instancia de Charge
        // TODO: Paso 2 - Guardar el Charge usando el repository
        // TODO: Paso 3 - Buscar el Charge por customerId
        // TODO: Paso 4 - Verificar que la lista no esté vacía y los datos coincidan

        Charge charge = new Charge("test-id", "cust-1", new BigDecimal("100.00"), "Prueba");
        repository.save(charge);

        List<Charge> charges = repository.findByCustomerId("cust-1");
        assertFalse(charges.isEmpty());
        assertEquals("test-id", charges.get(0).getId());
    }
}
