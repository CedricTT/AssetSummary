package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.feign.RecordFeignClient;
import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.RecordDTO;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.model.response.AssetResponse;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class AssetSummaryService {

    public AssetSummaryService(AssetRepository assetRepository, AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
    }

    private final AssetRepository assetRepository;

    private final AssetMapper assetMapper;

    @Autowired
    private RecordFeignClient recordFeignClient;

//    public AssetDTO updateAsset(RecordDTO request) {
//        Double amount = request.getAmount();
//        Asset utdAsset = assetRepository.findTopByNameOrderByDate(request.getPaymentMethod())
//                .orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));
//
//        return assetMapper.convertToDto(assetRepository.save(utdAsset));
//    }

    public AssetDTO createAsset(AssetDTO request) {

        assetRepository.findByName(request.getName()).ifPresent(s -> {
            throw new AssetAlreadyExisting("0030","Asset Already exist in given period of time");
        });

        Asset asset = assetRepository.save(assetMapper.convertToEntity(request));

        return assetMapper.convertToDto(asset);
    }

//    public List<AssetDTO> getLatestAsset(String[] assetNameList) {
//
//        List<Asset> UTDAsset = new ArrayList<>();
//
//        for(String name : assetNameList)
//            UTDAsset.add(assetRepository.findTopByNameOrderByDate(name)
//                    .orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record")));
//
//        return assetMapper.convertToDtoList(UTDAsset);
//    }

    public AssetResponse getAssetByName(String assetName) {

        AssetResponse assetResponse = new AssetResponse();

        Asset asset = assetRepository.findByName(assetName).
                orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));

        List<RecordDTO> queryRecord = recordFeignClient.readAssetRecordByName(assetName).getBody();

        assetResponse.setAssetDTO(assetMapper.convertToDto(asset));
        assetResponse.setSpending(queryRecord.stream().mapToDouble(RecordDTO::getAmount).sum());

        return assetResponse;
    }
}
