package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.FutureDateCreation;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

        if(LocalDate.now().isBefore(request.getDate()))
            throw new FutureDateCreation("Creating future date asset", "0031");

        assetRepository.findByNameAndDate(request.getName(), request.getDate()).ifPresent(s -> {
            throw new AssetAlreadyExisting("Asset Already exist in given period of time", "0030");
        });

        Asset asset = assetRepository.save(assetMapper.convertToEntity(request));

        return assetMapper.convertToDto(asset);
    }

    public List<AssetDTO> getUTDAsset(String[] assetNameList) {

        List<Asset> UTDAsset = assetRepository.finyByAssetName(assetNameList).get();

        return assetMapper.convertToDtoList(UTDAsset);
    }
}
