package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.RecordDTO;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.exception.FutureDateCreation;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public AssetDTO updateAsset(RecordDTO request) {
        Double amount = request.getAmount();
        Asset utdAsset = assetRepository.findTopByNameOrderByDate(request.getPaymentMethod())
                .orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));

        utdAsset.setDebit(utdAsset.getDebit() + amount);

        return assetMapper.convertToDto(assetRepository.save(utdAsset));
    }

    public AssetDTO createAsset(AssetDTO request) {

        if(LocalDate.now().isBefore(request.getDate()))
            throw new FutureDateCreation("0031","Creating future date asset");

        assetRepository.findByNameAndDate(request.getName(), request.getDate()).ifPresent(s -> {
            throw new AssetAlreadyExisting("0030","Asset Already exist in given period of time");
        });

        Asset asset = assetRepository.save(assetMapper.convertToEntity(request));

        return assetMapper.convertToDto(asset);
    }

    public List<AssetDTO> getLatestAsset(String[] assetNameList) {

        List<Asset> UTDAsset = new ArrayList<>();

        for(String name : assetNameList)
            UTDAsset.add(assetRepository.findTopByNameOrderByDate(name)
                    .orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record")));

        return assetMapper.convertToDtoList(UTDAsset);
    }

//    public List<List<AssetDTO>> getHistoryAsset(String[] assetNameList) {
//
//        List<List<AssetDTO>> historyAssetList = new ArrayList<>();
//
//        log.info(assetRepository.findByName(assetNameList[0]).toString());
//
//        for(String name : assetNameList)
//            historyAssetList.add(assetMapper
//                    .convertToDtoList(assetRepository.findByName(name)
//                    .orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"))));
//
//        return historyAssetList;
//    }

    public AssetDTO getAssetByName(String assetName) {

        Asset asset = assetRepository.findByName(assetName).
                orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));

        return assetMapper.convertToDto(asset);
    }
}
