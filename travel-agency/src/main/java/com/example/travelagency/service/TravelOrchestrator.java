package com.example.travelagency.service;

import com.example.travelagency.client.BillingClient;
import com.example.travelagency.client.FlightClient;
import com.example.travelagency.client.HotelClient;
import com.example.travelagency.dto.BookingDtos.*;
import com.example.travelagency.model.BookingState;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TravelOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(TravelOrchestrator.class);

    private final FlightClient flightClient;
    private final HotelClient hotelClient;
    private final BillingClient billingClient;

    private final Map<String, BookingState> bookingStates = new ConcurrentHashMap<>();

    public TravelOrchestrator(FlightClient flightClient, HotelClient hotelClient, BillingClient billingClient) {
        this.flightClient = flightClient;
        this.hotelClient = hotelClient;
        this.billingClient = billingClient;
    }

    public BookingState getState(String sagaId) {
        return bookingStates.get(sagaId);
    }

    public TravelBookingResponse bookTrip(TravelBookingRequest request) {
        String sagaId = UUID.randomUUID().toString();
        bookingStates.put(sagaId, BookingState.PENDING);

        log.info("[SAGA:{}] Iniciando reserva de viaje: {} -> {}", sagaId, request.origin(), request.destination());

        FlightBookingResponse flight;
        HotelBookingResponse hotel;
        ChargeResponse charge;

        // 1) Reservar vuelo
        try {
            flight = flightClient.book(new FlightBookingRequest(
                    sagaId,
                    request.customerId(),
                    request.origin(),
                    request.destination(),
                    request.departureDate(),
                    request.returnDate(),
                    request.guests(),
                    request.failFlightFlag()
            ));
            if (flight == null || !flight.confirmed()) {
                log.warn("[SAGA:{}] Fallo en vuelo: {}", sagaId, flight != null ? flight.message() : "sin respuesta");
                bookingStates.put(sagaId, BookingState.CANCELED);
                return new TravelBookingResponse(sagaId, false, false, false, "Fallo en vuelo");
            }
            log.info("[SAGA:{}] Vuelo reservado OK: {}", sagaId, flight.flightBookingId());
        } catch (FeignException e) {
            log.warn("[SAGA:{}] Excepción en vuelo: {}", sagaId, e.contentUTF8());
            bookingStates.put(sagaId, BookingState.CANCELED);
            return new TravelBookingResponse(sagaId, false, false, false, "Fallo en vuelo");
        }

        // 2) Reservar hotel
        FlightBookingCancel flightBookingCancel = new FlightBookingCancel(
                sagaId,
                flight.flightBookingId()
        );

        // 2) Reservar hotel

        try {
            hotel = hotelClient.book(new HotelBookingRequest(
                    sagaId,
                    request.customerId(),
                    request.destination(),
                    request.departureDate(),
                    request.returnDate(),
                    request.guests(),
                    request.failHotelFlag()
            ));

            if (hotel == null || !hotel.confirmed()) {
                log.warn("[SAGA:{}] Fallo en hotel: {}", sagaId, hotel != null ? hotel.message() : "sin respuesta");
                flightClient.cancel(flightBookingCancel); // compensación
                bookingStates.put(sagaId, BookingState.CANCELED);
                log.warn("[SAGA:{}] Vuelo cancelado correctamente (ID: {})", sagaId, flight.flightBookingId());
                return new TravelBookingResponse(sagaId, false, false, false, "Fallo en hotel");
            }
            log.info("[SAGA:{}] Hotel reservado OK: {}", sagaId, hotel.hotelBookingId());
        } catch (FeignException e) {
            log.warn("[SAGA:{}] Excepción en hotel: {}", sagaId, e.contentUTF8());
            flightClient.cancel(flightBookingCancel); // compensación
            bookingStates.put(sagaId, BookingState.CANCELED);
            log.warn("[SAGA:{}] Vuelo cancelado correctamente (ID: {})", sagaId, flight.flightBookingId());
            return new TravelBookingResponse(sagaId, false, false, false, "Fallo en hotel");
        }
        HotelBookingCancel hotelCancel = new HotelBookingCancel(
                sagaId,
                hotel.hotelBookingId()
        );


        // 3) Cobrar (billing)
        try {
            charge = billingClient.charge(new ChargeRequest(
                    sagaId,
                    request.customerId(),
                    request.amount(),
                    "Viaje a " + request.destination(),
                    request.failBillingFlag()
            ));
            if (charge == null || !charge.charged()) {
                log.warn("[SAGA:{}] Fallo en cobro: {}", sagaId, charge != null ? charge.message() : "sin respuesta");
                hotelClient.cancel(hotelCancel); // compensación
                log.warn("[SAGA:{}] Hotel cancelado correctamente (ID: {})", sagaId, hotel.hotelBookingId());
                flightClient.cancel(flightBookingCancel); // compensación
                log.warn("[SAGA:{}] Vuelo cancelado correctamente (ID: {})", sagaId, flight.flightBookingId());
                bookingStates.put(sagaId, BookingState.COMPENSATED);
                return new TravelBookingResponse(sagaId, false, false, false, "Fallo en cobro");
            }
            log.info("[SAGA:{}] Cobro realizado OK: {}", sagaId, charge.chargeId());
        } catch (FeignException e) {
            log.warn("[SAGA:{}] Excepción en cobro: {}", sagaId, e.contentUTF8());
            hotelClient.cancel(hotelCancel); // compensación
            log.warn("[SAGA:{}] Hotel cancelado correctamente (ID: {})", sagaId, hotel.hotelBookingId());
            flightClient.cancel(flightBookingCancel); // compensación
            log.warn("[SAGA:{}] Vuelo cancelado correctamente (ID: {})", sagaId, flight.flightBookingId());
            bookingStates.put(sagaId, BookingState.COMPENSATED);
            return new TravelBookingResponse(sagaId, false, false, false, "Fallo en cobro");
        }

        String msg = "Reserva y cobro completados";
        log.info("[SAGA:{}] {}", sagaId, msg);
        bookingStates.put(sagaId, BookingState.CONFIRMED);
        return new TravelBookingResponse(sagaId, true, true, true, msg);
    }
}