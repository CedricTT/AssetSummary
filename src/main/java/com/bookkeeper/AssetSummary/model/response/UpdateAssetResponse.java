package com.bookkeeper.AssetSummary.model.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateAssetResponse extends BaseResponse {
    Double currentBalance;
}
