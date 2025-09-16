# Java Avanzado — Lab: Patrones SAGA con Spring Boot (multi-módulo)

Bienvenido/a al laboratorio para practicar el patrón SAGA coordinando múltiples
microservicios con Spring Boot 3 (Java 21) y Spring Cloud 2025. El objetivo es
entender el flujo distribuido, diseñar compensaciones y agregar robustez (
timeouts, reintentos, idempotencia, observabilidad).

Este repo contiene 4 módulos:

- travel-agency: orquestador de la SAGA. Expone un endpoint REST para iniciar la
  reserva.
- flight-service: reserva de vuelos.
- hotel-service: reserva de hoteles.
- billing-service: cobro al cliente.

Comunicación vía HTTP utilizando REST y OpenFeign (en el orquestador).

## Requisitos previos

- JDK 21
- Maven 3.9+

## Puertos por defecto

- travel-agency → 8080
- flight-service → 8081
- hotel-service → 8082
- billing-service → 8083

Puedes cambiarlos editando los application.yml de cada módulo.

## Cómo ejecutar (paso a paso)

1) Compilar todo:
    - mvn -q -DskipTests package

2) Levantar los servicios (en terminales separadas):
    - mvn -pl flight-service spring-boot:run
    - mvn -pl hotel-service spring-boot:run
    - mvn -pl billing-service spring-boot:run
    - mvn -pl travel-agency spring-boot:run

3) Verificar health:
    - curl http://localhost:8081/actuator/health
    - curl http://localhost:8082/actuator/health
    - curl http://localhost:8083/actuator/health
    - curl http://localhost:8080/actuator/health

4) Ejecutar una reserva end-to-end (happy path):

```shell

   curl -X POST http://localhost:8080/api/travel/book \
     -H 'Content-Type: application/json' \
     -d '{
           "customerId":"c-123",
           "origin":"BOG",
           "destination":"MDE",
           "departureDate":"2030-04-10",
           "returnDate":"2030-04-15",
           "guests":2,
           "amount":500.00
         }'
```

Respuesta esperada (ejemplo):
```json

{
  "bookingId": "<uuid>",
  "flightConfirmed": true,
  "hotelConfirmed": true,
  "charged": true,
  "message": "Reserva y cobro completados"
}
```

## Qué es SAGA (resumen)

- Transacciones distribuidas sin 2-Phase Commit: cada servicio ejecuta una acción local y,
  si el flujo global falla, se ejecutan pasos de compensación para deshacer
  efectos previos.
- Orquestado: un orquestador central decide el siguiente paso (este lab).
- Coreografiado: servicios reaccionan a eventos (bonus al final).

## Estructura del orquestador (travel-agency)

- Controller: POST /api/travel/book
- Service (TravelOrchestrator):
    1) Reserva vuelo (flight-service)
    2) Reserva hotel (hotel-service)
    3) Cobra (billing-service)
       Si un paso falla, se devuelven mensajes indicativos y hay TODOs para
       agregar compensaciones.

## Ejercicios (guía paso a paso)

1) Compensaciones mínimas
    - En flight-service agrega POST /api/flights/cancel.
    - En hotel-service agrega POST /api/hotels/cancel.
    - En travel-agency, si falla hotel tras reservar vuelo, llama a cancelar
      vuelo; si falla billing tras hotel, cancela hotel y vuelo.

2) Simulación de fallos controlados
    - Agrega una bandera (ej. header X-Fail o query param) en
      flight/hotel/billing para forzar error 409/500.
    - Verifica que las compensaciones se disparen y dejen el sistema
      consistente.

3) Timeouts y reintentos en Feign
    - En travel-agency agrega propiedades:
   

```yaml
      feign.client.config.default.connectTimeout: 1000
      feign.client.config.default.readTimeout: 1000
      feign.client.config.default.retryer:
      org.springframework.cloud.openfeign.retryer.DefaultRetryer:
```

    - Experimenta con backoff y límites de reintentos.

4) Idempotencia
    - Pasa un sagaId (UUID) desde travel-agency a cada servicio.
    - Persiste el estado de las operaciones por sagaId (en memoria/mapa o base
      de datos) para evitar duplicados.

5) Consistencia de lectura y estados
    - Modela estados: PENDING, CONFIRMED, CANCELED, COMPENSATED.
    - Expón GET para consultar el estado de una reserva por ID.

6) Observabilidad
    - Agrega logs estructurados con el sagaId.
    - Integra Micrometer Jaeger (opcional) para trazabilidad
      distribuida.

7) Resiliencia avanzada (bonus)
    - Integra Resilience4j con Feign (circuit breaker, bulkhead, rate limiter).
    - Documenta decisiones de timeouts y circuitos.

8) Coreografía (bonus)
    - Cambia a eventos (Kafka/RabbitMQ) para que flight/hotel/billing reaccionen
      a eventos de la saga sin orquestador central.

## Mapa de módulos

- [Instrucciones del orquestador](travel-agency/README.md)
- [instrucciones de vuelos](flight-service/README.md)
- [instrucciones de hoteles](hotel-service/README.md): 
- [Instrucciones de facturación](billing-service/README.md): 

## Notas

- Versiones: Spring Boot 3.5.x y Spring Cloud 2025 (BOM). Ajusta si usas
  versiones más nuevas.
- Este código es una base para el laboratorio; muchos TODOs están a propósito
  para que puedas completarlos.
