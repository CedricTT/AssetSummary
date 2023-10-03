package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.RecordDTO;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(MockitoAnnotations.openMocks(this));
    }

    @Test
    void testCreateAsset() {
        AssetDTO assetDTO = createAssetDTO("Bank", LocalDate.now(), 10000.0, 0.0);

        Asset asset = createAsset("Bank", LocalDate.now(), "bank", 10000.0, 0.0, 10000.0);

        Mockito.when(assetMapper.convertToEntity(assetDTO)).thenReturn(asset);
        Mockito.when(assetRepository.save(asset)).thenReturn(asset);
        Mockito.when(assetMapper.convertToDto(asset)).thenReturn(assetDTO);

        assertEquals(assetDTO, assetSummaryService.createAsset(assetDTO));
    }

    @Test
    void testExistingCreation() {
        AssetDTO assetDTO = createAssetDTO("Bank", LocalDate.now(), 10000.0, 0.0);

        Asset asset = createAsset("Bank", LocalDate.now(), "bank", 10000.0, 0.0, 10000.0);

        Mockito.when(assetRepository.findByNameAndDate("Bank", LocalDate.now())).thenReturn(Optional.of(asset));

        Exception thrown = assertThrows(
                AssetAlreadyExisting.class,
                () -> assetSummaryService.createAsset(assetDTO),
                "Asset Already exist in given period of time"
        );

        assertEquals("Asset Already exist in given period of time", thrown.getMessage());
    }

    @Test
    void testCreatingFutureDateAsset() {
        AssetDTO assetDTO = createAssetDTO("Bank", LocalDate.now().plusDays(1), 10000.0, 0.0);

        Exception thrown = assertThrows(
                FutureDateCreation.class,
                () -> assetSummaryService.createAsset(assetDTO),
                "Creating future date asset"
        );

        assertEquals("Creating future date asset", thrown.getMessage());
    }

    @Test
    void testUpdatingAsset() {
        RecordDTO recordDTO = new RecordDTO("test", "Food", "Credit1", LocalDate.now(), 100.0);
        AssetDTO assetDTO = createAssetDTO("Credit1", LocalDate.now(), 0.0, 200.0);
        Asset asset = createAsset("Credit1", LocalDate.now(), "credit card", 0.0, 100.0, -100.0);

        Mockito.when(assetRepository.findTopByNameOrderByDate(recordDTO.getPaymentMethod())).thenReturn(Optional.of(asset));
        asset.setDebit(asset.getDebit() + recordDTO.getAmount());
        Mockito.when(assetRepository.save(asset)).thenReturn(asset);
        Mockito.when(assetMapper.convertToDto(asset)).thenReturn(assetDTO);

        assertEquals(assetDTO, assetSummaryService.updateAsset(recordDTO));
    }

    @Test
    void testUpdatingAssetNotFoundAsset() {
        RecordDTO recordDTO = new RecordDTO("test", "Food", "Credit124", LocalDate.now(), 100.0);

        Mockito.when(assetRepository.findTopByNameOrderByDate(recordDTO.getPaymentMethod())).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                AssetNotFound.class,
                () -> assetSummaryService.updateAsset(recordDTO),
                "Asset Not Found in given record"
        );

        assertEquals("Asset Not Found in given record", thrown.getMessage());
    }

    @Test
    void testGetLatestAsset() {

        String[] assetNameList = new String[]{"Bank", "Credit1", "Credit2"};
        List<AssetDTO> assetDTOList = new ArrayList<>();
        List<Asset> assetList = new ArrayList<>();

        AssetDTO bankDTO = createAssetDTO("Bank", LocalDate.now(), 10000.0, 0.0);
        AssetDTO credit1DTO = createAssetDTO("Credit1", LocalDate.now(), 0.0, 500.0);
        AssetDTO credit2DTO = createAssetDTO("Credit2", LocalDate.now(), 0.0, 3000.0);
        assetDTOList.add(bankDTO);
        assetDTOList.add(credit1DTO);
        assetDTOList.add(credit2DTO);

        Asset bank = createAsset("Bank", LocalDate.now(), "bank", 10000.0, 0.0, 10000.0);
        Asset credit1 = createAsset("Credit1", LocalDate.now(), "credit card", 0.0, 500.0, -500.0);
        Asset credit2 = createAsset("Credit2", LocalDate.now(), "credit card", 0.0, 3000.0, -3000.0);
        assetList.add(bank);
        assetList.add(credit1);
        assetList.add(credit2);

        Mockito.when(assetRepository.findTopByNameOrderByDate(assetNameList[0])).thenReturn(Optional.of(bank));
        Mockito.when(assetRepository.findTopByNameOrderByDate(assetNameList[1])).thenReturn(Optional.of(credit1));
        Mockito.when(assetRepository.findTopByNameOrderByDate(assetNameList[2])).thenReturn(Optional.of(credit2));
        Mockito.when(assetMapper.convertToDtoList(assetList)).thenReturn(assetDTOList);

        assertEquals(assetDTOList, assetSummaryService.getLatestAsset(assetNameList));
    }

    @Test
    void testGetLatestAssetNotFound() {
        String[] assetNameList = new String[]{"Bank", "Credit1", "Credit1234"};
        Asset bank = createAsset("Bank", LocalDate.now(), "bank", 10000.0, 0.0, 10000.0);
        Asset credit1 = createAsset("Credit1", LocalDate.now(), "credit card", 0.0, 500.0, -500.0);

        Mockito.when(assetRepository.findTopByNameOrderByDate(assetNameList[0])).thenReturn(Optional.of(bank));
        Mockito.when(assetRepository.findTopByNameOrderByDate(assetNameList[1])).thenReturn(Optional.of(credit1));
        Mockito.when(assetRepository.findTopByNameOrderByDate(assetNameList[2])).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                AssetNotFound.class,
                () -> assetSummaryService.getLatestAsset(assetNameList),
                "Asset Not Found in given record"
        );

        assertEquals("Asset Not Found in given record", thrown.getMessage());
    }

//    @Test
//    void testGetHistoryAsset() {
//        String[] assetNameList = new String[]{"Bank", "Credit1", "Credit2"};
//        LocalDate[] dates = new LocalDate[]{LocalDate.now().minusMonths(1), LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(3)};
//        Double[] credits = new Double[]{30000.0, 15000.0, 20000.0};
//        Double[] debits = new Double[]{ 0.0, 0.0, 0.0};
//
//        List<List<AssetDTO>> assetDTOList = new ArrayList<>();
//
//        List<AssetDTO> bankDTO = createListOfAssetDTO(assetNameList[0], dates, credits, debits);
//        List<AssetDTO> credit1DTO = createListOfAssetDTO(assetNameList[1], dates, credits, debits);
//        List<AssetDTO> credit2DTO = createListOfAssetDTO(assetNameList[2], dates, credits, debits);
//        assetDTOList.add(bankDTO);
//        assetDTOList.add(credit1DTO);
//        assetDTOList.add(credit2DTO);
//
//        List<Asset> bank = createListOfAsset(assetNameList[0], dates, credits, debits);
//        List<Asset> credit1 = createListOfAsset(assetNameList[1], dates, credits, debits);
//        List<Asset> credit2 = createListOfAsset(assetNameList[2], dates, credits, debits);
//
//        Mockito.when(assetRepository.findByName(assetNameList[0])).thenReturn(Optional.of(bank));
//        Mockito.when(assetRepository.findByName(assetNameList[1])).thenReturn(Optional.of(credit1));
//        Mockito.when(assetRepository.findByName(assetNameList[2])).thenReturn(Optional.of(credit2));
//
//        Mockito.when(assetMapper.convertToDtoList(bank)).thenReturn(bankDTO);
//        Mockito.when(assetMapper.convertToDtoList(credit1)).thenReturn(credit1DTO);
//        Mockito.when(assetMapper.convertToDtoList(credit2)).thenReturn(credit2DTO);
//
//        assertEquals(assetDTOList, assetSummaryService.getHistoryAsset(assetNameList));
//    }

//    @Test
//    void testGetHistoryAssetNotFound() {
//        String[] assetNameList = new String[]{"Bank", "Credit1", "Credit1342"};
//
//        LocalDate[] dates = new LocalDate[]{LocalDate.now().minusMonths(1), LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(3)};
//        Double[] credits = new Double[]{30000.0, 15000.0, 20000.0};
//        Double[] debits = new Double[]{ 0.0, 0.0, 0.0};
//
//        List<Asset> bank = createListOfAsset(assetNameList[0], dates, credits, debits);
//        List<Asset> credit1 = createListOfAsset(assetNameList[1], dates, credits, debits);
//
//        Mockito.when(assetRepository.findByName(assetNameList[0])).thenReturn(Optional.of(bank));
//        Mockito.when(assetRepository.findByName(assetNameList[1])).thenReturn(Optional.of(credit1));
//        Mockito.when(assetRepository.findByName(assetNameList[2])).thenReturn(Optional.empty());
//
//        Exception thrown = assertThrows(
//                AssetNotFound.class,
//                () -> assetSummaryService.getHistoryAsset(assetNameList),
//                "Asset Not Found in given record"
//        );
//
//        assertEquals("Asset Not Found in given record", thrown.getMessage());
//    }

    @Test
    void testGetAssetSuccess() {

        String assetName = "Bank";

        Asset bankAsset = createAsset("Bank", LocalDate.now(), "bank", 100000.0, 0.0, 100000.0);
        AssetDTO bankAssetDTO = new AssetDTO("Bank", LocalDate.now(), "bank", 100000.0, 0.0, 100000.0, LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(assetRepository.findByName("Bank")).thenReturn(Optional.of(bankAsset));
        Mockito.when(assetMapper.convertToDto(bankAsset)).thenReturn(bankAssetDTO);

        assertEquals(bankAssetDTO, assetSummaryService.getAssetByName(assetName));
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

    private AssetDTO createAssetDTO(String name, LocalDate date, Double credit, Double debit) {
        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setName(name);
        assetDTO.setDate(date);
        assetDTO.setCredit(credit);
        assetDTO.setDebit(debit);
        return assetDTO;
    }

    private List<AssetDTO> createListOfAssetDTO(String name, LocalDate[] dates, Double[] credits, Double[] debit) {
        List<AssetDTO> list = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            list.add(new AssetDTO(name, dates[i], "test", credits[i], debit[i], 10000.0, LocalDateTime.now(), LocalDateTime.now()));

        return list;
    }

    private Asset createAsset(String name, LocalDate date, String type, Double credit, Double debit, Double balance) {
        Asset asset = new Asset();
        asset.setName(name);
        asset.setDate(date);
        asset.setType(type);
        asset.setCredit(credit);
        asset.setDebit(debit);
        asset.setBalance(balance);
        asset.setCreated_Date(LocalDateTime.now());
        asset.setUpdated_Date(LocalDateTime.now());
        return asset;
    }

    private List<Asset> createListOfAsset(String name, LocalDate[] dates, Double[] credits, Double[] debit) {
        List<Asset> list = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            Asset asset = new Asset();
            asset.setName(name);
            asset.setDate(dates[i]);
            asset.setCredit(credits[i]);
            asset.setDebit(debit[i]);
            list.add(asset);
        }
        return list;
    }
}