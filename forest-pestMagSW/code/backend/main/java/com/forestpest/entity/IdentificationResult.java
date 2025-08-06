package com.forestpest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**getFeedbackTime
 * 病虫害识别结果实体类
 */
public class IdentificationResult extends BaseEntity {
    
    @NotBlank(message = "病虫害ID不能为空")
    private String pestId;
    
    @NotBlank(message = "病虫害名称不能为空")
    private String pestName;
    
    @NotNull(message = "置信度不能为空")
    @DecimalMin(value = "0.0", message = "置信度不能小于0")
    @DecimalMax(value = "1.0", message = "置信度不能大于1")
    private Double confidence;
    
    @NotBlank(message = "识别方法不能为空")
    private String identificationMethod;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime identificationTime;
    
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    private String imagePath;
    
    private List<String> inputSymptoms;
    
    private String location;
    
    private String notes;
    
    private String status;
    
    private List<String> alternativePests;
    
    private String verificationStatus;
    
    private String verifiedBy;

    public IdentificationResult() {
        super();
        this.identificationTime = LocalDateTime.now();
        this.status = "PENDING";
        this.verificationStatus = "UNVERIFIED";
    }

    public IdentificationResult(String pestId, String pestName, Double confidence, String identificationMethod, String userId) {
        super();
        this.pestId = pestId;
        this.pestName = pestName;
        this.confidence = confidence;
        this.identificationMethod = identificationMethod;
        this.userId = userId;
        this.identificationTime = LocalDateTime.now();
        this.status = "PENDING";
        this.verificationStatus = "UNVERIFIED";
    }

    // Getters and Setters
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

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getIdentificationMethod() {
        return identificationMethod;
    }

    public void setIdentificationMethod(String identificationMethod) {
        this.identificationMethod = identificationMethod;
    }

    public LocalDateTime getIdentificationTime() {
        return identificationTime;
    }

    public void setIdentificationTime(LocalDateTime identificationTime) {
        this.identificationTime = identificationTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<String> getInputSymptoms() {
        return inputSymptoms;
    }

    public void setInputSymptoms(List<String> inputSymptoms) {
        this.inputSymptoms = inputSymptoms;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getAlternativePests() {
        return alternativePests;
    }

    public void setAlternativePests(List<String> alternativePests) {
        this.alternativePests = alternativePests;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    @Override
    public String toString() {
        return "IdentificationResult{" +
                "id='" + id + '\'' +
                ", pestId='" + pestId + '\'' +
                ", pestName='" + pestName + '\'' +
                ", confidence=" + confidence +
                ", identificationMethod='" + identificationMethod + '\'' +
                ", identificationTime=" + identificationTime +
                ", userId='" + userId + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", inputSymptoms=" + inputSymptoms +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                '}';
    }
}