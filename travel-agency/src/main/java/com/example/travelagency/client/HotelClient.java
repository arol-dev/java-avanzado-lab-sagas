package com.example.travelagency.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.travelagency.dto.BookingDtos.HotelBookingRequest;
import com.example.travelagency.dto.BookingDtos.HotelBookingResponse;

@FeignClient(name = "hotel-service", url = "${services.hotel.url}")
public interface HotelClient {

    @PostMapping("/api/hotels/book")
    HotelBookingResponse book(@RequestBody HotelBookingRequest request);

    @PostMapping("/api/hotels/cancel")
    void cancel(@RequestBody HotelBookingRequest request);
}
