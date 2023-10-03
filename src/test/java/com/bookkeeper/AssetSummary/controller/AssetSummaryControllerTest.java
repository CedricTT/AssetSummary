package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.mapper.AssetMapper;
import com.bookkeeper.AssetSummary.repository.AssetRepository;
import com.bookkeeper.AssetSummary.service.AssetSummaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

//    @Test
//    void testGetAssetSuccess() throws Exception {
//        String[] assetName = new String[]{"test","test1","test2"};
//
//        mvc.perform(MockMvcRequestBuilders
//                .get("/api/v1/assetSummary/asset")
//                .param("asset_names", assetName))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(content().contentType("application/json"));
//    }
//
//    @Test
//    void testGetAssetBadRequest() throws Exception {
//        mvc.perform(MockMvcRequestBuilders
//                    .get("/api/v1/assetSummary/asset"))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }

//    @Test
//    void testCreateAssetSuccess() throws Exception {
//        mvc.perform(MockMvcRequestBuilders
//                        .post("/api/v1/assetSummary/create")
//                        .content(asJsonString(new AssetDTO("test", LocalDate.now(), 100.0, 0.0)))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }

    @Test
    void testCreateAssetBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                    .post("/api/v1/assetSummary/create"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAssetByAssetName() throws Exception {
        String assetName = "Bank";
        mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/assetSummary/asset")
                .param(("assetName"), assetName))
                .andDo(print())
                .andExpect(status().isOk());
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));
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