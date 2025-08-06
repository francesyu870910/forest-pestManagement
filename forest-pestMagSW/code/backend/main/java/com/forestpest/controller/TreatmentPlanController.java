package com.forestpest.controller;

import com.forestpest.common.ApiResponse;
import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;
import com.forestpest.entity.TreatmentMethod;
import com.forestpest.service.TreatmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * 防治方案控制
 */
@RestController
@RequestMapping("/api/treatment")
@CrossOrigin(origins = "*")
public class TreatmentPlanController {
    
    @Autowired
    private TreatmentPlanService treatmentPlanService;
    
    /**
     * 生成防治方案
     */
    @PostMapping("/plan/generate")
    public ApiResponse<TreatmentPlan> generateTreatmentPlan(
            @RequestBody @Valid GeneratePlanRequest request) {
        
        TreatmentPlan plan = treatmentPlanService.generateTreatmentPlan(
            request.getPestId(), request.getUserId(), request.getParameters());
        return ApiResponse.success(plan);
    }
    
    /**
     * 获取防治方案详情
     */
    @GetMapping("/plan/{planId}")
    public ApiResponse<TreatmentPlan> getTreatmentPlan(
            @PathVariable @NotBlank String planId) {
        
        TreatmentPlan plan = treatmentPlanService.getTreatmentPlan(planId);
        if (plan == null) {
            return ApiResponse.error("防治方案不存在");
        }
        return ApiResponse.success(plan);
    }
    
    /**
     * 获取用户的防治方案列表
     */
    @GetMapping("/plan/user/{userId}")
    public ApiResponse<List<TreatmentPlan>> getUserTreatmentPlans(
            @PathVariable @NotBlank String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<TreatmentPlan> plans;
        if (page > 0 || size != 20) {
            plans = treatmentPlanService.getUserTreatmentPlans(userId, page, size);
        } else {
            plans = treatmentPlanService.getUserTreatmentPlans(userId);
        }
        return ApiResponse.success(plans);
    }
    
    /**
     * 修改防治方案
     */
    @PutMapping("/plan/{planId}")
    public ApiResponse<TreatmentPlan> updateTreatmentPlan(
            @PathVariable @NotBlank String planId,
            @RequestParam @NotBlank String userId,
            @RequestBody @Valid TreatmentPlan updatedPlan) {
        
        TreatmentPlan plan = treatmentPlanService.updateTreatmentPlan(planId, userId, updatedPlan);
        return ApiResponse.success(plan);
    }
    
    /**
     * 保存自定义防治方案
     */
    @PostMapping("/plan/custom")
    public ApiResponse<TreatmentPlan> saveCustomTreatmentPlan(
            @RequestBody @Valid CustomPlanRequest request) {
        
        TreatmentPlan plan = treatmentPlanService.saveCustomTreatmentPlan(request.getPlan(), request.getUserId());
        return ApiResponse.success(plan);
    }
    
    /**
     * 删除防治方案
     */
    @DeleteMapping("/plan/{planId}")
    public ApiResponse<Boolean> deleteTreatmentPlan(
            @PathVariable @NotBlank String planId,
            @RequestParam @NotBlank String userId) {
        
        boolean deleted = treatmentPlanService.deleteTreatmentPlan(planId, userId);
        if (!deleted) {
            return ApiResponse.error("删除失败，方案不存在或无权限");
        }
        return ApiResponse.success(true);
    }
    
    /**
     * 创建防治任务
     */
    @PostMapping("/task")
    public ApiResponse<TreatmentTask> createTreatmentTask(
            @RequestBody @Valid CreateTaskRequest request) {
        
        TreatmentTask task = treatmentPlanService.createTreatmentTask(
            request.getPlanId(), request.getTask(), request.getUserId());
        return ApiResponse.success(task);
    }
    
    /**
     * 分配防治任务
     */
    @PutMapping("/task/{taskId}/assign")
    public ApiResponse<TreatmentTask> assignTreatmentTask(
            @PathVariable @NotBlank String taskId,
            @RequestBody @Valid AssignTaskRequest request) {
        
        TreatmentTask task = treatmentPlanService.assignTreatmentTask(
            taskId, request.getAssigneeId(), request.getAssignerId());
        return ApiResponse.success(task);
    }
    
    /**
     * 更新任务状态
     */
    @PutMapping("/task/{taskId}/status")
    public ApiResponse<TreatmentTask> updateTaskStatus(
            @PathVariable @NotBlank String taskId,
            @RequestBody @Valid UpdateTaskStatusRequest request) {
        
        TreatmentTask task = treatmentPlanService.updateTaskStatus(
            taskId, request.getStatus(), request.getUserId());
        return ApiResponse.success(task);
    }
    
    /**
     * 获取防治任务列表
     */
    @GetMapping("/task/plan/{planId}")
    public ApiResponse<List<TreatmentTask>> getTreatmentTasks(
            @PathVariable @NotBlank String planId) {
        
        List<TreatmentTask> tasks = treatmentPlanService.getTreatmentTasks(planId);
        return ApiResponse.success(tasks);
    }
    
    /**
     * 获取用户分配的任务
     */
    @GetMapping("/task/assigned/{userId}")
    public ApiResponse<List<TreatmentTask>> getUserAssignedTasks(
            @PathVariable @NotBlank String userId) {
        
        List<TreatmentTask> tasks = treatmentPlanService.getUserAssignedTasks(userId);
        return ApiResponse.success(tasks);
    }
    
    /**
     * 获取用户创建的任务
     */
    @GetMapping("/task/created/{userId}")
    public ApiResponse<List<TreatmentTask>> getUserCreatedTasks(
            @PathVariable @NotBlank String userId) {
        
        List<TreatmentTask> tasks = treatmentPlanService.getUserCreatedTasks(userId);
        return ApiResponse.success(tasks);
    }
    
    /**
     * 完成防治任务
     */
    @PutMapping("/task/{taskId}/complete")
    public ApiResponse<TreatmentTask> completeTreatmentTask(
            @PathVariable @NotBlank String taskId,
            @RequestBody @Valid CompleteTaskRequest request) {
        
        TreatmentTask task = treatmentPlanService.completeTreatmentTask(
            taskId, request.getUserId(), request.getCompletionData());
        return ApiResponse.success(task);
    }
    
    /**
     * 获取防治进度统计
     */
    @GetMapping("/progress/plan/{planId}")
    public ApiResponse<Map<String, Object>> getTreatmentProgress(
            @PathVariable @NotBlank String planId) {
        
        Map<String, Object> progress = treatmentPlanService.getTreatmentProgress(planId);
        return ApiResponse.success(progress);
    }
    
    /**
     * 获取用户防治进度统计
     */
    @GetMapping("/progress/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserTreatmentProgress(
            @PathVariable @NotBlank String userId) {
        
        Map<String, Object> progress = treatmentPlanService.getUserTreatmentProgress(userId);
        return ApiResponse.success(progress);
    }
    
    /**
     * 获取系统防治进度统计
     */
    @GetMapping("/progress/system")
    public ApiResponse<Map<String, Object>> getSystemTreatmentProgress() {
        Map<String, Object> progress = treatmentPlanService.getSystemTreatmentProgress();
        return ApiResponse.success(progress);
    }
    
    /**
     * 获取防治方案模板
     */
    @GetMapping("/template")
    public ApiResponse<List<TreatmentPlan>> getTreatmentPlanTemplates() {
        List<TreatmentPlan> templates = treatmentPlanService.getTreatmentPlanTemplates();
        return ApiResponse.success(templates);
    }
    
    /**
     * 根据模板创建防治方案
     */
    @PostMapping("/plan/from-template")
    public ApiResponse<TreatmentPlan> createPlanFromTemplate(
            @RequestBody @Valid CreateFromTemplateRequest request) {
        
        TreatmentPlan plan = treatmentPlanService.createPlanFromTemplate(
            request.getTemplateId(), request.getPestId(), 
            request.getUserId(), request.getParameters());
        return ApiResponse.success(plan);
    }
    
    /**
     * 获取防治方法列表
     */
    @GetMapping("/method")
    public ApiResponse<List<TreatmentMethod>> getTreatmentMethods() {
        List<TreatmentMethod> methods = treatmentPlanService.getTreatmentMethods();
        return ApiResponse.success(methods);
    }
    
    /**
     * 根据病虫害获取推荐防治方法
     */
    @GetMapping("/method/recommended")
    public ApiResponse<List<TreatmentMethod>> getRecommendedTreatmentMethods(
            @RequestParam @NotBlank String pestId) {
        
        List<TreatmentMethod> methods = treatmentPlanService.getRecommendedTreatmentMethods(pestId);
        return ApiResponse.success(methods);
    }
    
    /**
     * 评估防治方案效果
     */
    @GetMapping("/plan/{planId}/evaluation")
    public ApiResponse<Map<String, Object>> evaluateTreatmentPlan(
            @PathVariable @NotBlank String planId) {
        
        Map<String, Object> evaluation = treatmentPlanService.evaluateTreatmentPlan(planId);
        return ApiResponse.success(evaluation);
    }
    
    /**
     * 获取防治方案执行历史
     */
    @GetMapping("/plan/{planId}/history")
    public ApiResponse<List<Map<String, Object>>> getTreatmentPlanHistory(
            @PathVariable @NotBlank String planId) {
        
        List<Map<String, Object>> history = treatmentPlanService.getTreatmentPlanHistory(planId);
        return ApiResponse.success(history);
    }
    
    /**
     * 复制防治方案
     */
    @PostMapping("/plan/{planId}/copy")
    public ApiResponse<TreatmentPlan> copyTreatmentPlan(
            @PathVariable @NotBlank String planId,
            @RequestParam @NotBlank String userId) {
        
        TreatmentPlan copiedPlan = treatmentPlanService.copyTreatmentPlan(planId, userId);
        return ApiResponse.success(copiedPlan);
    }
    
    /**
     * 搜索防治方案
     */
    @GetMapping("/plan/search")
    public ApiResponse<List<TreatmentPlan>> searchTreatmentPlans(
            @RequestParam @NotBlank String keyword,
            @RequestParam @NotBlank String userId) {
        
        List<TreatmentPlan> plans = treatmentPlanService.searchTreatmentPlans(keyword, userId);
        return ApiResponse.success(plans);
    }
    
    /**
     * 获取防治方案统计信息
     */
    @GetMapping("/statistics/plan/{userId}")
    public ApiResponse<Map<String, Object>> getTreatmentPlanStatistics(
            @PathVariable @NotBlank String userId) {
        
        Map<String, Object> statistics = treatmentPlanService.getTreatmentPlanStatistics(userId);
        return ApiResponse.success(statistics);
    }
    
    /**
     * 导出防治方案
     */
    @GetMapping("/plan/{planId}/export")
    public ApiResponse<String> exportTreatmentPlan(
            @PathVariable @NotBlank String planId,
            @RequestParam(defaultValue = "json") String format) {
        
        String exportData = treatmentPlanService.exportTreatmentPlan(planId, format);
        return ApiResponse.success(exportData);
    }
    
    /**
     * 批量创建防治任务
     */
    @PostMapping("/task/batch")
    public ApiResponse<List<TreatmentTask>> batchCreateTreatmentTasks(
            @RequestBody @Valid BatchCreateTasksRequest request) {
        
        List<TreatmentTask> tasks = treatmentPlanService.batchCreateTreatmentTasks(
            request.getPlanId(), request.getTasks(), request.getUserId());
        return ApiResponse.success(tasks);
    }
    
    /**
     * 获取防治任务统计
     */
    @GetMapping("/statistics/task/{userId}")
    public ApiResponse<Map<String, Object>> getTreatmentTaskStatistics(
            @PathVariable @NotBlank String userId) {
        
        Map<String, Object> statistics = treatmentPlanService.getTreatmentTaskStatistics(userId);
        return ApiResponse.success(statistics);
    }
    
    /**
     * 获取防治效果趋势
     */
    @GetMapping("/trends")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getTreatmentEffectTrends(
            @RequestParam(defaultValue = "monthly") String period) {
        
        Map<String, List<Map<String, Object>>> trends = treatmentPlanService.getTreatmentEffectTrends(period);
        return ApiResponse.success(trends);
    }
    
    /**
     * 获取防治方案建议
     */
    @GetMapping("/suggestions")
    public ApiResponse<List<String>> getTreatmentPlanSuggestions(
            @RequestParam @NotBlank String pestId,
            @RequestParam(required = false) String region) {
        
        List<String> suggestions = treatmentPlanService.getTreatmentPlanSuggestions(pestId, region);
        return ApiResponse.success(suggestions);
    }
    
    /**
     * 获取热门防治方案
     */
    @GetMapping("/popular")
    public ApiResponse<List<Map<String, Object>>> getPopularTreatmentPlans(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> popular = treatmentPlanService.getPopularTreatmentPlans(limit);
        return ApiResponse.success(popular);
    }
    
    /**
     * 获取防治成本估算
     */
    @GetMapping("/plan/{planId}/cost-estimate")
    public ApiResponse<Map<String, Object>> estimateTreatmentCost(
            @PathVariable @NotBlank String planId) {
        
        Map<String, Object> costEstimate = treatmentPlanService.estimateTreatmentCost(planId);
        return ApiResponse.success(costEstimate);
    }
    
    /**
     * 获取防治方案对比
     */
    @PostMapping("/plan/compare")
    public ApiResponse<Map<String, Object>> compareTreatmentPlans(
            @RequestBody @Valid ComparePlansRequest request) {
        
        Map<String, Object> comparison = treatmentPlanService.compareTreatmentPlans(request.getPlanIds());
        return ApiResponse.success(comparison);
    }
    
    /**
     * 获取防治方案执行报告
     */
    @GetMapping("/plan/{planId}/report")
    public ApiResponse<Map<String, Object>> getTreatmentExecutionReport(
            @PathVariable @NotBlank String planId) {
        
        Map<String, Object> report = treatmentPlanService.getTreatmentExecutionReport(planId);
        return ApiResponse.success(report);
    }
    
    /**
     * 设置防治提醒
     */
    @PostMapping("/reminder")
    public ApiResponse<Boolean> setTreatmentReminder(
            @RequestBody @Valid SetReminderRequest request) {
        
        boolean success = treatmentPlanService.setTreatmentReminder(
            request.getPlanId(), request.getUserId(), request.getReminderConfig());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取防治提醒列表
     */
    @GetMapping("/reminder/{userId}")
    public ApiResponse<List<Map<String, Object>>> getTreatmentReminders(
            @PathVariable @NotBlank String userId) {
        
        List<Map<String, Object>> reminders = treatmentPlanService.getTreatmentReminders(userId);
        return ApiResponse.success(reminders);
    }
    
    /**
     * 取消防治提醒
     */
    @DeleteMapping("/reminder/{reminderId}")
    public ApiResponse<Boolean> cancelTreatmentReminder(
            @PathVariable @NotBlank String reminderId,
            @RequestParam @NotBlank String userId) {
        
        boolean success = treatmentPlanService.cancelTreatmentReminder(reminderId, userId);
        return ApiResponse.success(success);
    }
    
    // ========== 请求对象类 ==========
    
    /**
     * 生成方案请求
     */
    public static class GeneratePlanRequest {
        @NotBlank(message = "病虫害ID不能为空")
        private String pestId;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private Map<String, Object> parameters;
        
        // Getters and Setters
        public String getPestId() {
            return pestId;
        }
        
        public void setPestId(String pestId) {
            this.pestId = pestId;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
        
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
    
    /**
     * 自定义方案请求
     */
    public static class CustomPlanRequest {
        @Valid
        private TreatmentPlan plan;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public TreatmentPlan getPlan() {
            return plan;
        }
        
        public void setPlan(TreatmentPlan plan) {
            this.plan = plan;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 创建任务请求
     */
    public static class CreateTaskRequest {
        @NotBlank(message = "方案ID不能为空")
        private String planId;
        
        @Valid
        private TreatmentTask task;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public String getPlanId() {
            return planId;
        }
        
        public void setPlanId(String planId) {
            this.planId = planId;
        }
        
        public TreatmentTask getTask() {
            return task;
        }
        
        public void setTask(TreatmentTask task) {
            this.task = task;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 分配任务请求
     */
    public static class AssignTaskRequest {
        @NotBlank(message = "被分配人ID不能为空")
        private String assigneeId;
        
        @NotBlank(message = "分配人ID不能为空")
        private String assignerId;
        
        // Getters and Setters
        public String getAssigneeId() {
            return assigneeId;
        }
        
        public void setAssigneeId(String assigneeId) {
            this.assigneeId = assigneeId;
        }
        
        public String getAssignerId() {
            return assignerId;
        }
        
        public void setAssignerId(String assignerId) {
            this.assignerId = assignerId;
        }
    }
    
    /**
     * 更新任务状态请求
     */
    public static class UpdateTaskStatusRequest {
        @NotBlank(message = "状态不能为空")
        private String status;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 完成任务请求
     */
    public static class CompleteTaskRequest {
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private Map<String, Object> completionData;
        
        // Getters and Setters
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public Map<String, Object> getCompletionData() {
            return completionData;
        }
        
        public void setCompletionData(Map<String, Object> completionData) {
            this.completionData = completionData;
        }
    }
    
    /**
     * 从模板创建请求
     */
    public static class CreateFromTemplateRequest {
        @NotBlank(message = "模板ID不能为空")
        private String templateId;
        
        @NotBlank(message = "病虫害ID不能为空")
        private String pestId;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private Map<String, Object> parameters;
        
        // Getters and Setters
        public String getTemplateId() {
            return templateId;
        }
        
        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
        
        public String getPestId() {
            return pestId;
        }
        
        public void setPestId(String pestId) {
            this.pestId = pestId;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
        
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
    
    /**
     * 批量创建任务请求
     */
    public static class BatchCreateTasksRequest {
        @NotBlank(message = "方案ID不能为空")
        private String planId;
        
        @NotEmpty(message = "任务列表不能为空")
        private List<TreatmentTask> tasks;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public String getPlanId() {
            return planId;
        }
        
        public void setPlanId(String planId) {
            this.planId = planId;
        }
        
        public List<TreatmentTask> getTasks() {
            return tasks;
        }
        
        public void setTasks(List<TreatmentTask> tasks) {
            this.tasks = tasks;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 对比方案请求
     */
    public static class ComparePlansRequest {
        @NotEmpty(message = "方案ID列表不能为空")
        private List<String> planIds;
        
        // Getters and Setters
        public List<String> getPlanIds() {
            return planIds;
        }
        
        public void setPlanIds(List<String> planIds) {
            this.planIds = planIds;
        }
    }
    
    /**
     * 设置提醒请求
     */
    public static class SetReminderRequest {
        @NotBlank(message = "方案ID不能为空")
        private String planId;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private Map<String, Object> reminderConfig;
        
        // Getters and Setters
        public String getPlanId() {
            return planId;
        }
        
        public void setPlanId(String planId) {
            this.planId = planId;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public Map<String, Object> getReminderConfig() {
            return reminderConfig;
        }
        
        public void setReminderConfig(Map<String, Object> reminderConfig) {
            this.reminderConfig = reminderConfig;
        }
    }
}
