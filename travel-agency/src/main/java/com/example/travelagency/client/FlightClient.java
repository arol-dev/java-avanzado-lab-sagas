package com.example.travelagency.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.travelagency.dto.BookingDtos.FlightBookingRequest;
import com.example.travelagency.dto.BookingDtos.FlightBookingResponse;

@FeignClient(name = "flight-service", url = "${services.flight.url}")
public interface FlightClient {

    @PostMapping("/api/flights/book")
    FlightBookingResponse book(@RequestBody FlightBookingRequest request);

    @PostMapping("/api/flights/cancel")
    void cancel(@RequestBody FlightBookingRequest request);

}
