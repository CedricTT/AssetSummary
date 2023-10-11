package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.TransactionRecord;
import com.bookkeeper.AssetSummary.model.response.AssetResponse;
import com.bookkeeper.AssetSummary.model.response.UpdateAssetResponse;
import com.bookkeeper.AssetSummary.service.AssetSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/assetSummary")
@RequiredArgsConstructor
public class AssetSummaryController {

    private final AssetSummaryService assetSummaryService;

    @CrossOrigin
    @PostMapping(value = "/update")
    public ResponseEntity<UpdateAssetResponse> updateAsset(@Valid @RequestBody TransactionRecord request) {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        AssetDTO updatedAsset = assetSummaryService.updateAsset(request);
        UpdateAssetResponse updateAssetResponse = UpdateAssetResponse.builder()
                .currentBalance(updatedAsset.getBalance())
                .requestTime(request.getRequestTime())
                .status("SUCCESS")
                .build();

        return new ResponseEntity<>(updateAssetResponse, httpHeaders, HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping(value = "/create")
    public ResponseEntity<AssetDTO> createAsset(@Valid @RequestBody AssetDTO request) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("Creating new asset: {}", request.toString());
        return new ResponseEntity<>(assetSummaryService.createAsset(request), httpHeaders, HttpStatus.OK);
    }

    @GetMapping(value = "/asset", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AssetResponse> getAssetByName(@RequestParam String assetName) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("Getting asset: {}", assetName);
        AssetResponse assetResponse = assetSummaryService.getAssetByName(assetName);
        return new ResponseEntity<>(assetResponse, httpHeaders, HttpStatus.OK);
    }
}
