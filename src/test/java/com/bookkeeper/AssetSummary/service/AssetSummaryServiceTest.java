package com.bookkeeper.AssetSummary.service;

import com.bookkeeper.AssetSummary.model.dto.*;
import com.bookkeeper.AssetSummary.model.entity.Asset;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.exception.ForbiddenException;
import com.bookkeeper.AssetSummary.model.exception.GlobalException;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        String email = "test@gmail.com";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";

        AssetDTO assetDTO = new AssetDTO("Bank","bank", 10000.0, "Purple");

        Asset mappedAsset = createAsset("Bank","bank", 10000.0);

        ArgumentCaptor<Asset> argumentCaptor = ArgumentCaptor.forClass(Asset.class);
        when(assetMapper.convertToEntity(assetDTO)).thenReturn(mappedAsset);
        when(assetRepository.save(isA(Asset.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(assetMapper.convertToDto(isA(Asset.class))).thenReturn(assetDTO);

        assertEquals(assetDTO, assetSummaryService.createAsset(uid, email, assetDTO));
        verify(assetRepository).save(argumentCaptor.capture());
        assertEquals(email, argumentCaptor.getValue().getEmail());
        assertEquals(uid, argumentCaptor.getValue().getUID());
    }

    @Test
    void testExistingCreation() {

        String email = "test@gmail.com";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";

        AssetDTO assetDTO = new AssetDTO("Bank","bank", 10000.0, "Purple");

        Asset asset = createAsset("Bank","bank", 10000.0, email, uid);

        when(assetRepository.findByNameAndUID("Bank", uid)).thenReturn(Optional.of(asset));

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
        AssetDTO bankAssetDTO = new AssetDTO("Bank", "bank", 100000.0, "Purple");
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
        AssetDTO assetDTO = new AssetDTO("Bank", "bank account", 30000.0, "Purple");
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map.put("email", "test@gmail.com");
        map.put("request_record", paymentDTO);

        ArgumentCaptor<Asset> argumentCaptor = ArgumentCaptor.forClass(Asset.class);
        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.of(asset));
        when(assetRepository.save(isA(Asset.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.empty());
        when(assetMapper.convertToDto(isA(Asset.class))).thenReturn(assetDTO);

        assetSummaryService.updateAsset(map);
        verify(assetRepository).save(argumentCaptor.capture());
        assertEquals(20000, argumentCaptor.getValue().getBalance());
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
        AssetDTO assetDTO = new AssetDTO("Bank", "bank account", 30000.0, "Purple");
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map.put("email", "test@gmail.com");
        map.put("request_record", paymentDTO);

        ArgumentCaptor<Asset> argumentCaptor = ArgumentCaptor.forClass(Asset.class);
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.of(asset));
        when(assetRepository.save(isA(Asset.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.empty());
        when(assetMapper.convertToDto(isA(Asset.class))).thenReturn(assetDTO);

        assetSummaryService.updateAsset(map);
        verify(assetRepository).save(argumentCaptor.capture());
        assertEquals(40000, argumentCaptor.getValue().getBalance());
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
        Asset bank = createAsset("Bank", "bank account", 40000.0);
        AssetDTO bankDTO = new AssetDTO("Bank", "bank account", 40000.0, "Purple");
        Asset creditCard = createAsset("Credit Card", "credit card", 10000.0);
        AssetDTO creditCardDTO = new AssetDTO("Credit Card", "credit card", 10000.0, "Purple");
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map.put("email", "test@gmail.com");
        map.put("request_record", paymentDTO);

        ArgumentCaptor<Asset> argumentCaptor = ArgumentCaptor.forClass(Asset.class);
        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.of(bank));
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.of(creditCard));
        when(assetRepository.save(isA(Asset.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(assetMapper.convertToDto(bank)).thenReturn(bankDTO);
        when(assetMapper.convertToDto(creditCard)).thenReturn(creditCardDTO);

        assetSummaryService.updateAsset(map);
        verify(assetRepository, times(2)).save(argumentCaptor.capture());
        List<Asset> assets = argumentCaptor.getAllValues();
        assertEquals(30000, assets.get(0).getBalance());
        assertEquals(20000, assets.get(1).getBalance());
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map.put("email", "test@gmail.com");
        map.put("request_record", paymentDTO);

        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.empty());
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.empty());

        UpdatedAsset expectResult = UpdatedAsset
                .builder()
                .transactionValue(100.0)
                .build();

        assertEquals(expectResult, assetSummaryService.updateAsset(map));
    }

    @Test
    void testUpdateAsset_invalidRequest() {
        String assetTo = "Shop";
        PaymentDTO paymentDTO = PaymentDTO
                .builder()
                .description("test")
                .date(LocalDate.now())
                .category("Food")
                .paymentMethod("FPS")
                .amount(100.0)
                .paymentFrom(null)
                .paymentTo(assetTo)
                .build();
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map.put("email", "test@gmail.com");
        map.put("request_record", paymentDTO);

        assertThrows(GlobalException.class,
                () -> assetSummaryService.updateAsset(map),
                "Invalid request");
    }

    @Test
    void testUpdateAsset_reverseRecord() {
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
        PaymentDTO reverse = PaymentDTO
                .builder()
                .description("reverse")
                .date(LocalDate.now())
                .category("Income")
                .paymentMethod("Fund Transfer")
                .amount(5000)
                .paymentFrom(assetTo)
                .paymentTo(assetFrom)
                .build();
        Asset bank = createAsset("Bank", "bank account", 30000.0);
        AssetDTO bankDTO = new AssetDTO("Bank", "bank account", 30000.0, "Purple");
        Asset creditCard = createAsset("Credit Card", "credit card", 10000.0);
        AssetDTO creditCardDTO = new AssetDTO("Credit Card", "credit card", 10000.0, "Purple");
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map.put("email", "test@gmail.com");
        map.put("request_record", paymentDTO);
        map.put("reverse_record", reverse);
        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.of(bank));
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.of(creditCard));
        when(assetRepository.save(isA(Asset.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(assetMapper.convertToDto(bank)).thenReturn(bankDTO);
        when(assetMapper.convertToDto(creditCard)).thenReturn(creditCardDTO);

        assetSummaryService.updateAsset(map);
        ArgumentCaptor<Asset> argumentCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepository, times(2)).save(argumentCaptor.capture());
        List<Asset> assets = argumentCaptor.getAllValues();
        assertEquals(25000, assets.get(0).getBalance());
        assertEquals(15000, assets.get(1).getBalance());
    }

    @Test
    void testInvalidReverse() {
        String assetTo = "Credit Card";
        String assetFrom = "Bank";
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
        PaymentDTO reverse_null = PaymentDTO
                .builder()
                .description("reverse")
                .date(LocalDate.now())
                .category("Income")
                .paymentMethod("Fund Transfer")
                .amount(5000)
                .paymentTo(assetFrom)
                .build();
        HashMap<String, Object> map_null = new HashMap<>();
        map_null.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map_null.put("email", "test@gmail.com");
        map_null.put("request_record", paymentDTO);
        map_null.put("reverse_record", reverse_null);

        assertThrows(
                GlobalException.class,
                () -> assetSummaryService.updateAsset(map_null),
                "Invalid request"
        );

        PaymentDTO reverse_unequal = PaymentDTO
                .builder()
                .description("reverse")
                .date(LocalDate.now())
                .category("Income")
                .paymentMethod("Fund Transfer")
                .amount(5000)
                .paymentTo(assetTo)
                .paymentFrom(assetFrom)
                .build();
        HashMap<String, Object> map_unequal = new HashMap<>();
        map_unequal.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map_unequal.put("email", "test@gmail.com");
        map_unequal.put("request_record", paymentDTO);
        map_unequal.put("reverse_record", reverse_unequal);

        assertThrows(
                GlobalException.class,
                () -> assetSummaryService.updateAsset(map_unequal),
                "Invalid reverse request"
        );
    }

    @Test
    void testMissingUserInfo() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", "test@gmail.com");

        assertThrows(
                ForbiddenException.class,
                () -> assetSummaryService.updateAsset(map),
                "Missing user info"
        );
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
        assetDTOList.add(new AssetDTO("Bank","bank", 10000.0, "Purple"));
        assetDTOList.add(new AssetDTO("Credit Card","credit card", -500.0, "Purple"));
        assetDTOList.add(new AssetDTO("Debit Card","debit card", 2000.0, "Purple"));

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
    void testCancelTransaction() {
        String assetTo = "Credit Card";
        String assetFrom = "Bank";
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        PaymentDTO paymentDTO = PaymentDTO
                .builder()
                .description("reverse")
                .date(LocalDate.now())
                .category("Income")
                .paymentFrom("Fund Transfer")
                .amount(5000)
                .paymentFrom(assetTo)
                .paymentTo(assetFrom)
                .build();
        Asset bank = createAsset("Bank", "bank account", 30000.0);
        AssetDTO bankDTO = new AssetDTO("Bank", "bank account", 30000.0, "Purple");
        Asset creditCard = createAsset("Credit Card", "credit card", 10000.0);
        AssetDTO creditCardDTO = new AssetDTO("Credit Card", "credit card", 10000.0, "Purple");
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", "sdg3258rgdsjhgbj32dfgf8865");
        map.put("email", "test@gmail.com");
        map.put("cancel", paymentDTO);
        when(assetRepository.findByNameAndUID(assetFrom, uid)).thenReturn(Optional.of(bank));
        when(assetRepository.findByNameAndUID(assetTo, uid)).thenReturn(Optional.of(creditCard));
        when(assetRepository.save(isA(Asset.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(assetMapper.convertToDto(bank)).thenReturn(bankDTO);
        when(assetMapper.convertToDto(creditCard)).thenReturn(creditCardDTO);

        assetSummaryService.cancelTransaction(map);
        ArgumentCaptor<Asset> argumentCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepository, times(2)).save(argumentCaptor.capture());
        List<Asset> assets = argumentCaptor.getAllValues();
        assertEquals(5000, assets.get(0).getBalance());
        assertEquals(35000, assets.get(1).getBalance());
    }

    private Asset createAsset(String name, String type, Double balance) {
        Asset asset = new Asset();
        asset.setName(name);
        asset.setType(type);
        asset.setBalance(balance);
        asset.setCreated_Date(LocalDateTime.now());
        asset.setUpdated_Date(LocalDateTime.now());
        asset.setColor("Purple");
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