package com.example.travelagency.client;

import com.example.travelagency.dto.BookingDtos.ChargeRequest;
import com.example.travelagency.dto.BookingDtos.ChargeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "billing-service", url = "${services.billing.url}")
public interface BillingClient {

    @PostMapping("/api/billing/charge")
    ChargeResponse charge(@RequestBody ChargeRequest request, @RequestParam(name = "fail", required = false) Integer failCode);
}
