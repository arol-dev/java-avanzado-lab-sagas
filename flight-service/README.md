# flight-service

Servicio de Vuelos del laboratorio SAGA.

## Requisitos
- Java 21
- Maven 3.9+

## Configuración
- Puerto por defecto: `8081` (ver `src/main/resources/application.yml`).

## Ejecutar
```bash
mvn -pl flight-service spring-boot:run
```

## Endpoints
- POST `/api/flights/book` — reserva un vuelo (simulado) y devuelve un id de reserva.

Cuerpo ejemplo:
```json
{
  "customerId": "c-123",
  "origin": "BOG",
  "destination": "MDE",
  "departureDate": "2030-04-10",
  "returnDate": "2030-04-15",
  "guests": 2
}
```

## TODO
- Implementar `/api/flights/cancel` para permitir compensación cuando falle otro paso del SAGA.
- Persistir reservas y validar idempotencia.
