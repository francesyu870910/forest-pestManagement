package com.forestpest.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 病虫害实体类
 */
public class Pest extends BaseEntity {
    
    @NotBlank(message = "病虫害名称不能为空")
    @Size(max = 100, message = "病虫害名称长度不能超过100个字符")
    private String name;
    
    @Size(max = 200, message = "学名长度不能超过200个字符")
    private String scientificName;
    
    @NotBlank(message = "病虫害类别不能为空")
    @Size(max = 50, message = "类别长度不能超过50个字符")
    private String category;
    
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;
    
    private List<String> symptoms;
    
    @Size(max = 1000, message = "危害特征长度不能超过1000个字符")
    private String harmCharacteristics;
    
    @Size(max = 1000, message = "发生规律长度不能超过1000个字符")
    private String occurrencePattern;
    
    @Size(max = 1000, message = "适宜环境条件长度不能超过1000个字符")
    private String suitableEnvironment;
    
    private String riskLevel;
    
    private List<String> hostPlants;
    
    private List<String> images;
    
    private String distributionArea;
    
    private String preventionMethods;

    public Pest() {
        super();
    }

    public Pest(String name, String scientificName, String category, String description) {
        super();
        this.name = name;
        this.scientificName = scientificName;
        this.category = category;
        this.description = description;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getHarmCharacteristics() {
        return harmCharacteristics;
    }

    public void setHarmCharacteristics(String harmCharacteristics) {
        this.harmCharacteristics = harmCharacteristics;
    }

    public String getOccurrencePattern() {
        return occurrencePattern;
    }

    public void setOccurrencePattern(String occurrencePattern) {
        this.occurrencePattern = occurrencePattern;
    }

    public String getSuitableEnvironment() {
        return suitableEnvironment;
    }

    public void setSuitableEnvironment(String suitableEnvironment) {
        this.suitableEnvironment = suitableEnvironment;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getHostPlants() {
        return hostPlants;
    }

    public void setHostPlants(List<String> hostPlants) {
        this.hostPlants = hostPlants;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDistributionArea() {
        return distributionArea;
    }

    public void setDistributionArea(String distributionArea) {
        this.distributionArea = distributionArea;
    }

    public String getPreventionMethods() {
        return preventionMethods;
    }

    public void setPreventionMethods(String preventionMethods) {
        this.preventionMethods = preventionMethods;
    }

    @Override
    public String toString() {
        return "Pest{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", symptoms=" + symptoms +
                ", harmCharacteristics='" + harmCharacteristics + '\'' +
                ", occurrencePattern='" + occurrencePattern + '\'' +
                ", suitableEnvironment='" + suitableEnvironment + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", hostPlants=" + hostPlants +
                ", distributionArea='" + distributionArea + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}