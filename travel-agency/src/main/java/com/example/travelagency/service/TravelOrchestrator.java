package com.example.travelagency.service;

import com.example.travelagency.client.BillingClient;
import com.example.travelagency.client.FlightClient;
import com.example.travelagency.client.HotelClient;
import com.example.travelagency.dto.BookingDtos;
import com.example.travelagency.dto.BookingDtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio orquestador del patrón SAGA.
 *
 * Flujo feliz (simplificado):
 * 1) Reserva vuelo
 * 2) Reserva hotel
 * 3) Cobra al cliente
 *
 * Si falla hotel, TODO: compensar cancelando vuelo.
 * Si falla billing, TODO: compensar cancelando hotel y vuelo.
 *
 * Este servicio usa Feign para llamadas HTTP síncronas (para el lab); en producción
 * se recomienda colas/eventos y/o timeouts, reintentos, idempotencia, etc.
 */
@Service
public class TravelOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(TravelOrchestrator.class);

    private final FlightClient flightClient;
    private final HotelClient hotelClient;
    private final BillingClient billingClient;

    public TravelOrchestrator(FlightClient flightClient, HotelClient hotelClient, BillingClient billingClient) {
        this.flightClient = flightClient;
        this.hotelClient = hotelClient;
        this.billingClient = billingClient;
    }

    public TravelBookingResponse bookTrip(TravelBookingRequest request) {
        String sagaId = UUID.randomUUID().toString();
        log.info("[SAGA:{}] Iniciando reserva de viaje: {} -> {}", sagaId, request.origin(), request.destination());

        // 1) Reservar vuelo
        FlightBookingResponse flight = flightClient.book(new FlightBookingRequest(
                request.customerId(), request.origin(), request.destination(), request.departureDate(), request.returnDate(), request.guests()
        ));
        if (flight == null || !flight.confirmed()) {
            String msg = "Reserva de vuelo fallida";
            log.warn("[SAGA:{}] {}", sagaId, msg);
            return new TravelBookingResponse(sagaId, false, false, false, msg);
        }

        // 2) Reservar hotel
        HotelBookingResponse hotel = hotelClient.book(new HotelBookingRequest(
                request.customerId(), request.destination(), request.departureDate(), request.returnDate(), request.guests()
        ));
        if (hotel == null || !hotel.confirmed()) {
            String msg = "Reserva de hotel fallida (TODO: compensar vuelo)";
            log.warn("[SAGA:{}] {}", sagaId, msg);
            // TODO: Llamar a cancelación de vuelo (compensación)
            FlightBookingResponse response = flightClient.cancel(new FlightBookingRequest(
                    request.customerId(), request.origin(), request.destination(), request.departureDate(), request.returnDate(), request.guests()));
            return new TravelBookingResponse(sagaId, response.confirmed(), false, false, msg);
        }

        // 3) Cobrar (billing)
        var charge = billingClient.charge(new ChargeRequest(request.customerId(), request.amount(), "Viaje a " + request.destination()));
        if (charge == null || !charge.charged()) {
            String msg = "Cobro fallido (TODO: compensar hotel y vuelo)";
            log.warn("[SAGA:{}] {}", sagaId, msg);
            // TODO: Llamar a cancelación de hotel y vuelo (compensaciones)
            FlightBookingResponse flightResponse = flightClient.cancel(new FlightBookingRequest(
                    request.customerId(), request.origin(), request.destination(), request.departureDate(), request.returnDate(), request.guests()));
            HotelBookingResponse hotelResponse = hotelClient.cancel(new HotelBookingRequest(
                    request.customerId(), request.destination(), request.departureDate(), request.returnDate(), request.guests()));
            return new TravelBookingResponse(sagaId, flightResponse.confirmed(), hotelResponse.confirmed(), false, msg);
        }

        String msg = "Reserva y cobro completados";
        log.info("[SAGA:{}] {}", sagaId, msg);
        return new TravelBookingResponse(sagaId, true, true, true, msg);
    }
}
