package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.dto.*;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.response.*;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import com.bookkeeper.AssetSummary.service.AssetSummaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssetSummaryController.class)
class AssetSummaryControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AssetSummaryService assetSummaryService;

    @MockBean
    private AssetRepository assetRepository;

    @MockBean
    private AssetMapper assetMapper;

    @Test
    void testCreateAssetSuccess() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, "Purple");
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";
        mvc.perform(post("/api/v1/asset")
                        .header("user-uid", uid)
                        .header("user-email", email)
                        .content(mapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateAssetValidation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AssetDTO assetDTO_Missing = new AssetDTO(null, "bank", 10000.0, "Purple");
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, "Purple");

        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";

        mvc.perform(post("/api/v1/asset")
                        .header("user-uid", uid)
                        .header("user-email", email)
                        .content(mapper.writeValueAsString(assetDTO_Missing))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/v1/asset")
                        .content(mapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAssetBusinessLogic() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, "Purple");
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";

        mvc.perform(post("/api/v1/asset")
                .header("user-uid", uid)
                .header("user-email", email)
                .content(mapper.writeValueAsString(assetDTO))
                .contentType(MediaType.APPLICATION_JSON));

        ArgumentCaptor<AssetDTO> assetDTOArgumentCaptor = ArgumentCaptor.forClass(AssetDTO.class);
        verify(assetSummaryService).createAsset(any(), any(), assetDTOArgumentCaptor.capture());
        assertThat(assetDTOArgumentCaptor.getValue().getName()).isEqualTo("Bank");
        assertThat(assetDTOArgumentCaptor.getValue().getType()).isEqualTo("bank");
    }

    @Test
    void testCreateAssetResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, "Purple");
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";

        when(assetSummaryService.createAsset(any(), any(), any())).thenReturn(assetDTO);

        MvcResult mvcResult = mvc.perform(
                post("/api/v1/asset")
                        .header("user-uid", uid)
                        .header("user-email", email)
                        .content(objectMapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        BaseResponse expectedResponse = BaseResponse.builder()
                .status("SUCCESS").requestTime(LocalDateTime.now().withNano(0))
                .build();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    void testCreateAssetException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, "Purple");
        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";

        MvcResult forbiddenResult = mvc.perform(post("/api/v1/asset")
                        .header("user-uid", "")
                        .header("user-email", email)
                        .content(objectMapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponse forbiddenResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.FORBIDDEN.value())
                .code("999")
                .message("Missing user info")
                .status("FAILED")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();

        String forbiddenResultBody = forbiddenResult.getResponse().getContentAsString();
        String forbiddenResponseBody = objectMapper.writeValueAsString(forbiddenResponse);
        assertThat(forbiddenResultBody).isEqualToIgnoringWhitespace(forbiddenResponseBody);

        when(assetSummaryService.createAsset(uid, email, assetDTO)).thenThrow(new AssetAlreadyExisting("0030", "Asset already exist"));

        MvcResult mvcResult = mvc.perform(
                post("/api/v1/asset")
                        .header("user-uid", uid)
                        .header("user-email", email)
                        .content(objectMapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponse expectedResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code("0030")
                .message("Asset already exist")
                .status("FAILED")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void testGetSingleAssetByAssetName() throws Exception {
        String assetName = "Bank";
        mvc.perform(
                get("/api/v1/asset/single")
                .param(("assetName"), assetName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetSingleAssetByAssetNameValidation() throws Exception {
        mvc.perform(
                get("/api/v1/asset/single"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetSingleAssetResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String assetName = "Bank";
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, "Purple");
        SingleAssetResponse assetResponse = SingleAssetResponse
                .builder()
                .asset(assetDTO)
                .status("SUCCESS")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();
        when(assetSummaryService.getAssetByName(assetName)).thenReturn(assetDTO);
        MvcResult mvcResult = mvc.perform(
                get("/api/v1/asset/single")
                .param(("assetName"), assetName))
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(assetResponse));
    }

    @Test
    void testGetSingleAssetException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String assetName = "Bank";
        when(assetSummaryService.getAssetByName(assetName)).thenThrow(new AssetNotFound("0031", "Asset Not Found in given record"));
        MvcResult mvcResult = mvc.perform(
                        get("/api/v1/asset/single")
                                .param(("assetName"), assetName)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn();
        ErrorResponse expectedResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Asset Not Found in given record")
                .status("FAILED")
                .code("0031")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void testGetAsset() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";

        List<AssetDTO> assetDTOList = new ArrayList<>();
        assetDTOList.add(new AssetDTO("Bank","bank", 10000.0, "Purple"));
        assetDTOList.add(new AssetDTO("Credit Card","credit card", -500.0, "Purple"));
        assetDTOList.add(new AssetDTO("Debit Card","debit card", 2000.0, "Purple"));

        AssetResponse assetResponse = AssetResponse
                .builder()
                .asset(assetDTOList)
                .status("SUCCESS")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();

        when(assetSummaryService.getAsset(uid, email)).thenReturn(assetDTOList);

        MvcResult mvcResult = mvc.perform(
                        get("/api/v1/asset")
                                .header("user-uid", uid)
                                .header("user-email", email))
                        .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(assetResponse));
    }

    @Test
    void testGetAssetException_MissingHeaders() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";

        mvc.perform(
                get("/api/v1/asset")
                        .header("user-email", email))
                .andExpect(status().isBadRequest());

        mvc.perform(
                get("/api/v1/asset")
                        .header("user-uid", uid))
                .andExpect(status().isBadRequest());

        MvcResult mvcResult = mvc.perform(
                        get("/api/v1/asset")
                                .header("user-uid", "")
                                .header("user-email", email))
                .andReturn();

        ErrorResponse expectedResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.FORBIDDEN.value())
                .message("Missing user info")
                .status("FAILED")
                .code("999")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void testGetAssetException_AssetNotFound() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";

        when(assetSummaryService.getAsset(uid, email)).thenThrow(new AssetNotFound("0050", "No record found"));

        MvcResult mvcResult = mvc.perform(
                        get("/api/v1/asset")
                                .header("user-email", email)
                                .header("user-uid", uid))
                .andReturn();

        ErrorResponse expectedResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("No record found")
                .status("FAILED")
                .code("0050")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void testUpdateAssetSuccess() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(100)
                .currency("HKD")
                .date(LocalDate.now())
                .category("Food")
                .description("Lunch")
                .paymentFrom("Bank")
                .paymentTo("Friend")
                .paymentMethod("FPS")
                .build();

        doNothing().when(assetSummaryService).updateAsset(uid, paymentDTO);
        mvc.perform(
                put("/api/v1/asset")
                        .header("user-uid", uid)
                        .header("user-email", email)
                        .content(objectMapper.writeValueAsString(paymentDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateValidation() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(100)
                .date(LocalDate.now())
                .description("Lunch")
                .paymentFrom("Bank")
                .paymentTo("Friend")
                .paymentMethod("FPS")
                .build();
        mvc.perform(put("/api/v1/asset")
                        .content(objectMapper.writeValueAsString(paymentDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(100)
                .currency("HKD")
                .date(LocalDate.now())
                .category("Food")
                .description("Lunch")
                .paymentFrom("Bank")
                .paymentTo("Friend")
                .paymentMethod("FPS")
                .build();

        doNothing().when(assetSummaryService).updateAsset(uid, paymentDTO);

        MvcResult mvcResult = mvc.perform(put("/api/v1/asset")
                        .header("user-uid", uid)
                        .header("user-email", email)
                        .content(objectMapper.writeValueAsString(paymentDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        BaseResponse expectedResponse = BaseResponse.builder()
                .status("SUCCESS")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    void testUpdateException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String uid = "sdg3258rgdsjhgbj32dfgf8865";
        String email = "test@gmail.com";
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(100)
                .currency("HKD")
                .date(LocalDate.now())
                .category("Food")
                .description("Lunch")
                .paymentFrom("Bank")
                .paymentTo("Friend")
                .paymentMethod("FPS")
                .build();

        doThrow(new AssetNotFound("0040", "Asset Not Found in given record")).when(assetSummaryService).updateAsset(uid, paymentDTO);

        MvcResult mvcResult = mvc.perform(put("/api/v1/asset")
                        .content(objectMapper.writeValueAsString(paymentDTO))
                        .header("user-uid", uid)
                        .header("user-email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        ErrorResponse expectedResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Asset Not Found in given record")
                .status("FAILED")
                .code("0040")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }
}