package com.forestpest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 药剂信息实体类
 */
public class Pesticide extends BaseEntity {
    
    @NotBlank(message = "药剂名称不能为空")
    @Size(max = 100, message = "药剂名称长度不能超过100个字符")
    private String name;
    
    @NotBlank(message = "有效成分不能为空")
    @Size(max = 200, message = "有效成分长度不能超过200个字符")
    private String activeIngredient;
    
    @NotBlank(message = "规格不能为空")
    @Size(max = 50, message = "规格长度不能超过50个字符")
    private String specification;
    
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能小于0")
    private Integer stockQuantity;
    
    @NotNull(message = "有效期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    
    @Size(max = 100, message = "供应商长度不能超过100个字符")
    private String supplier;
    
    @DecimalMin(value = "0.0", message = "单价不能小于0")
    private BigDecimal unitPrice;
    
    @NotBlank(message = "安全等级不能为空")
    @Size(max = 20, message = "安全等级长度不能超过20个字符")
    private String safetyLevel;
    
    private String category;
    
    private String storageConditions;
    
    private String usageInstructions;
    
    private String toxicityLevel;
    
    private Integer minStockLevel;
    
    private String unit;
    
    private String manufacturer;
    
    private String registrationNumber;
    
    private String status;

    public Pesticide() {
        super();
        this.status = "ACTIVE";
        this.stockQuantity = 0;
    }

    public Pesticide(String name, String activeIngredient, String specification, Integer stockQuantity, LocalDate expiryDate, String safetyLevel) {
        super();
        this.name = name;
        this.activeIngredient = activeIngredient;
        this.specification = specification;
        this.stockQuantity = stockQuantity;
        this.expiryDate = expiryDate;
        this.safetyLevel = safetyLevel;
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActiveIngredient() {
        return activeIngredient;
    }

    public void setActiveIngredient(String activeIngredient) {
        this.activeIngredient = activeIngredient;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSafetyLevel() {
        return safetyLevel;
    }

    public void setSafetyLevel(String safetyLevel) {
        this.safetyLevel = safetyLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStorageConditions() {
        return storageConditions;
    }

    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }

    public String getUsageInstructions() {
        return usageInstructions;
    }

    public void setUsageInstructions(String usageInstructions) {
        this.usageInstructions = usageInstructions;
    }

    public String getToxicityLevel() {
        return toxicityLevel;
    }

    public void setToxicityLevel(String toxicityLevel) {
        this.toxicityLevel = toxicityLevel;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Pesticide{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", activeIngredient='" + activeIngredient + '\'' +
                ", specification='" + specification + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", expiryDate=" + expiryDate +
                ", supplier='" + supplier + '\'' +
                ", unitPrice=" + unitPrice +
                ", safetyLevel='" + safetyLevel + '\'' +
                ", category='" + category + '\'' +
                ", toxicityLevel='" + toxicityLevel + '\'' +
                ", minStockLevel=" + minStockLevel +
                ", unit='" + unit + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", status='" + status + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}