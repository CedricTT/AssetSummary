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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AssetSummaryServiceTest {

    @InjectMocks
    private AssetSummaryService assetSummaryService;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private RecordFeignClient recordFeignClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(MockitoAnnotations.openMocks(this));
    }

    @Test
    void testCreateAsset() {
        AssetDTO assetDTO = new AssetDTO("Bank","bank", 10000.0);

        Asset asset = createAsset("Bank","bank", 10000.0);

        Mockito.when(assetMapper.convertToEntity(assetDTO)).thenReturn(asset);
        Mockito.when(assetRepository.save(asset)).thenReturn(asset);
        Mockito.when(assetMapper.convertToDto(asset)).thenReturn(assetDTO);

        assertEquals(assetDTO, assetSummaryService.createAsset(assetDTO));
    }

    @Test
    void testExistingCreation() {
        AssetDTO assetDTO = new AssetDTO("Bank","bank", 10000.0);

        Asset asset = createAsset("Bank","bank", 10000.0);

        Mockito.when(assetRepository.findByName("Bank")).thenReturn(Optional.of(asset));

        Exception thrown = assertThrows(
                AssetAlreadyExisting.class,
                () -> assetSummaryService.createAsset(assetDTO),
                "Asset Already exist in given period of time"
        );

        assertEquals("Asset Already exist in given period of time", thrown.getMessage());
    }

    @Test
    void testGetAssetSuccess() {

        String assetName = "Bank";
        Double spending = 5000.0;
        Asset bankAsset = createAsset("Bank", "bank", 100000.0);
        AssetDTO bankAssetDTO = new AssetDTO("Bank", "bank", 100000.0);
        List<RecordDTO> recordDTOList = new ArrayList<>();
        recordDTOList.add(new RecordDTO("Bank exp 1", "test", "FPS", LocalDate.now(), 2500.0, "Bank", "Other"));
        recordDTOList.add(new RecordDTO("Bank exp 2", "test", "FPS", LocalDate.now(), 2500.0, "Bank", "Other"));
        when(recordFeignClient.readAssetRecordByName(assetName)).thenReturn(new ResponseEntity<>(recordDTOList, HttpStatus.OK));

        AssetResponse assetResponse = new AssetResponse(bankAssetDTO, spending);

        when(assetRepository.findByName("Bank")).thenReturn(Optional.of(bankAsset));
        when(assetMapper.convertToDto(bankAsset)).thenReturn(bankAssetDTO);

        assertEquals(assetResponse, assetSummaryService.getAssetByName(assetName));
    }

    @Test
    void testGetAssetNotFound() {

        String assetName = "Test";

        Mockito.when(assetRepository.findByName(assetName)).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                AssetNotFound.class,
                () -> assetSummaryService.getAssetByName(assetName),
                "Asset Not Found in given record"
        );

        assertEquals("Asset Not Found in given record", thrown.getMessage());
    }

    @Test
    void testGetAssetRecordException() {
        String assetName = "Bank";
        Asset bankAsset = createAsset("Bank", "bank", 100000.0);

        when(assetRepository.findByName(assetName)).thenReturn(Optional.of(bankAsset));
        when(recordFeignClient.readAssetRecordByName(assetName)).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        Exception thrown = assertThrows(
            HttpException.class,
                () -> assetSummaryService.getAssetByName(assetName),
                "Error occurs when calling record service"
        );

        assertEquals("Error occurs when calling record service", thrown.getMessage());
    }

    @Test
    void testUpdateAssetSuccess() {

        String assetName = "bank";
        RecordDTO recordDTO = new RecordDTO("test", "test", "test", LocalDate.now(), 10000.0, "bank", "test");
        Asset asset = createAsset("bank", "bank account", 30000.0);
        when(assetRepository.findByName(assetName)).thenReturn(Optional.of(asset));


    }

    private Asset createAsset(String name, String type, Double balance) {
        Asset asset = new Asset();
        asset.setName(name);
        asset.setType(type);
        asset.setBalance(balance);
        asset.setCreated_Date(LocalDateTime.now());
        asset.setUpdated_Date(LocalDateTime.now());
        return asset;
    }
}