package com.forestpest.util;

import com.forestpest.data.storage.DataStorage;
import com.forestpest.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据导出工具类
 * 提供将模拟数据导出为JSON文件的功能
 */
@Component
public class DataExportUtil {
    
    @Autowired
    private DataStorage dataStorage;
    
    private final ObjectMapper objectMapper;
    
    public DataExportUtil() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * 导出所有数据到JSON文件
     */
    public void exportAllDataToJson(String outputDir) throws IOException {
        // 创建输出目录
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 导出各类数据
        exportUsersToJson(outputDir);
        exportPestsToJson(outputDir);
        exportPesticidesToJson(outputDir);
        exportTreatmentPlansToJson(outputDir);
        exportTreatmentTasksToJson(outputDir);
        exportForestResourcesToJson(outputDir);
        exportKnowledgeBaseToJson(outputDir);
        
        // 导出数据摘要
        exportDataSummaryToJson(outputDir);
        
        System.out.println("数据导出完成，输出目录: " + outputDir);
    }
    
    /**
     * 导出用户数据
     */
    private void exportUsersToJson(String outputDir) throws IOException {
        File file = new File(outputDir, "users.json");
        objectMapper.writeValue(file, dataStorage.getUserStorage().findAll());
    }
    
    /**
     * 导出病虫害数据
     */
    private void exportPestsToJson(String outputDir) throws IOException {
        File file = new File(outputDir, "pests.json");
        objectMapper.writeValue(file, dataStorage.getPestStorage().findAll());
    }
    
    /**
     * 导出药剂数据
     */
    private void exportPesticidesToJson(String outputDir) throws IOException {
        File file = new File(outputDir, "pesticides.json");
        objectMapper.writeValue(file, dataStorage.getPesticideStorage().findAll());
    }
    
    /**
     * 导出防治方案数据
     */
    private void exportTreatmentPlansToJson(String outputDir) throws IOException {
        File file = new File(outputDir, "treatment_plans.json");
        objectMapper.writeValue(file, dataStorage.getTreatmentStorage().findAllPlans());
    }
    
    /**
     * 导出防治任务数据
     */
    private void exportTreatmentTasksToJson(String outputDir) throws IOException {
        File file = new File(outputDir, "treatment_tasks.json");
        objectMapper.writeValue(file, dataStorage.getTreatmentStorage().findAllTasks());
    }
    
    /**
     * 导出森林资源数据
     */
    private void exportForestResourcesToJson(String outputDir) throws IOException {
        File file = new File(outputDir, "forest_resources.json");
        objectMapper.writeValue(file, dataStorage.getForestResourceStorage().findAll());
    }
    
    /**
     * 导出知识库数据
     */
    private void exportKnowledgeBaseToJson(String outputDir) throws IOException {
        File file = new File(outputDir, "knowledge_base.json");
        objectMapper.writeValue(file, dataStorage.getKnowledgeStorage().findAll());
    }
    
    /**
     * 导出数据摘要
     */
    private void exportDataSummaryToJson(String outputDir) throws IOException {
        Map<String, Object> summary = new HashMap<>();
        summary.put("exportTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        summary.put("userCount", dataStorage.getUserStorage().count());
        summary.put("pestCount", dataStorage.getPestStorage().count());
        summary.put("pesticideCount", dataStorage.getPesticideStorage().count());
        summary.put("treatmentPlanCount", dataStorage.getTreatmentStorage().planCount());
        summary.put("treatmentTaskCount", dataStorage.getTreatmentStorage().taskCount());
        summary.put("forestResourceCount", dataStorage.getForestResourceStorage().count());
        summary.put("knowledgeBaseCount", dataStorage.getKnowledgeStorage().count());
        summary.put("evaluationCount", dataStorage.getEvaluationStorage().count());
        summary.put("predictionCount", dataStorage.getPredictionStorage().predictionCount());
        summary.put("alertCount", dataStorage.getPredictionStorage().alertCount());
        
        File file = new File(outputDir, "data_summary.json");
        objectMapper.writeValue(file, summary);
    }
    
    /**
     * 导出指定类型的数据
     */
    public void exportDataByType(String dataType, String outputPath) throws IOException {
        switch (dataType.toLowerCase()) {
            case "users":
                objectMapper.writeValue(new File(outputPath), dataStorage.getUserStorage().findAll());
                break;
            case "pests":
                objectMapper.writeValue(new File(outputPath), dataStorage.getPestStorage().findAll());
                break;
            case "pesticides":
                objectMapper.writeValue(new File(outputPath), dataStorage.getPesticideStorage().findAll());
                break;
            case "treatment_plans":
                objectMapper.writeValue(new File(outputPath), dataStorage.getTreatmentStorage().findAllPlans());
                break;
            case "treatment_tasks":
                objectMapper.writeValue(new File(outputPath), dataStorage.getTreatmentStorage().findAllTasks());
                break;
            case "forest_resources":
                objectMapper.writeValue(new File(outputPath), dataStorage.getForestResourceStorage().findAll());
                break;
            case "knowledge_base":
                objectMapper.writeValue(new File(outputPath), dataStorage.getKnowledgeStorage().findAll());
                break;
            default:
                throw new IllegalArgumentException("不支持的数据类型: " + dataType);
        }
    }
    
    /**
     * 获取数据的JSON字符串表示
     */
    public String getDataAsJsonString(String dataType) throws IOException {
        switch (dataType.toLowerCase()) {
            case "users":
                return objectMapper.writeValueAsString(dataStorage.getUserStorage().findAll());
            case "pests":
                return objectMapper.writeValueAsString(dataStorage.getPestStorage().findAll());
            case "pesticides":
                return objectMapper.writeValueAsString(dataStorage.getPesticideStorage().findAll());
            case "treatment_plans":
                return objectMapper.writeValueAsString(dataStorage.getTreatmentStorage().findAllPlans());
            case "treatment_tasks":
                return objectMapper.writeValueAsString(dataStorage.getTreatmentStorage().findAllTasks());
            case "forest_resources":
                return objectMapper.writeValueAsString(dataStorage.getForestResourceStorage().findAll());
            case "knowledge_base":
                return objectMapper.writeValueAsString(dataStorage.getKnowledgeStorage().findAll());
            default:
                throw new IllegalArgumentException("不支持的数据类型: " + dataType);
        }
    }
}