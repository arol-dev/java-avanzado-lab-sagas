package com.example.travelagency.client;

import com.example.travelagency.dto.BookingDtos;
import com.example.travelagency.dto.BookingDtos.FlightBookingRequest;
import com.example.travelagency.dto.BookingDtos.FlightBookingResponse;
import com.example.travelagency.dto.BookingDtos.FlightCancelingRequest;
import com.example.travelagency.dto.BookingDtos.FlightCancelingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "flight-service", url = "${services.flight.url}")
public interface FlightClient {

    @PostMapping("/api/flights/book")
    FlightBookingResponse book(@RequestBody FlightBookingRequest request);

    @PostMapping("/api/flights/cancel")
    FlightCancelingResponse cancel(@RequestBody FlightCancelingRequest request);
}
