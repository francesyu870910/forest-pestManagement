package com.forestpest.data.factory;

import com.forestpest.data.storage.DataStorage;
import com.forestpest.data.manager.DataRelationshipManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 主数据工厂类
 * 负责协调各个数据生成器，初始化系统演示数据
 */
@Component
public class DataFactory {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Autowired
    private UserDataFactory userDataFactory;
    
    @Autowired
    private PestDataFactory pestDataFactory;
    
    @Autowired
    private PesticideDataFactory pesticideDataFactory;
    
    @Autowired
    private TreatmentDataFactory treatmentDataFactory;
    
    @Autowired
    private ForestResourceDataFactory forestResourceDataFactory;
    
    @Autowired
    private KnowledgeDataFactory knowledgeDataFactory;
    
    @Autowired
    private DataRelationshipManager relationshipManager;
    
    /**
     * 应用启动后自动初始化数据
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        System.out.println("开始初始化模拟数据...");
        
        // 重置所有数据
        dataStorage.resetAllData();
        
        // 按依赖顺序生成数据
        generateAllData();
        
        System.out.println("模拟数据初始化完成！");
        printDataSummary();
    }
    
    /**
     * 生成所有模拟数据
     */
    public void generateAllData() {
        // 1. 生成基础数据（无依赖）
        userDataFactory.generateUsers();
        pestDataFactory.generatePests();
        pesticideDataFactory.generatePesticides();
        forestResourceDataFactory.generateForestResources();
        knowledgeDataFactory.generateKnowledgeBase();
        
        // 2. 生成依赖数据
        treatmentDataFactory.generateTreatmentPlans();
        treatmentDataFactory.generateTreatmentTasks();
        
        // 3. 生成关联数据
        generateIdentificationResults();
        generateUsageRecords();
        generateEvaluations();
        generatePredictions();
        
        // 4. 验证和修复数据关联关系
        validateAndFixRelationships();
    }
    
    /**
     * 重新生成所有数据
     */
    public void regenerateAllData() {
        System.out.println("重新生成模拟数据...");
        initializeData();
    }
    
    /**
     * 生成识别结果数据
     */
    private void generateIdentificationResults() {
        // 这里可以添加识别结果的生成逻辑
        // 暂时留空，后续在相应的模块中实现
    }
    
    /**
     * 生成药剂使用记录
     */
    private void generateUsageRecords() {
        // 这里可以添加使用记录的生成逻辑
        // 暂时留空，后续在相应的模块中实现
    }
    
    /**
     * 生成效果评估数据
     */
    private void generateEvaluations() {
        // 这里可以添加评估数据的生成逻辑
        // 暂时留空，后续在相应的模块中实现
    }
    
    /**
     * 生成预测预警数据
     */
    private void generatePredictions() {
        // 这里可以添加预测数据的生成逻辑
        // 暂时留空，后续在相应的模块中实现
    }
    
    /**
     * 验证和修复数据关联关系
     */
    private void validateAndFixRelationships() {
        System.out.println("开始验证数据关联关系...");
        
        DataRelationshipManager.ValidationResult result = relationshipManager.validateAllRelationships();
        
        if (result.hasErrors()) {
            System.out.println("发现数据关联错误，开始修复...");
            relationshipManager.fixRelationships();
            System.out.println("数据关联修复完成");
        } else {
            System.out.println("数据关联关系验证通过");
        }
        
        // 打印验证结果摘要
        System.out.println("验证结果: 错误" + result.getErrors().size() + "个, 警告" + result.getWarnings().size() + "个");
    }
    
    /**
     * 打印数据统计摘要
     */
    private void printDataSummary() {
        System.out.println("=== 数据统计摘要 ===");
        System.out.println("用户数量: " + dataStorage.getUserStorage().count());
        System.out.println("病虫害数量: " + dataStorage.getPestStorage().count());
        System.out.println("药剂数量: " + dataStorage.getPesticideStorage().count());
        System.out.println("防治方案数量: " + dataStorage.getTreatmentStorage().planCount());
        System.out.println("防治任务数量: " + dataStorage.getTreatmentStorage().taskCount());
        System.out.println("森林资源数量: " + dataStorage.getForestResourceStorage().count());
        System.out.println("知识库条目数量: " + dataStorage.getKnowledgeStorage().count());
        System.out.println("效果评估数量: " + dataStorage.getEvaluationStorage().count());
        System.out.println("预测数量: " + dataStorage.getPredictionStorage().predictionCount());
        System.out.println("预警数量: " + dataStorage.getPredictionStorage().alertCount());
        System.out.println("==================");
    }
}