package com.example.travelagency.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTOs usados por la Agencia de Viajes y los microservicios remotos.
 * Nota: Es un lab, mantuvimos los DTOs mínimos y en un solo archivo para simplicidad.
 */
public class BookingDtos {

    // -------- Solicitud al orquestador --------
    public record TravelBookingRequest(
            @NotBlank String customerId,
            @NotBlank String origin,
            @NotBlank String destination,
            @NotNull @Future LocalDate departureDate,
            @NotNull @Future LocalDate returnDate,
            @Min(1) int guests,
            @NotNull BigDecimal amount,
            String failFlightFlag,
            String failHotelFlag,
            String failBillingFlag
    ) {}

    public record TravelBookingResponse(
            String bookingId,
            boolean flightConfirmed,
            boolean hotelConfirmed,
            boolean charged,
            String message
    ) {}

    // -------- Flight --------
    public record FlightBookingRequest(
            @NotBlank String sagaId,
            String customerId,
            String origin,
            String destination,
            LocalDate departureDate,
            LocalDate returnDate,
            int guests,
            String failFlag
    ) {}

    public record FlightBookingCancel(
            @NotBlank String sagaId,
            String flightId
    ) {}

    public record FlightBookingResponse(
            String flightBookingId,
            boolean confirmed,
            String message
    ) {}

    public record FlightCancelResponse(
            String flightBookingId,
            boolean confirmed,
            String message
    ) {}

    // -------- Hotel --------
    public record HotelBookingRequest(
            @NotBlank String sagaId,
            String customerId,
            String destination,
            LocalDate checkIn,
            LocalDate checkOut,
            int guests,
            String failFlag
    ) {}

    public record HotelBookingResponse(
            String hotelBookingId,
            boolean confirmed,
            String message
    ) {}

    public record HotelCancellationResponse(
            String hotelBookingId,
            boolean confirmed,
            String message
    ) {}
    public record HotelBookingCancel(
            String sagaId,
            String hotelBookingId
    ) {}


    // -------- Billing --------
    public record ChargeRequest(
            @NotBlank String sagaId,
            String customerId,
            BigDecimal amount,
            String reason,
            String failFlag
    ) {}

    public record ChargeResponse(
            String chargeId,
            boolean charged,
            String message
    ) {}
}
