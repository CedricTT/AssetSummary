package com.bookkeeper.AssetSummary.model.dto;

import com.bookkeeper.AssetSummary.model.entity.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdatedAsset {

    Asset assetFrom;

    Asset assetTo;

    Double transactionValue;
}
