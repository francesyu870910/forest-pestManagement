package com.forestpest.service;

import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;
import com.forestpest.entity.TreatmentMethod;

import java.util.List;
import java.util.Map;

/**
 * 防治方案服务接口
 */
public interface TreatmentPlanService {
    
    /**
     * 基于病虫害类型生成防治方案
     */
    TreatmentPlan generateTreatmentPlan(String pestId, String userId, Map<String, Object> parameters);
    
    /**
     * 获取防治方案详情
     */
    TreatmentPlan getTreatmentPlan(String planId);
    
    /**
     * 获取用户的防治方案列表
     */
    List<TreatmentPlan> getUserTreatmentPlans(String userId);
    
    /**
     * 获取用户的防治方案列表（分页）
     */
    List<TreatmentPlan> getUserTreatmentPlans(String userId, int page, int size);
    
    /**
     * 修改防治方案
     */
    TreatmentPlan updateTreatmentPlan(String planId, String userId, TreatmentPlan updatedPlan);
    
    /**
     * 保存自定义防治方案
     */
    TreatmentPlan saveCustomTreatmentPlan(TreatmentPlan plan, String userId);
    
    /**
     * 删除防治方案
     */
    boolean deleteTreatmentPlan(String planId, String userId);
    
    /**
     * 创建防治任务
     */
    TreatmentTask createTreatmentTask(String planId, TreatmentTask task, String userId);
    
    /**
     * 分配防治任务
     */
    TreatmentTask assignTreatmentTask(String taskId, String assigneeId, String assignerId);
    
    /**
     * 更新任务状态
     */
    TreatmentTask updateTaskStatus(String taskId, String status, String userId);
    
    /**
     * 获取防治任务列表
     */
    List<TreatmentTask> getTreatmentTasks(String planId);
    
    /**
     * 获取用户分配的任务
     */
    List<TreatmentTask> getUserAssignedTasks(String userId);
    
    /**
     * 获取用户创建的任务
     */
    List<TreatmentTask> getUserCreatedTasks(String userId);
    
    /**
     * 完成防治任务
     */
    TreatmentTask completeTreatmentTask(String taskId, String userId, Map<String, Object> completionData);
    
    /**
     * 获取防治进度统计
     */
    Map<String, Object> getTreatmentProgress(String planId);
    
    /**
     * 获取用户防治进度统计
     */
    Map<String, Object> getUserTreatmentProgress(String userId);
    
    /**
     * 获取系统防治进度统计
     */
    Map<String, Object> getSystemTreatmentProgress();
    
    /**
     * 获取防治方案模板
     */
    List<TreatmentPlan> getTreatmentPlanTemplates();
    
    /**
     * 根据模板创建防治方案
     */
    TreatmentPlan createPlanFromTemplate(String templateId, String pestId, String userId, Map<String, Object> parameters);
    
    /**
     * 获取防治方法列表
     */
    List<TreatmentMethod> getTreatmentMethods();
    
    /**
     * 根据病虫害获取推荐防治方法
     */
    List<TreatmentMethod> getRecommendedTreatmentMethods(String pestId);
    
    /**
     * 评估防治方案效果
     */
    Map<String, Object> evaluateTreatmentPlan(String planId);
    
    /**
     * 获取防治方案执行历史
     */
    List<Map<String, Object>> getTreatmentPlanHistory(String planId);
    
    /**
     * 复制防治方案
     */
    TreatmentPlan copyTreatmentPlan(String planId, String userId);
    
    /**
     * 搜索防治方案
     */
    List<TreatmentPlan> searchTreatmentPlans(String keyword, String userId);
    
    /**
     * 获取防治方案统计信息
     */
    Map<String, Object> getTreatmentPlanStatistics(String userId);
    
    /**
     * 导出防治方案
     */
    String exportTreatmentPlan(String planId, String format);
    
    /**
     * 批量创建防治任务
     */
    List<TreatmentTask> batchCreateTreatmentTasks(String planId, List<TreatmentTask> tasks, String userId);
    
    /**
     * 获取防治任务统计
     */
    Map<String, Object> getTreatmentTaskStatistics(String userId);
    
    /**
     * 获取防治效果趋势
     */
    Map<String, List<Map<String, Object>>> getTreatmentEffectTrends(String period);
    
    /**
     * 验证防治方案
     */
    boolean validateTreatmentPlan(TreatmentPlan plan);
    
    /**
     * 获取防治方案建议
     */
    List<String> getTreatmentPlanSuggestions(String pestId, String region);
    
    /**
     * 获取热门防治方案
     */
    List<Map<String, Object>> getPopularTreatmentPlans(int limit);
    
    /**
     * 获取防治成本估算
     */
    Map<String, Object> estimateTreatmentCost(String planId);
    
    /**
     * 获取防治方案对比
     */
    Map<String, Object> compareTreatmentPlans(List<String> planIds);
    
    /**
     * 获取防治方案执行报告
     */
    Map<String, Object> getTreatmentExecutionReport(String planId);
    
    /**
     * 设置防治提醒
     */
    boolean setTreatmentReminder(String planId, String userId, Map<String, Object> reminderConfig);
    
    /**
     * 获取防治提醒列表
     */
    List<Map<String, Object>> getTreatmentReminders(String userId);
    
    /**
     * 取消防治提醒
     */
    boolean cancelTreatmentReminder(String reminderId, String userId);
}