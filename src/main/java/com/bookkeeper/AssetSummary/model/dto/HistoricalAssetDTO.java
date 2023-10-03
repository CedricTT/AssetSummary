package com.bookkeeper.AssetSummary.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoricalAssetDTO {

    String name;

    AssetDTO[] assetDTOS;
}
