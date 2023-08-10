package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import org.springframework.stereotype.Service;

@Service
public class AssetSummaryService {

    public AssetSummaryService(AssetRepository assetRepository, AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
    }

    private final AssetRepository assetRepository;

    private final AssetMapper assetMapper;

    public AssetDTO updateAsset(AssetDTO request) {
        return request;
    }

    public AssetDTO createAsset(AssetDTO request) {
        Asset asset = assetRepository.save(assetMapper.convertToEntity(request));

        return assetMapper.convertToDto(asset);
    }
}
