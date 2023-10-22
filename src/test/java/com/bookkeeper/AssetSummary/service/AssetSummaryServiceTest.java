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
        Asset bankAsset = createAsset("Bank", "bank", 100000.0);
        AssetDTO bankAssetDTO = new AssetDTO("Bank", "bank", 100000.0);
        when(assetRepository.findByName("Bank")).thenReturn(Optional.of(bankAsset));
        when(assetMapper.convertToDto(bankAsset)).thenReturn(bankAssetDTO);

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

//    @Test
//    void testGetAssetRecordException() {
//        String assetName = "Bank";
//        Asset bankAsset = createAsset("Bank", "bank", 100000.0);
//
//        when(assetRepository.findByName(assetName)).thenReturn(Optional.of(bankAsset));
//        when(recordFeignClient.readAssetRecordByName(assetName)).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
//
//        Exception thrown = assertThrows(
//            HttpException.class,
//                () -> assetSummaryService.getAssetByName(assetName),
//                "Error occurs when calling record service"
//        );
//
//        assertEquals("Error occurs when calling record service", thrown.getMessage());
//    }

    @Test
    void testUpdateAssetFrom() {
        String assetFrom = "Bank";
        String assetTo = "Shop";
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
        AssetDTO assetDTO = new AssetDTO("Bank", "bank account", 20000.0);

        when(assetRepository.findByName(assetFrom)).thenReturn(Optional.of(asset));
        when(assetRepository.findByName(assetTo)).thenReturn(Optional.empty());
        asset.setBalance(20000.0);
        when(assetMapper.convertToDto(asset)).thenReturn(assetDTO);

        assertEquals(assetDTO, assetSummaryService.updateAsset(paymentDTO));
    }

    @Test
    void testUpdateAssetTo() {
        String assetTo = "Bank";
        String assetFrom = "Trader";
        PaymentDTO paymentDTO = PaymentDTO
                .builder()
                .description("test")
                .date(LocalDate.now())
                .category("Investment")
                .paymentMethod("Security")
                .amount(10000.0)
                .paymentFrom(assetFrom)
                .paymentTo(assetFrom)
                .build();
        Asset asset = createAsset("Bank", "bank account", 30000.0);
        AssetDTO assetDTO = new AssetDTO("Bank", "bank account", 40000.0);

        when(assetRepository.findByName(assetTo)).thenReturn(Optional.of(asset));
        when(assetRepository.findByName(assetFrom)).thenReturn(Optional.empty());
        asset.setBalance(40000.0);
        when(assetMapper.convertToDto(asset)).thenReturn(assetDTO);

        assertEquals(assetDTO, assetSummaryService.updateAsset(paymentDTO));
    }

    @Test
    void testUpdateAsset_AssetNotFound() {
        String assetFrom = "Bank";
        String assetTo = "Shop";
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

        when(assetRepository.findByName(assetFrom)).thenReturn(Optional.empty());
        when(assetRepository.findByName(assetTo)).thenReturn(Optional.empty());

        assertEquals(null, assetSummaryService.updateAsset(paymentDTO));
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
}