package com.example.travelagency.client;

import com.example.travelagency.dto.BookingDtos.HotelBookingRequest;
import com.example.travelagency.dto.BookingDtos.HotelBookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "hotel-service", url = "${services.hotel.url}")
public interface HotelClient {

    @PostMapping("/api/hotels/book")
    HotelBookingResponse book(@RequestBody HotelBookingRequest request);

    // TODO: implementar cancelación de hotel para la compensación SAGA
}
