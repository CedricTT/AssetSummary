package com.bookkeeper.AssetSummary.client;

import com.bookkeeper.AssetSummary.model.response.PaymentRecordResponse;
import org.hibernate.validator.constraints.Range;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("PAYMENT-RECORD")
public interface PaymentRecordFeignClient {

    @GetMapping(value = "/api/v1/paymentRecord/{year}/{month}")
    ResponseEntity<PaymentRecordResponse> query(@PathVariable(value = "year") int year,
                                                @PathVariable(value = "month") @Range(min = 1, max = 12) int month,
                                                @RequestParam(value = "assetName", required = false) String assetName);
}
