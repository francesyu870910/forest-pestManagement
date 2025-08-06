package com.forestpest.service.impl;

import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;
import com.forestpest.entity.TreatmentMethod;
import com.forestpest.entity.Pest;
import com.forestpest.repository.PestRepository;
import com.forestpest.service.TreatmentPlanService;
import com.forestpest.data.storage.DataStorage;
import com.forestpest.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 防治方案服务简化实现类
 * 
 * 这是TreatmentPlanServiceImpl的简化版本，只实现核心功能。
 * 复杂的业务逻辑方法暂时抛出未实现异常。
 */
@Service
public class TreatmentPlanServiceImpl implements TreatmentPlanService {
    
    @Autowired
    private PestRepository pestRepository;
    
    @Autowired
    private DataStorage dataStorage;
    
    // 防治方案存储
    private final Map<String, TreatmentPlan> treatmentPlans = new ConcurrentHashMap<>();
    
    // 防治任务存储
    private final Map<String, TreatmentTask> treatmentTasks = new ConcurrentHashMap<>();
    
    // 用户方案索引 - 用户ID -> 方案ID列表
    private final Map<String, List<String>> userPlanIndex = new ConcurrentHashMap<>();
    
    // 方案任务索引 - 方案ID -> 任务ID列表
    private final Map<String, List<String>> planTaskIndex = new ConcurrentHashMap<>();
    
    /**
     * 统一的未实现异常处理方法
     */
    private void throwNotImplementedException(String methodName) {
        throw new UnsupportedOperationException(
            String.format("方法 %s 在简化版本中暂未实现，请使用完整版本或等待后续更新", methodName)
        );
    }
    
    /**
     * 保存方案到存储并更新索引
     */
    private void saveTreatmentPlan(TreatmentPlan plan, String userId) {
        treatmentPlans.put(plan.getId(), plan);
        userPlanIndex.computeIfAbsent(userId, k -> new ArrayList<>()).add(plan.getId());
    }
    
    // ==================== 核心实现方法 ====================
    
    @Override
    public TreatmentPlan generateTreatmentPlan(String pestId, String userId, Map<String, Object> parameters) {
        // 验证输入参数
        if (pestId == null || pestId.trim().isEmpty()) {
            throw new BusinessException("病虫害ID不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证病虫害是否存在
        Optional<Pest> pestOpt = pestRepository.findById(pestId);
        if (!pestOpt.isPresent()) {
            throw new BusinessException("病虫害信息不存在");
        }
        
        Pest pest = pestOpt.get();
        
        // 创建基本的防治方案
        TreatmentPlan plan = new TreatmentPlan();
        plan.setId(dataStorage.generateId());
        plan.setPestId(pestId);
        plan.setPlanName("防治方案 - " + pest.getName());
        plan.setDescription("针对" + pest.getName() + "的防治方案");
        plan.setCreatedBy(userId);
        plan.setCreatedTime(LocalDateTime.now());
        plan.setStatus("DRAFT");
        plan.setPriority("MEDIUM");
        
        // 设置基本的防治方法
        List<TreatmentMethod> methods = new ArrayList<>();
        TreatmentMethod basicMethod = new TreatmentMethod();
        basicMethod.setMethodType("综合防治");
        basicMethod.setMethodName("标准防治方案");
        basicMethod.setDescription("采用综合防治方法控制" + pest.getName());
        methods.add(basicMethod);
        plan.setMethods(methods);
        
        // 应用参数设置
        if (parameters != null) {
            if (parameters.containsKey("targetArea")) {
                plan.setTargetArea((String) parameters.get("targetArea"));
            }
            if (parameters.containsKey("estimatedCost")) {
                plan.setEstimatedCost((Double) parameters.get("estimatedCost"));
            }
            if (parameters.containsKey("estimatedDuration")) {
                plan.setEstimatedDuration((Integer) parameters.get("estimatedDuration"));
            }
        }
        
        // 保存方案
        saveTreatmentPlan(plan, userId);
        
        return plan;
    }
    
    @Override
    public TreatmentPlan getTreatmentPlan(String planId) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new BusinessException("方案ID不能为空");
        }
        
        return treatmentPlans.get(planId);
    }
    
    @Override
    public List<TreatmentPlan> getUserTreatmentPlans(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        List<String> planIds = userPlanIndex.get(userId);
        if (planIds == null) {
            return new ArrayList<>();
        }
        
        return planIds.stream()
                .map(treatmentPlans::get)
                .filter(Objects::nonNull)
                .sorted((p1, p2) -> p2.getCreatedTime().compareTo(p1.getCreatedTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentPlan> getUserTreatmentPlans(String userId, int page, int size) {
        List<TreatmentPlan> allPlans = getUserTreatmentPlans(userId);
        
        int start = page * size;
        int end = Math.min(start + size, allPlans.size());
        
        if (start >= allPlans.size()) {
            return new ArrayList<>();
        }
        
        return allPlans.subList(start, end);
    }
    
    @Override
    public TreatmentPlan updateTreatmentPlan(String planId, String userId, TreatmentPlan updatedPlan) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new BusinessException("方案ID不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        if (updatedPlan == null) {
            throw new BusinessException("更新的方案信息不能为空");
        }
        
        TreatmentPlan existingPlan = treatmentPlans.get(planId);
        if (existingPlan == null) {
            throw new BusinessException("防治方案不存在");
        }
        
        if (!userId.equals(existingPlan.getCreatedBy())) {
            throw new BusinessException("无权限修改此防治方案");
        }
        
        // 更新方案信息
        existingPlan.setPlanName(updatedPlan.getPlanName());
        existingPlan.setDescription(updatedPlan.getDescription());
        existingPlan.setMethods(updatedPlan.getMethods());
        existingPlan.setTargetArea(updatedPlan.getTargetArea());
        existingPlan.setEstimatedCost(updatedPlan.getEstimatedCost());
        existingPlan.setEstimatedDuration(updatedPlan.getEstimatedDuration());
        existingPlan.setPriority(updatedPlan.getPriority());
        existingPlan.setUpdatedTime(LocalDateTime.now());
        existingPlan.setUpdatedBy(userId);
        
        return existingPlan;
    }
    
    @Override
    public TreatmentPlan saveCustomTreatmentPlan(TreatmentPlan plan, String userId) {
        if (plan == null) {
            throw new BusinessException("防治方案不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 设置基本信息
        plan.setId(dataStorage.generateId());
        plan.setCreatedBy(userId);
        plan.setCreatedTime(LocalDateTime.now());
        plan.setStatus("DRAFT");
        
        // 验证方案
        if (!validateTreatmentPlan(plan)) {
            throw new BusinessException("防治方案验证失败");
        }
        
        // 保存方案
        saveTreatmentPlan(plan, userId);
        
        return plan;
    }
    
    @Override
    public boolean deleteTreatmentPlan(String planId, String userId) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new BusinessException("方案ID不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        TreatmentPlan plan = treatmentPlans.get(planId);
        if (plan == null) {
            return false;
        }
        
        if (!userId.equals(plan.getCreatedBy())) {
            throw new BusinessException("无权限删除此防治方案");
        }
        
        // 删除相关任务
        List<String> taskIds = planTaskIndex.get(planId);
        if (taskIds != null) {
            for (String taskId : taskIds) {
                treatmentTasks.remove(taskId);
            }
            planTaskIndex.remove(planId);
        }
        
        // 删除方案
        treatmentPlans.remove(planId);
        
        // 更新用户索引
        List<String> userPlans = userPlanIndex.get(userId);
        if (userPlans != null) {
            userPlans.remove(planId);
        }
        
        return true;
    }
    
    @Override
    public TreatmentTask createTreatmentTask(String planId, TreatmentTask task, String userId) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new BusinessException("方案ID不能为空");
        }
        
        if (task == null) {
            throw new BusinessException("任务信息不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        TreatmentPlan plan = treatmentPlans.get(planId);
        if (plan == null) {
            throw new BusinessException("防治方案不存在");
        }
        
        if (!userId.equals(plan.getCreatedBy())) {
            throw new BusinessException("无权限为此方案创建任务");
        }
        
        // 设置任务基本信息
        task.setId(dataStorage.generateId());
        task.setPlanId(planId);
        task.setCreatedBy(userId);
        task.setCreatedTime(LocalDateTime.now());
        task.setStatus("PENDING");
        
        // 保存任务
        treatmentTasks.put(task.getId(), task);
        
        // 更新索引
        planTaskIndex.computeIfAbsent(planId, k -> new ArrayList<>()).add(task.getId());
        
        return task;
    }
    
    @Override
    public TreatmentTask assignTreatmentTask(String taskId, String assigneeId, String assignerId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new BusinessException("任务ID不能为空");
        }
        
        if (assigneeId == null || assigneeId.trim().isEmpty()) {
            throw new BusinessException("分配对象ID不能为空");
        }
        
        if (assignerId == null || assignerId.trim().isEmpty()) {
            throw new BusinessException("分配人ID不能为空");
        }
        
        TreatmentTask task = treatmentTasks.get(taskId);
        if (task == null) {
            throw new BusinessException("防治任务不存在");
        }
        
        // 检查权限
        TreatmentPlan plan = treatmentPlans.get(task.getPlanId());
        if (plan == null || !assignerId.equals(plan.getCreatedBy())) {
            throw new BusinessException("无权限分配此任务");
        }
        
        // 更新任务分配信息
        task.setAssignedTo(assigneeId);
        task.setStatus("ASSIGNED");
        task.setUpdatedTime(LocalDateTime.now());
        task.setUpdatedBy(assignerId);
        
        return task;
    }
    
    @Override
    public TreatmentTask updateTaskStatus(String taskId, String status, String userId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new BusinessException("任务ID不能为空");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new BusinessException("任务状态不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        TreatmentTask task = treatmentTasks.get(taskId);
        if (task == null) {
            throw new BusinessException("防治任务不存在");
        }
        
        // 检查权限
        if (!userId.equals(task.getAssignedTo()) && !userId.equals(task.getCreatedBy())) {
            throw new BusinessException("无权限更新此任务状态");
        }
        
        task.setStatus(status);
        task.setUpdatedTime(LocalDateTime.now());
        task.setUpdatedBy(userId);
        
        return task;
    }
    
    @Override
    public List<TreatmentTask> getTreatmentTasks(String planId) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new BusinessException("方案ID不能为空");
        }
        
        List<String> taskIds = planTaskIndex.get(planId);
        if (taskIds == null) {
            return new ArrayList<>();
        }
        
        return taskIds.stream()
                .map(treatmentTasks::get)
                .filter(Objects::nonNull)
                .sorted((t1, t2) -> t1.getCreatedTime().compareTo(t2.getCreatedTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentTask> getUserAssignedTasks(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return treatmentTasks.values().stream()
                .filter(task -> userId.equals(task.getAssignedTo()))
                .sorted((t1, t2) -> t2.getCreatedTime().compareTo(t1.getCreatedTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentTask> getUserCreatedTasks(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return treatmentTasks.values().stream()
                .filter(task -> userId.equals(task.getCreatedBy()))
                .sorted((t1, t2) -> t2.getCreatedTime().compareTo(t1.getCreatedTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public TreatmentTask completeTreatmentTask(String taskId, String userId, Map<String, Object> completionData) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new BusinessException("任务ID不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        TreatmentTask task = treatmentTasks.get(taskId);
        if (task == null) {
            throw new BusinessException("防治任务不存在");
        }
        
        if (!userId.equals(task.getAssignedTo())) {
            throw new BusinessException("只有任务执行者可以完成任务");
        }
        
        task.setStatus("COMPLETED");
        task.setActualEndTime(LocalDateTime.now());
        task.setUpdatedTime(LocalDateTime.now());
        task.setUpdatedBy(userId);
        
        // 处理完成数据
        if (completionData != null) {
            if (completionData.containsKey("executionNotes")) {
                task.setExecutionNotes((String) completionData.get("executionNotes"));
            }
            if (completionData.containsKey("actualCost")) {
                task.setActualCost((Double) completionData.get("actualCost"));
            }
        }
        
        return task;
    }
    
    @Override
    public Map<String, Object> getTreatmentProgress(String planId) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new BusinessException("方案ID不能为空");
        }
        
        List<TreatmentTask> tasks = getTreatmentTasks(planId);
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("totalTasks", tasks.size());
        
        if (tasks.isEmpty()) {
            progress.put("completedTasks", 0);
            progress.put("progressPercentage", 0.0);
            return progress;
        }
        
        long completedTasks = tasks.stream()
                .mapToLong(task -> "COMPLETED".equals(task.getStatus()) ? 1 : 0)
                .sum();
        
        progress.put("completedTasks", completedTasks);
        progress.put("progressPercentage", (double) completedTasks / tasks.size() * 100);
        
        // 按状态统计
        Map<String, Long> statusStats = tasks.stream()
                .collect(Collectors.groupingBy(TreatmentTask::getStatus, Collectors.counting()));
        progress.put("statusStats", statusStats);
        
        return progress;
    }
    
    @Override
    public Map<String, Object> getUserTreatmentProgress(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        List<TreatmentPlan> userPlans = getUserTreatmentPlans(userId);
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("totalPlans", userPlans.size());
        
        int completedPlans = 0;
        int totalTasks = 0;
        int completedTasks = 0;
        
        for (TreatmentPlan plan : userPlans) {
            Map<String, Object> planProgress = getTreatmentProgress(plan.getId());
            int planTotalTasks = (Integer) planProgress.get("totalTasks");
            int planCompletedTasks = ((Long) planProgress.get("completedTasks")).intValue();
            
            totalTasks += planTotalTasks;
            completedTasks += planCompletedTasks;
            
            if (planTotalTasks > 0 && planCompletedTasks == planTotalTasks) {
                completedPlans++;
            }
        }
        
        progress.put("completedPlans", completedPlans);
        progress.put("totalTasks", totalTasks);
        progress.put("completedTasks", completedTasks);
        progress.put("overallProgress", totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0.0);
        
        return progress;
    }
    
    @Override
    public Map<String, Object> getSystemTreatmentProgress() {
        Map<String, Object> progress = new HashMap<>();
        
        progress.put("totalPlans", treatmentPlans.size());
        progress.put("totalTasks", treatmentTasks.size());
        progress.put("totalUsers", userPlanIndex.size());
        
        // 按状态统计方案
        Map<String, Long> planStatusStats = treatmentPlans.values().stream()
                .collect(Collectors.groupingBy(TreatmentPlan::getStatus, Collectors.counting()));
        progress.put("planStatusStats", planStatusStats);
        
        // 按状态统计任务
        Map<String, Long> taskStatusStats = treatmentTasks.values().stream()
                .collect(Collectors.groupingBy(TreatmentTask::getStatus, Collectors.counting()));
        progress.put("taskStatusStats", taskStatusStats);
        
        return progress;
    }
    
    @Override
    public boolean validateTreatmentPlan(TreatmentPlan plan) {
        if (plan == null) {
            return false;
        }
        
        // 验证必要字段
        if (plan.getPlanName() == null || plan.getPlanName().trim().isEmpty()) {
            return false;
        }
        
        if (plan.getPestId() == null || plan.getPestId().trim().isEmpty()) {
            return false;
        }
        
        // 验证防治方法
        if (plan.getMethods() == null || plan.getMethods().isEmpty()) {
            return false;
        }
        
        // 验证病虫害是否存在
        return pestRepository.existsById(plan.getPestId());
    }
    
    // ==================== 复杂功能 - 抛出未实现异常 ====================
    
    @Override
    public List<TreatmentPlan> getTreatmentPlanTemplates() {
        throwNotImplementedException("getTreatmentPlanTemplates");
        return null;
    }
    
    @Override
    public TreatmentPlan createPlanFromTemplate(String templateId, String pestId, String userId, Map<String, Object> parameters) {
        throwNotImplementedException("createPlanFromTemplate");
        return null;
    }
    
    @Override
    public List<TreatmentMethod> getTreatmentMethods() {
        throwNotImplementedException("getTreatmentMethods");
        return null;
    }
    
    @Override
    public List<TreatmentMethod> getRecommendedTreatmentMethods(String pestId) {
        throwNotImplementedException("getRecommendedTreatmentMethods");
        return null;
    }
    
    @Override
    public Map<String, Object> evaluateTreatmentPlan(String planId) {
        throwNotImplementedException("evaluateTreatmentPlan");
        return null;
    }
    
    @Override
    public List<Map<String, Object>> getTreatmentPlanHistory(String planId) {
        throwNotImplementedException("getTreatmentPlanHistory");
        return null;
    }
    
    @Override
    public TreatmentPlan copyTreatmentPlan(String planId, String userId) {
        throwNotImplementedException("copyTreatmentPlan");
        return null;
    }
    
    @Override
    public List<TreatmentPlan> searchTreatmentPlans(String keyword, String userId) {
        throwNotImplementedException("searchTreatmentPlans");
        return null;
    }
    
    @Override
    public Map<String, Object> getTreatmentPlanStatistics(String userId) {
        throwNotImplementedException("getTreatmentPlanStatistics");
        return null;
    }
    
    @Override
    public String exportTreatmentPlan(String planId, String format) {
        throwNotImplementedException("exportTreatmentPlan");
        return null;
    }
    
    @Override
    public List<TreatmentTask> batchCreateTreatmentTasks(String planId, List<TreatmentTask> tasks, String userId) {
        throwNotImplementedException("batchCreateTreatmentTasks");
        return null;
    }
    
    @Override
    public Map<String, Object> getTreatmentTaskStatistics(String userId) {
        throwNotImplementedException("getTreatmentTaskStatistics");
        return null;
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> getTreatmentEffectTrends(String period) {
        throwNotImplementedException("getTreatmentEffectTrends");
        return null;
    }
    
    @Override
    public List<String> getTreatmentPlanSuggestions(String pestId, String region) {
        throwNotImplementedException("getTreatmentPlanSuggestions");
        return null;
    }
    
    @Override
    public List<Map<String, Object>> getPopularTreatmentPlans(int limit) {
        throwNotImplementedException("getPopularTreatmentPlans");
        return null;
    }
    
    @Override
    public Map<String, Object> estimateTreatmentCost(String planId) {
        throwNotImplementedException("estimateTreatmentCost");
        return null;
    }
    
    @Override
    public Map<String, Object> compareTreatmentPlans(List<String> planIds) {
        throwNotImplementedException("compareTreatmentPlans");
        return null;
    }
    
    @Override
    public Map<String, Object> getTreatmentExecutionReport(String planId) {
        throwNotImplementedException("getTreatmentExecutionReport");
        return null;
    }
    
    @Override
    public boolean setTreatmentReminder(String planId, String userId, Map<String, Object> reminderConfig) {
        throwNotImplementedException("setTreatmentReminder");
        return false;
    }
    
    @Override
    public List<Map<String, Object>> getTreatmentReminders(String userId) {
        throwNotImplementedException("getTreatmentReminders");
        return null;
    }
    
    @Override
    public boolean cancelTreatmentReminder(String reminderId, String userId) {
        throwNotImplementedException("cancelTreatmentReminder");
        return false;
    }
}