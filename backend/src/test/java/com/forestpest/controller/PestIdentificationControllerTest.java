package com.forestpest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestpest.entity.IdentificationResult;
import com.forestpest.entity.Pest;
import com.forestpest.service.PestIdentificationService;
import com.forestpest.controller.PestIdentificationController.SymptomsRequest;
import com.forestpest.controller.PestIdentificationController.MultipleConditionsRequest;
import com.forestpest.controller.PestIdentificationController.FeedbackRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 病虫害识别控制器测试类
 */
@WebMvcTest(PestIdentificationController.class)
class PestIdentificationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PestIdentificationService pestIdentificationService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private IdentificationResult testResult;
    private Pest testPest;
    private String testUserId;
    
    @BeforeEach
    void setUp() {
        testUserId = "test-user-123";
        
        // 创建测试病虫害数据
        testPest = new Pest();
        testPest.setId("pest-001");
        testPest.setName("松毛虫");
        testPest.setScientificName("Dendrolimus punctatus");
        testPest.setType("害虫");
        
        // 创建测试识别结果
        testResult = new IdentificationResult();
        testResult.setId("result-001");
        testResult.setUserId(testUserId);
        testResult.setPestId(testPest.getId());
        testResult.setConfidence(0.85);
        testResult.setIdentificationMethod("图片识别");
        testResult.setIdentificationTime(LocalDateTime.now());
    }
    
    @Test
    void testIdentifyByImage_Success() throws Exception {
        // Arrange
        MockMultipartFile imageFile = new MockMultipartFile(
            "image", "test.jpg", "image/jpeg", "test image content".getBytes());
        
        when(pestIdentificationService.identifyByImage(any(), eq(testUserId)))
            .thenReturn(testResult);
        
        // Act & Assert
        mockMvc.perform(multipart("/api/identification/image")
                .file(imageFile)
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testResult.getId()))
                .andExpect(jsonPath("$.data.userId").value(testUserId))
                .andExpect(jsonPath("$.data.confidence").value(0.85))
                .andExpect(jsonPath("$.data.identificationMethod").value("图片识别"));
        
        verify(pestIdentificationService).identifyByImage(any(), eq(testUserId));
    }
    
    @Test
    void testIdentifyByImage_MissingUserId() throws Exception {
        // Arrange
        MockMultipartFile imageFile = new MockMultipartFile(
            "image", "test.jpg", "image/jpeg", "test image content".getBytes());
        
        // Act & Assert
        mockMvc.perform(multipart("/api/identification/image")
                .file(imageFile))
                .andExpect(status().isBadRequest());
        
        verify(pestIdentificationService, never()).identifyByImage(any(), any());
    }
    
    @Test
    void testIdentifyBySymptoms_Success() throws Exception {
        // Arrange
        SymptomsRequest request = new SymptomsRequest();
        request.setSymptoms(Arrays.asList("叶片被啃食", "有虫粪"));
        request.setUserId(testUserId);
        
        List<IdentificationResult> results = Arrays.asList(testResult);
        when(pestIdentificationService.identifyBySymptoms(request.getSymptoms(), testUserId))
            .thenReturn(results);
        
        // Act & Assert
        mockMvc.perform(post("/api/identification/symptoms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testResult.getId()));
        
        verify(pestIdentificationService).identifyBySymptoms(request.getSymptoms(), testUserId);
    }
    
    @Test
    void testIdentifyBySymptoms_EmptySymptoms() throws Exception {
        // Arrange
        SymptomsRequest request = new SymptomsRequest();
        request.setSymptoms(new ArrayList<>());
        request.setUserId(testUserId);
        
        // Act & Assert
        mockMvc.perform(post("/api/identification/symptoms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(pestIdentificationService, never()).identifyBySymptoms(any(), any());
    }
    
    @Test
    void testIdentifyBySymptoms_MissingUserId() throws Exception {
        // Arrange
        SymptomsRequest request = new SymptomsRequest();
        request.setSymptoms(Arrays.asList("叶片被啃食"));
        // userId is null
        
        // Act & Assert
        mockMvc.perform(post("/api/identification/symptoms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(pestIdentificationService, never()).identifyBySymptoms(any(), any());
    }
    
    @Test
    void testIdentifyByMultipleConditions_Success() throws Exception {
        // Arrange
        MultipleConditionsRequest request = new MultipleConditionsRequest();
        request.setSymptoms(Arrays.asList("叶片被啃食"));
        request.setHostPlant("松树");
        request.setSeason("春季");
        request.setRegion("华北");
        request.setUserId(testUserId);
        
        List<IdentificationResult> results = Arrays.asList(testResult);
        when(pestIdentificationService.identifyByMultipleConditions(
            request.getSymptoms(), request.getHostPlant(), 
            request.getSeason(), request.getRegion(), testUserId))
            .thenReturn(results);
        
        // Act & Assert
        mockMvc.perform(post("/api/identification/conditions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testResult.getId()));
        
        verify(pestIdentificationService).identifyByMultipleConditions(
            request.getSymptoms(), request.getHostPlant(), 
            request.getSeason(), request.getRegion(), testUserId);
    }
    
    @Test
    void testGetIdentificationHistory_Success() throws Exception {
        // Arrange
        List<IdentificationResult> history = Arrays.asList(testResult);
        when(pestIdentificationService.getIdentificationHistory(testUserId))
            .thenReturn(history);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/history/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testResult.getId()));
        
        verify(pestIdentificationService).getIdentificationHistory(testUserId);
    }
    
    @Test
    void testGetIdentificationHistory_WithPagination() throws Exception {
        // Arrange
        List<IdentificationResult> history = Arrays.asList(testResult);
        when(pestIdentificationService.getIdentificationHistory(testUserId, 1, 10))
            .thenReturn(history);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/history/{userId}", testUserId)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pestIdentificationService).getIdentificationHistory(testUserId, 1, 10);
    }
    
    @Test
    void testGetIdentificationResult_Success() throws Exception {
        // Arrange
        when(pestIdentificationService.getIdentificationResult(testResult.getId()))
            .thenReturn(testResult);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/result/{resultId}", testResult.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testResult.getId()));
        
        verify(pestIdentificationService).getIdentificationResult(testResult.getId());
    }
    
    @Test
    void testGetIdentificationResult_NotFound() throws Exception {
        // Arrange
        when(pestIdentificationService.getIdentificationResult("non-existent"))
            .thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/result/{resultId}", "non-existent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("识别结果不存在"));
        
        verify(pestIdentificationService).getIdentificationResult("non-existent");
    }
    
    @Test
    void testDeleteIdentificationResult_Success() throws Exception {
        // Arrange
        when(pestIdentificationService.deleteIdentificationResult(testResult.getId(), testUserId))
            .thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(delete("/api/identification/result/{resultId}", testResult.getId())
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(pestIdentificationService).deleteIdentificationResult(testResult.getId(), testUserId);
    }
    
    @Test
    void testDeleteIdentificationResult_Failed() throws Exception {
        // Arrange
        when(pestIdentificationService.deleteIdentificationResult(testResult.getId(), testUserId))
            .thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(delete("/api/identification/result/{resultId}", testResult.getId())
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败，记录不存在或无权限"));
        
        verify(pestIdentificationService).deleteIdentificationResult(testResult.getId(), testUserId);
    }
    
    @Test
    void testGetIdentificationStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalIdentifications", 10);
        statistics.put("averageConfidence", 0.8);
        
        when(pestIdentificationService.getIdentificationStatistics(testUserId))
            .thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/statistics/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalIdentifications").value(10))
                .andExpect(jsonPath("$.data.averageConfidence").value(0.8));
        
        verify(pestIdentificationService).getIdentificationStatistics(testUserId);
    }
    
    @Test
    void testGetSystemIdentificationStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalIdentifications", 100);
        statistics.put("totalUsers", 20);
        
        when(pestIdentificationService.getSystemIdentificationStatistics())
            .thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/statistics/system"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalIdentifications").value(100))
                .andExpect(jsonPath("$.data.totalUsers").value(20));
        
        verify(pestIdentificationService).getSystemIdentificationStatistics();
    }
    
    @Test
    void testUpdateUserFeedback_Success() throws Exception {
        // Arrange
        FeedbackRequest request = new FeedbackRequest();
        request.setUserId(testUserId);
        request.setCorrect(true);
        request.setFeedback("识别正确");
        
        IdentificationResult updatedResult = new IdentificationResult();
        updatedResult.setId(testResult.getId());
        updatedResult.setUserFeedback("识别正确");
        updatedResult.setFeedbackCorrect(true);
        
        when(pestIdentificationService.updateUserFeedback(
            testResult.getId(), testUserId, true, "识别正确"))
            .thenReturn(updatedResult);
        
        // Act & Assert
        mockMvc.perform(put("/api/identification/feedback/{resultId}", testResult.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userFeedback").value("识别正确"));
        
        verify(pestIdentificationService).updateUserFeedback(
            testResult.getId(), testUserId, true, "识别正确");
    }
    
    @Test
    void testGetAccuracyStatistics_Success() throws Exception {
        // Arrange
        Map<String, Double> accuracy = new HashMap<>();
        accuracy.put("overall", 0.85);
        accuracy.put("图片识别", 0.9);
        
        when(pestIdentificationService.getAccuracyStatistics())
            .thenReturn(accuracy);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/accuracy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.overall").value(0.85))
                .andExpect(jsonPath("$.data.图片识别").value(0.9));
        
        verify(pestIdentificationService).getAccuracyStatistics();
    }
    
    @Test
    void testGetPossiblePestsByHostPlant_Success() throws Exception {
        // Arrange
        List<Pest> pests = Arrays.asList(testPest);
        when(pestIdentificationService.getPossiblePestsByHostPlant("松树"))
            .thenReturn(pests);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/pests/host-plant")
                .param("hostPlant", "松树"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testPest.getId()));
        
        verify(pestIdentificationService).getPossiblePestsByHostPlant("松树");
    }
    
    @Test
    void testGetCommonPestsBySeason_Success() throws Exception {
        // Arrange
        List<Pest> pests = Arrays.asList(testPest);
        when(pestIdentificationService.getCommonPestsBySeason("春季"))
            .thenReturn(pests);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/pests/season")
                .param("season", "春季"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pestIdentificationService).getCommonPestsBySeason("春季");
    }
    
    @Test
    void testGetCommonPestsByRegion_Success() throws Exception {
        // Arrange
        List<Pest> pests = Arrays.asList(testPest);
        when(pestIdentificationService.getCommonPestsByRegion("华北"))
            .thenReturn(pests);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/pests/region")
                .param("region", "华北"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pestIdentificationService).getCommonPestsByRegion("华北");
    }
    
    @Test
    void testFindSimilarIdentifications_Success() throws Exception {
        // Arrange
        List<IdentificationResult> similar = Arrays.asList(testResult);
        when(pestIdentificationService.findSimilarIdentifications(testResult.getId()))
            .thenReturn(similar);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/similar/{resultId}", testResult.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pestIdentificationService).findSimilarIdentifications(testResult.getId());
    }
    
    @Test
    void testBatchIdentifyByImages_Success() throws Exception {
        // Arrange
        MockMultipartFile image1 = new MockMultipartFile(
            "images", "test1.jpg", "image/jpeg", "test image 1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile(
            "images", "test2.jpg", "image/jpeg", "test image 2".getBytes());
        
        List<IdentificationResult> results = Arrays.asList(testResult, testResult);
        when(pestIdentificationService.batchIdentifyByImages(any(), eq(testUserId)))
            .thenReturn(results);
        
        // Act & Assert
        mockMvc.perform(multipart("/api/identification/batch")
                .file(image1)
                .file(image2)
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
        
        verify(pestIdentificationService).batchIdentifyByImages(any(), eq(testUserId));
    }
    
    @Test
    void testGetIdentificationSuggestions_Success() throws Exception {
        // Arrange
        List<String> suggestions = Arrays.asList("叶片发黄", "叶片卷曲", "叶片有斑点");
        when(pestIdentificationService.getIdentificationSuggestions("叶片"))
            .thenReturn(suggestions);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/suggestions")
                .param("partialSymptom", "叶片"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
        
        verify(pestIdentificationService).getIdentificationSuggestions("叶片");
    }
    
    @Test
    void testExportIdentificationHistory_Success() throws Exception {
        // Arrange
        String exportData = "exported data";
        when(pestIdentificationService.exportIdentificationHistory(testUserId, "json"))
            .thenReturn(exportData);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/export/{userId}", testUserId)
                .param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(exportData));
        
        verify(pestIdentificationService).exportIdentificationHistory(testUserId, "json");
    }
    
    @Test
    void testGetPopularIdentifiedPests_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> popular = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("pestId", testPest.getId());
        item.put("pestName", testPest.getName());
        item.put("identificationCount", 10L);
        popular.add(item);
        
        when(pestIdentificationService.getPopularIdentifiedPests(5))
            .thenReturn(popular);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/popular")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].pestId").value(testPest.getId()));
        
        verify(pestIdentificationService).getPopularIdentifiedPests(5);
    }
    
    @Test
    void testGetIdentificationTrends_Success() throws Exception {
        // Arrange
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        List<Map<String, Object>> trendData = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("period", "2024-01");
        item.put("count", 15L);
        trendData.add(item);
        trends.put("identificationTrend", trendData);
        
        when(pestIdentificationService.getIdentificationTrends("monthly"))
            .thenReturn(trends);
        
        // Act & Assert
        mockMvc.perform(get("/api/identification/trends")
                .param("period", "monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.identificationTrend").isArray());
        
        verify(pestIdentificationService).getIdentificationTrends("monthly");
    }
}