package com.forestpest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 病虫害预警实体类
 */
public class PestAlert extends BaseEntity {
    
    @NotBlank(message = "预测ID不能为空")
    private String predictionId;
    
    @NotBlank(message = "预警等级不能为空")
    @Size(max = 20, message = "预警等级长度不能超过20个字符")
    private String alertLevel;
    
    @NotNull(message = "预警时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alertTime;
    
    @NotBlank(message = "目标受众不能为空")
    @Size(max = 200, message = "目标受众长度不能超过200个字符")
    private String targetAudience;
    
    @NotBlank(message = "预警消息不能为空")
    @Size(max = 1000, message = "预警消息长度不能超过1000个字符")
    private String message;
    
    @NotBlank(message = "预警状态不能为空")
    @Size(max = 20, message = "预警状态长度不能超过20个字符")
    private String status;
    
    private String pestId;
    
    private String pestName;
    
    private String targetArea;
    
    private String alertType;
    
    private String urgency;
    
    private String severity;
    
    private String certainty;
    
    private List<String> affectedAreas;
    
    private String effectiveTime;
    
    private String expiryTime;
    
    private String instructions;
    
    private List<String> contacts;
    
    private String acknowledgedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acknowledgedTime;

    public PestAlert() {
        super();
        this.alertTime = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public PestAlert(String predictionId, String alertLevel, String targetAudience, String message) {
        super();
        this.predictionId = predictionId;
        this.alertLevel = alertLevel;
        this.targetAudience = targetAudience;
        this.message = message;
        this.alertTime = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public String getPredictionId() {
        return predictionId;
    }

    public void setPredictionId(String predictionId) {
        this.predictionId = predictionId;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public LocalDateTime getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(LocalDateTime alertTime) {
        this.alertTime = alertTime;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPestId() {
        return pestId;
    }

    public void setPestId(String pestId) {
        this.pestId = pestId;
    }

    public String getPestName() {
        return pestName;
    }

    public void setPestName(String pestName) {
        this.pestName = pestName;
    }

    public String getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(String targetArea) {
        this.targetArea = targetArea;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCertainty() {
        return certainty;
    }

    public void setCertainty(String certainty) {
        this.certainty = certainty;
    }

    public List<String> getAffectedAreas() {
        return affectedAreas;
    }

    public void setAffectedAreas(List<String> affectedAreas) {
        this.affectedAreas = affectedAreas;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public LocalDateTime getAcknowledgedTime() {
        return acknowledgedTime;
    }

    public void setAcknowledgedTime(LocalDateTime acknowledgedTime) {
        this.acknowledgedTime = acknowledgedTime;
    }

    @Override
    public String toString() {
        return "PestAlert{" +
                "id='" + id + '\'' +
                ", predictionId='" + predictionId + '\'' +
                ", alertLevel='" + alertLevel + '\'' +
                ", alertTime=" + alertTime +
                ", targetAudience='" + targetAudience + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", pestId='" + pestId + '\'' +
                ", pestName='" + pestName + '\'' +
                ", targetArea='" + targetArea + '\'' +
                ", alertType='" + alertType + '\'' +
                ", urgency='" + urgency + '\'' +
                ", severity='" + severity + '\'' +
                ", certainty='" + certainty + '\'' +
                ", effectiveTime='" + effectiveTime + '\'' +
                ", expiryTime='" + expiryTime + '\'' +
                ", acknowledgedBy='" + acknowledgedBy + '\'' +
                ", acknowledgedTime=" + acknowledgedTime +
                ", createdTime=" + createdTime +
                '}';
    }
}