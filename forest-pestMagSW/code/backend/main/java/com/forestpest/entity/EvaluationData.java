package com.forestpest.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 评估数据实体类
 */
public class EvaluationData {
    
    @DecimalMin(value = "0.0", message = "受害面积不能小于0")
    private Double affectedArea;
    
    @Size(max = 50, message = "严重程度长度不能超过50个字符")
    private String severityLevel;
    
    @Min(value = 0, message = "病虫害数量不能小于0")
    private Integer pestPopulation;
    
    @Size(max = 1000, message = "损害描述长度不能超过1000个字符")
    private String damageDescription;
    
    private Double damageRate;
    
    private String healthStatus;
    
    private Integer healthyPlants;
    
    private Integer damagedPlants;
    
    private Integer deadPlants;
    
    private String vegetationCoverage;
    
    private String soilCondition;
    
    private String notes;

    public EvaluationData() {
    }

    public EvaluationData(Double affectedArea, String severityLevel, Integer pestPopulation, String damageDescription) {
        this.affectedArea = affectedArea;
        this.severityLevel = severityLevel;
        this.pestPopulation = pestPopulation;
        this.damageDescription = damageDescription;
    }

    // Getters and Setters
    public Double getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(Double affectedArea) {
        this.affectedArea = affectedArea;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }

    public Integer getPestPopulation() {
        return pestPopulation;
    }

    public void setPestPopulation(Integer pestPopulation) {
        this.pestPopulation = pestPopulation;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public Double getDamageRate() {
        return damageRate;
    }

    public void setDamageRate(Double damageRate) {
        this.damageRate = damageRate;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Integer getHealthyPlants() {
        return healthyPlants;
    }

    public void setHealthyPlants(Integer healthyPlants) {
        this.healthyPlants = healthyPlants;
    }

    public Integer getDamagedPlants() {
        return damagedPlants;
    }

    public void setDamagedPlants(Integer damagedPlants) {
        this.damagedPlants = damagedPlants;
    }

    public Integer getDeadPlants() {
        return deadPlants;
    }

    public void setDeadPlants(Integer deadPlants) {
        this.deadPlants = deadPlants;
    }

    public String getVegetationCoverage() {
        return vegetationCoverage;
    }

    public void setVegetationCoverage(String vegetationCoverage) {
        this.vegetationCoverage = vegetationCoverage;
    }

    public String getSoilCondition() {
        return soilCondition;
    }

    public void setSoilCondition(String soilCondition) {
        this.soilCondition = soilCondition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "EvaluationData{" +
                "affectedArea=" + affectedArea +
                ", severityLevel='" + severityLevel + '\'' +
                ", pestPopulation=" + pestPopulation +
                ", damageDescription='" + damageDescription + '\'' +
                ", damageRate=" + damageRate +
                ", healthStatus='" + healthStatus + '\'' +
                ", healthyPlants=" + healthyPlants +
                ", damagedPlants=" + damagedPlants +
                ", deadPlants=" + deadPlants +
                ", vegetationCoverage='" + vegetationCoverage + '\'' +
                ", soilCondition='" + soilCondition + '\'' +
                '}';
    }
}