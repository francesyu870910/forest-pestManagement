package com.forestpest.data.storage;

import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 防治方案和任务数据存储
 */
@Component
public class TreatmentStorage {
    
    private final Map<String, TreatmentPlan> treatmentPlans = new ConcurrentHashMap<>();
    private final Map<String, TreatmentTask> treatmentTasks = new ConcurrentHashMap<>();
    private final Map<String, List<String>> pestIdIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> planTaskIndex = new ConcurrentHashMap<>();
    
    // TreatmentPlan methods
    public void savePlan(TreatmentPlan plan) {
        treatmentPlans.put(plan.getId(), plan);
        
        // 更新病虫害ID索引
        pestIdIndex.computeIfAbsent(plan.getPestId(), k -> new ArrayList<>()).add(plan.getId());
    }
    
    public Optional<TreatmentPlan> findPlanById(String id) {
        return Optional.ofNullable(treatmentPlans.get(id));
    }
    
    public List<TreatmentPlan> findAllPlans() {
        return new ArrayList<>(treatmentPlans.values());
    }
    
    public List<TreatmentPlan> findPlansByPestId(String pestId) {
        List<String> ids = pestIdIndex.get(pestId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(treatmentPlans::get)
                .filter(plan -> plan != null)
                .collect(Collectors.toList());
    }
    
    public List<TreatmentPlan> findPlansByStatus(String status) {
        return treatmentPlans.values().stream()
                .filter(plan -> status.equals(plan.getStatus()))
                .collect(Collectors.toList());
    }
    
    public List<TreatmentPlan> findPlansByPriority(String priority) {
        return treatmentPlans.values().stream()
                .filter(plan -> priority.equals(plan.getPriority()))
                .collect(Collectors.toList());
    }
    
    public void deletePlanById(String id) {
        TreatmentPlan plan = treatmentPlans.remove(id);
        if (plan != null) {
            // 清理索引
            List<String> pestIds = pestIdIndex.get(plan.getPestId());
            if (pestIds != null) {
                pestIds.remove(id);
            }
            
            // 删除相关任务
            List<String> taskIds = planTaskIndex.get(id);
            if (taskIds != null) {
                taskIds.forEach(treatmentTasks::remove);
                planTaskIndex.remove(id);
            }
        }
    }
    
    // TreatmentTask methods
    public void saveTask(TreatmentTask task) {
        treatmentTasks.put(task.getId(), task);
        
        // 更新方案任务索引
        planTaskIndex.computeIfAbsent(task.getPlanId(), k -> new ArrayList<>()).add(task.getId());
    }
    
    public Optional<TreatmentTask> findTaskById(String id) {
        return Optional.ofNullable(treatmentTasks.get(id));
    }
    
    public List<TreatmentTask> findAllTasks() {
        return new ArrayList<>(treatmentTasks.values());
    }
    
    public List<TreatmentTask> findTasksByPlanId(String planId) {
        List<String> ids = planTaskIndex.get(planId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(treatmentTasks::get)
                .filter(task -> task != null)
                .collect(Collectors.toList());
    }
    
    public List<TreatmentTask> findTasksByStatus(String status) {
        return treatmentTasks.values().stream()
                .filter(task -> status.equals(task.getStatus()))
                .collect(Collectors.toList());
    }
    
    public List<TreatmentTask> findTasksByAssignedTo(String assignedTo) {
        return treatmentTasks.values().stream()
                .filter(task -> assignedTo.equals(task.getAssignedTo()))
                .collect(Collectors.toList());
    }
    
    public void deleteTaskById(String id) {
        TreatmentTask task = treatmentTasks.remove(id);
        if (task != null) {
            // 清理索引
            List<String> taskIds = planTaskIndex.get(task.getPlanId());
            if (taskIds != null) {
                taskIds.remove(id);
            }
        }
    }
    
    public void clear() {
        treatmentPlans.clear();
        treatmentTasks.clear();
        pestIdIndex.clear();
        planTaskIndex.clear();
    }
    
    public int planCount() {
        return treatmentPlans.size();
    }
    
    public int taskCount() {
        return treatmentTasks.size();
    }
}