package com.example.travelagency.client;

import com.example.travelagency.dto.BookingDtos.HotelBookingRequest;
import com.example.travelagency.dto.BookingDtos.HotelBookingResponse;
import com.example.travelagency.dto.BookingDtos.HotelCancelingRequest;
import com.example.travelagency.dto.BookingDtos.HotelCancelingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hotel-service", url = "${services.hotel.url}")
public interface HotelClient {

    @PostMapping("/api/hotels/book")
    HotelBookingResponse book(@RequestBody HotelBookingRequest request, @RequestParam(name = "fail", required = false) Integer failCode);

    @PostMapping("/api/hotels/cancel")
    HotelCancelingResponse cancel(@RequestBody HotelCancelingRequest request);
}
