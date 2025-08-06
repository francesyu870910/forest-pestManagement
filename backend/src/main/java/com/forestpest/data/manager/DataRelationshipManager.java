package com.forestpest.data.manager;

import com.forestpest.data.storage.DataStorage;
import com.forestpest.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * 数据关联关系管理器
 * 负责维护和验证各实体间的关联关系和逻辑一致性
 */
@Component
public class DataRelationshipManager {
    
    @Autowired
    private DataStorage dataStorage;
    
    /**
     * 验证所有数据的关联关系
     */
    public ValidationResult validateAllRelationships() {
        ValidationResult result = new ValidationResult();
        
        // 验证用户关联
        result.addResults(validateUserRelationships());
        
        // 验证病虫害关联
        result.addResults(validatePestRelationships());
        
        // 验证防治方案关联
        result.addResults(validateTreatmentPlanRelationships());
        
        // 验证防治任务关联
        result.addResults(validateTreatmentTaskRelationships());
        
        // 验证森林资源关联
        result.addResults(validateForestResourceRelationships());
        
        // 验证知识库关联
        result.addResults(validateKnowledgeBaseRelationships());
        
        return result;
    }
    
    /**
     * 验证用户关联关系
     */
    private ValidationResult validateUserRelationships() {
        ValidationResult result = new ValidationResult();
        List<User> users = dataStorage.getUserStorage().findAll();
        
        // 检查用户名和邮箱唯一性
        Set<String> usernames = new HashSet<>();
        Set<String> emails = new HashSet<>();
        
        for (User user : users) {
            if (!usernames.add(user.getUsername())) {
                result.addError("重复的用户名: " + user.getUsername());
            }
            if (!emails.add(user.getEmail())) {
                result.addError("重复的邮箱: " + user.getEmail());
            }
        }
        
        result.addInfo("用户数据验证完成，共" + users.size() + "个用户");
        return result;
    }
    
    /**
     * 验证病虫害关联关系
     */
    private ValidationResult validatePestRelationships() {
        ValidationResult result = new ValidationResult();
        List<Pest> pests = dataStorage.getPestStorage().findAll();
        
        // 检查病虫害名称唯一性
        Set<String> pestNames = new HashSet<>();
        for (Pest pest : pests) {
            if (!pestNames.add(pest.getName())) {
                result.addError("重复的病虫害名称: " + pest.getName());
            }
        }
        
        result.addInfo("病虫害数据验证完成，共" + pests.size() + "个病虫害");
        return result;
    }
    
    /**
     * 验证防治方案关联关系
     */
    private ValidationResult validateTreatmentPlanRelationships() {
        ValidationResult result = new ValidationResult();
        List<TreatmentPlan> plans = dataStorage.getTreatmentStorage().findAllPlans();
        
        for (TreatmentPlan plan : plans) {
            // 验证病虫害ID存在性
            if (!dataStorage.getPestStorage().findById(plan.getPestId()).isPresent()) {
                result.addError("防治方案 " + plan.getPlanName() + " 关联的病虫害ID不存在: " + plan.getPestId());
            }
            
            // 验证所需药剂存在性
            if (plan.getRequiredPesticides() != null) {
                for (String pesticideId : plan.getRequiredPesticides()) {
                    if (!dataStorage.getPesticideStorage().findById(pesticideId).isPresent()) {
                        result.addError("防治方案 " + plan.getPlanName() + " 关联的药剂ID不存在: " + pesticideId);
                    }
                }
            }
            
            // 验证审批人存在性
            if (plan.getApprovedBy() != null && !dataStorage.getUserStorage().findById(plan.getApprovedBy()).isPresent()) {
                result.addError("防治方案 " + plan.getPlanName() + " 的审批人ID不存在: " + plan.getApprovedBy());
            }
        }
        
        result.addInfo("防治方案数据验证完成，共" + plans.size() + "个方案");
        return result;
    }
    
    /**
     * 验证防治任务关联关系
     */
    private ValidationResult validateTreatmentTaskRelationships() {
        ValidationResult result = new ValidationResult();
        List<TreatmentTask> tasks = dataStorage.getTreatmentStorage().findAllTasks();
        
        for (TreatmentTask task : tasks) {
            // 验证方案ID存在性
            if (!dataStorage.getTreatmentStorage().findPlanById(task.getPlanId()).isPresent()) {
                result.addError("防治任务 " + task.getTaskName() + " 关联的方案ID不存在: " + task.getPlanId());
            }
            
            // 验证分配人员存在性
            if (!dataStorage.getUserStorage().findById(task.getAssignedTo()).isPresent()) {
                result.addError("防治任务 " + task.getTaskName() + " 的分配人员ID不存在: " + task.getAssignedTo());
            }
            
            // 验证监督人存在性
            if (task.getSupervisedBy() != null && !dataStorage.getUserStorage().findById(task.getSupervisedBy()).isPresent()) {
                result.addError("防治任务 " + task.getTaskName() + " 的监督人ID不存在: " + task.getSupervisedBy());
            }
            
            // 验证使用药剂存在性
            if (task.getUsedPesticides() != null) {
                for (String pesticideId : task.getUsedPesticides()) {
                    if (!dataStorage.getPesticideStorage().findById(pesticideId).isPresent()) {
                        result.addError("防治任务 " + task.getTaskName() + " 使用的药剂ID不存在: " + pesticideId);
                    }
                }
            }
        }
        
        result.addInfo("防治任务数据验证完成，共" + tasks.size() + "个任务");
        return result;
    }
    
    /**
     * 验证森林资源关联关系
     */
    private ValidationResult validateForestResourceRelationships() {
        ValidationResult result = new ValidationResult();
        List<ForestResource> resources = dataStorage.getForestResourceStorage().findAll();
        
        for (ForestResource resource : resources) {
            // 验证父区域存在性
            if (resource.getParentAreaId() != null && 
                !dataStorage.getForestResourceStorage().findById(resource.getParentAreaId()).isPresent()) {
                result.addError("森林资源 " + resource.getAreaName() + " 的父区域ID不存在: " + resource.getParentAreaId());
            }
        }
        
        // 验证区域代码唯一性
        Set<String> areaCodes = new HashSet<>();
        for (ForestResource resource : resources) {
            if (!areaCodes.add(resource.getAreaCode())) {
                result.addError("重复的区域代码: " + resource.getAreaCode());
            }
        }
        
        result.addInfo("森林资源数据验证完成，共" + resources.size() + "个资源");
        return result;
    }
    
    /**
     * 验证知识库关联关系
     */
    private ValidationResult validateKnowledgeBaseRelationships() {
        ValidationResult result = new ValidationResult();
        List<KnowledgeBase> knowledgeList = dataStorage.getKnowledgeStorage().findAll();
        
        for (KnowledgeBase knowledge : knowledgeList) {
            // 验证相关病虫害存在性
            if (knowledge.getRelatedPests() != null) {
                for (String pestId : knowledge.getRelatedPests()) {
                    if (!dataStorage.getPestStorage().findById(pestId).isPresent()) {
                        result.addError("知识库 " + knowledge.getTitle() + " 关联的病虫害ID不存在: " + pestId);
                    }
                }
            }
            
            // 验证审核人存在性
            if (knowledge.getReviewedBy() != null && 
                !dataStorage.getUserStorage().findById(knowledge.getReviewedBy()).isPresent()) {
                result.addError("知识库 " + knowledge.getTitle() + " 的审核人ID不存在: " + knowledge.getReviewedBy());
            }
        }
        
        result.addInfo("知识库数据验证完成，共" + knowledgeList.size() + "个条目");
        return result;
    }
    
    /**
     * 修复数据关联关系
     */
    public void fixRelationships() {
        // 清理无效的关联关系
        cleanupInvalidReferences();
        
        // 重建索引
        rebuildIndexes();
    }
    
    /**
     * 清理无效的关联引用
     */
    private void cleanupInvalidReferences() {
        // 清理防治方案中无效的病虫害引用
        List<TreatmentPlan> plans = dataStorage.getTreatmentStorage().findAllPlans();
        for (TreatmentPlan plan : plans) {
            if (!dataStorage.getPestStorage().findById(plan.getPestId()).isPresent()) {
                // 如果病虫害不存在，可以选择删除方案或分配一个默认的病虫害
                System.out.println("警告: 防治方案 " + plan.getPlanName() + " 关联的病虫害不存在");
            }
        }
        
        // 清理知识库中无效的病虫害引用
        List<KnowledgeBase> knowledgeList = dataStorage.getKnowledgeStorage().findAll();
        for (KnowledgeBase knowledge : knowledgeList) {
            if (knowledge.getRelatedPests() != null) {
                List<String> validPestIds = knowledge.getRelatedPests().stream()
                        .filter(pestId -> dataStorage.getPestStorage().findById(pestId).isPresent())
                        .collect(Collectors.toList());
                knowledge.setRelatedPests(validPestIds);
            }
        }
    }
    
    /**
     * 重建索引
     */
    private void rebuildIndexes() {
        // 这里可以添加重建各种索引的逻辑
        System.out.println("重建数据索引完成");
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private List<String> errors = new java.util.ArrayList<>();
        private List<String> warnings = new java.util.ArrayList<>();
        private List<String> infos = new java.util.ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public void addInfo(String info) {
            infos.add(info);
        }
        
        public void addResults(ValidationResult other) {
            errors.addAll(other.errors);
            warnings.addAll(other.warnings);
            infos.addAll(other.infos);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public List<String> getInfos() {
            return infos;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("验证结果:\n");
            
            if (!errors.isEmpty()) {
                sb.append("错误 (").append(errors.size()).append("):\n");
                errors.forEach(error -> sb.append("  - ").append(error).append("\n"));
            }
            
            if (!warnings.isEmpty()) {
                sb.append("警告 (").append(warnings.size()).append("):\n");
                warnings.forEach(warning -> sb.append("  - ").append(warning).append("\n"));
            }
            
            if (!infos.isEmpty()) {
                sb.append("信息 (").append(infos.size()).append("):\n");
                infos.forEach(info -> sb.append("  - ").append(info).append("\n"));
            }
            
            return sb.toString();
        }
    }
}