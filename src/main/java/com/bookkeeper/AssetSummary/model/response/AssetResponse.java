package com.bookkeeper.AssetSummary.model.response;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AssetResponse {

    AssetDTO assetDTO;
    Double Spending;
}
