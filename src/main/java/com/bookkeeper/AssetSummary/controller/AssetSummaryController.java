package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.service.AssetSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/assetsummary")
@RequiredArgsConstructor
public class AssetSummaryController {

    private final AssetSummaryService assetSummaryService;

    @CrossOrigin
    @PostMapping(value = "/update")
    public ResponseEntity updateAsset(@RequestBody AssetDTO request) {
        return ResponseEntity.ok(assetSummaryService.updateAsset(request));
    }

    @CrossOrigin
    @PostMapping(value = "/create")
    public ResponseEntity createAsset(@RequestBody AssetDTO request) {
        return ResponseEntity.ok(assetSummaryService.createAsset(request));
    }
}
