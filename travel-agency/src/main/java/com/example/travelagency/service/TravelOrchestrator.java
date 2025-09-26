package com.example.travelagency.service;

import com.example.travelagency.client.BillingClient;
import com.example.travelagency.client.FlightClient;
import com.example.travelagency.client.HotelClient;
import com.example.travelagency.dto.BookingDtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    //""Persistencia reservas""
    private final HashMap<String,TravelBookingResponse> mapReservas = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(TravelOrchestrator.class);

    private final FlightClient flightClient;
    private final HotelClient hotelClient;
    private final BillingClient billingClient;

    public TravelOrchestrator(FlightClient flightClient, HotelClient hotelClient, BillingClient billingClient) {
        this.flightClient = flightClient;
        this.hotelClient = hotelClient;
        this.billingClient = billingClient;
    }

    public TravelBookingResponse bookTrip(TravelBookingRequest request, String fakeSagaId, String failHeader) {
        String sagaId = fakeSagaId != null && !fakeSagaId.isEmpty() ? fakeSagaId :  UUID.randomUUID().toString();
        log.info("[SAGA:{}] Iniciando reserva de viaje: {} -> {}", sagaId, request.origin(), request.destination());

        TravelBookingResponse travelResponse =  new TravelBookingResponse(sagaId, false, false, false, "Iniciada reserva", Status.PENDING);
        mapReservas.put(travelResponse.bookingId(),travelResponse);

        // 1) Reservar vuelo
        FlightBookingRequest flightBookReq = new FlightBookingRequest(
                request.customerId(), request.origin(), request.destination(), request.departureDate(), request.returnDate(), request.guests());
        FlightBookingResponse flight = flightClient.book(flightBookReq, sagaId);
        if (flight == null || !flight.confirmed()) {
            String msg = "Reserva de vuelo fallida";
            log.warn("[SAGA:{}] {}", sagaId, msg);
            travelResponse = new TravelBookingResponse(sagaId, false, false, false, msg, Status.CANCELED);
            mapReservas.put(travelResponse.bookingId(),travelResponse);
            return travelResponse;
        }

        // 2) Reservar hotel
        HotelBookingRequest hotelBookingReq = new HotelBookingRequest(
                request.customerId(), request.destination(), request.departureDate(), request.returnDate(), request.guests());
        HotelBookingResponse hotel = hotelClient.book(hotelBookingReq, sagaId, failHeader);
        if (hotel == null || !hotel.confirmed()) {
            String msg = "Reserva de hotel fallida";
            log.warn("[SAGA:{}] {}", sagaId, msg);
            //Cancelar vuelo
            flightClient.cancel(flightBookReq);
            travelResponse = new TravelBookingResponse(sagaId, false, false, false, msg, Status.CANCELED);
            mapReservas.put(travelResponse.bookingId(),travelResponse);
            return travelResponse;
        }

        // 3) Cobrar (billing)
        ChargeRequest ChargeReq = new ChargeRequest(request.customerId(), request.amount(), "Viaje a " + request.destination());
        ChargeResponse charge = billingClient.charge(ChargeReq, sagaId, failHeader);
        if (charge == null || !charge.charged()) {
            String msg = "Cobro fallido";
            log.warn("[SAGA:{}] {}", sagaId, msg);
            //Cancelar vuelo y hotel
            flightClient.cancel(flightBookReq);
            hotelClient.cancel(hotelBookingReq);
            travelResponse =  new TravelBookingResponse(sagaId, false, false, false, msg, Status.CANCELED);
            mapReservas.put(travelResponse.bookingId(),travelResponse);
            return travelResponse;
        }

        String msg = "Reserva y cobro completados";
        log.info("[SAGA:{}] {}", sagaId, msg);
        travelResponse =  new TravelBookingResponse(sagaId, true, true, false, msg, Status.CONFIRMED);
        log.info("[Añadida reserva con id: {}", travelResponse.bookingId());
        mapReservas.put(travelResponse.bookingId(),travelResponse);
        return travelResponse;
    }


    public TravelBookingResponse get(String id) {
        if(mapReservas.get(id) != null) {
            return mapReservas.get(id);
        } else {
            return null;
        }
    }

}
