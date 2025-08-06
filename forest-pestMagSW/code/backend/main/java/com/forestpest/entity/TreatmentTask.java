package com.forestpest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 防治任务实体类
 */
public class TreatmentTask extends BaseEntity {
    
    @NotBlank(message = "防治方案ID不能为空")
    private String planId;
    
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 200, message = "任务名称长度不能超过200个字符")
    private String taskName;
    
    @Size(max = 1000, message = "任务描述长度不能超过1000个字符")
    private String description;
    
    @NotBlank(message = "分配人员不能为空")
    private String assignedTo;
    
    @NotBlank(message = "任务状态不能为空")
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualStartTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualEndTime;
    
    @Size(max = 1000, message = "执行备注长度不能超过1000个字符")
    private String executionNotes;
    
    private List<String> usedPesticides;
    
    private String targetArea;
    
    private String priority;
    
    private Double actualCost;
    
    private String weather;
    
    private List<String> photos;
    
    private String supervisedBy;
    
    private String completionRate;

    public TreatmentTask() {
        super();
        this.status = "PENDING";
        this.priority = "MEDIUM";
    }

    public TreatmentTask(String planId, String taskName, String assignedTo, LocalDateTime scheduledTime) {
        super();
        this.planId = planId;
        this.taskName = taskName;
        this.assignedTo = assignedTo;
        this.scheduledTime = scheduledTime;
        this.status = "PENDING";
        this.priority = "MEDIUM";
    }

    // Getters and Setters
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public String getExecutionNotes() {
        return executionNotes;
    }

    public void setExecutionNotes(String executionNotes) {
        this.executionNotes = executionNotes;
    }

    public List<String> getUsedPesticides() {
        return usedPesticides;
    }

    public void setUsedPesticides(List<String> usedPesticides) {
        this.usedPesticides = usedPesticides;
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

    public Double getActualCost() {
        return actualCost;
    }

    public void setActualCost(Double actualCost) {
        this.actualCost = actualCost;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getSupervisedBy() {
        return supervisedBy;
    }

    public void setSupervisedBy(String supervisedBy) {
        this.supervisedBy = supervisedBy;
    }

    public String getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(String completionRate) {
        this.completionRate = completionRate;
    }

    @Override
    public String toString() {
        return "TreatmentTask{" +
                "id='" + id + '\'' +
                ", planId='" + planId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", status='" + status + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", actualStartTime=" + actualStartTime +
                ", actualEndTime=" + actualEndTime +
                ", targetArea='" + targetArea + '\'' +
                ", priority='" + priority + '\'' +
                ", actualCost=" + actualCost +
                ", supervisedBy='" + supervisedBy + '\'' +
                ", completionRate='" + completionRate + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}