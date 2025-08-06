package com.forestpest.service;

import com.forestpest.entity.PestPrediction;
import com.forestpest.entity.PestAlert;
import com.forestpest.repository.PredictionRepository;
import com.forestpest.service.impl.PredictionServiceImpl;
import com.forestpest.exception.ForestPestSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PredictionService单元测试
 */
@ExtendWith(MockitoExtension.class)
class PredictionServiceTest {
    
    @Mock
    private PredictionRepository predictionRepository;
    
    @InjectMocks
    private PredictionServiceImpl predictionService;
    
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
  
    // ========== 预测管理测试 ==========
    
    @Test
    void testCreatePrediction_Success() {
        // Given
        when(predictionRepository.savePrediction(any(PestPrediction.class))).thenReturn(testPrediction);
        
        // When
        PestPrediction result = predictionService.createPrediction(testPrediction, "user-1");
        
        // Then
        assertNotNull(result);
        assertEquals(testPrediction.getPestId(), result.getPestId());
        assertEquals(testPrediction.getTargetArea(), result.getTargetArea());
        assertEquals("user-1", result.getCreatedBy());
        assertEquals("ACTIVE", result.getStatus());
        verify(predictionRepository).savePrediction(any(PestPrediction.class));
    }
    
    @Test
    void testCreatePrediction_NullPrediction() {
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.createPrediction(null, "user-1"));
        assertEquals("预测数据不能为空", exception.getMessage());
    }
    
    @Test
    void testCreatePrediction_InvalidPrediction() {
        // Given
        testPrediction.setPestId(""); // 无效的病虫害ID
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.createPrediction(testPrediction, "user-1"));
        assertEquals("预测数据验证失败", exception.getMessage());
    }
    
    @Test
    void testGetPredictionById_Success() {
        // Given
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        
        // When
        PestPrediction result = predictionService.getPredictionById("pred-1");
        
        // Then
        assertNotNull(result);
        assertEquals("pred-1", result.getId());
        assertEquals("pest-1", result.getPestId());
    }
    
    @Test
    void testGetPredictionById_NotFound() {
        // Given
        when(predictionRepository.findPredictionById("non-existent")).thenReturn(Optional.empty());
        
        // When
        PestPrediction result = predictionService.getPredictionById("non-existent");
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testGetAllPredictions() {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionRepository.findAllPredictions()).thenReturn(predictions);
        
        // When
        List<PestPrediction> result = predictionService.getAllPredictions();
        
        // Then
        assertEquals(1, result.size());
        assertEquals("pred-1", result.get(0).getId());
    }
    
    @Test
    void testGetUserPredictions() {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionRepository.findPredictionsByPredictedBy("user-1")).thenReturn(predictions);
        
        // When
        List<PestPrediction> result = predictionService.getUserPredictions("user-1");
        
        // Then
        assertEquals(1, result.size());
        assertEquals("user-1", result.get(0).getCreatedBy());
    }
    
    @Test
    void testUpdatePrediction_Success() {
        // Given
        PestPrediction updatedPrediction = new PestPrediction();
        updatedPrediction.setTargetArea("更新的区域");
        updatedPrediction.setRiskLevel("中风险");
        updatedPrediction.setProbability(0.6);
        
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        when(predictionRepository.savePrediction(any(PestPrediction.class))).thenReturn(testPrediction);
        
        // When
        PestPrediction result = predictionService.updatePrediction("pred-1", updatedPrediction, "user-1");
        
        // Then
        assertNotNull(result);
        verify(predictionRepository).savePrediction(any(PestPrediction.class));
    }
    
    @Test
    void testUpdatePrediction_NotFound() {
        // Given
        when(predictionRepository.findPredictionById("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.updatePrediction("non-existent", testPrediction, "user-1"));
        assertEquals("预测不存在", exception.getMessage());
    }
    
    @Test
    void testUpdatePrediction_NoPermission() {
        // Given
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.updatePrediction("pred-1", testPrediction, "other-user"));
        assertEquals("无权限修改此预测", exception.getMessage());
    }
    
    @Test
    void testDeletePrediction_Success() {
        // Given
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        doNothing().when(predictionRepository).deletePredictionById("pred-1");
        
        // When
        boolean result = predictionService.deletePrediction("pred-1", "user-1");
        
        // Then
        assertTrue(result);
        verify(predictionRepository).deletePredictionById("pred-1");
    }
    
    @Test
    void testDeletePrediction_NotFound() {
        // Given
        when(predictionRepository.findPredictionById("non-existent")).thenReturn(Optional.empty());
        
        // When
        boolean result = predictionService.deletePrediction("non-existent", "user-1");
        
        // Then
        assertFalse(result);
        verify(predictionRepository, never()).deletePredictionById(anyString());
    }
    
    @Test
    void testDeletePrediction_NoPermission() {
        // Given
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        
        // When
        boolean result = predictionService.deletePrediction("pred-1", "other-user");
        
        // Then
        assertFalse(result);
        verify(predictionRepository, never()).deletePredictionById(anyString());
    }    

    // ========== 预警管理测试 ==========
    
    @Test
    void testCreateAlert_Success() {
        // Given
        when(predictionRepository.saveAlert(any(PestAlert.class))).thenReturn(testAlert);
        
        // When
        PestAlert result = predictionService.createAlert(testAlert, "user-1");
        
        // Then
        assertNotNull(result);
        assertEquals(testAlert.getPredictionId(), result.getPredictionId());
        assertEquals(testAlert.getAlertLevel(), result.getAlertLevel());
        assertEquals("user-1", result.getCreatedBy());
        assertEquals("ACTIVE", result.getStatus());
        verify(predictionRepository).saveAlert(any(PestAlert.class));
    }
    
    @Test
    void testCreateAlert_NullAlert() {
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.createAlert(null, "user-1"));
        assertEquals("预警数据不能为空", exception.getMessage());
    }
    
    @Test
    void testCreateAlert_InvalidAlert() {
        // Given
        testAlert.setMessage(""); // 无效的消息
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.createAlert(testAlert, "user-1"));
        assertEquals("预警数据验证失败", exception.getMessage());
    }
    
    @Test
    void testCreateAlertFromPrediction_Success() {
        // Given
        Map<String, Object> alertConfig = new HashMap<>();
        alertConfig.put("targetAudience", "管理员");
        
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        when(predictionRepository.saveAlert(any(PestAlert.class))).thenReturn(testAlert);
        
        // When
        PestAlert result = predictionService.createAlertFromPrediction("pred-1", "user-1", alertConfig);
        
        // Then
        assertNotNull(result);
        assertEquals("pred-1", result.getPredictionId());
        verify(predictionRepository).saveAlert(any(PestAlert.class));
    }
    
    @Test
    void testCreateAlertFromPrediction_PredictionNotFound() {
        // Given
        when(predictionRepository.findPredictionById("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.createAlertFromPrediction("non-existent", "user-1", new HashMap<>()));
        assertEquals("预测不存在", exception.getMessage());
    }
    
    @Test
    void testGetAlertById_Success() {
        // Given
        when(predictionRepository.findAlertById("alert-1")).thenReturn(Optional.of(testAlert));
        
        // When
        PestAlert result = predictionService.getAlertById("alert-1");
        
        // Then
        assertNotNull(result);
        assertEquals("alert-1", result.getId());
        assertEquals("pred-1", result.getPredictionId());
    }
    
    @Test
    void testGetAlertById_NotFound() {
        // Given
        when(predictionRepository.findAlertById("non-existent")).thenReturn(Optional.empty());
        
        // When
        PestAlert result = predictionService.getAlertById("non-existent");
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testGetAllAlerts() {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionRepository.findAllAlerts()).thenReturn(alerts);
        
        // When
        List<PestAlert> result = predictionService.getAllAlerts();
        
        // Then
        assertEquals(1, result.size());
        assertEquals("alert-1", result.get(0).getId());
    }
    
    @Test
    void testUpdateAlert_Success() {
        // Given
        PestAlert updatedAlert = new PestAlert();
        updatedAlert.setAlertLevel("中级");
        updatedAlert.setMessage("更新的预警消息");
        
        when(predictionRepository.findAlertById("alert-1")).thenReturn(Optional.of(testAlert));
        when(predictionRepository.saveAlert(any(PestAlert.class))).thenReturn(testAlert);
        
        // When
        PestAlert result = predictionService.updateAlert("alert-1", updatedAlert, "user-1");
        
        // Then
        assertNotNull(result);
        verify(predictionRepository).saveAlert(any(PestAlert.class));
    }
    
    @Test
    void testUpdateAlert_NotFound() {
        // Given
        when(predictionRepository.findAlertById("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.updateAlert("non-existent", testAlert, "user-1"));
        assertEquals("预警不存在", exception.getMessage());
    }
    
    @Test
    void testDeleteAlert_Success() {
        // Given
        when(predictionRepository.findAlertById("alert-1")).thenReturn(Optional.of(testAlert));
        doNothing().when(predictionRepository).deleteAlertById("alert-1");
        
        // When
        boolean result = predictionService.deleteAlert("alert-1", "user-1");
        
        // Then
        assertTrue(result);
        verify(predictionRepository).deleteAlertById("alert-1");
    }
    
    @Test
    void testAcknowledgeAlert_Success() {
        // Given
        when(predictionRepository.findAlertById("alert-1")).thenReturn(Optional.of(testAlert));
        when(predictionRepository.saveAlert(any(PestAlert.class))).thenReturn(testAlert);
        
        // When
        boolean result = predictionService.acknowledgeAlert("alert-1", "user-1");
        
        // Then
        assertTrue(result);
        verify(predictionRepository).saveAlert(any(PestAlert.class));
    }
    
    @Test
    void testHandleAlert_Success() {
        // Given
        when(predictionRepository.findAlertById("alert-1")).thenReturn(Optional.of(testAlert));
        when(predictionRepository.saveAlert(any(PestAlert.class))).thenReturn(testAlert);
        
        // When
        boolean result = predictionService.handleAlert("alert-1", "user-1", "已处理");
        
        // Then
        assertTrue(result);
        verify(predictionRepository).saveAlert(any(PestAlert.class));
    }    
    
    // ========== 预测算法测试 ==========
    
    @Test
    void testGeneratePredictionFromHistory() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("historicalOccurrences", 3);
        
        // When
        PestPrediction result = predictionService.generatePredictionFromHistory("pest-1", "测试区域", parameters);
        
        // Then
        assertNotNull(result);
        assertEquals("pest-1", result.getPestId());
        assertEquals("测试区域", result.getTargetArea());
        assertEquals("历史数据模型", result.getPredictionModel());
        assertTrue(result.getProbability() >= 0.0 && result.getProbability() <= 1.0);
        assertNotNull(result.getRiskLevel());
    }
    
    @Test
    void testGeneratePredictionFromWeather() {
        // Given
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("temperature", 25.0);
        weatherData.put("humidity", 80.0);
        weatherData.put("rainfall", 15.0);
        
        // When
        PestPrediction result = predictionService.generatePredictionFromWeather("pest-1", "测试区域", weatherData);
        
        // Then
        assertNotNull(result);
        assertEquals("pest-1", result.getPestId());
        assertEquals("测试区域", result.getTargetArea());
        assertEquals("天气预测模型", result.getPredictionModel());
        assertTrue(result.getProbability() >= 0.0 && result.getProbability() <= 1.0);
        assertNotNull(result.getWeatherConditions());
    }
    
    @Test
    void testGeneratePredictionFromEnvironment() {
        // Given
        Map<String, Object> environmentData = new HashMap<>();
        environmentData.put("vegetationCoverage", 0.9);
        environmentData.put("soilMoisture", 0.7);
        
        // When
        PestPrediction result = predictionService.generatePredictionFromEnvironment("pest-1", "测试区域", environmentData);
        
        // Then
        assertNotNull(result);
        assertEquals("pest-1", result.getPestId());
        assertEquals("测试区域", result.getTargetArea());
        assertEquals("环境因子模型", result.getPredictionModel());
        assertTrue(result.getProbability() >= 0.0 && result.getProbability() <= 1.0);
    }
    
    @Test
    void testGenerateComprehensivePrediction() {
        // Given
        Map<String, Object> allData = new HashMap<>();
        allData.put("historicalData", true);
        allData.put("historicalProbability", 0.6);
        allData.put("weatherData", true);
        allData.put("weatherProbability", 0.7);
        allData.put("environmentData", true);
        allData.put("environmentProbability", 0.5);
        
        // When
        PestPrediction result = predictionService.generateComprehensivePrediction("pest-1", "测试区域", allData);
        
        // Then
        assertNotNull(result);
        assertEquals("pest-1", result.getPestId());
        assertEquals("测试区域", result.getTargetArea());
        assertEquals("综合预测模型", result.getPredictionModel());
        assertTrue(result.getProbability() >= 0.0 && result.getProbability() <= 1.0);
        assertEquals("高", result.getConfidence());
    }
    
    @Test
    void testCalculateRiskLevel() {
        // Given
        Map<String, Object> factors = new HashMap<>();
        
        // When & Then
        assertEquals("极高风险", predictionService.calculateRiskLevel(0.9, factors));
        assertEquals("高风险", predictionService.calculateRiskLevel(0.7, factors));
        assertEquals("中风险", predictionService.calculateRiskLevel(0.5, factors));
        assertEquals("低风险", predictionService.calculateRiskLevel(0.3, factors));
        assertEquals("极低风险", predictionService.calculateRiskLevel(0.1, factors));
    }
    
    @Test
    void testCalculateProbability() {
        // Given
        Map<String, Object> factors = new HashMap<>();
        factors.put("temperature", 25.0);
        factors.put("humidity", 75.0);
        
        // When
        double result = predictionService.calculateProbability("pest-1", "测试区域", factors);
        
        // Then
        assertTrue(result >= 0.0 && result <= 1.0);
    }
    
    @Test
    void testEvaluatePredictionAccuracy() {
        // Given
        Map<String, Object> actualData = new HashMap<>();
        actualData.put("occurred", true);
        
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        
        // When
        Map<String, Object> result = predictionService.evaluatePredictionAccuracy("pred-1", actualData);
        
        // Then
        assertNotNull(result);
        assertEquals("pred-1", result.get("predictionId"));
        assertEquals(0.8, result.get("predictedProbability"));
        assertEquals(true, result.get("actualOccurred"));
        assertTrue(result.containsKey("accuracy"));
        assertTrue(result.containsKey("accuracyLevel"));
    }
    
    @Test
    void testEvaluatePredictionAccuracy_PredictionNotFound() {
        // Given
        when(predictionRepository.findPredictionById("non-existent")).thenReturn(Optional.empty());
        
        // When
        Map<String, Object> result = predictionService.evaluatePredictionAccuracy("non-existent", new HashMap<>());
        
        // Then
        assertEquals("预测不存在", result.get("error"));
    }    

    // ========== 预警触发测试 ==========
    
    @Test
    void testCheckAlertTriggers() {
        // Given
        List<PestPrediction> highRiskPredictions = Arrays.asList(testPrediction);
        when(predictionRepository.findHighRiskPredictions()).thenReturn(highRiskPredictions);
        when(predictionRepository.findAlertsByPredictionId("pred-1")).thenReturn(new ArrayList<>());
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        when(predictionRepository.saveAlert(any(PestAlert.class))).thenReturn(testAlert);
        
        // When
        List<PestAlert> result = predictionService.checkAlertTriggers();
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testTriggerAlertFromPrediction_Success() {
        // Given
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        when(predictionRepository.saveAlert(any(PestAlert.class))).thenReturn(testAlert);
        
        // When
        PestAlert result = predictionService.triggerAlertFromPrediction("pred-1", "测试触发");
        
        // Then
        assertNotNull(result);
        assertEquals("pred-1", result.getPredictionId());
        verify(predictionRepository).saveAlert(any(PestAlert.class));
    }
    
    @Test
    void testTriggerAlertFromPrediction_PredictionNotFound() {
        // Given
        when(predictionRepository.findPredictionById("non-existent")).thenReturn(Optional.empty());
        
        // When
        PestAlert result = predictionService.triggerAlertFromPrediction("non-existent", "测试触发");
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testBatchTriggerAlerts() {
        // Given
        List<String> predictionIds = Arrays.asList("pred-1", "pred-2");
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        when(predictionRepository.findPredictionById("pred-2")).thenReturn(Optional.of(testPrediction));
        when(predictionRepository.saveAlert(any(PestAlert.class))).thenReturn(testAlert);
        
        // When
        List<PestAlert> result = predictionService.batchTriggerAlerts(predictionIds, "批量触发");
        
        // Then
        assertEquals(2, result.size());
        verify(predictionRepository, times(2)).saveAlert(any(PestAlert.class));
    }
    
    @Test
    void testSetAlertRule() {
        // Given
        Map<String, Object> ruleConfig = new HashMap<>();
        ruleConfig.put("condition", "riskLevel >= 高风险");
        ruleConfig.put("action", "自动创建预警");
        
        // When
        boolean result = predictionService.setAlertRule("test-rule", ruleConfig);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testGetAlertRules() {
        // When
        List<Map<String, Object>> result = predictionService.getAlertRules();
        
        // Then
        assertNotNull(result);
        // 应该包含初始化的默认规则
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testDeleteAlertRule() {
        // Given
        Map<String, Object> ruleConfig = new HashMap<>();
        predictionService.setAlertRule("test-rule", ruleConfig);
        
        // When
        boolean result = predictionService.deleteAlertRule("test-rule");
        
        // Then
        assertTrue(result);
    }
    
    // ========== 通知发送测试 ==========
    
    @Test
    void testSendAlertNotification_Success() {
        // Given
        List<String> recipients = Arrays.asList("user1", "user2");
        when(predictionRepository.findAlertById("alert-1")).thenReturn(Optional.of(testAlert));
        
        // When
        boolean result = predictionService.sendAlertNotification("alert-1", recipients);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testSendAlertNotification_AlertNotFound() {
        // Given
        when(predictionRepository.findAlertById("non-existent")).thenReturn(Optional.empty());
        
        // When
        boolean result = predictionService.sendAlertNotification("non-existent", Arrays.asList("user1"));
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testBatchSendAlertNotifications() {
        // Given
        List<String> alertIds = Arrays.asList("alert-1", "alert-2");
        List<String> recipients = Arrays.asList("user1", "user2");
        when(predictionRepository.findAlertById(anyString())).thenReturn(Optional.of(testAlert));
        
        // When
        Map<String, Boolean> result = predictionService.batchSendAlertNotifications(alertIds, recipients);
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.get("alert-1"));
        assertTrue(result.get("alert-2"));
    }
    
    @Test
    void testSetNotificationPreference() {
        // Given
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("email", true);
        preferences.put("sms", false);
        preferences.put("system", true);
        
        // When
        boolean result = predictionService.setNotificationPreference("user-1", preferences);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testGetNotificationPreference() {
        // Given
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("email", true);
        predictionService.setNotificationPreference("user-1", preferences);
        
        // When
        Map<String, Object> result = predictionService.getNotificationPreference("user-1");
        
        // Then
        assertNotNull(result);
        assertEquals(true, result.get("email"));
    }
    
    @Test
    void testGetNotificationHistory() {
        // When
        List<Map<String, Object>> result = predictionService.getNotificationHistory("alert-1");
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }    

    // ========== 统计分析测试 ==========
    
    @Test
    void testGetPredictionStatistics() {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionRepository.findAllPredictions()).thenReturn(predictions);
        
        Map<String, Long> riskLevelStats = new HashMap<>();
        riskLevelStats.put("高风险", 1L);
        when(predictionRepository.countPredictionsByRiskLevel()).thenReturn(riskLevelStats);
        
        // When
        Map<String, Object> result = predictionService.getPredictionStatistics();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("totalPredictions"));
        assertTrue(result.containsKey("riskLevelStats"));
        assertTrue(result.containsKey("monthlyStats"));
        assertTrue(result.containsKey("averageProbability"));
    }
    
    @Test
    void testGetUserPredictionStatistics() {
        // Given
        List<PestPrediction> userPredictions = Arrays.asList(testPrediction);
        when(predictionRepository.findPredictionsByPredictedBy("user-1")).thenReturn(userPredictions);
        
        // When
        Map<String, Object> result = predictionService.getUserPredictionStatistics("user-1");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("totalPredictions"));
        assertTrue(result.containsKey("riskLevelStats"));
        assertTrue(result.containsKey("averageProbability"));
    }
    
    @Test
    void testGetAlertStatistics() {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionRepository.findAllAlerts()).thenReturn(alerts);
        
        Map<String, Long> alertLevelStats = new HashMap<>();
        alertLevelStats.put("高级", 1L);
        when(predictionRepository.countAlertsByLevel()).thenReturn(alertLevelStats);
        
        Map<String, Long> areaStats = new HashMap<>();
        areaStats.put("测试区域", 1L);
        when(predictionRepository.countAlertsByArea()).thenReturn(areaStats);
        
        // When
        Map<String, Object> result = predictionService.getAlertStatistics();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("totalAlerts"));
        assertTrue(result.containsKey("alertLevelStats"));
        assertTrue(result.containsKey("statusStats"));
        assertTrue(result.containsKey("areaStats"));
    }
    
    @Test
    void testGetUserAlertStatistics() {
        // Given
        List<PestAlert> userAlerts = Arrays.asList(testAlert);
        when(predictionRepository.findAllAlerts()).thenReturn(userAlerts);
        
        // When
        Map<String, Object> result = predictionService.getUserAlertStatistics("user-1");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("totalAlerts"));
        assertTrue(result.containsKey("alertLevelStats"));
        assertTrue(result.containsKey("statusStats"));
    }
    
    @Test
    void testGetPredictionAccuracyStatistics() {
        // When
        Map<String, Object> result = predictionService.getPredictionAccuracyStatistics();
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("overallAccuracy"));
        assertTrue(result.containsKey("modelAccuracies"));
    }
    
    @Test
    void testGetAlertResponseStatistics() {
        // Given
        when(predictionRepository.countAlerts()).thenReturn(10L);
        when(predictionRepository.findActiveAlerts()).thenReturn(Arrays.asList(testAlert));
        
        // When
        Map<String, Object> result = predictionService.getAlertResponseStatistics();
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("totalAlerts"));
        assertTrue(result.containsKey("acknowledgedAlerts"));
        assertTrue(result.containsKey("averageResponseTime"));
    }
    
    @Test
    void testGetRiskLevelDistribution() {
        // Given
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("高风险", 5L);
        distribution.put("中风险", 10L);
        distribution.put("低风险", 15L);
        when(predictionRepository.countPredictionsByRiskLevel()).thenReturn(distribution);
        
        // When
        Map<String, Long> result = predictionService.getRiskLevelDistribution();
        
        // Then
        assertNotNull(result);
        assertEquals(5L, result.get("高风险").longValue());
        assertEquals(10L, result.get("中风险").longValue());
        assertEquals(15L, result.get("低风险").longValue());
    }
    
    @Test
    void testGetAlertLevelDistribution() {
        // Given
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("紧急", 2L);
        distribution.put("高级", 5L);
        distribution.put("中级", 8L);
        when(predictionRepository.countAlertsByLevel()).thenReturn(distribution);
        
        // When
        Map<String, Long> result = predictionService.getAlertLevelDistribution();
        
        // Then
        assertNotNull(result);
        assertEquals(2L, result.get("紧急").longValue());
        assertEquals(5L, result.get("高级").longValue());
        assertEquals(8L, result.get("中级").longValue());
    }
    
    @Test
    void testGetPredictionTrends() {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionRepository.findAllPredictions()).thenReturn(predictions);
        
        // When
        Map<String, List<Map<String, Object>>> result = predictionService.getPredictionTrends("monthly");
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("countTrend"));
    }
    
    @Test
    void testGetAlertTrends() {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionRepository.findAllAlerts()).thenReturn(alerts);
        
        // When
        Map<String, List<Map<String, Object>>> result = predictionService.getAlertTrends("monthly");
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("countTrend"));
    }
    
    // ========== 模型管理测试 ==========
    
    @Test
    void testGetPredictionModels() {
        // When
        List<Map<String, Object>> result = predictionService.getPredictionModels();
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // 应该包含初始化的默认模型
        assertTrue(result.stream().anyMatch(model -> "历史数据预测模型".equals(model.get("name"))));
    }
    
    @Test
    void testGetPredictionModelDetails() {
        // When
        Map<String, Object> result = predictionService.getPredictionModelDetails("history-model");
        
        // Then
        assertNotNull(result);
        assertEquals("history-model", result.get("id"));
        assertEquals("历史数据预测模型", result.get("name"));
    }
    
    @Test
    void testTrainPredictionModel() {
        // Given
        Map<String, Object> trainingData = new HashMap<>();
        trainingData.put("samples", 1000);
        trainingData.put("features", Arrays.asList("temperature", "humidity"));
        
        // When
        boolean result = predictionService.trainPredictionModel("history-model", trainingData);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testEvaluatePredictionModel() {
        // Given
        Map<String, Object> testData = new HashMap<>();
        testData.put("testSamples", 200);
        
        // When
        Map<String, Object> result = predictionService.evaluatePredictionModel("history-model", testData);
        
        // Then
        assertNotNull(result);
        assertEquals("history-model", result.get("modelId"));
        assertTrue(result.containsKey("accuracy"));
        assertTrue(result.containsKey("precision"));
        assertTrue(result.containsKey("recall"));
    }
    
    @Test
    void testDeployPredictionModel() {
        // When
        boolean result = predictionService.deployPredictionModel("history-model");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testDeployPredictionModel_ModelNotFound() {
        // When
        boolean result = predictionService.deployPredictionModel("non-existent-model");
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testComparePredictionModels() {
        // Given
        List<String> modelIds = Arrays.asList("history-model", "weather-model");
        
        // When
        Map<String, Object> result = predictionService.comparePredictionModels(modelIds);
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("models"));
        assertTrue(result.containsKey("comparisonTime"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> models = (List<Map<String, Object>>) result.get("models");
        assertEquals(2, models.size());
    }
    
    // ========== 数据导出测试 ==========
    
    @Test
    void testExportPredictionData_JSON() {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionRepository.findAllPredictions()).thenReturn(predictions);
        
        // When
        String result = predictionService.exportPredictionData("json", new HashMap<>());
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testExportPredictionData_CSV() {
        // Given
        List<PestPrediction> predictions = Arrays.asList(testPrediction);
        when(predictionRepository.findAllPredictions()).thenReturn(predictions);
        
        // When
        String result = predictionService.exportPredictionData("csv", new HashMap<>());
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("ID,病虫害ID,目标区域"));
    }
    
    @Test
    void testExportPredictionData_UnsupportedFormat() {
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.exportPredictionData("xml", new HashMap<>()));
        assertEquals("不支持的导出格式: xml", exception.getMessage());
    }
    
    @Test
    void testExportAlertData_JSON() {
        // Given
        List<PestAlert> alerts = Arrays.asList(testAlert);
        when(predictionRepository.findAllAlerts()).thenReturn(alerts);
        
        // When
        String result = predictionService.exportAlertData("json", new HashMap<>());
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testExportPredictionReport() {
        // Given
        when(predictionRepository.findPredictionById("pred-1")).thenReturn(Optional.of(testPrediction));
        
        // When
        String result = predictionService.exportPredictionReport("pred-1", "json");
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testExportPredictionReport_PredictionNotFound() {
        // Given
        when(predictionRepository.findPredictionById("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> predictionService.exportPredictionReport("non-existent", "json"));
        assertEquals("预测不存在", exception.getMessage());
    }
    
    // ========== 配置管理测试 ==========
    
    @Test
    void testGetPredictionConfig() {
        // When
        Map<String, Object> result = predictionService.getPredictionConfig();
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("defaultValidityPeriod"));
        assertTrue(result.containsKey("minProbabilityThreshold"));
    }
    
    @Test
    void testUpdatePredictionConfig() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("defaultValidityPeriod", "10天");
        config.put("minProbabilityThreshold", 0.2);
        
        // When
        boolean result = predictionService.updatePredictionConfig(config);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testGetAlertConfig() {
        // When
        Map<String, Object> result = predictionService.getAlertConfig();
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("defaultAlertLevel"));
        assertTrue(result.containsKey("autoExpireHours"));
    }
    
    @Test
    void testUpdateAlertConfig() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("defaultAlertLevel", "高级");
        config.put("autoExpireHours", 48);
        
        // When
        boolean result = predictionService.updateAlertConfig(config);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testResetConfigToDefault() {
        // When
        boolean result = predictionService.resetConfigToDefault("prediction");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testValidateConfig() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("defaultValidityPeriod", "7天");
        
        // When
        Map<String, Object> result = predictionService.validateConfig(config);
        
        // Then
        assertNotNull(result);
        assertEquals(true, result.get("isValid"));
        assertTrue(result.containsKey("errors"));
        assertTrue(result.containsKey("warnings"));
    }
    
    // ========== 系统维护测试 ==========
    
    @Test
    void testPerformHealthCheck() {
        // Given
        when(predictionRepository.countPredictions()).thenReturn(10L);
        when(predictionRepository.countAlerts()).thenReturn(5L);
        
        // When
        Map<String, Object> result = predictionService.performHealthCheck();
        
        // Then
        assertNotNull(result);
        assertEquals("healthy", result.get("status"));
        assertEquals(10L, result.get("predictionCount"));
        assertEquals(5L, result.get("alertCount"));
        assertTrue(result.containsKey("activeModels"));
        assertTrue(result.containsKey("checkTime"));
    }
    
    @Test
    void testGetSystemStatus() {
        // Given
        when(predictionRepository.countPredictions()).thenReturn(10L);
        when(predictionRepository.countAlerts()).thenReturn(5L);
        
        // When
        Map<String, Object> result = predictionService.getSystemStatus();
        
        // Then
        assertNotNull(result);
        assertEquals("healthy", result.get("status"));
    }
    
    @Test
    void testReinitializeSystem() {
        // When
        boolean result = predictionService.reinitializeSystem();
        
        // Then
        assertTrue(result);
    }
}