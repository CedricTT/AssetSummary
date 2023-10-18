package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.client.RecordFeignClient;
import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.TransactionRecord;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Slf4j
public class AssetSummaryService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private RecordFeignClient recordFeignClient;

    public AssetDTO updateAsset(TransactionRecord request) {

        Asset requestedAsset = assetRepository.findByName(request.getAssetName())
                .orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));
        requestedAsset.setBalance(requestedAsset.getBalance() + request.getAmount());
        assetRepository.save(requestedAsset);

        return assetMapper.convertToDto(requestedAsset);
    }

    public AssetDTO createAsset(AssetDTO request) {

        assetRepository.findByName(request.getName()).ifPresent(s -> {
            throw new AssetAlreadyExisting("0030","Asset Already exist in given period of time");
        });

        Asset asset = assetRepository.save(assetMapper.convertToEntity(request));

        return assetMapper.convertToDto(asset);
    }

    public AssetDTO getAssetByName(String assetName) {

        Asset asset = assetRepository.findByName(assetName).
                orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));

        return assetMapper.convertToDto(asset);
    }
}
