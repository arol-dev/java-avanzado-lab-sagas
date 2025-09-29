package com.example.travelagency.service;

import com.example.travelagency.client.BillingClient;
import com.example.travelagency.client.FlightClient;
import com.example.travelagency.client.HotelClient;
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

    public TravelBookingResponse bookTrip(TravelBookingRequest request, Integer failCode) {
        String sagaId = UUID.randomUUID().toString();
        log.info("[SAGA:{}] Iniciando reserva de viaje: {} -> {}", sagaId, request.origin(), request.destination());

        FlightBookingResponse flight = null;
        try{
            // 1) Reservar vuelo
            flight = flightClient.book(new FlightBookingRequest(sagaId, request.customerId(), request.origin(), request.destination(), request.departureDate(), request.returnDate(), request.guests()
            ));
        } catch (Exception e) {
            if (flight == null || !flight.confirmed()) {
                String msg = "Reserva de vuelo fallida";
                log.warn("[SAGA:{}] {}", sagaId, msg);
                return new TravelBookingResponse(sagaId, false, false, false, msg);
            }
        }

        HotelBookingResponse hotel = null;
        try{
            // 2) Reservar hotel
            hotel = hotelClient.book(new HotelBookingRequest(sagaId, request.customerId(), request.destination(), request.departureDate(), request.returnDate(), request.guests()
            ), failCode);
        } catch (Exception e) {
            if (hotel == null || !hotel.confirmed()) {
                String msg = "Reserva de hotel fallida (TODO: compensar vuelo)";
                log.warn("[SAGA:{}] {}", sagaId, msg);

                flightCompensation(sagaId, flight);

                return new TravelBookingResponse(sagaId, true, false, false, msg);
            }
        }

        ChargeResponse charge = null;
        try {
            // 3) Cobrar (billing)
            charge = billingClient.charge(new ChargeRequest(sagaId, request.customerId(), request.amount(), "Viaje a " + request.destination()), failCode);
        } catch (Exception e){
            if (charge == null || !charge.charged()) {
                String msg = "Cobro fallido (TODO: compensar hotel y vuelo)";
                log.warn("[SAGA:{}] {}", sagaId, msg);

                flightCompensation(sagaId, flight);
                hotelCompensation(sagaId, hotel);

                return new TravelBookingResponse(sagaId, true, true, false, msg);
            }
        }

        String msg = "Reserva y cobro completados";
        log.info("[SAGA:{}] {}", sagaId, msg);
        return new TravelBookingResponse(sagaId, true, true, true, msg);
    }

    private void hotelCompensation(String sagaId, HotelBookingResponse hotel) {
        log.info("[SAGA:{}] {}", sagaId, "Iniciando compensación del hotel: "+ hotel.hotelBookingId());
        try {
            HotelCancelingResponse hcr = hotelClient.cancel(new HotelCancelingRequest(sagaId, hotel.hotelBookingId()));
            if (hcr != null && hcr.confirmed()) {
                log.info("[SAGA:{}] {}", sagaId, hcr.message());
            }
        }catch (Exception e){
            log.warn("[SAGA:{}] {}", sagaId, "Fallo en la compensación del hotel.");
        }
    }

    private void flightCompensation(String sagaId, FlightBookingResponse flight){
        log.info("[SAGA:{}] {}", sagaId, "Iniciando compensación del vuelo: "+ flight.flightBookingId());
        try {
            FlightCancelingResponse fcr = flightClient.cancel(new FlightCancelingRequest(sagaId, flight.flightBookingId()));
            if(fcr!=null && fcr.confirmed()){
                log.info("[SAGA:{}] {}", sagaId, fcr.message());
            }
        }catch (Exception e){
            log.warn("[SAGA:{}] {}", sagaId, "Fallo en la compensación del vuelo.");
        }
    }
}
