package com.bookkeeper.AssetSummary.model.response;

import com.bookkeeper.AssetSummary.model.entity.Asset;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UpdateAssetResponse extends BaseResponse {

    Asset assetFrom;

    Asset assetTo;

    Double transactionValue;
}
