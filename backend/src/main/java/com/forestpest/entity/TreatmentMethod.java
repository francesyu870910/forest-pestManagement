package com.forestpest.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 防治方法实体类
 */
public class TreatmentMethod {
    
    @NotBlank(message = "防治方法类型不能为空")
    @Size(max = 50, message = "防治方法类型长度不能超过50个字符")
    private String methodType;
    
    @NotBlank(message = "方法名称不能为空")
    @Size(max = 100, message = "方法名称长度不能超过100个字符")
    private String methodName;
    
    @Size(max = 1000, message = "方法描述长度不能超过1000个字符")
    private String description;
    
    private List<String> operationSteps;
    
    @Size(max = 200, message = "用量说明长度不能超过200个字符")
    private String dosage;
    
    @Size(max = 500, message = "注意事项长度不能超过500个字符")
    private String precautions;
    
    private String effectiveness;
    
    private String applicableConditions;
    
    private Integer executionOrder;
    
    private String duration;
    
    private String frequency;

    public TreatmentMethod() {
    }

    public TreatmentMethod(String methodType, String methodName, String description) {
        this.methodType = methodType;
        this.methodName = methodName;
        this.description = description;
    }

    // Getters and Setters
    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getOperationSteps() {
        return operationSteps;
    }

    public void setOperationSteps(List<String> operationSteps) {
        this.operationSteps = operationSteps;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public String getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(String effectiveness) {
        this.effectiveness = effectiveness;
    }

    public String getApplicableConditions() {
        return applicableConditions;
    }

    public void setApplicableConditions(String applicableConditions) {
        this.applicableConditions = applicableConditions;
    }

    public Integer getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Integer executionOrder) {
        this.executionOrder = executionOrder;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "TreatmentMethod{" +
                "methodType='" + methodType + '\'' +
                ", methodName='" + methodName + '\'' +
                ", description='" + description + '\'' +
                ", operationSteps=" + operationSteps +
                ", dosage='" + dosage + '\'' +
                ", precautions='" + precautions + '\'' +
                ", effectiveness='" + effectiveness + '\'' +
                ", applicableConditions='" + applicableConditions + '\'' +
                ", executionOrder=" + executionOrder +
                ", duration='" + duration + '\'' +
                ", frequency='" + frequency + '\'' +
                '}';
    }
}