package com.forestpest.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Repository工厂类
 * 提供统一的Repository访问接口
 */
@Component
public class RepositoryFactory {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PestRepository pestRepository;
    
    @Autowired
    private PesticideRepository pesticideRepository;
    
    @Autowired
    private TreatmentRepository treatmentRepository;
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private PredictionRepository predictionRepository;
    
    @Autowired
    private ForestResourceRepository forestResourceRepository;
    
    @Autowired
    private KnowledgeRepository knowledgeRepository;
    
    /**
     * 获取用户Repository
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }
    
    /**
     * 获取病虫害Repository
     */
    public PestRepository getPestRepository() {
        return pestRepository;
    }
    
    /**
     * 获取药剂Repository
     */
    public PesticideRepository getPesticideRepository() {
        return pesticideRepository;
    }
    
    /**
     * 获取防治Repository
     */
    public TreatmentRepository getTreatmentRepository() {
        return treatmentRepository;
    }
    
    /**
     * 获取评估Repository
     */
    public EvaluationRepository getEvaluationRepository() {
        return evaluationRepository;
    }
    
    /**
     * 获取预测Repository
     */
    public PredictionRepository getPredictionRepository() {
        return predictionRepository;
    }
    
    /**
     * 获取森林资源Repository
     */
    public ForestResourceRepository getForestResourceRepository() {
        return forestResourceRepository;
    }
    
    /**
     * 获取知识库Repository
     */
    public KnowledgeRepository getKnowledgeRepository() {
        return knowledgeRepository;
    }
    
    /**
     * 初始化所有Repository的数据
     */
    public void initializeAllRepositories() {
        System.out.println("开始初始化所有Repository数据...");
        
        // 这里可以添加Repository级别的初始化逻辑
        // 例如：缓存预热、索引构建等
        
        System.out.println("Repository数据初始化完成");
    }
    
    /**
     * 清理所有Repository的缓存
     */
    public void clearAllCaches() {
        System.out.println("清理Repository缓存...");
        
        // 这里可以添加缓存清理逻辑
        
        System.out.println("Repository缓存清理完成");
    }
    
    /**
     * 获取Repository统计信息
     */
    public RepositoryStatistics getStatistics() {
        RepositoryStatistics stats = new RepositoryStatistics();
        
        stats.setUserCount(userRepository.count());
        stats.setPestCount(pestRepository.count());
        stats.setPesticideCount(pesticideRepository.count());
        stats.setTreatmentPlanCount(treatmentRepository.countPlans());
        stats.setTreatmentTaskCount(treatmentRepository.countTasks());
        stats.setEvaluationCount(evaluationRepository.count());
        stats.setPredictionCount(predictionRepository.countPredictions());
        stats.setAlertCount(predictionRepository.countAlerts());
        stats.setForestResourceCount(forestResourceRepository.count());
        stats.setKnowledgeCount(knowledgeRepository.count());
        
        return stats;
    }
    
    /**
     * Repository统计信息类
     */
    public static class RepositoryStatistics {
        private long userCount;
        private long pestCount;
        private long pesticideCount;
        private long treatmentPlanCount;
        private long treatmentTaskCount;
        private long evaluationCount;
        private long predictionCount;
        private long alertCount;
        private long forestResourceCount;
        private long knowledgeCount;
        
        // Getters and Setters
        public long getUserCount() {
            return userCount;
        }
        
        public void setUserCount(long userCount) {
            this.userCount = userCount;
        }
        
        public long getPestCount() {
            return pestCount;
        }
        
        public void setPestCount(long pestCount) {
            this.pestCount = pestCount;
        }
        
        public long getPesticideCount() {
            return pesticideCount;
        }
        
        public void setPesticideCount(long pesticideCount) {
            this.pesticideCount = pesticideCount;
        }
        
        public long getTreatmentPlanCount() {
            return treatmentPlanCount;
        }
        
        public void setTreatmentPlanCount(long treatmentPlanCount) {
            this.treatmentPlanCount = treatmentPlanCount;
        }
        
        public long getTreatmentTaskCount() {
            return treatmentTaskCount;
        }
        
        public void setTreatmentTaskCount(long treatmentTaskCount) {
            this.treatmentTaskCount = treatmentTaskCount;
        }
        
        public long getEvaluationCount() {
            return evaluationCount;
        }
        
        public void setEvaluationCount(long evaluationCount) {
            this.evaluationCount = evaluationCount;
        }
        
        public long getPredictionCount() {
            return predictionCount;
        }
        
        public void setPredictionCount(long predictionCount) {
            this.predictionCount = predictionCount;
        }
        
        public long getAlertCount() {
            return alertCount;
        }
        
        public void setAlertCount(long alertCount) {
            this.alertCount = alertCount;
        }
        
        public long getForestResourceCount() {
            return forestResourceCount;
        }
        
        public void setForestResourceCount(long forestResourceCount) {
            this.forestResourceCount = forestResourceCount;
        }
        
        public long getKnowledgeCount() {
            return knowledgeCount;
        }
        
        public void setKnowledgeCount(long knowledgeCount) {
            this.knowledgeCount = knowledgeCount;
        }
        
        @Override
        public String toString() {
            return "RepositoryStatistics{" +
                    "userCount=" + userCount +
                    ", pestCount=" + pestCount +
                    ", pesticideCount=" + pesticideCount +
                    ", treatmentPlanCount=" + treatmentPlanCount +
                    ", treatmentTaskCount=" + treatmentTaskCount +
                    ", evaluationCount=" + evaluationCount +
                    ", predictionCount=" + predictionCount +
                    ", alertCount=" + alertCount +
                    ", forestResourceCount=" + forestResourceCount +
                    ", knowledgeCount=" + knowledgeCount +
                    '}';
        }
    }
}