package com.bookkeeper.AssetSummary.model.mapper;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import org.springframework.beans.BeanUtils;

public class AssetMapper extends BaseMapper<Asset, AssetDTO> {

    @Override
    public Asset convertToEntity(AssetDTO dto) {
        Asset asset = new Asset();

        if(dto != null)
            BeanUtils.copyProperties(dto, asset);

        return asset;
    }

    @Override
    public AssetDTO convertToDto(Asset entity) {
        AssetDTO AssetDTO = new AssetDTO();

        if(entity != null)
            BeanUtils.copyProperties(entity, AssetDTO);

        return AssetDTO;
    }
}
