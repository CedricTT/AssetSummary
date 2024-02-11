package com.bookkeeper.AssetSummary.model.response;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AssetResponse extends BaseResponse{

    List<AssetDTO> asset;
}
