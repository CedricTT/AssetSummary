package com.bookkeeper.AssetSummary.model.response;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UpdateAssetResponse extends BaseResponse {

    AssetDTO assetFrom;

    AssetDTO assetTo;

    Double transactionValue;
}
