package com.forestpest.repository;

import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 防治方案和任务数据访问接口
 */
public interface TreatmentRepository {
    
    // ========== TreatmentPlan 相关方法 ==========
    
    /**
     * 保存防治方案
     */
    TreatmentPlan savePlan(TreatmentPlan plan);
    
    /**
     * 根据ID查找防治方案
     */
    Optional<TreatmentPlan> findPlanById(String id);
    
    /**
     * 查找所有防治方案
     */
    List<TreatmentPlan> findAllPlans();
    
    /**
     * 根据病虫害ID查找防治方案
     */
    List<TreatmentPlan> findPlansByPestId(String pestId);
    
    /**
     * 根据状态查找防治方案
     */
    List<TreatmentPlan> findPlansByStatus(String status);
    
    /**
     * 根据优先级查找防治方案
     */
    List<TreatmentPlan> findPlansByPriority(String priority);
    
    /**
     * 根据审批状态查找防治方案
     */
    List<TreatmentPlan> findPlansByApprovalStatus(String approvalStatus);
    
    /**
     * 根据目标区域查找防治方案
     */
    List<TreatmentPlan> findPlansByTargetArea(String targetArea);
    
    /**
     * 根据创建者查找防治方案
     */
    List<TreatmentPlan> findPlansByCreatedBy(String createdBy);
    
    /**
     * 删除防治方案
     */
    void deletePlanById(String id);
    
    /**
     * 统计防治方案数量
     */
    long countPlans();
    
    // ========== TreatmentTask 相关方法 ==========
    
    /**
     * 保存防治任务
     */
    TreatmentTask saveTask(TreatmentTask task);
    
    /**
     * 根据ID查找防治任务
     */
    Optional<TreatmentTask> findTaskById(String id);
    
    /**
     * 查找所有防治任务
     */
    List<TreatmentTask> findAllTasks();
    
    /**
     * 根据方案ID查找防治任务
     */
    List<TreatmentTask> findTasksByPlanId(String planId);
    
    /**
     * 根据分配人查找防治任务
     */
    List<TreatmentTask> findTasksByAssignedTo(String assignedTo);
    
    /**
     * 根据状态查找防治任务
     */
    List<TreatmentTask> findTasksByStatus(String status);
    
    /**
     * 根据监督人查找防治任务
     */
    List<TreatmentTask> findTasksBySupervisedBy(String supervisedBy);
    
    /**
     * 根据计划时间范围查找防治任务
     */
    List<TreatmentTask> findTasksByScheduledTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据目标区域查找防治任务
     */
    List<TreatmentTask> findTasksByTargetArea(String targetArea);
    
    /**
     * 根据优先级查找防治任务
     */
    List<TreatmentTask> findTasksByPriority(String priority);
    
    /**
     * 删除防治任务
     */
    void deleteTaskById(String id);
    
    /**
     * 统计防治任务数量
     */
    long countTasks();
    
    // ========== 批量操作方法 ==========
    
    /**
     * 批量保存防治方案
     */
    List<TreatmentPlan> saveAllPlans(Iterable<TreatmentPlan> plans);
    
    /**
     * 批量保存防治任务
     */
    List<TreatmentTask> saveAllTasks(Iterable<TreatmentTask> tasks);
    
    /**
     * 清空所有防治方案
     */
    void deleteAllPlans();
    
    /**
     * 清空所有防治任务
     */
    void deleteAllTasks();
    
    /**
     * 清空所有数据
     */
    void deleteAll();
    
    // ========== 复合查询方法 ==========
    
    /**
     * 获取用户的待执行任务
     */
    List<TreatmentTask> findPendingTasksByUser(String userId);
    
    /**
     * 获取用户的进行中任务
     */
    List<TreatmentTask> findInProgressTasksByUser(String userId);
    
    /**
     * 获取用户的已完成任务
     */
    List<TreatmentTask> findCompletedTasksByUser(String userId);
    
    /**
     * 获取需要审批的方案
     */
    List<TreatmentPlan> findPlansNeedingApproval();
    
    /**
     * 获取已批准但未执行的方案
     */
    List<TreatmentPlan> findApprovedPlansNotStarted();
}