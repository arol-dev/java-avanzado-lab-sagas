package com.example.travelagency.client;

import com.example.travelagency.dto.BookingDtos.FlightBookingRequest;
import com.example.travelagency.dto.BookingDtos.FlightBookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "flight-service", url = "${services.flight.url}")
public interface FlightClient {

    @PostMapping("/api/flights/book")
    FlightBookingResponse book(@RequestBody FlightBookingRequest request, @RequestHeader(value = "sagaId", required = true) String sagaId);

    @PostMapping("/api/flights/cancel")
    FlightBookingResponse cancel(@RequestBody FlightBookingRequest request);
}
