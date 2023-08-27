package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.FutureDateCreation;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(MockitoAnnotations.openMocks(this));
    }

    @Test
    void testCreateAsset() {
        AssetDTO assetDTO = createAssetDTO("Bank", LocalDate.now(), 10000.0, 0.0);

        Asset asset = createAsset("Bank", LocalDate.now(), 10000.0, 0.0);

        Mockito.when(assetMapper.convertToEntity(assetDTO)).thenReturn(asset);
        Mockito.when(assetRepository.save(asset)).thenReturn(asset);
        Mockito.when(assetMapper.convertToDto(asset)).thenReturn(assetDTO);

        assertEquals(assetDTO, assetSummaryService.createAsset(assetDTO));
    }

    @Test
    void testExistingCreation() {
        AssetDTO assetDTO = createAssetDTO("Bank", LocalDate.now(), 10000.0, 0.0);

        Asset asset = createAsset("Bank", LocalDate.now(), 10000.0, 0.0);

        Mockito.when(assetRepository.findByNameAndDate("Bank", LocalDate.now())).thenReturn(Optional.of(asset));

        Exception thrown = assertThrows(
                AssetAlreadyExisting.class,
                () -> assetSummaryService.createAsset(assetDTO),
                "Asset Already exist in given period of time"
        );

        assertEquals("0030", thrown.getMessage());
    }

    @Test
    void testCreatingFutureDateAsset() {
        AssetDTO assetDTO = createAssetDTO("Bank", LocalDate.now().plusDays(1), 10000.0, 0.0);

        Exception thrown = assertThrows(
                FutureDateCreation.class,
                () -> assetSummaryService.createAsset(assetDTO),
                "Creating future date asset"
        );

        assertEquals("0031", thrown.getMessage());
    }

    @Test
    void testUpdatingAsset() {

    }

    @Test
    void testGetUTDAsset() {

        String[] assetNameList = new String[]{"Bank", "Credit1", "Credit2"};
        List<AssetDTO> assetDTOList = new ArrayList<>();

        assetDTOList.add(createAssetDTO("Bank", LocalDate.now(), 10000.0, 0.0));
        assetDTOList.add(createAssetDTO("Credit1", LocalDate.now(), 0.0, 500.0));
        assetDTOList.add(createAssetDTO("Credit2", LocalDate.now(), 0.0, 3000.0));

        List<Asset> assetList = new ArrayList<>();

        assetList.add(createAsset("Bank", LocalDate.now(), 10000.0, 0.0));
        assetList.add(createAsset("Credit1", LocalDate.now(), 0.0, 500.0));
        assetList.add(createAsset("Credit2", LocalDate.now(), 0.0, 3000.0));

        Mockito.when(assetRepository.finyByAssetName(assetNameList)).thenReturn(Optional.of(assetList));
        Mockito.when(assetMapper.convertToDtoList(assetList)).thenReturn(assetDTOList);

        assertEquals(assetDTOList, assetSummaryService.getUTDAsset(assetNameList));
    }

    private AssetDTO createAssetDTO(String name, LocalDate date, Double credit, Double debit) {
        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setName(name);
        assetDTO.setDate(date);
        assetDTO.setCredit(credit);
        assetDTO.setDebit(debit);
        return assetDTO;
    }

    private Asset createAsset(String name, LocalDate date, Double credit, Double debit) {
        Asset asset = new Asset();
        asset.setName(name);
        asset.setDate(date);
        asset.setCredit(credit);
        asset.setDebit(debit);
        return asset;
    }
}