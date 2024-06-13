package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.model.dto.*;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.*;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AssetSummaryService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetMapper assetMapper;

    public void updateAsset(HashMap<String, Object> message) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String UID = (String) message.get("uid");
        if(UID == null || UID.isEmpty())
            throw new ForbiddenException("999", "Missing user info");

        PaymentDTO request = mapper.convertValue(message.get("request_record"), PaymentDTO.class);
        if(request == null || request.getPaymentFrom() == null || request.getPaymentTo() == null)
            throw new GlobalException("0203", "Invalid request");

        if(message.get("reverse_record") != null) {
            PaymentDTO reverse_record = (PaymentDTO) message.get("reverse_record");
            reverseAsset(reverse_record, request, UID);
            return;
        }

        Optional<Asset> assetFrom = assetRepository.findByNameAndUID(request.getPaymentFrom(), UID);
        assetFrom.ifPresent(asset -> {
            asset.setBalance(asset.getBalance() - (request.getEstimateValue() != null ? request.getEstimateValue() : request.getAmount()));
            assetRepository.save(asset);
            log.info("Updating asset: {}", asset.getName());
        });

        Optional<Asset> assetTo = assetRepository.findByNameAndUID(request.getPaymentTo(), UID);
        assetTo.ifPresent(asset -> {
            asset.setBalance(asset.getBalance() + (request.getEstimateValue() != null ? request.getEstimateValue() : request.getAmount()));
            assetRepository.save(asset);
            log.info("Updating asset: {}", asset.getName());
        });
    }

    public AssetDTO createAsset(String uid, String email, AssetDTO request) {

        assetRepository.findByNameAndUID(request.getName(), uid).ifPresent(s -> {
            throw new AssetAlreadyExisting("0200","Asset Already exist");
        });

        Asset asset = assetMapper.convertToEntity(request);
        asset.setUID(uid);
        asset.setEmail(email);

        return assetMapper.convertToDto(assetRepository.save(asset));
    }

    public AssetDTO getAssetByName(String assetName) {

        Asset asset = assetRepository.findByName(assetName).
                orElseThrow(() -> new AssetNotFound("0202", "Asset Not Found in given record"));

        return assetMapper.convertToDto(asset);
    }

    public List<AssetDTO> getAsset(String userUID, String userEmail) {

        List<Asset> assetList = assetRepository.findByEmailAndUID(userEmail, userUID).orElseThrow(
                () -> new AssetNotFound("0201", "No record found"));

        return assetMapper.convertToDtoList(assetList);
    }

    private void reverseAsset(PaymentDTO reverseRecord, PaymentDTO request, String UID) {

        if(reverseRecord.getPaymentFrom() == null || reverseRecord.getPaymentTo() == null)
            throw new GlobalException("0203", "Invalid request");

        if(!reverseRecord.getPaymentFrom().equals(request.getPaymentTo()))
            throw new GlobalException("0203", "Invalid reverse request");

        Optional<Asset> assetFrom = assetRepository.findByNameAndUID(request.getPaymentFrom(), UID);
        assetFrom.ifPresentOrElse(asset -> {
            asset.setBalance(asset.getBalance() + reverseRecord.getAmount() - request.getAmount());
            assetRepository.save(asset);
            log.info("Reverse asset: {}", asset.getName());
        }, () -> {
            throw new GlobalException("0203", "Invalid reverse request");
        });

        Optional<Asset> assetTo = assetRepository.findByNameAndUID(request.getPaymentTo(), UID);
        assetTo.ifPresentOrElse(asset -> {
            asset.setBalance(asset.getBalance() - reverseRecord.getAmount() + request.getAmount());
            assetRepository.save(asset);
            log.info("Reverse asset: {}", asset.getName());
        }, () -> {
            throw new GlobalException("0203", "Invalid reverse request");
        });
    }

    public void cancelTransaction(HashMap<String, Object> message) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        PaymentDTO request = (PaymentDTO) message.get("cancel");
        String UID = (String) message.get("uid");

        Optional<Asset> assetFrom = assetRepository.findByNameAndUID(request.getPaymentFrom(), UID);
        assetFrom.ifPresentOrElse(asset -> {
            asset.setBalance(asset.getBalance() - request.getAmount());
            assetRepository.save(asset);
            log.info("Cancel transaction for asset: {}", asset.getName());
        }, () -> {
            throw new GlobalException("0203", "Invalid reverse request");
        });

        Optional<Asset> assetTo = assetRepository.findByNameAndUID(request.getPaymentTo(), UID);
        assetTo.ifPresentOrElse(asset -> {
            asset.setBalance(asset.getBalance() + request.getAmount());
            assetRepository.save(asset);
            log.info("Cancel transaction for asset: {}", asset.getName());
        }, () -> {
            throw new GlobalException("0203", "Invalid reverse request");
        });
    }
}
