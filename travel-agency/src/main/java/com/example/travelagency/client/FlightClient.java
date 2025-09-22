package com.example.travelagency.client;

import com.example.travelagency.dto.BookingDtos.FlightBookingRequest;
import com.example.travelagency.dto.BookingDtos.FlightBookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "flight-service", url = "${services.flight.url}")
public interface FlightClient {

    @PostMapping("/api/flights/book")
    FlightBookingResponse book(@RequestBody FlightBookingRequest request);

    // TODO: implementar cancelación de vuelo para la compensación SAGA
    @DeleteMapping("/api/flights/cancel")
    FlightBookingResponse cancel(@RequestBody FlightBookingRequest request);
}
