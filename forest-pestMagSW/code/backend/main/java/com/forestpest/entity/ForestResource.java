package com.forestpest.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 森林资源实体类
 */
public class ForestResource extends BaseEntity {
    
    @NotBlank(message = "区域名称不能为空")
    @Size(max = 100, message = "区域名称长度不能超过100个字符")
    private String areaName;
    
    @NotBlank(message = "区域代码不能为空")
    @Size(max = 50, message = "区域代码长度不能超过50个字符")
    private String areaCode;
    
    @NotBlank(message = "区域类型不能为空")
    @Size(max = 50, message = "区域类型长度不能超过50个字符")
    private String areaType;
    
    @DecimalMin(value = "0.0", message = "面积不能小于0")
    private Double area;
    
    private String parentAreaId;
    
    private List<String> treeSpecies;
    
    private String dominantSpecies;
    
    private Integer forestAge;
    
    private String standCondition;
    
    private String topography;
    
    private String soilType;
    
    private String climate;
    
    private String elevation;
    
    private String slope;
    
    private String aspect;
    
    private String healthStatus;
    
    private String managementLevel;
    
    private String accessibility;
    
    private String coordinates;
    
    private String boundary;
    
    private String notes;

    public ForestResource() {
        super();
        this.healthStatus = "GOOD";
        this.managementLevel = "NORMAL";
    }

    public ForestResource(String areaName, String areaCode, String areaType, Double area) {
        super();
        this.areaName = areaName;
        this.areaCode = areaCode;
        this.areaType = areaType;
        this.area = area;
        this.healthStatus = "GOOD";
        this.managementLevel = "NORMAL";
    }

    // Getters and Setters
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getParentAreaId() {
        return parentAreaId;
    }

    public void setParentAreaId(String parentAreaId) {
        this.parentAreaId = parentAreaId;
    }

    public List<String> getTreeSpecies() {
        return treeSpecies;
    }

    public void setTreeSpecies(List<String> treeSpecies) {
        this.treeSpecies = treeSpecies;
    }

    public String getDominantSpecies() {
        return dominantSpecies;
    }

    public void setDominantSpecies(String dominantSpecies) {
        this.dominantSpecies = dominantSpecies;
    }

    public Integer getForestAge() {
        return forestAge;
    }

    public void setForestAge(Integer forestAge) {
        this.forestAge = forestAge;
    }

    public String getStandCondition() {
        return standCondition;
    }

    public void setStandCondition(String standCondition) {
        this.standCondition = standCondition;
    }

    public String getTopography() {
        return topography;
    }

    public void setTopography(String topography) {
        this.topography = topography;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getAspect() {
        return aspect;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getManagementLevel() {
        return managementLevel;
    }

    public void setManagementLevel(String managementLevel) {
        this.managementLevel = managementLevel;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "ForestResource{" +
                "id='" + id + '\'' +
                ", areaName='" + areaName + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", areaType='" + areaType + '\'' +
                ", area=" + area +
                ", parentAreaId='" + parentAreaId + '\'' +
                ", treeSpecies=" + treeSpecies +
                ", dominantSpecies='" + dominantSpecies + '\'' +
                ", forestAge=" + forestAge +
                ", standCondition='" + standCondition + '\'' +
                ", topography='" + topography + '\'' +
                ", soilType='" + soilType + '\'' +
                ", climate='" + climate + '\'' +
                ", elevation='" + elevation + '\'' +
                ", healthStatus='" + healthStatus + '\'' +
                ", managementLevel='" + managementLevel + '\'' +
                ", accessibility='" + accessibility + '\'' +
                ", coordinates='" + coordinates + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}