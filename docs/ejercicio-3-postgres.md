# Ejercicio 3: Persistencia con Postgres y Testcontainers

## Objetivo

En este ejercicio aprenderás a integrar una base de datos real (PostgreSQL) en tus pruebas de integración utilizando **Testcontainers**. Esto permite validar que tu lógica de persistencia y tus consultas JPA funcionan correctamente contra el mismo motor de base de datos que usarás en producción, sin necesidad de instalar nada localmente.

## Archivo a Completar

[BillingPersistenceTest.java](file:///Users/anyulled/IdeaProjects/java-avanzado-lab-sagas-1/billing-service/src/test/java/com/example/billingservice/BillingPersistenceTest.java)

## Guía Paso a Paso

### Paso 1: Crear una instancia de Charge

Instanciamos el objeto que queremos persistir.

```java
Charge charge = new Charge("test-1", "cust-1", new BigDecimal("100.00"), "Reserva de vuelo");
```

### Paso 2: Guardar el Charge

Usamos el repositorio autowired para guardar la entidad en la base de datos gestionada por Testcontainers.

```java
repository.save(charge);
```

### Paso 3: Buscar por Customer ID

Validamos que podemos recuperar la información usando nuestros métodos del repositorio.

```java
List<Charge> charges = repository.findByCustomerId("cust-1");
```

### Paso 4: Verificación final

Aseguramos que los datos recuperados coinciden con los guardados.

```java
assertFalse(charges.isEmpty());
assertEquals("test-1", charges.get(0).getId());
```

## Verificación

Ejecuta el siguiente comando (requiere Docker instalado y corriendo):

```bash
mvn -pl billing-service -Dtest=BillingPersistenceTest test
```

## Conceptos Clave

| Concepto | Descripción |
| :--- | :--- |
| Testcontainers | Librería de Java que permite levantar contenedores Docker para pruebas. |
| @ServiceConnection | Anotación de Spring Boot 3.1+ que configura automáticamente la conexión al contenedor. |
| Data JPA | Abstracción de Spring para facilitar la persistencia con Java Persistence API. |

## Imports Necesarios

```java
import static org.junit.jupiter.api.Assertions.*;
import com.example.billingservice.model.Charge;
```
