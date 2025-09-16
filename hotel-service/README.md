# hotel-service

Servicio de Hoteles del laboratorio SAGA.

## Requisitos
- Java 21
- Maven 3.9+

## Configuración
- Puerto por defecto: `8082` (ver `src/main/resources/application.yml`).

## Ejecutar
```bash
mvn -pl hotel-service spring-boot:run
```

## Endpoints
- POST `/api/hotels/book` — reserva un hotel (simulado) y devuelve un id de reserva.

Cuerpo ejemplo:
```json
{
  "customerId": "c-123",
  "destination": "MDE",
  "checkIn": "2030-04-10",
  "checkOut": "2030-04-15",
  "guests": 2
}
```

## TODO
- Implementar `/api/hotels/cancel` para permitir compensación cuando falle otro paso del SAGA.
- Persistir reservas y validar idempotencia.
