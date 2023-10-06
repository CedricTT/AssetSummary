package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.dto.AssetDTO;
import com.bookkeeper.AssetSummary.model.exception.AssetAlreadyExisting;
import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import com.bookkeeper.AssetSummary.service.AssetSummaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, 0.0, 10000.0);
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
        AssetDTO assetDTO = new AssetDTO(null, "bank", 10000.0, 0.0, 10000.0);
        mvc.perform(post("/api/v1/assetSummary/create")
                .content(mapper.writeValueAsString(assetDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAssetBusinessLogic() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, 0.0, 10000.0);
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
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, 0.0, 10000.0);
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
        AssetDTO assetDTO = new AssetDTO("Bank", "bank", 10000.0, 0.0, 10000.0);
        when(assetSummaryService.createAsset(assetDTO)).thenThrow(AssetAlreadyExisting.class);
        MvcResult mvcResult = mvc.perform(
                post("/api/v1/assetSummary/create")
                        .content(mapper.writeValueAsString(assetDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andReturn();
        ErrorResult expectedErrorResponse = new ErrorResult("0010", "Asset Already Exist");
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = mapper.writeValueAsString(expectedErrorResponse);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void testGetAssetByAssetName() throws Exception {
        String assetName = "Bank";
        mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/assetSummary/asset")
                .param(("assetName"), assetName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void testGetAssetByAssetNameBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/assetSummary/asset"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}