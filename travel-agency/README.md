# travel-agency

Agencia de Viajes (orquestador SAGA) usando Spring Boot 3 y Spring Cloud 2025 con OpenFeign.

Este módulo expone un endpoint REST para iniciar la reserva de un viaje y orquesta las llamadas a los microservicios de vuelos, hoteles y facturación.

## Requisitos
- Java 21
- Maven 3.9+

## Configuración
- Puerto por defecto: `8080`
- Configura las URLs de los microservicios dependientes en `src/main/resources/application.yml`:

```yaml
services:
  flight:
    url: http://localhost:8081
  hotel:
    url: http://localhost:8082
  billing:
    url: http://localhost:8083
```

## Ejecutar
Desde la raíz del proyecto:

```bash
mvn -pl travel-agency spring-boot:run
```

O desde este módulo:

```bash
mvn spring-boot:run
```

## Endpoint principal
- POST `http://localhost:8080/api/travel/book`

Ejemplo de cuerpo:

```json
{
  "customerId": "c-123",
  "origin": "BOG",
  "destination": "MDE",
  "departureDate": "2030-04-10",
  "returnDate": "2030-04-15",
  "guests": 2,
  "amount": 500.00
}
```

## TODO (para el laboratorio)
- Implementar endpoints de cancelación en `flight-service` y `hotel-service` y usarlos aquí para compensación si falla algún paso.
- Agregar reintentos con backoff, timeouts y manejo de errores en Feign.
- Hacer idempotente el proceso (por ejemplo, pasando un `sagaId` en las cabeceras o cuerpo y registrando estado).
- Registrar eventos de la SAGA (iniciado, reservado vuelo, reservado hotel, cobrado, compensado, etc.).
- Versionar DTOs y manejar compatibilidad.
