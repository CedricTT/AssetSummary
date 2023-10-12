package com.bookkeeper.AssetSummary.model.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UpdateAssetResponse extends BaseResponse {
    Double currentBalance;
}
