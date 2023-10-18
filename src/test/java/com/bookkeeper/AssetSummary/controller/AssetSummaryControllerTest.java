package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.TransactionRecord;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.response.ErrorResponse;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.model.response.AssetResponse;
import com.bookkeeper.AssetSummary.model.response.UpdateAssetResponse;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0);
        mvc.perform(post("/api/v1/assetSummary")
                        .content(mapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateAssetValidation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetDTO assetDTO = new AssetDTO(null, "bank", 10000.0);
        mvc.perform(post("/api/v1/assetSummary")
                .content(mapper.writeValueAsString(assetDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAssetBusinessLogic() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0);
        mvc.perform(post("/api/v1/assetSummary")
                .content(mapper.writeValueAsString(assetDTO))
                .contentType(MediaType.APPLICATION_JSON));

        ArgumentCaptor<AssetDTO> assetDTOArgumentCaptor = ArgumentCaptor.forClass(AssetDTO.class);
        verify(assetSummaryService).createAsset(assetDTOArgumentCaptor.capture());
        assertThat(assetDTOArgumentCaptor.getValue().getName()).isEqualTo("Bank");
        assertThat(assetDTOArgumentCaptor.getValue().getType()).isEqualTo("bank");
    }

    @Test
    void testCreateAssetOutput() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0);
        when(assetSummaryService.createAsset(any())).thenReturn(assetDTO);
        MvcResult mvcResult = mvc.perform(
                post("/api/v1/assetSummary")
                .content(mapper.writeValueAsString(assetDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(mapper.writeValueAsString(assetDTO));
    }

    @Test
    void testCreateAssetException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0);
        when(assetSummaryService.createAsset(assetDTO)).thenThrow(new AssetAlreadyExisting("0030", "Asset Already exist in given period of time"));
        MvcResult mvcResult = mvc.perform(
                post("/api/v1/assetSummary")
                        .content(objectMapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ErrorResponse expectedResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Asset Already exist in given period of time")
                .status("FAILED")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void testGetAssetByAssetName() throws Exception {
        String assetName = "Bank";
        mvc.perform(
                get("/api/v1/assetSummary")
                .param(("assetName"), assetName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAssetByAssetNameBadRequest() throws Exception {
        mvc.perform(
                get("/api/v1/assetSummary"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAssetResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String assetName = "Bank";
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0);
        Double spending = 5000.0;
        AssetResponse assetResponse = AssetResponse
                .builder()
                .assetDTO(assetDTO)
                .Spending(spending)
                .status("SUCCESS")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();
        when(assetSummaryService.getAssetByName(assetName)).thenReturn(assetResponse);
        MvcResult mvcResult = mvc.perform(
                get("/api/v1/assetSummary")
                .param(("assetName"), assetName))
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(assetResponse));
    }

    @Test
    void testGetAssetException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String assetName = "Bank";
        when(assetSummaryService.getAssetByName(assetName)).thenThrow(new AssetNotFound("0031", "Asset Not Found in given record"));
        MvcResult mvcResult = mvc.perform(
                        get("/api/v1/assetSummary")
                                .param(("assetName"), assetName)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn();
        ErrorResponse expectedResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Asset Not Found in given record")
                .status("FAILED")
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
        LocalDateTime requestTime = LocalDateTime.now().withNano(3);
        TransactionRecord transactionRecord = new TransactionRecord("bank", -10000.0, requestTime);
        AssetDTO assetDTO = new AssetDTO("bank", "bank account", 20000.0);
        when(assetSummaryService.updateAsset(transactionRecord)).thenReturn(assetDTO);
        mvc.perform(put("/api/v1/assetSummary")
                        .content(objectMapper.writeValueAsString(transactionRecord))
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
        LocalDateTime requestTime = LocalDateTime.now();
        TransactionRecord transactionRecord = new TransactionRecord(null, -10000.0, requestTime);
        mvc.perform(put("/api/v1/assetSummary")
                        .content(objectMapper.writeValueAsString(transactionRecord))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalDateTime requestTime = LocalDateTime.now();
        TransactionRecord transactionRecord = new TransactionRecord("bank", -10000.0, requestTime);
        AssetDTO assetDTO = new AssetDTO("bank", "bank account", 20000.0);
        when(assetSummaryService.updateAsset(transactionRecord)).thenReturn(assetDTO);
        MvcResult mvcResult = mvc.perform(put("/api/v1/assetSummary")
                        .content(objectMapper.writeValueAsString(transactionRecord))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        UpdateAssetResponse expectedResponse = UpdateAssetResponse
                .builder()
                .currentBalance(20000.0)
                .status("SUCCESS")
                .requestTime(requestTime)
                .build();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    void testUpdateException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalDateTime requestTime = LocalDateTime.now();
        TransactionRecord transactionRecord = new TransactionRecord("bank", -10000.0, requestTime);
        when(assetSummaryService.updateAsset(any())).thenThrow(new AssetNotFound("0040", "Asset Not Found in given record"));
        MvcResult mvcResult = mvc.perform(put("/api/v1/assetSummary")
                        .content(objectMapper.writeValueAsString(transactionRecord))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        ErrorResponse expectedResponse = ErrorResponse
                .builder()
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Asset Not Found in given record")
                .status("FAILED")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }
}