# Llamada sin errores
```shell
curl -X POST http://localhost:8080/api/travel/book -H 'Content-Type: application/json' -d '{
"customerId":"c-123",
"origin":"BOG",
"destination":"MDE",
"departureDate":"2030-04-10",
"returnDate":"2030-04-15",
"guests":2,
"amount":500.00
}'
```
# Llamada con un SAGA ID '1' como header (idempotencia) 
```shell
curl -X POST http://localhost:8080/api/travel/book -H 'Content-Type: application/json' -H 'fakeSagaId: 1' -d '{
"customerId":"c-123",
"origin":"BOG",
"destination":"MDE",
"departureDate":"2030-04-10",
"returnDate":"2030-04-15",
"guests":2,
"amount":500.00
}'
```

# Llamada error 500
```shell
'curl -X POST http://localhost:8080/api/travel/book -H 'Content-Type: application/json' -H 'X-Fail: 500' -d '{
"customerId":"c-123",
"origin":"BOG",
"destination":"MDE",
"departureDate":"2030-04-10",
"returnDate":"2030-04-15",
"guests":2,
"amount":500.00
}'
```

# Llamada error 409
```shell
curl -X POST http://localhost:8080/api/travel/book -H 'Content-Type: application/json' -H 'X-Fail: 409' -d '{
"customerId":"c-123",
"origin":"BOG",
"destination":"MDE",
"departureDate":"2030-04-10",
"returnDate":"2030-04-15",
"guests":2,
"amount":500.00
}'
```
# Endpoint consultar el estado de una reserva por ID.
````shell
http://localhost:8080/api/travel/id/{id}
````