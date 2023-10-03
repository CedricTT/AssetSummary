package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.RecordDTO;
import com.bookkeeper.AssetSummary.service.AssetSummaryService;
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
    public ResponseEntity<AssetDTO> updateAsset(@RequestBody RecordDTO request) {
        return ResponseEntity.ok(assetSummaryService.updateAsset(request));
    }

    @CrossOrigin
    @PostMapping(value = "/create")
    public ResponseEntity<AssetDTO> createAsset(@RequestBody AssetDTO request) {
        log.info("Creating new asset: {}", request.toString());
        return ResponseEntity.ok(assetSummaryService.createAsset(request));
    }

//    @CrossOrigin
//    @GetMapping(value = "/asset")
//    public ResponseEntity<Map<String, Object>> getAsset(@RequestParam String[] asset_names) {
//        Map<String, Object> responseMap = new HashMap<>();
//        log.info("Getting asset name: {}", Arrays.toString(asset_names));
//        List<AssetDTO> latestAsset = assetSummaryService.getLatestAsset(asset_names);
//        responseMap.put("CurrentMonth", latestAsset);
//        List<List<AssetDTO>> historyAsset = assetSummaryService.getHistoryAsset(asset_names);
//        responseMap.put("HistoryData", historyAsset);
//        return ResponseEntity.ok(responseMap);
//    }

    @GetMapping(value = "/asset", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AssetDTO> getAssetByName(@RequestParam String assetName) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("Getting asset: {}", assetName);
        AssetDTO assetResponse = assetSummaryService.getAssetByName(assetName);
        return new ResponseEntity<>(assetResponse, httpHeaders, HttpStatus.OK);
    }
}
