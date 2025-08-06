package com.forestpest.service;

import com.forestpest.data.factory.DataFactory;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据初始化服务
 * 提供数据初始化和重置功能的服务接口
 */
@Service
public class DataInitializationService {
    
    @Autowired
    private DataFactory dataFactory;
    
    @Autowired
    private DataStorage dataStorage;
    
    /**
     * 初始化所有演示数据
     */
    public void initializeAllData() {
        System.out.println("开始初始化演示数据...");
        dataFactory.generateAllData();
        System.out.println("演示数据初始化完成！");
    }
    
    /**
     * 重置所有数据
     */
    public void resetAllData() {
        System.out.println("开始重置所有数据...");
        dataStorage.resetAllData();
        System.out.println("数据重置完成！");
    }
    
    /**
     * 重新生成所有数据
     */
    public void regenerateAllData() {
        System.out.println("开始重新生成所有数据...");
        resetAllData();
        initializeAllData();
        System.out.println("数据重新生成完成！");
    }
    
    /**
     * 获取数据统计信息
     */
    public DataStatistics getDataStatistics() {
        DataStatistics stats = new DataStatistics();
        stats.setUserCount(dataStorage.getUserStorage().count());
        stats.setPestCount(dataStorage.getPestStorage().count());
        stats.setPesticideCount(dataStorage.getPesticideStorage().count());
        stats.setTreatmentPlanCount(dataStorage.getTreatmentStorage().planCount());
        stats.setTreatmentTaskCount(dataStorage.getTreatmentStorage().taskCount());
        stats.setForestResourceCount(dataStorage.getForestResourceStorage().count());
        stats.setKnowledgeBaseCount(dataStorage.getKnowledgeStorage().count());
        stats.setEvaluationCount(dataStorage.getEvaluationStorage().count());
        stats.setPredictionCount(dataStorage.getPredictionStorage().predictionCount());
        stats.setAlertCount(dataStorage.getPredictionStorage().alertCount());
        return stats;
    }
    
    /**
     * 检查数据是否已初始化
     */
    public boolean isDataInitialized() {
        return dataStorage.getUserStorage().count() > 0 && 
               dataStorage.getPestStorage().count() > 0 &&
               dataStorage.getPesticideStorage().count() > 0;
    }
    
    /**
     * 数据统计信息类
     */
    public static class DataStatistics {
        private int userCount;
        private int pestCount;
        private int pesticideCount;
        private int treatmentPlanCount;
        private int treatmentTaskCount;
        private int forestResourceCount;
        private int knowledgeBaseCount;
        private int evaluationCount;
        private int predictionCount;
        private int alertCount;
        
        // Getters and Setters
        public int getUserCount() {
            return userCount;
        }
        
        public void setUserCount(int userCount) {
            this.userCount = userCount;
        }
        
        public int getPestCount() {
            return pestCount;
        }
        
        public void setPestCount(int pestCount) {
            this.pestCount = pestCount;
        }
        
        public int getPesticideCount() {
            return pesticideCount;
        }
        
        public void setPesticideCount(int pesticideCount) {
            this.pesticideCount = pesticideCount;
        }
        
        public int getTreatmentPlanCount() {
            return treatmentPlanCount;
        }
        
        public void setTreatmentPlanCount(int treatmentPlanCount) {
            this.treatmentPlanCount = treatmentPlanCount;
        }
        
        public int getTreatmentTaskCount() {
            return treatmentTaskCount;
        }
        
        public void setTreatmentTaskCount(int treatmentTaskCount) {
            this.treatmentTaskCount = treatmentTaskCount;
        }
        
        public int getForestResourceCount() {
            return forestResourceCount;
        }
        
        public void setForestResourceCount(int forestResourceCount) {
            this.forestResourceCount = forestResourceCount;
        }
        
        public int getKnowledgeBaseCount() {
            return knowledgeBaseCount;
        }
        
        public void setKnowledgeBaseCount(int knowledgeBaseCount) {
            this.knowledgeBaseCount = knowledgeBaseCount;
        }
        
        public int getEvaluationCount() {
            return evaluationCount;
        }
        
        public void setEvaluationCount(int evaluationCount) {
            this.evaluationCount = evaluationCount;
        }
        
        public int getPredictionCount() {
            return predictionCount;
        }
        
        public void setPredictionCount(int predictionCount) {
            this.predictionCount = predictionCount;
        }
        
        public int getAlertCount() {
            return alertCount;
        }
        
        public void setAlertCount(int alertCount) {
            this.alertCount = alertCount;
        }
        
        @Override
        public String toString() {
            return "DataStatistics{" +
                    "userCount=" + userCount +
                    ", pestCount=" + pestCount +
                    ", pesticideCount=" + pesticideCount +
                    ", treatmentPlanCount=" + treatmentPlanCount +
                    ", treatmentTaskCount=" + treatmentTaskCount +
                    ", forestResourceCount=" + forestResourceCount +
                    ", knowledgeBaseCount=" + knowledgeBaseCount +
                    ", evaluationCount=" + evaluationCount +
                    ", predictionCount=" + predictionCount +
                    ", alertCount=" + alertCount +
                    '}';
        }
    }
}