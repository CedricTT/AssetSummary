package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.dto.TransactionRecord;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.exception.AssetNotFound;
import com.bookkeeper.AssetSummary.model.exception.ErrorResponse;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.model.response.AssetResponse;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import com.bookkeeper.AssetSummary.service.AssetSummaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mvc.perform(post("/api/v1/assetSummary/create")
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
        mvc.perform(post("/api/v1/assetSummary/create")
                .content(mapper.writeValueAsString(assetDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAssetBusinessLogic() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0);
        mvc.perform(post("/api/v1/assetSummary/create")
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
                post("/api/v1/assetSummary/create")
                .content(mapper.writeValueAsString(assetDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(mapper.writeValueAsString(assetDTO));
    }

    @Test
    void testCreateAssetException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0);
        when(assetSummaryService.createAsset(assetDTO)).thenThrow(new AssetAlreadyExisting("0030", "Asset Already exist in given period of time"));
        MvcResult mvcResult = mvc.perform(
                post("/api/v1/assetSummary/create")
                        .content(mapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ErrorResponse expectedResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Asset Already exist in given period of time");
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = mapper.writeValueAsString(expectedResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void testGetAssetByAssetName() throws Exception {
        String assetName = "Bank";
        mvc.perform(
                get("/api/v1/assetSummary/asset")
                .param(("assetName"), assetName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAssetByAssetNameBadRequest() throws Exception {
        mvc.perform(
                get("/api/v1/assetSummary/asset"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAssetResponse() throws Exception {
        String assetName = "Bank";
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0);
        Double spending = 5000.0;
        AssetResponse assetResponse = new AssetResponse(assetDTO, spending);
        ObjectMapper mapper = new ObjectMapper();
        when(assetSummaryService.getAssetByName(assetName)).thenReturn(assetResponse);
        MvcResult mvcResult = mvc.perform(
                get("/api/v1/assetSummary/asset")
                .param(("assetName"), assetName))
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(mapper.writeValueAsString(assetResponse));
    }

    @Test
    void testGetAssetException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String assetName = "Bank";
        when(assetSummaryService.getAssetByName(assetName)).thenThrow(new AssetNotFound("0031", "Asset Not Found in given record"));
        MvcResult mvcResult = mvc.perform(
                        get("/api/v1/assetSummary/asset")
                                .param(("assetName"), assetName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        ErrorResponse expectedResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Asset Not Found in given record");
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = mapper.writeValueAsString(expectedResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void testUpdateAssetSuccess() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        LocalDateTime requestTime = LocalDateTime.now();
        TransactionRecord transactionRecord = new TransactionRecord("bank", -10000.0, requestTime);
        mvc.perform(post("/api/v1/assetSummary/update")
                        .content(objectMapper.writeValueAsString(transactionRecord))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}