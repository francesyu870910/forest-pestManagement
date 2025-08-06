package com.forestpest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * 病虫害预测实体类
 */
public class PestPrediction extends BaseEntity {
    
    @NotBlank(message = "病虫害ID不能为空")
    private String pestId;
    
    @NotBlank(message = "目标区域不能为空")
    @Size(max = 200, message = "目标区域长度不能超过200个字符")
    private String targetArea;
    
    @NotNull(message = "预测日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate predictionDate;
    
    @NotBlank(message = "风险等级不能为空")
    @Size(max = 20, message = "风险等级长度不能超过20个字符")
    private String riskLevel;
    
    @NotNull(message = "发生概率不能为空")
    @DecimalMin(value = "0.0", message = "发生概率不能小于0")
    @DecimalMax(value = "1.0", message = "发生概率不能大于1")
    private Double probability;
    
    @Size(max = 1000, message = "影响因素长度不能超过1000个字符")
    private String influencingFactors;
    
    @Size(max = 1000, message = "建议措施长度不能超过1000个字符")
    private String recommendedActions;
    
    private String pestName;
    
    private String predictionModel;
    
    private String weatherConditions;
    
    private String temperature;
    
    private String humidity;
    
    private String rainfall;
    
    private String windSpeed;
    
    private List<String> historicalData;
    
    private String confidence;
    
    private String validityPeriod;
    
    private String status;

    public PestPrediction() {
        super();
        this.predictionDate = LocalDate.now();
        this.status = "ACTIVE";
    }

    public PestPrediction(String pestId, String targetArea, LocalDate predictionDate, String riskLevel, Double probability) {
        super();
        this.pestId = pestId;
        this.targetArea = targetArea;
        this.predictionDate = predictionDate;
        this.riskLevel = riskLevel;
        this.probability = probability;
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public String getPestId() {
        return pestId;
    }

    public void setPestId(String pestId) {
        this.pestId = pestId;
    }

    public String getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(String targetArea) {
        this.targetArea = targetArea;
    }

    public LocalDate getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDate predictionDate) {
        this.predictionDate = predictionDate;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public String getInfluencingFactors() {
        return influencingFactors;
    }

    public void setInfluencingFactors(String influencingFactors) {
        this.influencingFactors = influencingFactors;
    }

    public String getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(String recommendedActions) {
        this.recommendedActions = recommendedActions;
    }

    public String getPestName() {
        return pestName;
    }

    public void setPestName(String pestName) {
        this.pestName = pestName;
    }

    public String getPredictionModel() {
        return predictionModel;
    }

    public void setPredictionModel(String predictionModel) {
        this.predictionModel = predictionModel;
    }

    public String getWeatherConditions() {
        return weatherConditions;
    }

    public void setWeatherConditions(String weatherConditions) {
        this.weatherConditions = weatherConditions;
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

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public List<String> getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(List<String> historicalData) {
        this.historicalData = historicalData;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PestPrediction{" +
                "id='" + id + '\'' +
                ", pestId='" + pestId + '\'' +
                ", targetArea='" + targetArea + '\'' +
                ", predictionDate=" + predictionDate +
                ", riskLevel='" + riskLevel + '\'' +
                ", probability=" + probability +
                ", influencingFactors='" + influencingFactors + '\'' +
                ", recommendedActions='" + recommendedActions + '\'' +
                ", pestName='" + pestName + '\'' +
                ", predictionModel='" + predictionModel + '\'' +
                ", weatherConditions='" + weatherConditions + '\'' +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", rainfall='" + rainfall + '\'' +
                ", confidence='" + confidence + '\'' +
                ", validityPeriod='" + validityPeriod + '\'' +
                ", status='" + status + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}