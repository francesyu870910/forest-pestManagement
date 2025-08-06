package com.forestpest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestpest.entity.PestPrediction;
import com.forestpest.entity.PestAlert;
import com.forestpest.service.PredictionService;
import com.forestpest.exception.ForestPestSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PredictionController集成测试
 */
@WebMvcTest(PredictionController.class)
class PredictionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PredictionService predictionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private PestPrediction testPrediction;
    private PestAlert testAlert;
    
    @BeforeEach
    void setUp() {
        // 设置测试预测数据
        testPrediction = new PestPrediction();
        testPrediction.setId("pred-1");
        testPrediction.setPestId("pest-1");
        testPrediction.setTargetArea("测试区域");
        testPrediction.setPredictionDate(LocalDate.now().plusDays(7));
        testPrediction.setRiskLevel("高风险");
        testPrediction.setProbability(0.8);
        testPrediction.setInfluencingFactors("温度、湿度、历史数据");
        testPrediction.setRecommendedActions("加强监测，准备防治措施");
        testPrediction.setCreatedTime(LocalDateTime.now());
        testPrediction.setCreatedBy("user-1");
        testPrediction.setStatus("ACTIVE");
        
        // 设置测试预警数据
        testAlert = new PestAlert();
        testAlert.setId("alert-1");
        testAlert.setPredictionId("pred-1");
        testAlert.setAlertLevel("高级");
        testAlert.setAlertTime(LocalDateTime.now());
        testAlert.setTargetAudience("所有用户");
        testAlert.setMessage("预测高风险病虫害，请及时采取防护措施");
        testAlert.setStatus("ACTIVE");
        testAlert.setCreatedTime(LocalDateTime.now());
        testAlert.setCreatedBy("user-1");
    }    
    
    // ========== 预测管理接口测试 ==========
    
    @Test
    void testCreatePrediction_Success() throws Exception {
        // Given
        PredictionController.CreatePredictionRequest request = 
            new PredictionController.CreatePredictionRequest();
        request.setPrediction(testPrediction);
        request.setUserId("user-1");
        
        when(predictionService.createPrediction(any(PestPrediction.class), eq("user-1")))
            .thenReturn(testPrediction);
        
        // When & Then
        mockMvc.perform(post("/api/prediction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("创建预测成功"))
                .andExpect(jsonPath("$.data.id").value("pred-1"))
                .andExpect(jsonPath("$.data.pestId").value("pest-1"));
        
        verify(predictionService).createPrediction(any(PestPrediction.class), eq("user-1"));
    }
    
    @Test
    void testCreatePrediction_ServiceException() throws Exception {
        // Given
        PredictionController.CreatePredictionRequest request = 
            new PredictionController.CreatePredictionRequest();
        request.setPrediction(testPrediction);
        request.setUserId("user-1");
        
        when(predictionService.createPrediction(any(PestPrediction.class), eq("user-1")))
            .thenThrow(new ForestPestSystemException("创建失败"));
        
        // When & Then
        mockMvc.perform(post("/api/prediction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("创建预测失败: 创建失败"));
    }
    
    @Test
    void testGetPrediction_Success() throws Exception {
        // Given
        when(predictionService.getPredictionById("pred-1")).thenReturn(testPrediction);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/pred-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("pred-1"))
                .andExpect(jsonPath("$.data.pestId").value("pest-1"))
                .andExpect(jsonPath("$.data.probability").value(0.8));
        
        verify(predictionService).getPredictionById("pred-1");
    }
    
    @Test
    void testGetPrediction_NotFound() throws Exception {
        // Given
        when(predictionService.getPredictionById("non-existent")).thenReturn(null);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/non-existent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("预测不存在"));
    }
    
    @Test
    void testGetAllPredictions_Success() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getAllPredictions()).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("pred-1"));
        
        verify(predictionService).getAllPredictions();
    }
    
    @Test
    void testGetAllPredictions_WithPagination() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getPredictions(1, 10)).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(predictionService).getPredictions(1, 10);
    }
    
    @Test
    void testGetUserPredictions_Success() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getUserPredictions("user-1")).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].createdBy").value("user-1"));
        
        verify(predictionService).getUserPredictions("user-1");
    }
    
    @Test
    void testUpdatePrediction_Success() throws Exception {
        // Given
        PredictionController.UpdatePredictionRequest request = 
            new PredictionController.UpdatePredictionRequest();
        request.setPrediction(testPrediction);
        request.setUserId("user-1");
        
        when(predictionService.updatePrediction(eq("pred-1"), any(PestPrediction.class), eq("user-1")))
            .thenReturn(testPrediction);
        
        // When & Then
        mockMvc.perform(put("/api/prediction/pred-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新预测成功"));
        
        verify(predictionService).updatePrediction(eq("pred-1"), any(PestPrediction.class), eq("user-1"));
    }
    
    @Test
    void testDeletePrediction_Success() throws Exception {
        // Given
        when(predictionService.deletePrediction("pred-1", "user-1")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/prediction/pred-1")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("删除预测成功"))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(predictionService).deletePrediction("pred-1", "user-1");
    }
    
    @Test
    void testDeletePrediction_Failed() throws Exception {
        // Given
        when(predictionService.deletePrediction("pred-1", "user-1")).thenReturn(false);
        
        // When & Then
        mockMvc.perform(delete("/api/prediction/pred-1")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败，预测不存在或无权限"));
    }
    
    @Test
    void testGetPredictionsByPest_Success() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getPredictionsByPestId("pest-1")).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/pest/pest-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].pestId").value("pest-1"));
        
        verify(predictionService).getPredictionsByPestId("pest-1");
    }
    
    @Test
    void testGetPredictionsByArea_Success() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getPredictionsByArea("测试区域")).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/area/测试区域"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].targetArea").value("测试区域"));
        
        verify(predictionService).getPredictionsByArea("测试区域");
    }
    
    @Test
    void testGetPredictionsByRiskLevel_Success() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getPredictionsByRiskLevel("高风险")).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/risk/高风险"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].riskLevel").value("高风险"));
        
        verify(predictionService).getPredictionsByRiskLevel("高风险");
    }
    
    @Test
    void testGetPredictionsByDateRange_Success() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getPredictionsByDateRange(startDate, endDate)).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/date-range")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(predictionService).getPredictionsByDateRange(startDate, endDate);
    }
    
    @Test
    void testGetHighRiskPredictions_Success() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getHighRiskPredictions()).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/high-risk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].riskLevel").value("高风险"));
        
        verify(predictionService).getHighRiskPredictions();
    }
    
    @Test
    void testGetRecentPredictions_Success() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.getRecentPredictions(5)).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/recent")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(predictionService).getRecentPredictions(5);
    }
    
    @Test
    void testSearchPredictions_Success() throws Exception {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionService.searchPredictions("测试")).thenReturn(predictions);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/search")
                .param("keyword", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(predictionService).searchPredictions("测试");
    } 
   
    // ========== 预警管理接口测试 ==========
    
    @Test
    void testCreateAlert_Success() throws Exception {
        // Given
        PredictionController.CreateAlertRequest request = 
            new PredictionController.CreateAlertRequest();
        request.setAlert(testAlert);
        request.setUserId("user-1");
        
        when(predictionService.createAlert(any(PestAlert.class), eq("user-1")))
            .thenReturn(testAlert);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/alert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("创建预警成功"))
                .andExpect(jsonPath("$.data.id").value("alert-1"))
                .andExpect(jsonPath("$.data.predictionId").value("pred-1"));
        
        verify(predictionService).createAlert(any(PestAlert.class), eq("user-1"));
    }
    
    @Test
    void testCreateAlertFromPrediction_Success() throws Exception {
        // Given
        PredictionController.CreateAlertFromPredictionRequest request = 
            new PredictionController.CreateAlertFromPredictionRequest();
        request.setUserId("user-1");
        request.setAlertConfig(Map.of("targetAudience", "管理员"));
        
        when(predictionService.createAlertFromPrediction(
            eq("pred-1"), eq("user-1"), any(Map.class)))
            .thenReturn(testAlert);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/pred-1/alert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("根据预测创建预警成功"));
        
        verify(predictionService).createAlertFromPrediction(
            eq("pred-1"), eq("user-1"), any(Map.class));
    }
    
    @Test
    void testGetAlert_Success() throws Exception {
        // Given
        when(predictionService.getAlertById("alert-1")).thenReturn(testAlert);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/alert-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("alert-1"))
                .andExpect(jsonPath("$.data.alertLevel").value("高级"));
        
        verify(predictionService).getAlertById("alert-1");
    }
    
    @Test
    void testGetAlert_NotFound() throws Exception {
        // Given
        when(predictionService.getAlertById("non-existent")).thenReturn(null);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/non-existent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("预警不存在"));
    }
    
    @Test
    void testGetAllAlerts_Success() throws Exception {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.getAllAlerts()).thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("alert-1"));
        
        verify(predictionService).getAllAlerts();
    }
    
    @Test
    void testGetUserAlerts_Success() throws Exception {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.getUserAlerts("user-1")).thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].createdBy").value("user-1"));
        
        verify(predictionService).getUserAlerts("user-1");
    }
    
    @Test
    void testUpdateAlert_Success() throws Exception {
        // Given
        PredictionController.UpdateAlertRequest request = 
            new PredictionController.UpdateAlertRequest();
        request.setAlert(testAlert);
        request.setUserId("user-1");
        
        when(predictionService.updateAlert(eq("alert-1"), any(PestAlert.class), eq("user-1")))
            .thenReturn(testAlert);
        
        // When & Then
        mockMvc.perform(put("/api/prediction/alert/alert-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新预警成功"));
        
        verify(predictionService).updateAlert(eq("alert-1"), any(PestAlert.class), eq("user-1"));
    }
    
    @Test
    void testDeleteAlert_Success() throws Exception {
        // Given
        when(predictionService.deleteAlert("alert-1", "user-1")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/prediction/alert/alert-1")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("删除预警成功"));
        
        verify(predictionService).deleteAlert("alert-1", "user-1");
    }
    
    @Test
    void testGetAlertsByPrediction_Success() throws Exception {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.getAlertsByPredictionId("pred-1")).thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/pred-1/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].predictionId").value("pred-1"));
        
        verify(predictionService).getAlertsByPredictionId("pred-1");
    }
    
    @Test
    void testGetAlertsByLevel_Success() throws Exception {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.getAlertsByLevel("高级")).thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/level/高级"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].alertLevel").value("高级"));
        
        verify(predictionService).getAlertsByLevel("高级");
    }
    
    @Test
    void testGetAlertsByStatus_Success() throws Exception {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.getAlertsByStatus("ACTIVE")).thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
        
        verify(predictionService).getAlertsByStatus("ACTIVE");
    }
    
    @Test
    void testGetActiveAlerts_Success() throws Exception {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.getActiveAlerts()).thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(predictionService).getActiveAlerts();
    }
    
    @Test
    void testGetUnhandledAlerts_Success() throws Exception {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.getUnhandledAlerts()).thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/unhandled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(predictionService).getUnhandledAlerts();
    }
    
    @Test
    void testGetUrgentAlerts_Success() throws Exception {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.getUrgentAlerts()).thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/urgent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(predictionService).getUrgentAlerts();
    }
    
    @Test
    void testAcknowledgeAlert_Success() throws Exception {
        // Given
        when(predictionService.acknowledgeAlert("alert-1", "user-1")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/alert/alert-1/acknowledge")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("确认预警成功"));
        
        verify(predictionService).acknowledgeAlert("alert-1", "user-1");
    }
    
    @Test
    void testHandleAlert_Success() throws Exception {
        // Given
        PredictionController.HandleAlertRequest request = 
            new PredictionController.HandleAlertRequest();
        request.setUserId("user-1");
        request.setHandleResult("已处理");
        
        when(predictionService.handleAlert("alert-1", "user-1", "已处理")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/alert/alert-1/handle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("处理预警成功"));
        
        verify(predictionService).handleAlert("alert-1", "user-1", "已处理");
    }    

    // ========== 预测算法接口测试 ==========
    
    @Test
    void testGeneratePredictionFromHistory_Success() throws Exception {
        // Given
        PredictionController.GenerateFromHistoryRequest request = 
            new PredictionController.GenerateFromHistoryRequest();
        request.setPestId("pest-1");
        request.setTargetArea("测试区域");
        request.setParameters(Map.of("historicalOccurrences", 3));
        
        when(predictionService.generatePredictionFromHistory(
            eq("pest-1"), eq("测试区域"), any(Map.class)))
            .thenReturn(testPrediction);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/generate/history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("基于历史数据生成预测成功"));
        
        verify(predictionService).generatePredictionFromHistory(
            eq("pest-1"), eq("测试区域"), any(Map.class));
    }
    
    @Test
    void testGeneratePredictionFromWeather_Success() throws Exception {
        // Given
        PredictionController.GenerateFromWeatherRequest request = 
            new PredictionController.GenerateFromWeatherRequest();
        request.setPestId("pest-1");
        request.setTargetArea("测试区域");
        request.setWeatherData(Map.of("temperature", 25.0, "humidity", 80.0));
        
        when(predictionService.generatePredictionFromWeather(
            eq("pest-1"), eq("测试区域"), any(Map.class)))
            .thenReturn(testPrediction);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/generate/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("基于天气数据生成预测成功"));
        
        verify(predictionService).generatePredictionFromWeather(
            eq("pest-1"), eq("测试区域"), any(Map.class));
    }
    
    @Test
    void testGeneratePredictionFromEnvironment_Success() throws Exception {
        // Given
        PredictionController.GenerateFromEnvironmentRequest request = 
            new PredictionController.GenerateFromEnvironmentRequest();
        request.setPestId("pest-1");
        request.setTargetArea("测试区域");
        request.setEnvironmentData(Map.of("vegetationCoverage", 0.9));
        
        when(predictionService.generatePredictionFromEnvironment(
            eq("pest-1"), eq("测试区域"), any(Map.class)))
            .thenReturn(testPrediction);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/generate/environment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("基于环境因子生成预测成功"));
        
        verify(predictionService).generatePredictionFromEnvironment(
            eq("pest-1"), eq("测试区域"), any(Map.class));
    }
    
    @Test
    void testGenerateComprehensivePrediction_Success() throws Exception {
        // Given
        PredictionController.GenerateComprehensiveRequest request = 
            new PredictionController.GenerateComprehensiveRequest();
        request.setPestId("pest-1");
        request.setTargetArea("测试区域");
        request.setAllData(Map.of("historicalData", true, "weatherData", true));
        
        when(predictionService.generateComprehensivePrediction(
            eq("pest-1"), eq("测试区域"), any(Map.class)))
            .thenReturn(testPrediction);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/generate/comprehensive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("综合预测生成成功"));
        
        verify(predictionService).generateComprehensivePrediction(
            eq("pest-1"), eq("测试区域"), any(Map.class));
    }
    
    @Test
    void testEvaluatePredictionAccuracy_Success() throws Exception {
        // Given
        PredictionController.EvaluateAccuracyRequest request = 
            new PredictionController.EvaluateAccuracyRequest();
        request.setActualData(Map.of("occurred", true));
        
        Map<String, Object> evaluation = Map.of(
            "predictionId", "pred-1",
            "accuracy", 0.85,
            "accuracyLevel", "高"
        );
        
        when(predictionService.evaluatePredictionAccuracy(eq("pred-1"), any(Map.class)))
            .thenReturn(evaluation);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/pred-1/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.predictionId").value("pred-1"))
                .andExpect(jsonPath("$.data.accuracy").value(0.85));
        
        verify(predictionService).evaluatePredictionAccuracy(eq("pred-1"), any(Map.class));
    }
    
    // ========== 预警触发接口测试 ==========
    
    @Test
    void testCheckAlertTriggers_Success() throws Exception {
        // Given
        List<PestAlert> triggeredAlerts = Arrays.asList(testAlert);
        when(predictionService.checkAlertTriggers()).thenReturn(triggeredAlerts);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/alert/check-triggers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("alert-1"));
        
        verify(predictionService).checkAlertTriggers();
    }
    
    @Test
    void testTriggerAlertsAutomatically_Success() throws Exception {
        // Given
        List<PestAlert> triggeredAlerts = Arrays.asList(testAlert);
        when(predictionService.triggerAlertsAutomatically()).thenReturn(triggeredAlerts);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/alert/trigger-auto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("自动触发预警成功"));
        
        verify(predictionService).triggerAlertsAutomatically();
    }
    
    @Test
    void testTriggerAlertFromPrediction_Success() throws Exception {
        // Given
        when(predictionService.triggerAlertFromPrediction("pred-1", "手动触发"))
            .thenReturn(testAlert);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/pred-1/trigger-alert")
                .param("triggerReason", "手动触发"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("触发预警成功"));
        
        verify(predictionService).triggerAlertFromPrediction("pred-1", "手动触发");
    }
    
    @Test
    void testBatchTriggerAlerts_Success() throws Exception {
        // Given
        PredictionController.BatchTriggerAlertsRequest request = 
            new PredictionController.BatchTriggerAlertsRequest();
        request.setPredictionIds(Arrays.asList("pred-1", "pred-2"));
        request.setTriggerReason("批量触发");
        
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionService.batchTriggerAlerts(anyList(), eq("批量触发")))
            .thenReturn(alerts);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/alert/batch-trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("批量触发预警成功"));
        
        verify(predictionService).batchTriggerAlerts(anyList(), eq("批量触发"));
    }
    
    @Test
    void testSetAlertRule_Success() throws Exception {
        // Given
        PredictionController.SetAlertRuleRequest request = 
            new PredictionController.SetAlertRuleRequest();
        request.setRuleId("test-rule");
        request.setRuleConfig(Map.of("condition", "riskLevel >= 高风险"));
        
        when(predictionService.setAlertRule(eq("test-rule"), any(Map.class)))
            .thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/alert/rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("设置预警规则成功"));
        
        verify(predictionService).setAlertRule(eq("test-rule"), any(Map.class));
    }
    
    @Test
    void testGetAlertRules_Success() throws Exception {
        // Given
        List<Map<String, Object>> rules = Arrays.asList(
            Map.of("id", "rule-1", "name", "高风险自动预警")
        );
        when(predictionService.getAlertRules()).thenReturn(rules);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("rule-1"));
        
        verify(predictionService).getAlertRules();
    }
    
    @Test
    void testDeleteAlertRule_Success() throws Exception {
        // Given
        when(predictionService.deleteAlertRule("rule-1")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/prediction/alert/rule/rule-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("删除预警规则成功"));
        
        verify(predictionService).deleteAlertRule("rule-1");
    }
    
    // ========== 统计分析接口测试 ==========
    
    @Test
    void testGetPredictionStatistics_Success() throws Exception {
        // Given
        Map<String, Object> statistics = Map.of(
            "totalPredictions", 100,
            "averageProbability", 0.65,
            "riskLevelStats", Map.of("高风险", 20L, "中风险", 50L, "低风险", 30L)
        );
        when(predictionService.getPredictionStatistics()).thenReturn(statistics);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPredictions").value(100))
                .andExpect(jsonPath("$.data.averageProbability").value(0.65));
        
        verify(predictionService).getPredictionStatistics();
    }
    
    @Test
    void testGetUserPredictionStatistics_Success() throws Exception {
        // Given
        Map<String, Object> statistics = Map.of(
            "totalPredictions", 10,
            "averageProbability", 0.7
        );
        when(predictionService.getUserPredictionStatistics("user-1")).thenReturn(statistics);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/statistics/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPredictions").value(10));
        
        verify(predictionService).getUserPredictionStatistics("user-1");
    }
    
    @Test
    void testGetAlertStatistics_Success() throws Exception {
        // Given
        Map<String, Object> statistics = Map.of(
            "totalAlerts", 50,
            "alertLevelStats", Map.of("紧急", 5L, "高级", 15L, "中级", 30L)
        );
        when(predictionService.getAlertStatistics()).thenReturn(statistics);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/alert/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalAlerts").value(50));
        
        verify(predictionService).getAlertStatistics();
    }
    
    @Test
    void testGetRiskLevelDistribution_Success() throws Exception {
        // Given
        Map<String, Long> distribution = Map.of(
            "高风险", 20L,
            "中风险", 50L,
            "低风险", 30L
        );
        when(predictionService.getRiskLevelDistribution()).thenReturn(distribution);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/statistics/risk-distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.高风险").value(20))
                .andExpect(jsonPath("$.data.中风险").value(50));
        
        verify(predictionService).getRiskLevelDistribution();
    }
    
    @Test
    void testGetPredictionTrends_Success() throws Exception {
        // Given
        Map<String, List<Map<String, Object>>> trends = Map.of(
            "countTrend", Arrays.asList(
                Map.of("period", "2024-01", "count", 10L),
                Map.of("period", "2024-02", "count", 15L)
            )
        );
        when(predictionService.getPredictionTrends("monthly")).thenReturn(trends);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/trends")
                .param("period", "monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.countTrend").isArray())
                .andExpect(jsonPath("$.data.countTrend[0].period").value("2024-01"));
        
        verify(predictionService).getPredictionTrends("monthly");
    }
    
    // ========== 模型管理接口测试 ==========
    
    @Test
    void testGetPredictionModels_Success() throws Exception {
        // Given
        List<Map<String, Object>> models = Arrays.asList(
            Map.of("id", "model-1", "name", "历史数据模型", "accuracy", 0.85)
        );
        when(predictionService.getPredictionModels()).thenReturn(models);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/model"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("model-1"));
        
        verify(predictionService).getPredictionModels();
    }
    
    @Test
    void testGetPredictionModelDetails_Success() throws Exception {
        // Given
        Map<String, Object> modelDetails = Map.of(
            "id", "model-1",
            "name", "历史数据模型",
            "accuracy", 0.85,
            "version", 1
        );
        when(predictionService.getPredictionModelDetails("model-1")).thenReturn(modelDetails);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/model/model-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("model-1"))
                .andExpect(jsonPath("$.data.accuracy").value(0.85));
        
        verify(predictionService).getPredictionModelDetails("model-1");
    }
    
    @Test
    void testTrainPredictionModel_Success() throws Exception {
        // Given
        PredictionController.TrainModelRequest request = 
            new PredictionController.TrainModelRequest();
        request.setTrainingData(Map.of("samples", 1000));
        
        when(predictionService.trainPredictionModel(eq("model-1"), any(Map.class)))
            .thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/model/model-1/train")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("训练预测模型成功"));
        
        verify(predictionService).trainPredictionModel(eq("model-1"), any(Map.class));
    }
    
    @Test
    void testDeployPredictionModel_Success() throws Exception {
        // Given
        when(predictionService.deployPredictionModel("model-1")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/prediction/model/model-1/deploy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("部署预测模型成功"));
        
        verify(predictionService).deployPredictionModel("model-1");
    }
    
    // ========== 数据导出接口测试 ==========
    
    @Test
    void testExportPredictionData_Success() throws Exception {
        // Given
        String exportData = "导出的预测数据";
        when(predictionService.exportPredictionData(eq("json"), any(Map.class)))
            .thenReturn(exportData);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/export/data")
                .param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(exportData));
        
        verify(predictionService).exportPredictionData(eq("json"), any(Map.class));
    }
    
    @Test
    void testExportPredictionReport_Success() throws Exception {
        // Given
        String report = "预测报告内容";
        when(predictionService.exportPredictionReport("pred-1", "json"))
            .thenReturn(report);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/pred-1/export/report")
                .param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(report));
        
        verify(predictionService).exportPredictionReport("pred-1", "json");
    }
    
    // ========== 配置管理接口测试 ==========
    
    @Test
    void testGetPredictionConfig_Success() throws Exception {
        // Given
        Map<String, Object> config = Map.of(
            "defaultValidityPeriod", "7天",
            "minProbabilityThreshold", 0.1
        );
        when(predictionService.getPredictionConfig()).thenReturn(config);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.defaultValidityPeriod").value("7天"));
        
        verify(predictionService).getPredictionConfig();
    }
    
    @Test
    void testUpdatePredictionConfig_Success() throws Exception {
        // Given
        Map<String, Object> config = Map.of(
            "defaultValidityPeriod", "10天",
            "minProbabilityThreshold", 0.2
        );
        when(predictionService.updatePredictionConfig(any(Map.class))).thenReturn(true);
        
        // When & Then
        mockMvc.perform(put("/api/prediction/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(config)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新预测配置成功"));
        
        verify(predictionService).updatePredictionConfig(any(Map.class));
    }
    
    @Test
    void testPerformHealthCheck_Success() throws Exception {
        // Given
        Map<String, Object> health = Map.of(
            "status", "healthy",
            "predictionCount", 100L,
            "alertCount", 50L
        );
        when(predictionService.performHealthCheck()).thenReturn(health);
        
        // When & Then
        mockMvc.perform(get("/api/prediction/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("healthy"))
                .andExpect(jsonPath("$.data.predictionCount").value(100));
        
        verify(predictionService).performHealthCheck();
    }
}