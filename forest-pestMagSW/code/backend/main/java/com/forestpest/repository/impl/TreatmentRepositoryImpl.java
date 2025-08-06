package com.forestpest.repository.impl;

import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;
import com.forestpest.repository.TreatmentRepository;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 防治方案和任务数据访问实现类
 */
@Repository
public class TreatmentRepositoryImpl implements TreatmentRepository {
    
    @Autowired
    private DataStorage dataStorage;
    
    // ========== TreatmentPlan 相关方法实现 ==========
    
    @Override
    public TreatmentPlan savePlan(TreatmentPlan plan) {
        if (plan.getId() == null) {
            plan.setId(dataStorage.generateId());
        }
        plan.setUpdatedTime(LocalDateTime.now());
        dataStorage.getTreatmentStorage().savePlan(plan);
        return plan;
    }
    
    @Override
    public Optional<TreatmentPlan> findPlanById(String id) {
        return dataStorage.getTreatmentStorage().findPlanById(id);
    }
    
    @Override
    public List<TreatmentPlan> findAllPlans() {
        return dataStorage.getTreatmentStorage().findAllPlans();
    }
    
    @Override
    public List<TreatmentPlan> findPlansByPestId(String pestId) {
        return dataStorage.getTreatmentStorage().findPlansByPestId(pestId);
    }
    
    @Override
    public List<TreatmentPlan> findPlansByStatus(String status) {
        return dataStorage.getTreatmentStorage().findPlansByStatus(status);
    }
    
    @Override
    public List<TreatmentPlan> findPlansByPriority(String priority) {
        return dataStorage.getTreatmentStorage().findPlansByPriority(priority);
    }
    
    @Override
    public List<TreatmentPlan> findPlansByApprovalStatus(String approvalStatus) {
        return findAllPlans().stream()
                .filter(plan -> approvalStatus.equals(plan.getApprovalStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentPlan> findPlansByTargetArea(String targetArea) {
        return findAllPlans().stream()
                .filter(plan -> targetArea.equals(plan.getTargetArea()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentPlan> findPlansByCreatedBy(String createdBy) {
        return findAllPlans().stream()
                .filter(plan -> createdBy.equals(plan.getCreatedBy()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deletePlanById(String id) {
        dataStorage.getTreatmentStorage().deletePlanById(id);
    }
    
    @Override
    public long countPlans() {
        return dataStorage.getTreatmentStorage().planCount();
    }
    
    // ========== TreatmentTask 相关方法实现 ==========
    
    @Override
    public TreatmentTask saveTask(TreatmentTask task) {
        if (task.getId() == null) {
            task.setId(dataStorage.generateId());
        }
        task.setUpdatedTime(LocalDateTime.now());
        dataStorage.getTreatmentStorage().saveTask(task);
        return task;
    }
    
    @Override
    public Optional<TreatmentTask> findTaskById(String id) {
        return dataStorage.getTreatmentStorage().findTaskById(id);
    }
    
    @Override
    public List<TreatmentTask> findAllTasks() {
        return dataStorage.getTreatmentStorage().findAllTasks();
    }
    
    @Override
    public List<TreatmentTask> findTasksByPlanId(String planId) {
        return dataStorage.getTreatmentStorage().findTasksByPlanId(planId);
    }
    
    @Override
    public List<TreatmentTask> findTasksByAssignedTo(String assignedTo) {
        return dataStorage.getTreatmentStorage().findTasksByAssignedTo(assignedTo);
    }
    
    @Override
    public List<TreatmentTask> findTasksByStatus(String status) {
        return dataStorage.getTreatmentStorage().findTasksByStatus(status);
    }
    
    @Override
    public List<TreatmentTask> findTasksBySupervisedBy(String supervisedBy) {
        return findAllTasks().stream()
                .filter(task -> supervisedBy.equals(task.getSupervisedBy()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentTask> findTasksByScheduledTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return findAllTasks().stream()
                .filter(task -> task.getScheduledTime() != null &&
                               !task.getScheduledTime().isBefore(startTime) &&
                               !task.getScheduledTime().isAfter(endTime))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentTask> findTasksByTargetArea(String targetArea) {
        return findAllTasks().stream()
                .filter(task -> targetArea.equals(task.getTargetArea()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentTask> findTasksByPriority(String priority) {
        return findAllTasks().stream()
                .filter(task -> priority.equals(task.getPriority()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteTaskById(String id) {
        dataStorage.getTreatmentStorage().deleteTaskById(id);
    }
    
    @Override
    public long countTasks() {
        return dataStorage.getTreatmentStorage().taskCount();
    }
    
    // ========== 批量操作方法实现 ==========
    
    @Override
    public List<TreatmentPlan> saveAllPlans(Iterable<TreatmentPlan> plans) {
        List<TreatmentPlan> savedPlans = new java.util.ArrayList<>();
        for (TreatmentPlan plan : plans) {
            savedPlans.add(savePlan(plan));
        }
        return savedPlans;
    }
    
    @Override
    public List<TreatmentTask> saveAllTasks(Iterable<TreatmentTask> tasks) {
        List<TreatmentTask> savedTasks = new java.util.ArrayList<>();
        for (TreatmentTask task : tasks) {
            savedTasks.add(saveTask(task));
        }
        return savedTasks;
    }
    
    @Override
    public void deleteAllPlans() {
        findAllPlans().forEach(plan -> deletePlanById(plan.getId()));
    }
    
    @Override
    public void deleteAllTasks() {
        findAllTasks().forEach(task -> deleteTaskById(task.getId()));
    }
    
    @Override
    public void deleteAll() {
        dataStorage.getTreatmentStorage().clear();
    }
    
    // ========== 复合查询方法实现 ==========
    
    @Override
    public List<TreatmentTask> findPendingTasksByUser(String userId) {
        return findTasksByAssignedTo(userId).stream()
                .filter(task -> "待执行".equals(task.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentTask> findInProgressTasksByUser(String userId) {
        return findTasksByAssignedTo(userId).stream()
                .filter(task -> "执行中".equals(task.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentTask> findCompletedTasksByUser(String userId) {
        return findTasksByAssignedTo(userId).stream()
                .filter(task -> "已完成".equals(task.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TreatmentPlan> findPlansNeedingApproval() {
        return findPlansByApprovalStatus("待审批");
    }
    
    @Override
    public List<TreatmentPlan> findApprovedPlansNotStarted() {
        return findAllPlans().stream()
                .filter(plan -> "已批准".equals(plan.getApprovalStatus()) && 
                               ("草稿".equals(plan.getStatus()) || "待审批".equals(plan.getStatus())))
                .collect(Collectors.toList());
    }
}