package com.bookkeeper.AssetSummary.model.response;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AssetSummaryResponse extends BaseResponse {

    AssetDTO assetDTO;
    int speeding;
}
