package com.forestpest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 药剂使用记录实体类
 */
public class PesticideUsageRecord extends BaseEntity {
    
    @NotBlank(message = "药剂ID不能为空")
    private String pesticideId;
    
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
    
    @NotNull(message = "使用数量不能为空")
    @Min(value = 1, message = "使用数量必须大于0")
    private Integer usedQuantity;
    
    @NotNull(message = "使用时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usageTime;
    
    @NotBlank(message = "使用人员不能为空")
    private String usedBy;
    
    @NotBlank(message = "目标区域不能为空")
    @Size(max = 200, message = "目标区域长度不能超过200个字符")
    private String targetArea;
    
    @Size(max = 500, message = "安全措施长度不能超过500个字符")
    private String safetyMeasures;
    
    private String pesticideName;
    
    private String concentration;
    
    private String applicationMethod;
    
    private String weather;
    
    private String temperature;
    
    private String humidity;
    
    private String windSpeed;
    
    private String notes;
    
    private String supervisedBy;
    
    private String effectivenessRating;

    public PesticideUsageRecord() {
        super();
        this.usageTime = LocalDateTime.now();
    }

    public PesticideUsageRecord(String pesticideId, String taskId, Integer usedQuantity, String usedBy, String targetArea) {
        super();
        this.pesticideId = pesticideId;
        this.taskId = taskId;
        this.usedQuantity = usedQuantity;
        this.usedBy = usedBy;
        this.targetArea = targetArea;
        this.usageTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getPesticideId() {
        return pesticideId;
    }

    public void setPesticideId(String pesticideId) {
        this.pesticideId = pesticideId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(Integer usedQuantity) {
        this.usedQuantity = usedQuantity;
    }

    public LocalDateTime getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(LocalDateTime usageTime) {
        this.usageTime = usageTime;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public String getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(String targetArea) {
        this.targetArea = targetArea;
    }

    public String getSafetyMeasures() {
        return safetyMeasures;
    }

    public void setSafetyMeasures(String safetyMeasures) {
        this.safetyMeasures = safetyMeasures;
    }

    public String getPesticideName() {
        return pesticideName;
    }

    public void setPesticideName(String pesticideName) {
        this.pesticideName = pesticideName;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getApplicationMethod() {
        return applicationMethod;
    }

    public void setApplicationMethod(String applicationMethod) {
        this.applicationMethod = applicationMethod;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSupervisedBy() {
        return supervisedBy;
    }

    public void setSupervisedBy(String supervisedBy) {
        this.supervisedBy = supervisedBy;
    }

    public String getEffectivenessRating() {
        return effectivenessRating;
    }

    public void setEffectivenessRating(String effectivenessRating) {
        this.effectivenessRating = effectivenessRating;
    }

    @Override
    public String toString() {
        return "PesticideUsageRecord{" +
                "id='" + id + '\'' +
                ", pesticideId='" + pesticideId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", usedQuantity=" + usedQuantity +
                ", usageTime=" + usageTime +
                ", usedBy='" + usedBy + '\'' +
                ", targetArea='" + targetArea + '\'' +
                ", pesticideName='" + pesticideName + '\'' +
                ", concentration='" + concentration + '\'' +
                ", applicationMethod='" + applicationMethod + '\'' +
                ", weather='" + weather + '\'' +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", supervisedBy='" + supervisedBy + '\'' +
                ", effectivenessRating='" + effectivenessRating + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}