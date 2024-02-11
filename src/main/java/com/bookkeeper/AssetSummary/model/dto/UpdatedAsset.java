package com.bookkeeper.AssetSummary.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdatedAsset {

    AssetDTO assetFrom;

    AssetDTO assetTo;

    Double transactionValue;
}
