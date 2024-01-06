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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AssetSummaryServiceTest {

    @InjectMocks
    private AssetSummaryService assetSummaryService;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private PaymentRecordFeignClient paymentRecordFeignClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(MockitoAnnotations.openMocks(this));
    }

    @Test
    void testCreateAsset() {

        String email = "test@gmail.com";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";

        AssetDTO assetDTO = new AssetDTO("Bank","bank", 10000.0);

        Asset mappedAsset = createAsset("Bank","bank", 10000.0);

        Mockito.when(assetMapper.convertToEntity(assetDTO)).thenReturn(mappedAsset);
        mappedAsset.setUID(uid);
        mappedAsset.setEmail(email);
        Mockito.when(assetRepository.save(mappedAsset)).thenReturn(mappedAsset);
        Mockito.when(assetMapper.convertToDto(mappedAsset)).thenReturn(assetDTO);

        assertEquals(assetDTO, assetSummaryService.createAsset(uid, email, assetDTO));
    }

    @Test
    void testExistingCreation() {

        String email = "test@gmail.com";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";

        AssetDTO assetDTO = new AssetDTO("Bank","bank", 10000.0);

        Asset asset = createAsset("Bank","bank", 10000.0, email, uid);

        Mockito.when(assetRepository.findByNameAndUID("Bank", uid)).thenReturn(Optional.of(asset));

        assertThrows(
                AssetAlreadyExisting.class,
                () -> assetSummaryService.createAsset(uid, email, assetDTO),
                "Asset already exist"
        );
    }

    @Test
    void testGetAssetByNameSuccess() {

        String assetName = "Bank";
        Asset bankAsset = createAsset("Bank", "bank", 100000.0);
        AssetDTO bankAssetDTO = new AssetDTO("Bank", "bank", 100000.0);
        when(assetRepository.findByName("Bank")).thenReturn(Optional.of(bankAsset));
        when(assetMapper.convertToDto(bankAsset)).thenReturn(bankAssetDTO);

        assertEquals(bankAssetDTO, assetSummaryService.getAssetByName(assetName));
    }

    @Test
    void testGetAssetByNameNotFound() {

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
    void testUpdateAssetFrom() {
        String assetFrom = "Bank";
        String assetTo = "Shop";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        PaymentDTO paymentDTO = PaymentDTO
                .builder()
                .description("test")
                .date(LocalDate.now())
                .category("Food")
                .paymentMethod("FPS")
                .amount(10000.0)
                .paymentFrom(assetFrom)
                .paymentTo(assetTo)
                .build();
        Asset asset = createAsset("Bank", "bank account", 30000.0);
        AssetDTO assetDTO = new AssetDTO("Bank", "bank account", 30000.0);

        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.of(asset));
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.empty());
        when(assetMapper.convertToDto(asset)).thenReturn(assetDTO);

        assetDTO.setBalance(20000.0);
        UpdatedAsset expectResult = UpdatedAsset
                .builder()
                .assetFrom(assetDTO)
                .transactionValue(10000.0)
                .build();

        assertEquals(expectResult, assetSummaryService.updateAsset(paymentDTO, uid));
    }

    @Test
    void testUpdateAssetTo() {
        String assetTo = "Bank";
        String assetFrom = "Trader";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        PaymentDTO paymentDTO = PaymentDTO
                .builder()
                .description("test")
                .date(LocalDate.now())
                .category("Investment")
                .paymentMethod("Security")
                .amount(10000.0)
                .paymentFrom(assetFrom)
                .paymentTo(assetTo)
                .build();
        Asset asset = createAsset("Bank", "bank account", 30000.0);
        AssetDTO assetDTO = new AssetDTO("Bank", "bank account", 30000.0);

        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.of(asset));
        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.empty());
        when(assetMapper.convertToDto(asset)).thenReturn(assetDTO);

        assetDTO.setBalance(40000.0);
        UpdatedAsset expectResult = UpdatedAsset
                .builder()
                .assetTo(assetDTO)
                .transactionValue(10000.0)
                .build();

        assertEquals(expectResult, assetSummaryService.updateAsset(paymentDTO, uid));
    }

    @Test
    void testUpdateAssetBoth() {
        String assetTo = "Credit Card";
        String assetFrom = "Bank";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        PaymentDTO paymentDTO = PaymentDTO
                .builder()
                .description("test")
                .date(LocalDate.now())
                .category("Repayment")
                .paymentMethod("Fund Transfer")
                .amount(10000.0)
                .paymentFrom(assetFrom)
                .paymentTo(assetTo)
                .build();
        Asset bank = createAsset("Bank", "bank account", 30000.0);
        AssetDTO bankDTO = new AssetDTO("Bank", "bank account", 30000.0);
        Asset creditCard = createAsset("Credit Card", "credit card", 10000.0);
        AssetDTO creditCardDTO = new AssetDTO("Credit Card", "credit card", 10000.0);

        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.of(bank));
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.of(creditCard));
        when(assetMapper.convertToDto(bank)).thenReturn(bankDTO);
        when(assetMapper.convertToDto(creditCard)).thenReturn(creditCardDTO);

        bankDTO.setBalance(20000.0);
        creditCardDTO.setBalance(20000.0);

        UpdatedAsset expectedResult = UpdatedAsset
                .builder()
                .assetFrom(bankDTO)
                .assetTo(creditCardDTO)
                .transactionValue(10000.0)
                .build();

        assertEquals(expectedResult, assetSummaryService.updateAsset(paymentDTO, uid));
    }

    @Test
    void testUpdateAsset_AssetNotFound() {
        String assetFrom = "Bank";
        String assetTo = "Shop";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        PaymentDTO paymentDTO = PaymentDTO
                .builder()
                .description("test")
                .date(LocalDate.now())
                .category("Food")
                .paymentMethod("FPS")
                .amount(100.0)
                .paymentFrom(assetFrom)
                .paymentTo(assetTo)
                .build();

        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.empty());
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.empty());

        UpdatedAsset expectResult = UpdatedAsset
                .builder()
                .transactionValue(100.0)
                .build();

        assertEquals(expectResult, assetSummaryService.updateAsset(paymentDTO, uid));
    }

    @Test
    void testGetAsset() {
        String email = "test@gmail.com";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        List<Asset> assetList = new ArrayList<>();
        assetList.add(createAsset("Bank","bank", 10000.0, email, uid));
        assetList.add(createAsset("Credit Card","credit card", -500.0, email, uid));
        assetList.add(createAsset("Debit Card","debit card", 2000.0, email, uid));

        List<AssetDTO> assetDTOList = new ArrayList<>();
        assetDTOList.add(new AssetDTO("Bank","bank", 10000.0));
        assetDTOList.add(new AssetDTO("Credit Card","credit card", -500.0));
        assetDTOList.add(new AssetDTO("Debit Card","debit card", 2000.0));

        Mockito.when(assetRepository.findByEmailAndUID(email, uid)).thenReturn(Optional.of(assetList));
        Mockito.when(assetMapper.convertToDtoList(assetList)).thenReturn(assetDTOList);

        assertEquals(assetDTOList, assetSummaryService.getAsset(uid, email));
    }

    @Test
    void testGetAssetEmpty() {
        String email = "test@gmail.com";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";

        Mockito.when(assetRepository.findByEmailAndUID(email, uid)).thenReturn(Optional.empty());

        assertThrows(
                AssetNotFound.class,
                () -> assetSummaryService.getAsset(uid, email),
                "No record found"
        );
    }

    @Test
    void testGetAssetSummary() {
        String assetName = "Bank";
        Asset bankAsset = createAsset("Bank", "bank", 100000.0);
        AssetDTO bankAssetDTO = new AssetDTO("Bank", "bank", 100000.0);
        List<PaymentDTO> paymentDTOList = new ArrayList<>();
        paymentDTOList.add(PaymentDTO
                .builder()
                .description("Lunch").category("Food").paymentMethod("Octopus")
                .date(LocalDate.now()).amount(50.0).paymentFrom("Bank").paymentTo("Food shop")
                .build());
        paymentDTOList.add(PaymentDTO
                .builder()
                .description("Train").category("Transportation").paymentMethod("Octopus")
                .date(LocalDate.now()).amount(20.0).paymentFrom("Bank").paymentTo("train")
                .build());
        paymentDTOList.add(PaymentDTO
                .builder()
                .description("Interest").category("Investment").paymentMethod("Security")
                .date(LocalDate.now()).amount(20.0).paymentFrom("Security").paymentTo("Bank")
                .build());
        PaymentRecordResponse paymentRecordResponse = PaymentRecordResponse
                .builder()
                .queryPaymentRecord(paymentDTOList)
                .build();

        when(assetRepository.findByName(assetName)).thenReturn(Optional.of(bankAsset));
        when(assetMapper.convertToDto(bankAsset)).thenReturn(bankAssetDTO);
        when(paymentRecordFeignClient.query(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth().getValue(),
                assetName))
                .thenReturn(new ResponseEntity<>(paymentRecordResponse, HttpStatus.OK));

        AssetSummary expectedValue = AssetSummary
                .builder()
                .assetDTO(bankAssetDTO)
                .speeding(70.0)
                .build();

        assertEquals(expectedValue, assetSummaryService.getAssetSummary(assetName));
    }

    @Test
    void testGetAssetSummaryException() {
        String assetName = "Bank";
        assertThrows(
                AssetNotFound.class,
                () -> assetSummaryService.getAssetSummary(assetName),
                "Asset Not Found in given record"
        );
    }

    @Test
    void testGetAssetSummaryExternalFail() {
        String assetName = "Bank";
        Asset bankAsset = createAsset("Bank", "bank", 100000.0);
        when(assetRepository.findByName(assetName)).thenReturn(Optional.of(bankAsset));
        when(paymentRecordFeignClient.query(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth().getValue(),
                assetName))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(
                ExternalSystemException.class,
                () -> assetSummaryService.getAssetSummary(assetName),
                "Failed on external system call"
        );
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

    private Asset createAsset(String name, String type, Double balance, String email, String uid) {
        Asset asset = new Asset();
        asset.setEmail(email);
        asset.setUID(uid);
        asset.setName(name);
        asset.setType(type);
        asset.setBalance(balance);
        asset.setCreated_Date(LocalDateTime.now());
        asset.setUpdated_Date(LocalDateTime.now());
        return asset;
    }
}