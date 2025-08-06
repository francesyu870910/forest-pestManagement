package com.forestpest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 效果评估实体类
 */
public class EffectEvaluation extends BaseEntity {
    
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
    
    @NotBlank(message = "病虫害ID不能为空")
    private String pestId;
    
    @NotBlank(message = "评估区域不能为空")
    @Size(max = 200, message = "评估区域长度不能超过200个字符")
    private String evaluatedArea;
    
    @NotNull(message = "评估时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluationTime;
    
    @NotBlank(message = "评估人员不能为空")
    private String evaluatedBy;
    
    @NotNull(message = "防治前数据不能为空")
    private EvaluationData beforeTreatment;
    
    @NotNull(message = "防治后数据不能为空")
    private EvaluationData afterTreatment;
    
    @DecimalMin(value = "0.0", message = "有效率不能小于0")
    @DecimalMax(value = "100.0", message = "有效率不能大于100")
    private Double effectivenessRate;
    
    @Size(max = 1000, message = "结论长度不能超过1000个字符")
    private String conclusion;
    
    private List<String> photos;
    
    private String evaluationMethod;
    
    private String weather;
    
    private String recommendations;
    
    private String followUpActions;
    
    private String status;
    
    private String reviewedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;

    public EffectEvaluation() {
        super();
        this.evaluationTime = LocalDateTime.now();
        this.status = "DRAFT";
    }

    public EffectEvaluation(String taskId, String pestId, String evaluatedArea, String evaluatedBy) {
        super();
        this.taskId = taskId;
        this.pestId = pestId;
        this.evaluatedArea = evaluatedArea;
        this.evaluatedBy = evaluatedBy;
        this.evaluationTime = LocalDateTime.now();
        this.status = "DRAFT";
    }

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPestId() {
        return pestId;
    }

    public void setPestId(String pestId) {
        this.pestId = pestId;
    }

    public String getEvaluatedArea() {
        return evaluatedArea;
    }

    public void setEvaluatedArea(String evaluatedArea) {
        this.evaluatedArea = evaluatedArea;
    }

    public LocalDateTime getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(LocalDateTime evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    public String getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(String evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public EvaluationData getBeforeTreatment() {
        return beforeTreatment;
    }

    public void setBeforeTreatment(EvaluationData beforeTreatment) {
        this.beforeTreatment = beforeTreatment;
    }

    public EvaluationData getAfterTreatment() {
        return afterTreatment;
    }

    public void setAfterTreatment(EvaluationData afterTreatment) {
        this.afterTreatment = afterTreatment;
    }

    public Double getEffectivenessRate() {
        return effectivenessRate;
    }

    public void setEffectivenessRate(Double effectivenessRate) {
        this.effectivenessRate = effectivenessRate;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getEvaluationMethod() {
        return evaluationMethod;
    }

    public void setEvaluationMethod(String evaluationMethod) {
        this.evaluationMethod = evaluationMethod;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getFollowUpActions() {
        return followUpActions;
    }

    public void setFollowUpActions(String followUpActions) {
        this.followUpActions = followUpActions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    @Override
    public String toString() {
        return "EffectEvaluation{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", pestId='" + pestId + '\'' +
                ", evaluatedArea='" + evaluatedArea + '\'' +
                ", evaluationTime=" + evaluationTime +
                ", evaluatedBy='" + evaluatedBy + '\'' +
                ", beforeTreatment=" + beforeTreatment +
                ", afterTreatment=" + afterTreatment +
                ", effectivenessRate=" + effectivenessRate +
                ", conclusion='" + conclusion + '\'' +
                ", evaluationMethod='" + evaluationMethod + '\'' +
                ", weather='" + weather + '\'' +
                ", status='" + status + '\'' +
                ", reviewedBy='" + reviewedBy + '\'' +
                ", reviewTime=" + reviewTime +
                ", createdTime=" + createdTime +
                '}';
    }
}