# Ejercicio 1: Camino Feliz del Orquestador SAGA

## Objetivo

El objetivo de este ejercicio es comprender cómo el orquestador coordina las llamadas a múltiples microservicios para completar un flujo de negocio exitoso. Aprenderás a usar `MockRestServiceServer` para simular respuestas de servicios externos en un entorno de pruebas de Spring Boot.

## Archivo a Completar

[TravelOrchestratorHappyPathTest.java](file:///Users/anyulled/IdeaProjects/java-avanzado-lab-sagas-1/travel-agency/src/test/java/com/example/travelagency/TravelOrchestratorHappyPathTest.java)

## Guía Paso a Paso

### Paso 1: Configurar Mock para Flight Service

Debemos indicar al servidor de mocks que espere una llamada POST a la URL del servicio de vuelos y responda con éxito.

```java
mockServer.expect(requestTo("http://flight-service/api/flights/book"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(
                objectMapper.writeValueAsString(new FlightBookingResponse("f-1", true, "Vuelo confirmado")),
                MediaType.APPLICATION_JSON));
```

### Paso 2: Configurar Mock para Hotel Service

Similar al paso anterior, configuramos la respuesta para el servicio de hoteles.

```java
mockServer.expect(requestTo("http://hotel-service/api/hotels/book"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(
                objectMapper.writeValueAsString(new HotelBookingResponse("h-1", true, "Hotel confirmado")),
                MediaType.APPLICATION_JSON));
```

### Paso 3: Configurar Mock para Billing Service

Finalmente, simulamos el proceso de cobro.

```java
mockServer.expect(requestTo("http://billing-service/api/billing/charge"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(
                objectMapper.writeValueAsString(new ChargeResponse("b-1", true, "Pago realizado")),
                MediaType.APPLICATION_JSON));
```

### Paso 4: Ejecutar y Verificar

Llamamos al método `bookTrip` del orquestador y verificamos que todos los pasos se hayan confirmado.

```java
TravelBookingResponse response = orchestrator.bookTrip(request);

assertNotNull(response);
assertTrue(response.flightConfirmed());
assertTrue(response.hotelConfirmed());
assertTrue(response.chargeConfirmed());
mockServer.verify();
```

## Verificación

Ejecuta el siguiente comando en la terminal:

```bash
mvn -pl travel-agency -Dtest=TravelOrchestratorHappyPathTest test
```

## Conceptos Clave

| Concepto | Descripción |
| :--- | :--- |
| SAGA Orchestration | Centraliza la lógica de control en un único componente. |
| MockRestServiceServer | Herramienta de Spring para probar clientes REST simulando el servidor. |
| Happy Path | El flujo de ejecución donde nada falla. |

## Imports Necesarios

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
```
