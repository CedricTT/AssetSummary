package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.client.PaymentRecordFeignClient;
import com.bookkeeper.AssetSummary.model.dto.*;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.exception.ExternalSystemException;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.model.response.PaymentRecordResponse;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class AssetSummaryService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private PaymentRecordFeignClient paymentRecordFeignClient;

    public UpdatedAsset updateAsset(PaymentDTO request, String UID) {

        UpdatedAsset.UpdatedAssetBuilder updatedAsset = UpdatedAsset.builder();

        Optional<Asset> assetFrom = assetRepository.findByNameAndUID(request.getPaymentFrom(), UID);
        assetFrom.ifPresent(asset -> {
            asset.setBalance(asset.getBalance() - request.getAmount());
            assetRepository.save(asset);
            updatedAsset.assetFrom(assetMapper.convertToDto(asset));
            log.info("Updating asset: {}", asset.getName());
        });

        Optional<Asset> assetTo = assetRepository.findByNameAndUID(request.getPaymentTo(), UID);
        assetTo.ifPresent(asset -> {
            asset.setBalance(asset.getBalance() + request.getAmount());
            assetRepository.save(asset);
            updatedAsset.assetTo(assetMapper.convertToDto(asset));
            log.info("Updating asset: {}", asset.getName());
        });

        updatedAsset.transactionValue(request.getAmount());

        return updatedAsset.build();
    }

    public AssetDTO createAsset(String uid, String email, AssetDTO request) {

        assetRepository.findByNameAndUID(request.getName(), uid).ifPresent(s -> {
            throw new AssetAlreadyExisting("0030","Asset Already exist");
        });

        Asset asset = assetMapper.convertToEntity(request);
        asset.setUID(uid);
        asset.setEmail(email);

        return assetMapper.convertToDto(assetRepository.save(asset));
    }

    public AssetDTO getAssetByName(String assetName) {

        Asset asset = assetRepository.findByName(assetName).
                orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));

        return assetMapper.convertToDto(asset);
    }

    public AssetSummary getAssetSummary(String assetName) {

        Asset asset = assetRepository.findByName(assetName).
                orElseThrow(() -> new AssetNotFound("0040", "Asset Not Found in given record"));

        ResponseEntity<PaymentRecordResponse> paymentRecordResponse = paymentRecordFeignClient.query(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth().getValue(),
                assetName);

        if(paymentRecordResponse.getStatusCode() != HttpStatus.OK)
            throw new ExternalSystemException("0001", "Fail on external system call");

        List<PaymentDTO> assetPaymentRecord = Objects.requireNonNull(paymentRecordResponse.getBody()).getQueryPaymentRecord();
        double spending = 0.0;
        if(!assetPaymentRecord.isEmpty())
            spending = assetPaymentRecord.stream().filter(p -> p.getPaymentFrom().equals(assetName)).mapToDouble(PaymentDTO::getAmount).sum();

        return AssetSummary
                .builder()
                .assetDTO(assetMapper.convertToDto(asset))
                .speeding(spending)
                .build();
    }

    public List<AssetDTO> getAsset(String userUID, String userEmail) {

        List<Asset> assetList = assetRepository.findByEmailAndUID(userEmail, userUID).orElseThrow(
                () -> new AssetNotFound("0050", "No record found"));

        return assetMapper.convertToDtoList(assetList);
    }
}
