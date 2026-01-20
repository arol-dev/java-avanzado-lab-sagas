# Ejercicio 2: Transacciones de Compensación

## Objetivo

En una arquitectura de microservicios, no podemos usar transacciones ACID tradicionales de base de datos entre distintos servicios. El patrón SAGA resuelve esto mediante **transacciones de compensación**: si un paso falla, el orquestador debe ejecutar acciones para deshacer los pasos anteriores que tuvieron éxito.

## Archivo a Completar

[TravelOrchestratorCompensationTest.java](file:///Users/anyulled/IdeaProjects/java-avanzado-lab-sagas-1/travel-agency/src/test/java/com/example/travelagency/TravelOrchestratorCompensationTest.java)

## Guía Paso a Paso

### Paso 1: Compensación de Vuelo al Fallar Hotel

Si el hotel no tiene disponibilidad, el orquestador debe cancelar el vuelo previamente reservado.

```java
// Dentro de shouldCompensateWhenHotelFails
mockServer.expect(requestTo("http://flight-service/api/flights/cancel"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess());
```

### Paso 2: Compensación de Hotel al Fallar Cobro

Si el cobro falla (ej. fondos insuficientes), debemos cancelar tanto el hotel como el vuelo.

```java
// Dentro de shouldCompensateWhenBillingFails
mockServer.expect(requestTo("http://hotel-service/api/hotels/cancel"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess());
```

### Paso 3: Compensación de Vuelo al Fallar Cobro

Continuando con el flujo de compensación tras el fallo de cobro:

```java
// Dentro de shouldCompensateWhenBillingFails
mockServer.expect(requestTo("http://flight-service/api/flights/cancel"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess());
```

## Verificación

Ejecuta el siguiente comando:

```bash
mvn -pl travel-agency -Dtest=TravelOrchestratorCompensationTest test
```

## Conceptos Clave

| Concepto | Descripción |
| :--- | :--- |
| Compensating Transaction | Una operación que revierte los efectos de una transacción confirmada previamente. |
| Eventual Consistency | El estado del sistema será consistente en algún punto del futuro después de las compensaciones. |
| Fail-fast | El orquestador detiene el proceso inmediatamente cuando detecta un fallo y comienza la limpieza. |

## Imports Necesarios

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
```
