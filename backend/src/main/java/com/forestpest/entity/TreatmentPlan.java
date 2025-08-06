package com.forestpest.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 防治方案实体类
 */
public class TreatmentPlan extends BaseEntity {
    
    @NotBlank(message = "病虫害ID不能为空")
    private String pestId;
    
    @NotBlank(message = "方案名称不能为空")
    @Size(max = 200, message = "方案名称长度不能超过200个字符")
    private String planName;
    
    @Size(max = 1000, message = "方案描述长度不能超过1000个字符")
    private String description;
    
    private List<TreatmentMethod> methods;
    
    private List<String> requiredPesticides;
    
    private List<String> requiredEquipment;
    
    @Size(max = 500, message = "时间安排长度不能超过500个字符")
    private String timeSchedule;
    
    private String targetArea;
    
    private String priority;
    
    private String status;
    
    private Double estimatedCost;
    
    private Integer estimatedDuration;
    
    private String approvalStatus;
    
    private String approvedBy;
    
    private String notes;

    public TreatmentPlan() {
        super();
        this.status = "DRAFT";
        this.approvalStatus = "PENDING";
        this.priority = "MEDIUM";
    }

    public TreatmentPlan(String pestId, String planName, String description) {
        super();
        this.pestId = pestId;
        this.planName = planName;
        this.description = description;
        this.status = "DRAFT";
        this.approvalStatus = "PENDING";
        this.priority = "MEDIUM";
    }

    // Getters and Setters
    public String getPestId() {
        return pestId;
    }

    public void setPestId(String pestId) {
        this.pestId = pestId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TreatmentMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<TreatmentMethod> methods) {
        this.methods = methods;
    }

    public List<String> getRequiredPesticides() {
        return requiredPesticides;
    }

    public void setRequiredPesticides(List<String> requiredPesticides) {
        this.requiredPesticides = requiredPesticides;
    }

    public List<String> getRequiredEquipment() {
        return requiredEquipment;
    }

    public void setRequiredEquipment(List<String> requiredEquipment) {
        this.requiredEquipment = requiredEquipment;
    }

    public String getTimeSchedule() {
        return timeSchedule;
    }

    public void setTimeSchedule(String timeSchedule) {
        this.timeSchedule = timeSchedule;
    }

    public String getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(String targetArea) {
        this.targetArea = targetArea;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "TreatmentPlan{" +
                "id='" + id + '\'' +
                ", pestId='" + pestId + '\'' +
                ", planName='" + planName + '\'' +
                ", description='" + description + '\'' +
                ", methods=" + methods +
                ", requiredPesticides=" + requiredPesticides +
                ", requiredEquipment=" + requiredEquipment +
                ", timeSchedule='" + timeSchedule + '\'' +
                ", targetArea='" + targetArea + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", estimatedCost=" + estimatedCost +
                ", estimatedDuration=" + estimatedDuration +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}