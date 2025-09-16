# billing-service

Servicio de Facturación del laboratorio SAGA.

## Requisitos
- Java 21
- Maven 3.9+

## Configuración
- Puerto por defecto: `8083` (ver `src/main/resources/application.yml`).

## Ejecutar
```bash
mvn -pl billing-service spring-boot:run
```

## Endpoints
- POST `/api/billing/charge` — realiza un cobro (simulado) y devuelve un id de transacción.

Cuerpo ejemplo:
```json
{
  "customerId": "c-123",
  "amount": 500.00,
  "reason": "Viaje a MDE"
}
```

## TODO
- Validar saldo/crédito del cliente, simular fallos para probar compensaciones.
- Implementar endpoint de reverso/devolución si lo requiere el flujo de compensación.
