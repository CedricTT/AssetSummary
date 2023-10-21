package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.client.PaymentRecordFeignClient;
import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.AssetSummary;
import com.bookkeeper.AssetSummary.model.dto.PaymentDTO;
import com.bookkeeper.AssetSummary.model.dto.TransactionRecord;
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

@Service
@Slf4j
public class AssetSummaryService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private PaymentRecordFeignClient paymentRecordFeignClient;

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
}
