package com.forestpest.service;

import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;
import com.forestpest.entity.TreatmentMethod;
import com.forestpest.entity.Pest;
import com.forestpest.exception.BusinessException;
import com.forestpest.repository.PestRepository;
import com.forestpest.service.impl.TreatmentPlanServiceImpl;
import com.forestpest.data.storage.DataStorage;
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
 * 防治方案服务测试类
 */
@ExtendWith(MockitoExtension.class)
class TreatmentPlanServiceTest {
    
    @Mock
    private PestRepository pestRepository;
    
    @Mock
    private DataStorage dataStorage;
    
    @InjectMocks
    private TreatmentPlanServiceImpl treatmentPlanService;
    
    private Pest testPest;
    private String testUserId;
    private TreatmentPlan testPlan;
    private TreatmentTask testTask;
    
    @BeforeEach
    void setUp() {
        testUserId = "test-user-123";
        
        // 创建测试病虫害数据
        testPest = new Pest();
        testPest.setId("pest-001");
        testPest.setName("松毛虫");
        testPest.setType("害虫");
        testPest.setSymptoms(Arrays.asList("叶片被啃食", "有虫粪"));
        
        // 创建测试防治方案
        testPlan = new TreatmentPlan();
        testPlan.setId("plan-001");
        testPlan.setPestId(testPest.getId());
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
        
        // Mock DataStorage
        when(dataStorage.generateId()).thenReturn("generated-id-" + System.currentTimeMillis());
    }
    
    @Test
    void testGenerateTreatmentPlan_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetArea", "100亩");
        parameters.put("estimatedCost", 3000.0);
        
        // Act
        TreatmentPlan plan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, parameters);
        
        // Assert
        assertNotNull(plan);
        assertNotNull(plan.getId());
        assertEquals(testPest.getId(), plan.getPestId());
        assertEquals(testUserId, plan.getCreatedBy());
        assertEquals("自动生成", plan.getPlanType());
        assertEquals("100亩", plan.getTargetArea());
        assertEquals(3000.0, plan.getEstimatedCost());
        assertNotNull(plan.getTreatmentMethods());
        assertFalse(plan.getTreatmentMethods().isEmpty());
        assertNotNull(plan.getTasks());
    }
    
    @Test
    void testGenerateTreatmentPlan_PestNotFound() {
        // Arrange
        when(pestRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.generateTreatmentPlan("non-existent", testUserId, null));
        assertEquals("病虫害信息不存在", exception.getMessage());
    }
    
    @Test
    void testGenerateTreatmentPlan_EmptyPestId() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.generateTreatmentPlan("", testUserId, null));
        assertEquals("病虫害ID不能为空", exception.getMessage());
    }
    
    @Test
    void testGenerateTreatmentPlan_EmptyUserId() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.generateTreatmentPlan(testPest.getId(), "", null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }
    
    @Test
    void testGetTreatmentPlan_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        TreatmentPlan retrievedPlan = treatmentPlanService.getTreatmentPlan(createdPlan.getId());
        
        // Assert
        assertNotNull(retrievedPlan);
        assertEquals(createdPlan.getId(), retrievedPlan.getId());
        assertEquals(createdPlan.getTitle(), retrievedPlan.getTitle());
    }
    
    @Test
    void testGetTreatmentPlan_NotFound() {
        // Act
        TreatmentPlan plan = treatmentPlanService.getTreatmentPlan("non-existent");
        
        // Assert
        assertNull(plan);
    }
    
    @Test
    void testGetUserTreatmentPlans_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        List<TreatmentPlan> plans = treatmentPlanService.getUserTreatmentPlans(testUserId);
        
        // Assert
        assertNotNull(plans);
        assertEquals(2, plans.size());
        assertTrue(plans.stream().allMatch(plan -> testUserId.equals(plan.getCreatedBy())));
    }
    
    @Test
    void testGetUserTreatmentPlans_WithPagination() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        for (int i = 0; i < 5; i++) {
            treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        }
        
        // Act
        List<TreatmentPlan> page1 = treatmentPlanService.getUserTreatmentPlans(testUserId, 0, 2);
        List<TreatmentPlan> page2 = treatmentPlanService.getUserTreatmentPlans(testUserId, 1, 2);
        
        // Assert
        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
        assertNotEquals(page1.get(0).getId(), page2.get(0).getId());
    }
    
    @Test
    void testUpdateTreatmentPlan_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        TreatmentPlan updatedPlan = new TreatmentPlan();
        updatedPlan.setTitle("更新的防治方案");
        updatedPlan.setDescription("更新的描述");
        updatedPlan.setEstimatedCost(5000.0);
        
        // Act
        TreatmentPlan result = treatmentPlanService.updateTreatmentPlan(createdPlan.getId(), testUserId, updatedPlan);
        
        // Assert
        assertNotNull(result);
        assertEquals("更新的防治方案", result.getTitle());
        assertEquals("更新的描述", result.getDescription());
        assertEquals(5000.0, result.getEstimatedCost());
        assertNotNull(result.getUpdatedTime());
        assertEquals(testUserId, result.getUpdatedBy());
    }
    
    @Test
    void testUpdateTreatmentPlan_NotFound() {
        // Arrange
        TreatmentPlan updatedPlan = new TreatmentPlan();
        updatedPlan.setTitle("更新的防治方案");
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.updateTreatmentPlan("non-existent", testUserId, updatedPlan));
        assertEquals("防治方案不存在", exception.getMessage());
    }
    
    @Test
    void testUpdateTreatmentPlan_NoPermission() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        TreatmentPlan updatedPlan = new TreatmentPlan();
        updatedPlan.setTitle("更新的防治方案");
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.updateTreatmentPlan(createdPlan.getId(), "other-user", updatedPlan));
        assertEquals("无权限修改此防治方案", exception.getMessage());
    }
    
    @Test
    void testSaveCustomTreatmentPlan_Success() {
        // Arrange
        when(pestRepository.existsById(testPest.getId())).thenReturn(true);
        
        TreatmentPlan customPlan = new TreatmentPlan();
        customPlan.setPestId(testPest.getId());
        customPlan.setTitle("自定义防治方案");
        customPlan.setDescription("用户自定义的方案");
        customPlan.setTreatmentMethods(Arrays.asList(new TreatmentMethod()));
        
        // Act
        TreatmentPlan savedPlan = treatmentPlanService.saveCustomTreatmentPlan(customPlan, testUserId);
        
        // Assert
        assertNotNull(savedPlan);
        assertNotNull(savedPlan.getId());
        assertEquals(testUserId, savedPlan.getCreatedBy());
        assertEquals("自定义", savedPlan.getPlanType());
        assertEquals("草稿", savedPlan.getStatus());
        assertNotNull(savedPlan.getCreatedTime());
    }
    
    @Test
    void testSaveCustomTreatmentPlan_NullPlan() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.saveCustomTreatmentPlan(null, testUserId));
        assertEquals("防治方案不能为空", exception.getMessage());
    }
    
    @Test
    void testDeleteTreatmentPlan_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        boolean deleted = treatmentPlanService.deleteTreatmentPlan(createdPlan.getId(), testUserId);
        
        // Assert
        assertTrue(deleted);
        assertNull(treatmentPlanService.getTreatmentPlan(createdPlan.getId()));
    }
    
    @Test
    void testDeleteTreatmentPlan_NoPermission() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        boolean deleted = treatmentPlanService.deleteTreatmentPlan(createdPlan.getId(), "other-user");
        
        // Assert
        assertFalse(deleted);
        assertNotNull(treatmentPlanService.getTreatmentPlan(createdPlan.getId()));
    }
    
    @Test
    void testCreateTreatmentTask_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        TreatmentTask newTask = new TreatmentTask();
        newTask.setTitle("新的防治任务");
        newTask.setDescription("任务描述");
        newTask.setPriority("中");
        
        // Act
        TreatmentTask createdTask = treatmentPlanService.createTreatmentTask(createdPlan.getId(), newTask, testUserId);
        
        // Assert
        assertNotNull(createdTask);
        assertNotNull(createdTask.getId());
        assertEquals(createdPlan.getId(), createdTask.getPlanId());
        assertEquals(testUserId, createdTask.getCreatedBy());
        assertEquals("待执行", createdTask.getStatus());
        assertNotNull(createdTask.getCreatedTime());
    }
    
    @Test
    void testCreateTreatmentTask_PlanNotFound() {
        // Arrange
        TreatmentTask newTask = new TreatmentTask();
        newTask.setTitle("新的防治任务");
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.createTreatmentTask("non-existent", newTask, testUserId));
        assertEquals("防治方案不存在", exception.getMessage());
    }
    
    @Test
    void testAssignTreatmentTask_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        TreatmentTask createdTask = treatmentPlanService.createTreatmentTask(createdPlan.getId(), testTask, testUserId);
        
        String assigneeId = "assignee-123";
        
        // Act
        TreatmentTask assignedTask = treatmentPlanService.assignTreatmentTask(createdTask.getId(), assigneeId, testUserId);
        
        // Assert
        assertNotNull(assignedTask);
        assertEquals(assigneeId, assignedTask.getAssigneeId());
        assertEquals("已分配", assignedTask.getStatus());
        assertNotNull(assignedTask.getAssignedTime());
    }
    
    @Test
    void testAssignTreatmentTask_TaskNotFound() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.assignTreatmentTask("non-existent", "assignee", testUserId));
        assertEquals("防治任务不存在", exception.getMessage());
    }
    
    @Test
    void testUpdateTaskStatus_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        TreatmentTask createdTask = treatmentPlanService.createTreatmentTask(createdPlan.getId(), testTask, testUserId);
        
        // Act
        TreatmentTask updatedTask = treatmentPlanService.updateTaskStatus(createdTask.getId(), "进行中", testUserId);
        
        // Assert
        assertNotNull(updatedTask);
        assertEquals("进行中", updatedTask.getStatus());
        assertNotNull(updatedTask.getUpdatedTime());
        assertEquals(testUserId, updatedTask.getUpdatedBy());
    }
    
    @Test
    void testCompleteTreatmentTask_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        TreatmentTask createdTask = treatmentPlanService.createTreatmentTask(createdPlan.getId(), testTask, testUserId);
        treatmentPlanService.assignTreatmentTask(createdTask.getId(), testUserId, testUserId);
        
        Map<String, Object> completionData = new HashMap<>();
        completionData.put("result", "任务完成");
        completionData.put("notes", "执行顺利");
        
        // Act
        TreatmentTask completedTask = treatmentPlanService.completeTreatmentTask(createdTask.getId(), testUserId, completionData);
        
        // Assert
        assertNotNull(completedTask);
        assertEquals("已完成", completedTask.getStatus());
        assertNotNull(completedTask.getCompletedTime());
        assertEquals(completionData, completedTask.getCompletionData());
    }
    
    @Test
    void testGetTreatmentProgress_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // 创建并完成一个任务
        TreatmentTask task1 = treatmentPlanService.createTreatmentTask(createdPlan.getId(), testTask, testUserId);
        treatmentPlanService.assignTreatmentTask(task1.getId(), testUserId, testUserId);
        treatmentPlanService.completeTreatmentTask(task1.getId(), testUserId, new HashMap<>());
        
        // 创建另一个未完成的任务
        TreatmentTask task2 = new TreatmentTask();
        task2.setTitle("未完成任务");
        treatmentPlanService.createTreatmentTask(createdPlan.getId(), task2, testUserId);
        
        // Act
        Map<String, Object> progress = treatmentPlanService.getTreatmentProgress(createdPlan.getId());
        
        // Assert
        assertNotNull(progress);
        assertTrue(progress.containsKey("totalTasks"));
        assertTrue(progress.containsKey("completedTasks"));
        assertTrue(progress.containsKey("progressPercentage"));
        assertTrue(progress.containsKey("statusStats"));
        
        // 验证进度计算
        int totalTasks = (Integer) progress.get("totalTasks");
        long completedTasks = (Long) progress.get("completedTasks");
        double progressPercentage = (Double) progress.get("progressPercentage");
        
        assertTrue(totalTasks > 0);
        assertTrue(completedTasks >= 0);
        assertEquals((double) completedTasks / totalTasks * 100, progressPercentage, 0.01);
    }
    
    @Test
    void testGetTreatmentPlanTemplates_Success() {
        // Act
        List<TreatmentPlan> templates = treatmentPlanService.getTreatmentPlanTemplates();
        
        // Assert
        assertNotNull(templates);
        assertFalse(templates.isEmpty());
        assertTrue(templates.stream().anyMatch(t -> t.getTitle().contains("生物防治")));
        assertTrue(templates.stream().anyMatch(t -> t.getTitle().contains("化学防治")));
        assertTrue(templates.stream().anyMatch(t -> t.getTitle().contains("物理防治")));
        assertTrue(templates.stream().anyMatch(t -> t.getTitle().contains("综合防治")));
    }
    
    @Test
    void testCreatePlanFromTemplate_Success() {
        // Arrange
        List<TreatmentPlan> templates = treatmentPlanService.getTreatmentPlanTemplates();
        TreatmentPlan template = templates.get(0);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetArea", "200亩");
        parameters.put("estimatedCost", 4000.0);
        
        // Act
        TreatmentPlan newPlan = treatmentPlanService.createPlanFromTemplate(
            template.getId(), testPest.getId(), testUserId, parameters);
        
        // Assert
        assertNotNull(newPlan);
        assertNotNull(newPlan.getId());
        assertEquals(testPest.getId(), newPlan.getPestId());
        assertEquals(testUserId, newPlan.getCreatedBy());
        assertTrue(newPlan.getTitle().contains(template.getTitle()));
        assertEquals("200亩", newPlan.getTargetArea());
        assertEquals(4000.0, newPlan.getEstimatedCost());
    }
    
    @Test
    void testGetTreatmentMethods_Success() {
        // Act
        List<TreatmentMethod> methods = treatmentPlanService.getTreatmentMethods();
        
        // Assert
        assertNotNull(methods);
        assertFalse(methods.isEmpty());
        assertTrue(methods.stream().anyMatch(m -> m.getType().contains("生物防治")));
        assertTrue(methods.stream().anyMatch(m -> m.getType().contains("化学防治")));
        assertTrue(methods.stream().anyMatch(m -> m.getType().contains("物理防治")));
        assertTrue(methods.stream().anyMatch(m -> m.getType().contains("栽培防治")));
    }
    
    @Test
    void testGetRecommendedTreatmentMethods_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        
        // Act
        List<TreatmentMethod> methods = treatmentPlanService.getRecommendedTreatmentMethods(testPest.getId());
        
        // Assert
        assertNotNull(methods);
        assertFalse(methods.isEmpty());
        // 对于害虫类型，应该推荐生物、物理、化学防治方法
        assertTrue(methods.stream().anyMatch(m -> 
            m.getType().contains("生物") || m.getType().contains("物理") || m.getType().contains("化学")));
    }
    
    @Test
    void testEvaluateTreatmentPlan_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        Map<String, Object> evaluation = treatmentPlanService.evaluateTreatmentPlan(createdPlan.getId());
        
        // Assert
        assertNotNull(evaluation);
        assertTrue(evaluation.containsKey("progress"));
        assertTrue(evaluation.containsKey("estimatedCost"));
        assertTrue(evaluation.containsKey("actualCost"));
        assertTrue(evaluation.containsKey("costVariance"));
        assertTrue(evaluation.containsKey("expectedDuration"));
        assertTrue(evaluation.containsKey("actualDuration"));
        assertTrue(evaluation.containsKey("effectivenessScore"));
        
        // 验证效果评分在合理范围内
        double effectivenessScore = (Double) evaluation.get("effectivenessScore");
        assertTrue(effectivenessScore >= 0.7 && effectivenessScore <= 1.0);
    }
    
    @Test
    void testCopyTreatmentPlan_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan originalPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        String newUserId = "new-user-123";
        
        // Act
        TreatmentPlan copiedPlan = treatmentPlanService.copyTreatmentPlan(originalPlan.getId(), newUserId);
        
        // Assert
        assertNotNull(copiedPlan);
        assertNotEquals(originalPlan.getId(), copiedPlan.getId());
        assertEquals(newUserId, copiedPlan.getCreatedBy());
        assertTrue(copiedPlan.getTitle().contains("副本"));
        assertEquals(originalPlan.getPestId(), copiedPlan.getPestId());
        assertEquals(originalPlan.getDescription(), copiedPlan.getDescription());
        assertEquals("草稿", copiedPlan.getStatus());
    }
    
    @Test
    void testSearchTreatmentPlans_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan plan1 = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        TreatmentPlan customPlan = new TreatmentPlan();
        customPlan.setPestId(testPest.getId());
        customPlan.setTitle("生物防治方案");
        customPlan.setDescription("专门的生物防治");
        customPlan.setTreatmentMethods(Arrays.asList(new TreatmentMethod()));
        when(pestRepository.existsById(testPest.getId())).thenReturn(true);
        treatmentPlanService.saveCustomTreatmentPlan(customPlan, testUserId);
        
        // Act
        List<TreatmentPlan> searchResults = treatmentPlanService.searchTreatmentPlans("生物", testUserId);
        
        // Assert
        assertNotNull(searchResults);
        assertFalse(searchResults.isEmpty());
        assertTrue(searchResults.stream().anyMatch(plan -> 
            plan.getTitle().toLowerCase().contains("生物") || 
            plan.getDescription().toLowerCase().contains("生物") ||
            plan.getPlanType().toLowerCase().contains("生物")));
    }
    
    @Test
    void testGetTreatmentPlanStatistics_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        Map<String, Object> statistics = treatmentPlanService.getTreatmentPlanStatistics(testUserId);
        
        // Assert
        assertNotNull(statistics);
        assertEquals(2, statistics.get("totalPlans"));
        assertTrue(statistics.containsKey("byType"));
        assertTrue(statistics.containsKey("byStatus"));
        assertTrue(statistics.containsKey("byMonth"));
        assertTrue(statistics.containsKey("totalEstimatedCost"));
        
        // 验证按类型统计
        @SuppressWarnings("unchecked")
        Map<String, Long> typeStats = (Map<String, Long>) statistics.get("byType");
        assertTrue(typeStats.containsKey("自动生成"));
        assertEquals(2L, typeStats.get("自动生成").longValue());
    }
    
    @Test
    void testExportTreatmentPlan_JSON() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        String exportData = treatmentPlanService.exportTreatmentPlan(createdPlan.getId(), "json");
        
        // Assert
        assertNotNull(exportData);
        assertFalse(exportData.isEmpty());
    }
    
    @Test
    void testExportTreatmentPlan_CSV() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        String exportData = treatmentPlanService.exportTreatmentPlan(createdPlan.getId(), "csv");
        
        // Assert
        assertNotNull(exportData);
        assertTrue(exportData.contains("方案标题,方案类型,创建时间,状态,预估成本"));
        assertTrue(exportData.contains(createdPlan.getTitle()));
    }
    
    @Test
    void testExportTreatmentPlan_UnsupportedFormat() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.exportTreatmentPlan(createdPlan.getId(), "xml"));
        assertEquals("不支持的导出格式: xml", exception.getMessage());
    }
    
    @Test
    void testBatchCreateTreatmentTasks_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        List<TreatmentTask> tasks = Arrays.asList(
            createTestTask("任务1", "描述1"),
            createTestTask("任务2", "描述2"),
            createTestTask("任务3", "描述3")
        );
        
        // Act
        List<TreatmentTask> createdTasks = treatmentPlanService.batchCreateTreatmentTasks(
            createdPlan.getId(), tasks, testUserId);
        
        // Assert
        assertNotNull(createdTasks);
        assertEquals(3, createdTasks.size());
        for (TreatmentTask task : createdTasks) {
            assertNotNull(task.getId());
            assertEquals(createdPlan.getId(), task.getPlanId());
            assertEquals(testUserId, task.getCreatedBy());
        }
    }
    
    @Test
    void testBatchCreateTreatmentTasks_TooManyTasks() {
        // Arrange
        List<TreatmentTask> tooManyTasks = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            tooManyTasks.add(createTestTask("任务" + i, "描述" + i));
        }
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.batchCreateTreatmentTasks("plan-id", tooManyTasks, testUserId));
        assertEquals("批量创建任务最多支持20个", exception.getMessage());
    }
    
    @Test
    void testGetTreatmentTaskStatistics_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // 创建并分配任务
        TreatmentTask task1 = treatmentPlanService.createTreatmentTask(createdPlan.getId(), testTask, testUserId);
        treatmentPlanService.assignTreatmentTask(task1.getId(), testUserId, testUserId);
        treatmentPlanService.completeTreatmentTask(task1.getId(), testUserId, new HashMap<>());
        
        TreatmentTask task2 = createTestTask("任务2", "描述2");
        TreatmentTask createdTask2 = treatmentPlanService.createTreatmentTask(createdPlan.getId(), task2, testUserId);
        treatmentPlanService.assignTreatmentTask(createdTask2.getId(), testUserId, testUserId);
        
        // Act
        Map<String, Object> statistics = treatmentPlanService.getTreatmentTaskStatistics(testUserId);
        
        // Assert
        assertNotNull(statistics);
        assertTrue(statistics.containsKey("totalTasks"));
        assertTrue(statistics.containsKey("byStatus"));
        assertTrue(statistics.containsKey("byPriority"));
        assertTrue(statistics.containsKey("completionRate"));
        
        // 验证完成率计算
        double completionRate = (Double) statistics.get("completionRate");
        assertTrue(completionRate >= 0.0 && completionRate <= 100.0);
    }
    
    @Test
    void testGetTreatmentEffectTrends_Success() {
        // Act
        Map<String, List<Map<String, Object>>> trends = treatmentPlanService.getTreatmentEffectTrends("monthly");
        
        // Assert
        assertNotNull(trends);
        assertTrue(trends.containsKey("effectivenessTrend"));
        
        List<Map<String, Object>> trendData = trends.get("effectivenessTrend");
        assertNotNull(trendData);
        assertFalse(trendData.isEmpty());
        
        // 验证趋势数据结构
        Map<String, Object> firstItem = trendData.get(0);
        assertTrue(firstItem.containsKey("period"));
        assertTrue(firstItem.containsKey("effectivenessScore"));
        assertTrue(firstItem.containsKey("completedPlans"));
        
        // 验证效果评分范围
        double effectivenessScore = (Double) firstItem.get("effectivenessScore");
        assertTrue(effectivenessScore >= 0.7 && effectivenessScore <= 1.0);
    }
    
    @Test
    void testValidateTreatmentPlan_Success() {
        // Arrange
        when(pestRepository.existsById(testPest.getId())).thenReturn(true);
        
        TreatmentPlan validPlan = new TreatmentPlan();
        validPlan.setTitle("有效的方案");
        validPlan.setPestId(testPest.getId());
        validPlan.setTreatmentMethods(Arrays.asList(new TreatmentMethod()));
        
        // Act
        boolean isValid = treatmentPlanService.validateTreatmentPlan(validPlan);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void testValidateTreatmentPlan_InvalidTitle() {
        // Arrange
        TreatmentPlan invalidPlan = new TreatmentPlan();
        invalidPlan.setTitle(""); // 空标题
        invalidPlan.setPestId(testPest.getId());
        invalidPlan.setTreatmentMethods(Arrays.asList(new TreatmentMethod()));
        
        // Act
        boolean isValid = treatmentPlanService.validateTreatmentPlan(invalidPlan);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testValidateTreatmentPlan_NoMethods() {
        // Arrange
        TreatmentPlan invalidPlan = new TreatmentPlan();
        invalidPlan.setTitle("有效的方案");
        invalidPlan.setPestId(testPest.getId());
        invalidPlan.setTreatmentMethods(new ArrayList<>()); // 空方法列表
        
        // Act
        boolean isValid = treatmentPlanService.validateTreatmentPlan(invalidPlan);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testGetTreatmentPlanSuggestions_ForPest() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        
        // Act
        List<String> suggestions = treatmentPlanService.getTreatmentPlanSuggestions(testPest.getId(), "华北");
        
        // Assert
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        
        // 对于害虫类型，应该包含生物防治建议
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("生物防治")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("华北")));
    }
    
    @Test
    void testGetPopularTreatmentPlans_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        treatmentPlanService.generateTreatmentPlan(testPest.getId(), "user2", null);
        
        // Act
        List<Map<String, Object>> popular = treatmentPlanService.getPopularTreatmentPlans(5);
        
        // Assert
        assertNotNull(popular);
        assertFalse(popular.isEmpty());
        
        Map<String, Object> firstItem = popular.get(0);
        assertTrue(firstItem.containsKey("planType"));
        assertTrue(firstItem.containsKey("pestId"));
        assertTrue(firstItem.containsKey("usageCount"));
        
        // 验证使用次数
        Long usageCount = (Long) firstItem.get("usageCount");
        assertTrue(usageCount >= 1);
    }
    
    @Test
    void testEstimateTreatmentCost_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        Map<String, Object> costEstimate = treatmentPlanService.estimateTreatmentCost(createdPlan.getId());
        
        // Assert
        assertNotNull(costEstimate);
        assertTrue(costEstimate.containsKey("materialCost"));
        assertTrue(costEstimate.containsKey("laborCost"));
        assertTrue(costEstimate.containsKey("equipmentCost"));
        assertTrue(costEstimate.containsKey("totalCost"));
        assertTrue(costEstimate.containsKey("estimatedCost"));
        assertTrue(costEstimate.containsKey("costDifference"));
        
        // 验证成本数据类型
        assertTrue(costEstimate.get("materialCost") instanceof Double);
        assertTrue(costEstimate.get("laborCost") instanceof Double);
        assertTrue(costEstimate.get("equipmentCost") instanceof Double);
        assertTrue(costEstimate.get("totalCost") instanceof Double);
    }
    
    @Test
    void testCompareTreatmentPlans_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan plan1 = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        TreatmentPlan plan2 = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        List<String> planIds = Arrays.asList(plan1.getId(), plan2.getId());
        
        // Act
        Map<String, Object> comparison = treatmentPlanService.compareTreatmentPlans(planIds);
        
        // Assert
        assertNotNull(comparison);
        assertTrue(comparison.containsKey("plans"));
        assertTrue(comparison.containsKey("comparisonTime"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> plans = (List<Map<String, Object>>) comparison.get("plans");
        assertEquals(2, plans.size());
        
        // 验证方案数据结构
        Map<String, Object> firstPlan = plans.get(0);
        assertTrue(firstPlan.containsKey("planId"));
        assertTrue(firstPlan.containsKey("title"));
        assertTrue(firstPlan.containsKey("planType"));
        assertTrue(firstPlan.containsKey("estimatedCost"));
        assertTrue(firstPlan.containsKey("progress"));
    }
    
    @Test
    void testCompareTreatmentPlans_TooFewPlans() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> treatmentPlanService.compareTreatmentPlans(Arrays.asList("plan1")));
        assertEquals("至少需要两个方案进行对比", exception.getMessage());
    }
    
    @Test
    void testGetTreatmentExecutionReport_Success() {
        // Arrange
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        TreatmentPlan createdPlan = treatmentPlanService.generateTreatmentPlan(testPest.getId(), testUserId, null);
        
        // Act
        Map<String, Object> report = treatmentPlanService.getTreatmentExecutionReport(createdPlan.getId());
        
        // Assert
        assertNotNull(report);
        assertTrue(report.containsKey("planId"));
        assertTrue(report.containsKey("planTitle"));
        assertTrue(report.containsKey("planType"));
        assertTrue(report.containsKey("createdTime"));
        assertTrue(report.containsKey("status"));
        assertTrue(report.containsKey("progress"));
        assertTrue(report.containsKey("taskSummary"));
        assertTrue(report.containsKey("costAnalysis"));
        assertTrue(report.containsKey("evaluation"));
        assertTrue(report.containsKey("reportGeneratedTime"));
        
        assertEquals(createdPlan.getId(), report.get("planId"));
        assertEquals(createdPlan.getTitle(), report.get("planTitle"));
    }
    
    // ========== 辅助方法 ==========
    
    private TreatmentTask createTestTask(String title, String description) {
        TreatmentTask task = new TreatmentTask();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority("中");
        return task;
    }
}