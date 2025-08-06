package com.forestpest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;
import com.forestpest.entity.TreatmentMethod;
import com.forestpest.service.TreatmentPlanService;
import com.forestpest.controller.TreatmentPlanController.*;
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
 * 防治方案控制器测试类
 */
@WebMvcTest(TreatmentPlanController.class)
class TreatmentPlanControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TreatmentPlanService treatmentPlanService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TreatmentPlan testPlan;
    private TreatmentTask testTask;
    private TreatmentMethod testMethod;
    private String testUserId;
    private String testPestId;
    
    @BeforeEach
    void setUp() {
        testUserId = "test-user-123";
        testPestId = "pest-001";
        
        // 创建测试防治方案
        testPlan = new TreatmentPlan();
        testPlan.setId("plan-001");
        testPlan.setPestId(testPestId);
        testPlan.setTitle("防治方案 - 松毛虫");
        testPlan.setDescription("针对松毛虫的综合防治方案");
        testPlan.setPlanType("自动生成");
        testPlan.setCreatedBy(testUserId);
        testPlan.setCreatedTime(LocalDateTime.now());
        testPlan.setStatus("草稿");
        testPlan.setEstimatedCost(2000.0);
        testPlan.setExpectedDuration(14);
        
        // 创建测试防治任务
        testTask = new TreatmentTask();
        testTask.setId("task-001");
        testTask.setPlanId(testPlan.getId());
        testTask.setTitle("执行生物防治");
        testTask.setDescription("释放天敌昆虫");
        testTask.setCreatedBy(testUserId);
        testTask.setCreatedTime(LocalDateTime.now());
        testTask.setStatus("待执行");
        testTask.setPriority("高");
        
        // 创建测试防治方法
        testMethod = new TreatmentMethod();
        testMethod.setId("method-001");
        testMethod.setType("生物防治");
        testMethod.setName("释放天敌昆虫");
        testMethod.setDescription("使用瓢虫等天敌昆虫控制害虫");
        testMethod.setRiskLevel("低");
        testMethod.setEffectiveness(0.85);
        testMethod.setCost(500.0);
    }
    
    @Test
    void testGenerateTreatmentPlan_Success() throws Exception {
        // Arrange
        GeneratePlanRequest request = new GeneratePlanRequest();
        request.setPestId(testPestId);
        request.setUserId(testUserId);
        request.setParameters(Map.of("targetArea", "100亩"));
        
        when(treatmentPlanService.generateTreatmentPlan(testPestId, testUserId, request.getParameters()))
            .thenReturn(testPlan);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/plan/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPlan.getId()))
                .andExpect(jsonPath("$.data.title").value(testPlan.getTitle()))
                .andExpect(jsonPath("$.data.pestId").value(testPestId));
        
        verify(treatmentPlanService).generateTreatmentPlan(testPestId, testUserId, request.getParameters());
    }
    
    @Test
    void testGenerateTreatmentPlan_MissingPestId() throws Exception {
        // Arrange
        GeneratePlanRequest request = new GeneratePlanRequest();
        request.setUserId(testUserId);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/plan/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(treatmentPlanService, never()).generateTreatmentPlan(any(), any(), any());
    }
    
    @Test
    void testGetTreatmentPlan_Success() throws Exception {
        // Arrange
        when(treatmentPlanService.getTreatmentPlan(testPlan.getId()))
            .thenReturn(testPlan);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/{planId}", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPlan.getId()))
                .andExpect(jsonPath("$.data.title").value(testPlan.getTitle()));
        
        verify(treatmentPlanService).getTreatmentPlan(testPlan.getId());
    }
    
    @Test
    void testGetTreatmentPlan_NotFound() throws Exception {
        // Arrange
        when(treatmentPlanService.getTreatmentPlan("non-existent"))
            .thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/{planId}", "non-existent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("防治方案不存在"));
        
        verify(treatmentPlanService).getTreatmentPlan("non-existent");
    }
    
    @Test
    void testGetUserTreatmentPlans_Success() throws Exception {
        // Arrange
        List<TreatmentPlan> plans = Arrays.asList(testPlan);
        when(treatmentPlanService.getUserTreatmentPlans(testUserId))
            .thenReturn(plans);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testPlan.getId()));
        
        verify(treatmentPlanService).getUserTreatmentPlans(testUserId);
    }
    
    @Test
    void testGetUserTreatmentPlans_WithPagination() throws Exception {
        // Arrange
        List<TreatmentPlan> plans = Arrays.asList(testPlan);
        when(treatmentPlanService.getUserTreatmentPlans(testUserId, 1, 10))
            .thenReturn(plans);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/user/{userId}", testUserId)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(treatmentPlanService).getUserTreatmentPlans(testUserId, 1, 10);
    }
    
    @Test
    void testUpdateTreatmentPlan_Success() throws Exception {
        // Arrange
        TreatmentPlan updatedPlan = new TreatmentPlan();
        updatedPlan.setTitle("更新的防治方案");
        updatedPlan.setDescription("更新的描述");
        
        when(treatmentPlanService.updateTreatmentPlan(testPlan.getId(), testUserId, updatedPlan))
            .thenReturn(testPlan);
        
        // Act & Assert
        mockMvc.perform(put("/api/treatment/plan/{planId}", testPlan.getId())
                .param("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPlan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPlan.getId()));
        
        verify(treatmentPlanService).updateTreatmentPlan(eq(testPlan.getId()), eq(testUserId), any(TreatmentPlan.class));
    }
    
    @Test
    void testSaveCustomTreatmentPlan_Success() throws Exception {
        // Arrange
        CustomPlanRequest request = new CustomPlanRequest();
        request.setPlan(testPlan);
        request.setUserId(testUserId);
        
        when(treatmentPlanService.saveCustomTreatmentPlan(testPlan, testUserId))
            .thenReturn(testPlan);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/plan/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPlan.getId()));
        
        verify(treatmentPlanService).saveCustomTreatmentPlan(any(TreatmentPlan.class), eq(testUserId));
    }
    
    @Test
    void testDeleteTreatmentPlan_Success() throws Exception {
        // Arrange
        when(treatmentPlanService.deleteTreatmentPlan(testPlan.getId(), testUserId))
            .thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(delete("/api/treatment/plan/{planId}", testPlan.getId())
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(treatmentPlanService).deleteTreatmentPlan(testPlan.getId(), testUserId);
    }
    
    @Test
    void testDeleteTreatmentPlan_Failed() throws Exception {
        // Arrange
        when(treatmentPlanService.deleteTreatmentPlan(testPlan.getId(), testUserId))
            .thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(delete("/api/treatment/plan/{planId}", testPlan.getId())
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败，方案不存在或无权限"));
        
        verify(treatmentPlanService).deleteTreatmentPlan(testPlan.getId(), testUserId);
    }
    
    @Test
    void testCreateTreatmentTask_Success() throws Exception {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest();
        request.setPlanId(testPlan.getId());
        request.setTask(testTask);
        request.setUserId(testUserId);
        
        when(treatmentPlanService.createTreatmentTask(testPlan.getId(), testTask, testUserId))
            .thenReturn(testTask);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testTask.getId()))
                .andExpect(jsonPath("$.data.title").value(testTask.getTitle()));
        
        verify(treatmentPlanService).createTreatmentTask(eq(testPlan.getId()), any(TreatmentTask.class), eq(testUserId));
    }
    
    @Test
    void testAssignTreatmentTask_Success() throws Exception {
        // Arrange
        AssignTaskRequest request = new AssignTaskRequest();
        request.setAssigneeId("assignee-123");
        request.setAssignerId(testUserId);
        
        testTask.setAssigneeId("assignee-123");
        testTask.setStatus("已分配");
        
        when(treatmentPlanService.assignTreatmentTask(testTask.getId(), "assignee-123", testUserId))
            .thenReturn(testTask);
        
        // Act & Assert
        mockMvc.perform(put("/api/treatment/task/{taskId}/assign", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.assigneeId").value("assignee-123"))
                .andExpect(jsonPath("$.data.status").value("已分配"));
        
        verify(treatmentPlanService).assignTreatmentTask(testTask.getId(), "assignee-123", testUserId);
    }
    
    @Test
    void testUpdateTaskStatus_Success() throws Exception {
        // Arrange
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus("进行中");
        request.setUserId(testUserId);
        
        testTask.setStatus("进行中");
        
        when(treatmentPlanService.updateTaskStatus(testTask.getId(), "进行中", testUserId))
            .thenReturn(testTask);
        
        // Act & Assert
        mockMvc.perform(put("/api/treatment/task/{taskId}/status", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("进行中"));
        
        verify(treatmentPlanService).updateTaskStatus(testTask.getId(), "进行中", testUserId);
    }
    
    @Test
    void testGetTreatmentTasks_Success() throws Exception {
        // Arrange
        List<TreatmentTask> tasks = Arrays.asList(testTask);
        when(treatmentPlanService.getTreatmentTasks(testPlan.getId()))
            .thenReturn(tasks);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/task/plan/{planId}", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testTask.getId()));
        
        verify(treatmentPlanService).getTreatmentTasks(testPlan.getId());
    }
    
    @Test
    void testGetUserAssignedTasks_Success() throws Exception {
        // Arrange
        List<TreatmentTask> tasks = Arrays.asList(testTask);
        when(treatmentPlanService.getUserAssignedTasks(testUserId))
            .thenReturn(tasks);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/task/assigned/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(treatmentPlanService).getUserAssignedTasks(testUserId);
    }
    
    @Test
    void testGetUserCreatedTasks_Success() throws Exception {
        // Arrange
        List<TreatmentTask> tasks = Arrays.asList(testTask);
        when(treatmentPlanService.getUserCreatedTasks(testUserId))
            .thenReturn(tasks);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/task/created/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(treatmentPlanService).getUserCreatedTasks(testUserId);
    }
    
    @Test
    void testCompleteTreatmentTask_Success() throws Exception {
        // Arrange
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setUserId(testUserId);
        request.setCompletionData(Map.of("result", "任务完成"));
        
        testTask.setStatus("已完成");
        
        when(treatmentPlanService.completeTreatmentTask(testTask.getId(), testUserId, request.getCompletionData()))
            .thenReturn(testTask);
        
        // Act & Assert
        mockMvc.perform(put("/api/treatment/task/{taskId}/complete", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("已完成"));
        
        verify(treatmentPlanService).completeTreatmentTask(eq(testTask.getId()), eq(testUserId), any(Map.class));
    }
    
    @Test
    void testGetTreatmentProgress_Success() throws Exception {
        // Arrange
        Map<String, Object> progress = new HashMap<>();
        progress.put("totalTasks", 5);
        progress.put("completedTasks", 3L);
        progress.put("progressPercentage", 60.0);
        
        when(treatmentPlanService.getTreatmentProgress(testPlan.getId()))
            .thenReturn(progress);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/progress/plan/{planId}", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTasks").value(5))
                .andExpect(jsonPath("$.data.completedTasks").value(3))
                .andExpect(jsonPath("$.data.progressPercentage").value(60.0));
        
        verify(treatmentPlanService).getTreatmentProgress(testPlan.getId());
    }
    
    @Test
    void testGetUserTreatmentProgress_Success() throws Exception {
        // Arrange
        Map<String, Object> progress = new HashMap<>();
        progress.put("totalPlans", 3);
        progress.put("completedPlans", 1);
        progress.put("overallProgress", 75.0);
        
        when(treatmentPlanService.getUserTreatmentProgress(testUserId))
            .thenReturn(progress);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/progress/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPlans").value(3))
                .andExpect(jsonPath("$.data.completedPlans").value(1))
                .andExpect(jsonPath("$.data.overallProgress").value(75.0));
        
        verify(treatmentPlanService).getUserTreatmentProgress(testUserId);
    }
    
    @Test
    void testGetSystemTreatmentProgress_Success() throws Exception {
        // Arrange
        Map<String, Object> progress = new HashMap<>();
        progress.put("totalPlans", 100);
        progress.put("totalTasks", 500);
        progress.put("totalUsers", 20);
        
        when(treatmentPlanService.getSystemTreatmentProgress())
            .thenReturn(progress);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/progress/system"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPlans").value(100))
                .andExpect(jsonPath("$.data.totalTasks").value(500))
                .andExpect(jsonPath("$.data.totalUsers").value(20));
        
        verify(treatmentPlanService).getSystemTreatmentProgress();
    }
    
    @Test
    void testGetTreatmentPlanTemplates_Success() throws Exception {
        // Arrange
        List<TreatmentPlan> templates = Arrays.asList(testPlan);
        when(treatmentPlanService.getTreatmentPlanTemplates())
            .thenReturn(templates);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/template"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testPlan.getId()));
        
        verify(treatmentPlanService).getTreatmentPlanTemplates();
    }
    
    @Test
    void testCreatePlanFromTemplate_Success() throws Exception {
        // Arrange
        CreateFromTemplateRequest request = new CreateFromTemplateRequest();
        request.setTemplateId("template-001");
        request.setPestId(testPestId);
        request.setUserId(testUserId);
        request.setParameters(Map.of("targetArea", "200亩"));
        
        when(treatmentPlanService.createPlanFromTemplate("template-001", testPestId, testUserId, request.getParameters()))
            .thenReturn(testPlan);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/plan/from-template")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPlan.getId()));
        
        verify(treatmentPlanService).createPlanFromTemplate("template-001", testPestId, testUserId, request.getParameters());
    }
    
    @Test
    void testGetTreatmentMethods_Success() throws Exception {
        // Arrange
        List<TreatmentMethod> methods = Arrays.asList(testMethod);
        when(treatmentPlanService.getTreatmentMethods())
            .thenReturn(methods);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/method"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testMethod.getId()));
        
        verify(treatmentPlanService).getTreatmentMethods();
    }
    
    @Test
    void testGetRecommendedTreatmentMethods_Success() throws Exception {
        // Arrange
        List<TreatmentMethod> methods = Arrays.asList(testMethod);
        when(treatmentPlanService.getRecommendedTreatmentMethods(testPestId))
            .thenReturn(methods);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/method/recommended")
                .param("pestId", testPestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(treatmentPlanService).getRecommendedTreatmentMethods(testPestId);
    }
    
    @Test
    void testEvaluateTreatmentPlan_Success() throws Exception {
        // Arrange
        Map<String, Object> evaluation = new HashMap<>();
        evaluation.put("effectivenessScore", 0.85);
        evaluation.put("costVariance", 200.0);
        
        when(treatmentPlanService.evaluateTreatmentPlan(testPlan.getId()))
            .thenReturn(evaluation);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/{planId}/evaluation", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.effectivenessScore").value(0.85))
                .andExpect(jsonPath("$.data.costVariance").value(200.0));
        
        verify(treatmentPlanService).evaluateTreatmentPlan(testPlan.getId());
    }
    
    @Test
    void testGetTreatmentPlanHistory_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> history = Arrays.asList(
            Map.of("action", "创建方案", "timestamp", LocalDateTime.now(), "userId", testUserId)
        );
        
        when(treatmentPlanService.getTreatmentPlanHistory(testPlan.getId()))
            .thenReturn(history);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/{planId}/history", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].action").value("创建方案"));
        
        verify(treatmentPlanService).getTreatmentPlanHistory(testPlan.getId());
    }
    
    @Test
    void testCopyTreatmentPlan_Success() throws Exception {
        // Arrange
        TreatmentPlan copiedPlan = new TreatmentPlan();
        copiedPlan.setId("plan-002");
        copiedPlan.setTitle(testPlan.getTitle() + " - 副本");
        
        when(treatmentPlanService.copyTreatmentPlan(testPlan.getId(), testUserId))
            .thenReturn(copiedPlan);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/plan/{planId}/copy", testPlan.getId())
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("plan-002"))
                .andExpect(jsonPath("$.data.title").value(testPlan.getTitle() + " - 副本"));
        
        verify(treatmentPlanService).copyTreatmentPlan(testPlan.getId(), testUserId);
    }
    
    @Test
    void testSearchTreatmentPlans_Success() throws Exception {
        // Arrange
        List<TreatmentPlan> searchResults = Arrays.asList(testPlan);
        when(treatmentPlanService.searchTreatmentPlans("生物", testUserId))
            .thenReturn(searchResults);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/search")
                .param("keyword", "生物")
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testPlan.getId()));
        
        verify(treatmentPlanService).searchTreatmentPlans("生物", testUserId);
    }
    
    @Test
    void testGetTreatmentPlanStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalPlans", 5);
        statistics.put("totalEstimatedCost", 10000.0);
        
        when(treatmentPlanService.getTreatmentPlanStatistics(testUserId))
            .thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/statistics/plan/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPlans").value(5))
                .andExpect(jsonPath("$.data.totalEstimatedCost").value(10000.0));
        
        verify(treatmentPlanService).getTreatmentPlanStatistics(testUserId);
    }
    
    @Test
    void testExportTreatmentPlan_Success() throws Exception {
        // Arrange
        String exportData = "exported plan data";
        when(treatmentPlanService.exportTreatmentPlan(testPlan.getId(), "json"))
            .thenReturn(exportData);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/{planId}/export", testPlan.getId())
                .param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(exportData));
        
        verify(treatmentPlanService).exportTreatmentPlan(testPlan.getId(), "json");
    }
    
    @Test
    void testBatchCreateTreatmentTasks_Success() throws Exception {
        // Arrange
        BatchCreateTasksRequest request = new BatchCreateTasksRequest();
        request.setPlanId(testPlan.getId());
        request.setTasks(Arrays.asList(testTask));
        request.setUserId(testUserId);
        
        List<TreatmentTask> createdTasks = Arrays.asList(testTask);
        when(treatmentPlanService.batchCreateTreatmentTasks(testPlan.getId(), request.getTasks(), testUserId))
            .thenReturn(createdTasks);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/task/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testTask.getId()));
        
        verify(treatmentPlanService).batchCreateTreatmentTasks(eq(testPlan.getId()), any(List.class), eq(testUserId));
    }
    
    @Test
    void testGetTreatmentTaskStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalTasks", 10);
        statistics.put("completionRate", 80.0);
        
        when(treatmentPlanService.getTreatmentTaskStatistics(testUserId))
            .thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/statistics/task/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTasks").value(10))
                .andExpect(jsonPath("$.data.completionRate").value(80.0));
        
        verify(treatmentPlanService).getTreatmentTaskStatistics(testUserId);
    }
    
    @Test
    void testGetTreatmentEffectTrends_Success() throws Exception {
        // Arrange
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        List<Map<String, Object>> trendData = Arrays.asList(
            Map.of("period", "2024-01", "effectivenessScore", 0.85, "completedPlans", 5)
        );
        trends.put("effectivenessTrend", trendData);
        
        when(treatmentPlanService.getTreatmentEffectTrends("monthly"))
            .thenReturn(trends);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/trends")
                .param("period", "monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.effectivenessTrend").isArray())
                .andExpect(jsonPath("$.data.effectivenessTrend[0].period").value("2024-01"));
        
        verify(treatmentPlanService).getTreatmentEffectTrends("monthly");
    }
    
    @Test
    void testGetTreatmentPlanSuggestions_Success() throws Exception {
        // Arrange
        List<String> suggestions = Arrays.asList(
            "建议优先使用生物防治方法",
            "可结合物理防治设置诱捕器"
        );
        
        when(treatmentPlanService.getTreatmentPlanSuggestions(testPestId, "华北"))
            .thenReturn(suggestions);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/suggestions")
                .param("pestId", testPestId)
                .param("region", "华北"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
        
        verify(treatmentPlanService).getTreatmentPlanSuggestions(testPestId, "华北");
    }
    
    @Test
    void testGetPopularTreatmentPlans_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> popular = Arrays.asList(
            Map.of("planType", "生物防治", "pestId", testPestId, "usageCount", 10L)
        );
        
        when(treatmentPlanService.getPopularTreatmentPlans(5))
            .thenReturn(popular);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/popular")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].planType").value("生物防治"));
        
        verify(treatmentPlanService).getPopularTreatmentPlans(5);
    }
    
    @Test
    void testEstimateTreatmentCost_Success() throws Exception {
        // Arrange
        Map<String, Object> costEstimate = new HashMap<>();
        costEstimate.put("totalCost", 3000.0);
        costEstimate.put("materialCost", 1500.0);
        costEstimate.put("laborCost", 1000.0);
        costEstimate.put("equipmentCost", 500.0);
        
        when(treatmentPlanService.estimateTreatmentCost(testPlan.getId()))
            .thenReturn(costEstimate);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/{planId}/cost-estimate", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCost").value(3000.0))
                .andExpect(jsonPath("$.data.materialCost").value(1500.0));
        
        verify(treatmentPlanService).estimateTreatmentCost(testPlan.getId());
    }
    
    @Test
    void testCompareTreatmentPlans_Success() throws Exception {
        // Arrange
        ComparePlansRequest request = new ComparePlansRequest();
        request.setPlanIds(Arrays.asList("plan-001", "plan-002"));
        
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("plans", Arrays.asList(
            Map.of("planId", "plan-001", "title", "方案1"),
            Map.of("planId", "plan-002", "title", "方案2")
        ));
        
        when(treatmentPlanService.compareTreatmentPlans(request.getPlanIds()))
            .thenReturn(comparison);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/plan/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.plans").isArray())
                .andExpect(jsonPath("$.data.plans.length()").value(2));
        
        verify(treatmentPlanService).compareTreatmentPlans(request.getPlanIds());
    }
    
    @Test
    void testGetTreatmentExecutionReport_Success() throws Exception {
        // Arrange
        Map<String, Object> report = new HashMap<>();
        report.put("planId", testPlan.getId());
        report.put("planTitle", testPlan.getTitle());
        report.put("progress", Map.of("progressPercentage", 75.0));
        
        when(treatmentPlanService.getTreatmentExecutionReport(testPlan.getId()))
            .thenReturn(report);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/plan/{planId}/report", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.planId").value(testPlan.getId()))
                .andExpect(jsonPath("$.data.planTitle").value(testPlan.getTitle()));
        
        verify(treatmentPlanService).getTreatmentExecutionReport(testPlan.getId());
    }
    
    @Test
    void testSetTreatmentReminder_Success() throws Exception {
        // Arrange
        SetReminderRequest request = new SetReminderRequest();
        request.setPlanId(testPlan.getId());
        request.setUserId(testUserId);
        request.setReminderConfig(Map.of("type", "daily", "time", "09:00"));
        
        when(treatmentPlanService.setTreatmentReminder(testPlan.getId(), testUserId, request.getReminderConfig()))
            .thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(post("/api/treatment/reminder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(treatmentPlanService).setTreatmentReminder(eq(testPlan.getId()), eq(testUserId), any(Map.class));
    }
    
    @Test
    void testGetTreatmentReminders_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> reminders = Arrays.asList(
            Map.of("id", "reminder-001", "planId", testPlan.getId(), "type", "daily")
        );
        
        when(treatmentPlanService.getTreatmentReminders(testUserId))
            .thenReturn(reminders);
        
        // Act & Assert
        mockMvc.perform(get("/api/treatment/reminder/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("reminder-001"));
        
        verify(treatmentPlanService).getTreatmentReminders(testUserId);
    }
    
    @Test
    void testCancelTreatmentReminder_Success() throws Exception {
        // Arrange
        when(treatmentPlanService.cancelTreatmentReminder("reminder-001", testUserId))
            .thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(delete("/api/treatment/reminder/{reminderId}", "reminder-001")
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(treatmentPlanService).cancelTreatmentReminder("reminder-001", testUserId);
    }
}