package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AssetSummaryService {

    public AssetDTO updateAsset(AssetDTO request) {
        return request;
    }
}
