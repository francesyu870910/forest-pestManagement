package com.forestpest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestpest.entity.EffectEvaluation;
import com.forestpest.entity.EvaluationData;
import com.forestpest.service.EffectEvaluationService;
import com.forestpest.exception.ForestPestSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * EffectEvaluationController集成测试
 */
@WebMvcTest(EffectEvaluationController.class)
class EffectEvaluationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EffectEvaluationService effectEvaluationService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private EffectEvaluation testEvaluation;
    private EvaluationData testData;
    
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
        testEvaluation.setEffectivenessRate(85.0);
        testEvaluation.setConclusion("防治效果良好");
        
        testData = new EvaluationData();
        testData.setAffectedArea(100.0);
        testData.setSeverityLevel("中等");
        testData.setPestPopulation(500);
        testData.setDamageDescription("中等程度虫害");
    }
    
    @Test
    void testCreateEvaluation_Success() throws Exception {
        // Given
        EffectEvaluationController.CreateEvaluationRequest request = 
            new EffectEvaluationController.CreateEvaluationRequest();
        request.setEvaluation(testEvaluation);
        request.setUserId("user-1");
        
        when(effectEvaluationService.createEvaluation(any(EffectEvaluation.class), eq("user-1")))
            .thenReturn(testEvaluation);
        
        // When & Then
        mockMvc.perform(post("/api/evaluation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("创建效果评估成功"))
                .andExpect(jsonPath("$.data.id").value("eval-1"))
                .andExpect(jsonPath("$.data.taskId").value("task-1"));
        
        verify(effectEvaluationService).createEvaluation(any(EffectEvaluation.class), eq("user-1"));
    }
    
    @Test
    void testCreateEvaluation_ServiceException() throws Exception {
        // Given
        EffectEvaluationController.CreateEvaluationRequest request = 
            new EffectEvaluationController.CreateEvaluationRequest();
        request.setEvaluation(testEvaluation);
        request.setUserId("user-1");
        
        when(effectEvaluationService.createEvaluation(any(EffectEvaluation.class), eq("user-1")))
            .thenThrow(new ForestPestSystemException("创建失败"));
        
        // When & Then
        mockMvc.perform(post("/api/evaluation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("创建效果评估失败: 创建失败"));
    }
    
    @Test
    void testGetEvaluation_Success() throws Exception {
        // Given
        when(effectEvaluationService.getEvaluationById("eval-1")).thenReturn(testEvaluation);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("eval-1"))
                .andExpect(jsonPath("$.data.taskId").value("task-1"))
                .andExpect(jsonPath("$.data.effectivenessRate").value(85.0));
        
        verify(effectEvaluationService).getEvaluationById("eval-1");
    }
    
    @Test
    void testGetEvaluation_NotFound() throws Exception {
        // Given
        when(effectEvaluationService.getEvaluationById("non-existent")).thenReturn(null);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/non-existent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("效果评估不存在"));
    }
    
    @Test
    void testGetUserEvaluations_Success() throws Exception {
        // Given
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(effectEvaluationService.getUserEvaluations("user-1")).thenReturn(evaluations);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("eval-1"));
        
        verify(effectEvaluationService).getUserEvaluations("user-1");
    }
    
    @Test
    void testGetPlanEvaluations_Success() throws Exception {
        // Given
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(effectEvaluationService.getEvaluationsByPlan("task-1")).thenReturn(evaluations);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/plan/task-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].taskId").value("task-1"));
        
        verify(effectEvaluationService).getEvaluationsByPlan("task-1");
    }
    
    @Test
    void testUpdateEvaluation_Success() throws Exception {
        // Given
        EffectEvaluationController.UpdateEvaluationRequest request = 
            new EffectEvaluationController.UpdateEvaluationRequest();
        request.setEvaluation(testEvaluation);
        request.setUserId("user-1");
        
        when(effectEvaluationService.updateEvaluation(eq("eval-1"), any(EffectEvaluation.class), eq("user-1")))
            .thenReturn(testEvaluation);
        
        // When & Then
        mockMvc.perform(put("/api/evaluation/eval-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新效果评估成功"));
        
        verify(effectEvaluationService).updateEvaluation(eq("eval-1"), any(EffectEvaluation.class), eq("user-1"));
    }
    
    @Test
    void testDeleteEvaluation_Success() throws Exception {
        // Given
        when(effectEvaluationService.deleteEvaluation("eval-1", "user-1")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/evaluation/eval-1")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.message").value("删除效果评估成功"))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(effectEvaluationService).deleteEvaluation("eval-1", "user-1");
    }
    
    @Test
    void testDeleteEvaluation_Failed() throws Exception {
        // Given
        when(effectEvaluationService.deleteEvaluation("eval-1", "user-1")).thenReturn(false);
        
        // When & Then
        mockMvc.perform(delete("/api/evaluation/eval-1")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败，评估不存在或无权限"));
    }
    
    @Test
    void testRecordEvaluationData_Success() throws Exception {
        // Given
        EffectEvaluationController.RecordDataRequest request = 
            new EffectEvaluationController.RecordDataRequest();
        request.setData(testData);
        request.setUserId("user-1");
        
        when(effectEvaluationService.recordEvaluationData(eq("eval-1"), any(EvaluationData.class), eq("user-1")))
            .thenReturn(testData);
        
        // When & Then
        mockMvc.perform(post("/api/evaluation/eval-1/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("录入评估数据成功"));
        
        verify(effectEvaluationService).recordEvaluationData(eq("eval-1"), any(EvaluationData.class), eq("user-1"));
    }
    
    @Test
    void testBatchRecordEvaluationData_Success() throws Exception {
        // Given
        EffectEvaluationController.BatchRecordDataRequest request = 
            new EffectEvaluationController.BatchRecordDataRequest();
        request.setDataList(Arrays.asList(testData));
        request.setUserId("user-1");
        
        when(effectEvaluationService.batchRecordEvaluationData(eq("eval-1"), anyList(), eq("user-1")))
            .thenReturn(Arrays.asList(testData));
        
        // When & Then
        mockMvc.perform(post("/api/evaluation/eval-1/data/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("批量录入评估数据成功"));
        
        verify(effectEvaluationService).batchRecordEvaluationData(eq("eval-1"), anyList(), eq("user-1"));
    }
    
    @Test
    void testGetEvaluationData_Success() throws Exception {
        // Given
        List<EvaluationData> dataList = Arrays.asList(testData);
        when(effectEvaluationService.getEvaluationData("eval-1")).thenReturn(dataList);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].affectedArea").value(100.0));
        
        verify(effectEvaluationService).getEvaluationData("eval-1");
    }
    
    @Test
    void testCalculateEffectIndicators_Success() throws Exception {
        // Given
        Map<String, Object> indicators = new HashMap<>();
        indicators.put("treatmentEffectiveness", 85.0);
        indicators.put("densityReduction", 70.0);
        indicators.put("overallScore", 80.0);
        indicators.put("effectLevel", "良好");
        
        when(effectEvaluationService.calculateEffectIndicators("eval-1")).thenReturn(indicators);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/indicators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.treatmentEffectiveness").value(85.0))
                .andExpect(jsonPath("$.data.effectLevel").value("良好"));
        
        verify(effectEvaluationService).calculateEffectIndicators("eval-1");
    }
    
    @Test
    void testGenerateEvaluationReport_Success() throws Exception {
        // Given
        Map<String, Object> report = new HashMap<>();
        report.put("evaluationId", "eval-1");
        report.put("taskId", "task-1");
        report.put("effectivenessRate", 85.0);
        
        when(effectEvaluationService.generateEvaluationReport("eval-1")).thenReturn(report);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.evaluationId").value("eval-1"))
                .andExpect(jsonPath("$.data.taskId").value("task-1"));
        
        verify(effectEvaluationService).generateEvaluationReport("eval-1");
    }
    
    @Test
    void testGenerateMultiDimensionalReport_Success() throws Exception {
        // Given
        Map<String, Object> report = new HashMap<>();
        report.put("evaluationId", "eval-1");
        report.put("costBenefitAnalysis", new HashMap<>());
        report.put("environmentalImpact", new HashMap<>());
        
        when(effectEvaluationService.generateMultiDimensionalReport("eval-1")).thenReturn(report);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/report/multi-dimensional"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.evaluationId").value("eval-1"));
        
        verify(effectEvaluationService).generateMultiDimensionalReport("eval-1");
    }
    
    @Test
    void testCompareEvaluations_Success() throws Exception {
        // Given
        EffectEvaluationController.CompareEvaluationsRequest request = 
            new EffectEvaluationController.CompareEvaluationsRequest();
        request.setEvaluationIds(Arrays.asList("eval-1", "eval-2"));
        
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("evaluations", Arrays.asList(new HashMap<>(), new HashMap<>()));
        comparison.put("bestEvaluation", new HashMap<>());
        
        when(effectEvaluationService.compareEvaluations(anyList())).thenReturn(comparison);
        
        // When & Then
        mockMvc.perform(post("/api/evaluation/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.evaluations").isArray());
        
        verify(effectEvaluationService).compareEvaluations(anyList());
    }
    
    @Test
    void testGetEvaluationStatistics_Success() throws Exception {
        // Given
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalEvaluations", 10);
        statistics.put("averageEffectiveness", 75.5);
        
        when(effectEvaluationService.getEvaluationStatistics()).thenReturn(statistics);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalEvaluations").value(10))
                .andExpect(jsonPath("$.data.averageEffectiveness").value(75.5));
        
        verify(effectEvaluationService).getEvaluationStatistics();
    }
    
    @Test
    void testGetUserEvaluationStatistics_Success() throws Exception {
        // Given
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalEvaluations", 5);
        statistics.put("averageEffectiveness", 80.0);
        
        when(effectEvaluationService.getUserEvaluationStatistics("user-1")).thenReturn(statistics);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/statistics/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalEvaluations").value(5));
        
        verify(effectEvaluationService).getUserEvaluationStatistics("user-1");
    }
    
    @Test
    void testGetEffectTrends_Success() throws Exception {
        // Given
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        trends.put("effectivenessTrend", Arrays.asList(new HashMap<>()));
        trends.put("countTrend", Arrays.asList(new HashMap<>()));
        
        when(effectEvaluationService.getEffectTrends("monthly")).thenReturn(trends);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/trends")
                .param("period", "monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.effectivenessTrend").isArray());
        
        verify(effectEvaluationService).getEffectTrends("monthly");
    }
    
    @Test
    void testGetEvaluationHistory_Success() throws Exception {
        // Given
        List<Map<String, Object>> history = Arrays.asList(
            Map.of("action", "创建评估", "timestamp", LocalDateTime.now(), "userId", "user-1")
        );
        
        when(effectEvaluationService.getEvaluationHistory("eval-1")).thenReturn(history);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].action").value("创建评估"));
        
        verify(effectEvaluationService).getEvaluationHistory("eval-1");
    }
    
    @Test
    void testExportEvaluationData_Success() throws Exception {
        // Given
        String exportData = "导出的数据内容";
        when(effectEvaluationService.exportEvaluationData("eval-1", "json")).thenReturn(exportData);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/export/data")
                .param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(exportData));
        
        verify(effectEvaluationService).exportEvaluationData("eval-1", "json");
    }
    
    @Test
    void testGetEvaluationTemplates_Success() throws Exception {
        // Given
        List<Map<String, Object>> templates = Arrays.asList(
            Map.of("id", "template-1", "name", "标准模板"),
            Map.of("id", "template-2", "name", "环保模板")
        );
        
        when(effectEvaluationService.getEvaluationTemplates()).thenReturn(templates);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("标准模板"));
        
        verify(effectEvaluationService).getEvaluationTemplates();
    }
    
    @Test
    void testCreateEvaluationFromTemplate_Success() throws Exception {
        // Given
        EffectEvaluationController.CreateFromTemplateRequest request = 
            new EffectEvaluationController.CreateFromTemplateRequest();
        request.setTemplateId("template-1");
        request.setPlanId("plan-1");
        request.setUserId("user-1");
        request.setParameters(new HashMap<>());
        
        when(effectEvaluationService.createEvaluationFromTemplate(
            eq("template-1"), eq("plan-1"), eq("user-1"), any(Map.class)))
            .thenReturn(testEvaluation);
        
        // When & Then
        mockMvc.perform(post("/api/evaluation/from-template")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("根据模板创建评估成功"));
        
        verify(effectEvaluationService).createEvaluationFromTemplate(
            eq("template-1"), eq("plan-1"), eq("user-1"), any(Map.class));
    }
    
    @Test
    void testGetEvaluationIndicators_Success() throws Exception {
        // Given
        List<Map<String, Object>> indicators = Arrays.asList(
            Map.of("name", "防治效果率", "unit", "%", "description", "效果描述")
        );
        
        when(effectEvaluationService.getEvaluationIndicators()).thenReturn(indicators);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/indicators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("防治效果率"));
        
        verify(effectEvaluationService).getEvaluationIndicators();
    }
    
    @Test
    void testCalculateOverallEffectScore_Success() throws Exception {
        // Given
        when(effectEvaluationService.calculateOverallEffectScore("eval-1")).thenReturn(85.5);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(85.5));
        
        verify(effectEvaluationService).calculateOverallEffectScore("eval-1");
    }
    
    @Test
    void testGetEffectRating_Success() throws Exception {
        // Given
        when(effectEvaluationService.getEffectRating("eval-1")).thenReturn("良好");
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("良好"));
        
        verify(effectEvaluationService).getEffectRating("eval-1");
    }
    
    @Test
    void testGetImprovementSuggestions_Success() throws Exception {
        // Given
        List<String> suggestions = Arrays.asList("建议1", "建议2", "建议3");
        when(effectEvaluationService.getImprovementSuggestions("eval-1")).thenReturn(suggestions);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("建议1"));
        
        verify(effectEvaluationService).getImprovementSuggestions("eval-1");
    }
    
    @Test
    void testReviewEvaluation_Success() throws Exception {
        // Given
        EffectEvaluationController.ReviewEvaluationRequest request = 
            new EffectEvaluationController.ReviewEvaluationRequest();
        request.setReviewerId("reviewer-1");
        request.setReviewResult("APPROVED");
        request.setComments("审核通过");
        
        when(effectEvaluationService.reviewEvaluation("eval-1", "reviewer-1", "APPROVED", "审核通过"))
            .thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/evaluation/eval-1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("审核评估成功"));
        
        verify(effectEvaluationService).reviewEvaluation("eval-1", "reviewer-1", "APPROVED", "审核通过");
    }
    
    @Test
    void testGetPendingReviewEvaluations_Success() throws Exception {
        // Given
        testEvaluation.setStatus("SUBMITTED");
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(effectEvaluationService.getPendingReviewEvaluations()).thenReturn(evaluations);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/pending-review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("SUBMITTED"));
        
        verify(effectEvaluationService).getPendingReviewEvaluations();
    }
    
    @Test
    void testGetReviewedEvaluations_Success() throws Exception {
        // Given
        testEvaluation.setReviewedBy("reviewer-1");
        List<EffectEvaluation> evaluations = Arrays.asList(testEvaluation);
        when(effectEvaluationService.getReviewedEvaluations("reviewer-1")).thenReturn(evaluations);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/reviewed/reviewer-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].reviewedBy").value("reviewer-1"));
        
        verify(effectEvaluationService).getReviewedEvaluations("reviewer-1");
    }
    
    @Test
    void testSetEvaluationReminder_Success() throws Exception {
        // Given
        EffectEvaluationController.SetReminderRequest request = 
            new EffectEvaluationController.SetReminderRequest();
        request.setPlanId("plan-1");
        request.setUserId("user-1");
        request.setReminderConfig(Map.of("interval", "7days"));
        
        when(effectEvaluationService.setEvaluationReminder("plan-1", "user-1", request.getReminderConfig()))
            .thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/evaluation/reminder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("设置评估提醒成功"));
        
        verify(effectEvaluationService).setEvaluationReminder("plan-1", "user-1", request.getReminderConfig());
    }
    
    @Test
    void testGetEvaluationReminders_Success() throws Exception {
        // Given
        List<Map<String, Object>> reminders = Arrays.asList(
            Map.of("id", "reminder-1", "planId", "plan-1", "interval", "7days")
        );
        when(effectEvaluationService.getEvaluationReminders("user-1")).thenReturn(reminders);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/reminder/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("reminder-1"));
        
        verify(effectEvaluationService).getEvaluationReminders("user-1");
    }
    
    @Test
    void testCancelEvaluationReminder_Success() throws Exception {
        // Given
        when(effectEvaluationService.cancelEvaluationReminder("reminder-1", "user-1")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/evaluation/reminder/reminder-1")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("取消评估提醒成功"));
        
        verify(effectEvaluationService).cancelEvaluationReminder("reminder-1", "user-1");
    }
    
    @Test
    void testGetEvaluationQualityAnalysis_Success() throws Exception {
        // Given
        Map<String, Object> analysis = Map.of(
            "totalEvaluations", 100,
            "completeEvaluations", 85,
            "completionRate", 85.0
        );
        when(effectEvaluationService.getEvaluationQualityAnalysis()).thenReturn(analysis);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/quality-analysis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalEvaluations").value(100))
                .andExpect(jsonPath("$.data.completionRate").value(85.0));
        
        verify(effectEvaluationService).getEvaluationQualityAnalysis();
    }
    
    @Test
    void testGetCostBenefitAnalysis_Success() throws Exception {
        // Given
        Map<String, Object> analysis = Map.of(
            "totalCost", 5000.0,
            "savedLoss", 15000.0,
            "benefitCostRatio", 3.0
        );
        when(effectEvaluationService.getCostBenefitAnalysis("eval-1")).thenReturn(analysis);
        
        // When & Then
        mockMvc.perform(get("/api/evaluation/eval-1/cost-benefit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCost").value(5000.0))
                .andExpect(jsonPath("$.data.benefitCostRatio").value(3.0));
        
        verify(effectEvaluationService).getCostBenefitAnalysis("eval-1");
    }
}