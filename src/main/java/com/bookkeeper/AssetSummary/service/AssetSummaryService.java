package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.feign.RecordFeignClient;
import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.RecordDTO;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.exception.HttpException;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.model.response.AssetResponse;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public AssetDTO updateAsset(RecordDTO request) {
        return null;
    }

    public AssetDTO createAsset(AssetDTO request) {

        assetRepository.findByName(request.getName()).ifPresent(s -> {
            throw new AssetAlreadyExisting("0030","Asset Already exist in given period of time");
        });

        Asset asset = assetRepository.save(assetMapper.convertToEntity(request));

        return assetMapper.convertToDto(asset);
    }

    public AssetResponse getAssetByName(String assetName) {

        AssetResponse assetResponse = new AssetResponse();

        Asset asset = assetRepository.findByName(assetName).
                orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));

        ResponseEntity<List<RecordDTO>> recordResponse = recordFeignClient.readAssetRecordByName(assetName);

        if(HttpStatus.INTERNAL_SERVER_ERROR == recordResponse.getStatusCode())
            throw new HttpException("0041", "Error occurs when calling record service");

        List<RecordDTO> queryRecord = recordFeignClient.readAssetRecordByName(assetName).getBody();

        assetResponse.setAssetDTO(assetMapper.convertToDto(asset));
        assetResponse.setSpending(queryRecord != null ? queryRecord.stream().mapToDouble(RecordDTO::getAmount).sum() : 0);

        return assetResponse;
    }
}
