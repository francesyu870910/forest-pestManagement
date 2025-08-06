package com.forestpest.service;

import com.forestpest.entity.EffectEvaluation;
import com.forestpest.entity.EvaluationData;
import com.forestpest.repository.EvaluationRepository;
import com.forestpest.service.impl.EffectEvaluationServiceImpl;
import com.forestpest.exception.ForestPestSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EffectEvaluationService单元测试
 */
@ExtendWith(MockitoExtension.class)
class EffectEvaluationServiceTest {
    
    @Mock
    private EvaluationRepository evaluationRepository;
    
    @InjectMocks
    private EffectEvaluationServiceImpl effectEvaluationService;
    
    private EffectEvaluation testEvaluation;
    private EvaluationData beforeData;
    private EvaluationData afterData;
    
    @BeforeEach
    void setUp() {
        // 设置测试数据
        testEvaluation = new EffectEvaluation();
        testEvaluation.setId("eval-1");
        testEvaluation.setTaskId("task-1");
        testEvaluation.setPestId("pest-1");
        testEvaluation.setEvaluatedArea("测试区域");
        testEvaluation.setEvaluatedBy("user-1");
        testEvaluation.setEvaluationTime(LocalDateTime.now());
        testEvaluation.setStatus("DRAFT");
        testEvaluation.setCreatedTime(LocalDateTime.now());
        testEvaluation.setCreatedBy("user-1");
        
        // 设置防治前数据
        beforeData = new EvaluationData();
        beforeData.setAffectedArea(100.0);
        beforeData.setSeverityLevel("严重");
        beforeData.setPestPopulation(1000);
        beforeData.setDamageDescription("严重虫害");
        beforeData.setHealthyPlants(500);
        beforeData.setDamagedPlants(400);
        beforeData.setDeadPlants(100);
        
        // 设置防治后数据
        afterData = new EvaluationData();
        afterData.setAffectedArea(30.0);
        afterData.setSeverityLevel("轻微");
        afterData.setPestPopulation(200);
        afterData.setDamageDescription("轻微虫害");
        afterData.setHealthyPlants(800);
        afterData.setDamagedPlants(150);
        afterData.setDeadPlants(50);
        
        testEvaluation.setBeforeTreatment(beforeData);
        testEvaluation.setAfterTreatment(afterData);
        testEvaluation.setEffectivenessRate(80.0);
    }
    
    @Test
    void testCreateEvaluation_Success() {
        // Given
        when(evaluationRepository.save(any(EffectEvaluation.class))).thenReturn(testEvaluation);
        
        // When
        EffectEvaluation result = effectEvaluationService.createEvaluation(testEvaluation, "user-1");
        
        // Then
        assertNotNull(result);
        assertEquals(testEvaluation.getTaskId(), result.getTaskId());
        assertEquals(testEvaluation.getPestId(), result.getPestId());
        assertEquals("user-1", result.getCreatedBy());
        assertEquals("DRAFT", result.getStatus());
        verify(evaluationRepository).save(any(EffectEvaluation.class));
    }
    
    @Test
    void testCreateEvaluation_NullEvaluation() {
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.createEvaluation(null, "user-1"));
        assertEquals("效果评估不能为空", exception.getMessage());
    }
    
    @Test
    void testCreateEvaluation_InvalidEvaluation() {
        // Given
        testEvaluation.setTaskId(""); // 无效的任务ID
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.createEvaluation(testEvaluation, "user-1"));
        assertEquals("效果评估验证失败", exception.getMessage());
    }
    
    @Test
    void testGetEvaluationById_Success() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        EffectEvaluation result = effectEvaluationService.getEvaluationById("eval-1");
        
        // Then
        assertNotNull(result);
        assertEquals("eval-1", result.getId());
        assertEquals("task-1", result.getTaskId());
    }
    
    @Test
    void testGetEvaluationById_NotFound() {
        // Given
        when(evaluationRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // When
        EffectEvaluation result = effectEvaluationService.getEvaluationById("non-existent");
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testGetEvaluationsByPlan() {
        // Given
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(evaluationRepository.findByTaskId("task-1")).thenReturn(evaluations);
        
        // When
        List<EffectEvaluation> result = effectEvaluationService.getEvaluationsByPlan("task-1");
        
        // Then
        assertEquals(1, result.size());
        assertEquals("task-1", result.get(0).getTaskId());
    }
    
    @Test
    void testGetUserEvaluations() {
        // Given
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(evaluationRepository.findByEvaluatedBy("user-1")).thenReturn(evaluations);
        
        // When
        List<EffectEvaluation> result = effectEvaluationService.getUserEvaluations("user-1");
        
        // Then
        assertEquals(1, result.size());
        assertEquals("user-1", result.get(0).getEvaluatedBy());
    }
    
    @Test
    void testUpdateEvaluation_Success() {
        // Given
        EffectEvaluation updatedEvaluation = new EffectEvaluation();
        updatedEvaluation.setEvaluatedArea("更新的区域");
        updatedEvaluation.setConclusion("更新的结论");
        updatedEvaluation.setRecommendations("更新的建议");
        
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        when(evaluationRepository.save(any(EffectEvaluation.class))).thenReturn(testEvaluation);
        
        // When
        EffectEvaluation result = effectEvaluationService.updateEvaluation("eval-1", updatedEvaluation, "user-1");
        
        // Then
        assertNotNull(result);
        verify(evaluationRepository).save(any(EffectEvaluation.class));
    }
    
    @Test
    void testUpdateEvaluation_NotFound() {
        // Given
        when(evaluationRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.updateEvaluation("non-existent", testEvaluation, "user-1"));
        assertEquals("效果评估不存在", exception.getMessage());
    }
    
    @Test
    void testUpdateEvaluation_NoPermission() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.updateEvaluation("eval-1", testEvaluation, "other-user"));
        assertEquals("无权限修改此效果评估", exception.getMessage());
    }
    
    @Test
    void testDeleteEvaluation_Success() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        doNothing().when(evaluationRepository).deleteById("eval-1");
        
        // When
        boolean result = effectEvaluationService.deleteEvaluation("eval-1", "user-1");
        
        // Then
        assertTrue(result);
        verify(evaluationRepository).deleteById("eval-1");
    }
    
    @Test
    void testDeleteEvaluation_NotFound() {
        // Given
        when(evaluationRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // When
        boolean result = effectEvaluationService.deleteEvaluation("non-existent", "user-1");
        
        // Then
        assertFalse(result);
        verify(evaluationRepository, never()).deleteById(anyString());
    }
    
    @Test
    void testDeleteEvaluation_NoPermission() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        boolean result = effectEvaluationService.deleteEvaluation("eval-1", "other-user");
        
        // Then
        assertFalse(result);
        verify(evaluationRepository, never()).deleteById(anyString());
    }
    
    @Test
    void testRecordEvaluationData_Success() {
        // Given
        EvaluationData newData = new EvaluationData();
        newData.setAffectedArea(50.0);
        newData.setPestPopulation(300);
        
        testEvaluation.setBeforeTreatment(null);
        testEvaluation.setAfterTreatment(null);
        
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        when(evaluationRepository.save(any(EffectEvaluation.class))).thenReturn(testEvaluation);
        
        // When
        EvaluationData result = effectEvaluationService.recordEvaluationData("eval-1", newData, "user-1");
        
        // Then
        assertNotNull(result);
        assertEquals(50.0, result.getAffectedArea());
        verify(evaluationRepository).save(any(EffectEvaluation.class));
    }
    
    @Test
    void testRecordEvaluationData_EvaluationNotFound() {
        // Given
        when(evaluationRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.recordEvaluationData("non-existent", new EvaluationData(), "user-1"));
        assertEquals("效果评估不存在", exception.getMessage());
    }
    
    @Test
    void testRecordEvaluationData_NullData() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.recordEvaluationData("eval-1", null, "user-1"));
        assertEquals("评估数据不能为空", exception.getMessage());
    }
    
    @Test
    void testBatchRecordEvaluationData_Success() {
        // Given
        List<EvaluationData> dataList = Arrays.asList(beforeData, afterData);
        
        testEvaluation.setBeforeTreatment(null);
        testEvaluation.setAfterTreatment(null);
        
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        when(evaluationRepository.save(any(EffectEvaluation.class))).thenReturn(testEvaluation);
        
        // When
        List<EvaluationData> result = effectEvaluationService.batchRecordEvaluationData("eval-1", dataList, "user-1");
        
        // Then
        assertEquals(2, result.size());
        verify(evaluationRepository, atLeast(2)).save(any(EffectEvaluation.class));
    }
    
    @Test
    void testBatchRecordEvaluationData_EmptyList() {
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.batchRecordEvaluationData("eval-1", new ArrayList<>(), "user-1"));
        assertEquals("评估数据列表不能为空", exception.getMessage());
    }
    
    @Test
    void testCalculateEffectIndicators_Success() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        Map<String, Object> result = effectEvaluationService.calculateEffectIndicators("eval-1");
        
        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("dataAvailable"));
        assertEquals(80.0, (Double) result.get("treatmentEffectiveness"));
        assertTrue(result.containsKey("densityReduction"));
        assertTrue(result.containsKey("areaReduction"));
        assertTrue(result.containsKey("healthImprovement"));
        assertTrue(result.containsKey("overallScore"));
        assertTrue(result.containsKey("effectLevel"));
    }
    
    @Test
    void testCalculateEffectIndicators_MissingData() {
        // Given
        testEvaluation.setBeforeTreatment(null);
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        Map<String, Object> result = effectEvaluationService.calculateEffectIndicators("eval-1");
        
        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("dataAvailable"));
        assertEquals("缺少防治前后对比数据", result.get("message"));
    }
    
    @Test
    void testCalculateEffectIndicators_EvaluationNotFound() {
        // Given
        when(evaluationRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.calculateEffectIndicators("non-existent"));
        assertEquals("效果评估不存在", exception.getMessage());
    }
    
    @Test
    void testGenerateEvaluationReport_Success() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        Map<String, Object> result = effectEvaluationService.generateEvaluationReport("eval-1");
        
        // Then
        assertNotNull(result);
        assertEquals("eval-1", result.get("evaluationId"));
        assertEquals("task-1", result.get("taskId"));
        assertEquals("pest-1", result.get("pestId"));
        assertTrue(result.containsKey("indicators"));
        assertTrue(result.containsKey("suggestions"));
        assertTrue(result.containsKey("reportGeneratedTime"));
    }
    
    @Test
    void testGenerateMultiDimensionalReport_Success() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        Map<String, Object> result = effectEvaluationService.generateMultiDimensionalReport("eval-1");
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("costBenefitAnalysis"));
        assertTrue(result.containsKey("environmentalImpact"));
        assertTrue(result.containsKey("sustainability"));
        assertTrue(result.containsKey("riskAssessment"));
    }
    
    @Test
    void testCompareEvaluations_Success() {
        // Given
        EffectEvaluation evaluation2 = new EffectEvaluation();
        evaluation2.setId("eval-2");
        evaluation2.setTaskId("task-2");
        evaluation2.setEffectivenessRate(70.0);
        
        List<String> evaluationIds = Arrays.asList("eval-1", "eval-2");
        
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        when(evaluationRepository.findById("eval-2")).thenReturn(Optional.of(evaluation2));
        
        // When
        Map<String, Object> result = effectEvaluationService.compareEvaluations(evaluationIds);
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("evaluations"));
        assertTrue(result.containsKey("bestEvaluation"));
        assertTrue(result.containsKey("comparisonTime"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> evaluations = (List<Map<String, Object>>) result.get("evaluations");
        assertEquals(2, evaluations.size());
    }
    
    @Test
    void testCompareEvaluations_InsufficientEvaluations() {
        // Given
        List<String> evaluationIds = Arrays.asList("eval-1");
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.compareEvaluations(evaluationIds));
        assertEquals("至少需要两个评估进行对比", exception.getMessage());
    }
    
    @Test
    void testGetEvaluationStatistics() {
        // Given
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(evaluationRepository.findAll()).thenReturn(evaluations);
        
        // When
        Map<String, Object> result = effectEvaluationService.getEvaluationStatistics();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("totalEvaluations"));
        assertTrue(result.containsKey("statusStats"));
        assertTrue(result.containsKey("monthlyStats"));
        assertTrue(result.containsKey("averageEffectiveness"));
    }
    
    @Test
    void testGetUserEvaluationStatistics() {
        // Given
        List<EffectEvaluation> userEvaluations = Arrays.asList(testEvaluation);
        when(evaluationRepository.findByEvaluatedBy("user-1")).thenReturn(userEvaluations);
        
        // When
        Map<String, Object> result = effectEvaluationService.getUserEvaluationStatistics("user-1");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("totalEvaluations"));
        assertTrue(result.containsKey("statusStats"));
        assertTrue(result.containsKey("averageEffectiveness"));
        assertTrue(result.containsKey("maxEffectiveness"));
    }
    
    @Test
    void testGetPlanEffectStatistics() {
        // Given
        List<EffectEvaluation> planEvaluations = Arrays.asList(testEvaluation);
        when(evaluationRepository.findByTaskId("task-1")).thenReturn(planEvaluations);
        
        // When
        Map<String, Object> result = effectEvaluationService.getPlanEffectStatistics("task-1");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("evaluationCount"));
        assertTrue(result.containsKey("averageEffectiveness"));
        assertTrue(result.containsKey("latestEvaluation"));
        assertTrue(result.containsKey("effectTrend"));
    }
    
    @Test
    void testGetEffectTrends() {
        // Given
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(evaluationRepository.findAll()).thenReturn(evaluations);
        
        // When
        Map<String, List<Map<String, Object>>> result = effectEvaluationService.getEffectTrends("monthly");
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("effectivenessTrend"));
        assertTrue(result.containsKey("countTrend"));
    }
    
    @Test
    void testGetEvaluationHistory() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        List<Map<String, Object>> result = effectEvaluationService.getEvaluationHistory("eval-1");
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).containsKey("action"));
        assertTrue(result.get(0).containsKey("timestamp"));
        assertTrue(result.get(0).containsKey("userId"));
    }
    
    @Test
    void testExportEvaluationData_JSON() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        String result = effectEvaluationService.exportEvaluationData("eval-1", "json");
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testExportEvaluationData_CSV() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        String result = effectEvaluationService.exportEvaluationData("eval-1", "csv");
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("受害面积,严重程度,虫口密度,损害描述"));
    }
    
    @Test
    void testExportEvaluationData_UnsupportedFormat() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.exportEvaluationData("eval-1", "xml"));
        assertEquals("不支持的导出格式: xml", exception.getMessage());
    }
    
    @Test
    void testGetEvaluationTemplates() {
        // When
        List<Map<String, Object>> result = effectEvaluationService.getEvaluationTemplates();
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(template -> "标准防治效果评估".equals(template.get("name"))));
        assertTrue(result.stream().anyMatch(template -> "环境友好型评估".equals(template.get("name"))));
    }
    
    @Test
    void testCreateEvaluationFromTemplate_Success() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("evaluatedArea", "模板测试区域");
        parameters.put("pestId", "pest-template");
        
        when(evaluationRepository.save(any(EffectEvaluation.class))).thenReturn(testEvaluation);
        
        // When
        EffectEvaluation result = effectEvaluationService.createEvaluationFromTemplate(
            "template-standard", "plan-1", "user-1", parameters);
        
        // Then
        assertNotNull(result);
        verify(evaluationRepository).save(any(EffectEvaluation.class));
    }
    
    @Test
    void testCreateEvaluationFromTemplate_TemplateNotFound() {
        // When & Then
        ForestPestSystemException exception = assertThrows(ForestPestSystemException.class,
            () -> effectEvaluationService.createEvaluationFromTemplate(
                "non-existent-template", "plan-1", "user-1", new HashMap<>()));
        assertEquals("模板不存在", exception.getMessage());
    }
    
    @Test
    void testGetEvaluationIndicators() {
        // When
        List<Map<String, Object>> result = effectEvaluationService.getEvaluationIndicators();
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(indicator -> "防治效果率".equals(indicator.get("name"))));
        assertTrue(result.stream().anyMatch(indicator -> "虫口密度下降率".equals(indicator.get("name"))));
    }
    
    @Test
    void testCalculateOverallEffectScore() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        double result = effectEvaluationService.calculateOverallEffectScore("eval-1");
        
        // Then
        assertTrue(result >= 0.0 && result <= 100.0);
    }
    
    @Test
    void testGetEffectRating() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        String result = effectEvaluationService.getEffectRating("eval-1");
        
        // Then
        assertNotNull(result);
        assertTrue(Arrays.asList("优秀", "良好", "中等", "及格", "不及格").contains(result));
    }
    
    @Test
    void testGetImprovementSuggestions() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        
        // When
        List<String> result = effectEvaluationService.getImprovementSuggestions("eval-1");
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testValidateEvaluation_ValidEvaluation() {
        // When
        boolean result = effectEvaluationService.validateEvaluation(testEvaluation);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testValidateEvaluation_NullEvaluation() {
        // When
        boolean result = effectEvaluationService.validateEvaluation(null);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testValidateEvaluation_MissingTaskId() {
        // Given
        testEvaluation.setTaskId("");
        
        // When
        boolean result = effectEvaluationService.validateEvaluation(testEvaluation);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testValidateEvaluation_MissingPestId() {
        // Given
        testEvaluation.setPestId(null);
        
        // When
        boolean result = effectEvaluationService.validateEvaluation(testEvaluation);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testValidateEvaluation_MissingEvaluatedArea() {
        // Given
        testEvaluation.setEvaluatedArea("");
        
        // When
        boolean result = effectEvaluationService.validateEvaluation(testEvaluation);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testReviewEvaluation_Success() {
        // Given
        when(evaluationRepository.findById("eval-1")).thenReturn(Optional.of(testEvaluation));
        when(evaluationRepository.save(any(EffectEvaluation.class))).thenReturn(testEvaluation);
        
        // When
        boolean result = effectEvaluationService.reviewEvaluation("eval-1", "reviewer-1", "APPROVED", "审核通过");
        
        // Then
        assertTrue(result);
        verify(evaluationRepository).save(any(EffectEvaluation.class));
    }
    
    @Test
    void testReviewEvaluation_EvaluationNotFound() {
        // Given
        when(evaluationRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // When
        boolean result = effectEvaluationService.reviewEvaluation("non-existent", "reviewer-1", "APPROVED", "审核通过");
        
        // Then
        assertFalse(result);
        verify(evaluationRepository, never()).save(any(EffectEvaluation.class));
    }
    
    @Test
    void testGetPendingReviewEvaluations() {
        // Given
        testEvaluation.setStatus("SUBMITTED");
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(evaluationRepository.findAll()).thenReturn(evaluations);
        
        // When
        List<EffectEvaluation> result = effectEvaluationService.getPendingReviewEvaluations();
        
        // Then
        assertEquals(1, result.size());
        assertEquals("SUBMITTED", result.get(0).getStatus());
    }
    
    @Test
    void testGetReviewedEvaluations() {
        // Given
        testEvaluation.setReviewedBy("reviewer-1");
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(evaluationRepository.findAll()).thenReturn(evaluations);
        
        // When
        List<EffectEvaluation> result = effectEvaluationService.getReviewedEvaluations("reviewer-1");
        
        // Then
        assertEquals(1, result.size());
        assertEquals("reviewer-1", result.get(0).getReviewedBy());
    }
}