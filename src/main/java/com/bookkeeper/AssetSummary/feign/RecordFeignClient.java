package com.bookkeeper.AssetSummary.feign;

import com.bookkeeper.AssetSummary.model.dto.RecordDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("RECORDS")
public interface RecordFeignClient {

    @GetMapping(value = "/api/v1/record/assetRecord")
    ResponseEntity<List<RecordDTO>> readAssetRecordByName(@RequestParam String assetName);
}
