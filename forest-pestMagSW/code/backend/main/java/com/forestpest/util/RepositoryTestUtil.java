package com.forestpest.util;

import com.forestpest.repository.RepositoryFactory;
import com.forestpest.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository测试工具类
 * 提供Repository功能的测试和验证
 */
@Component
public class RepositoryTestUtil {
    
    @Autowired
    private RepositoryFactory repositoryFactory;
    
    /**
     * 测试所有Repository的基本功能
     */
    public void testAllRepositories() {
        System.out.println("开始测试Repository功能...");
        
        testUserRepository();
        testPestRepository();
        testPesticideRepository();
        testTreatmentRepository();
        testEvaluationRepository();
        testPredictionRepository();
        testForestResourceRepository();
        testKnowledgeRepository();
        
        System.out.println("Repository功能测试完成");
    }
    
    /**
     * 测试用户Repository
     */
    private void testUserRepository() {
        System.out.println("测试UserRepository...");
        
        try {
            // 测试查询功能
            List<User> allUsers = repositoryFactory.getUserRepository().findAll();
            System.out.println("用户总数: " + allUsers.size());
            
            // 测试按角色查询
            List<User> admins = repositoryFactory.getUserRepository().findByRole("ADMIN");
            System.out.println("管理员数量: " + admins.size());
            
            // 测试按状态查询
            List<User> activeUsers = repositoryFactory.getUserRepository().findActiveUsers();
            System.out.println("活跃用户数量: " + activeUsers.size());
            
            System.out.println("UserRepository测试通过");
        } catch (Exception e) {
            System.err.println("UserRepository测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试病虫害Repository
     */
    private void testPestRepository() {
        System.out.println("测试PestRepository...");
        
        try {
            // 测试查询功能
            List<Pest> allPests = repositoryFactory.getPestRepository().findAll();
            System.out.println("病虫害总数: " + allPests.size());
            
            // 测试按类别查询
            List<String> categories = repositoryFactory.getPestRepository().findAllCategories();
            System.out.println("病虫害类别数: " + categories.size());
            
            // 测试高风险病虫害查询
            List<Pest> highRiskPests = repositoryFactory.getPestRepository().findHighRiskPests();
            System.out.println("高风险病虫害数量: " + highRiskPests.size());
            
            System.out.println("PestRepository测试通过");
        } catch (Exception e) {
            System.err.println("PestRepository测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试药剂Repository
     */
    private void testPesticideRepository() {
        System.out.println("测试PesticideRepository...");
        
        try {
            // 测试查询功能
            List<Pesticide> allPesticides = repositoryFactory.getPesticideRepository().findAll();
            System.out.println("药剂总数: " + allPesticides.size());
            
            // 测试库存不足查询
            List<Pesticide> lowStockPesticides = repositoryFactory.getPesticideRepository().findLowStockPesticides();
            System.out.println("库存不足药剂数量: " + lowStockPesticides.size());
            
            // 测试即将过期查询
            List<Pesticide> expiringSoon = repositoryFactory.getPesticideRepository().findExpiringSoon(30);
            System.out.println("30天内过期药剂数量: " + expiringSoon.size());
            
            System.out.println("PesticideRepository测试通过");
        } catch (Exception e) {
            System.err.println("PesticideRepository测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试防治Repository
     */
    private void testTreatmentRepository() {
        System.out.println("测试TreatmentRepository...");
        
        try {
            // 测试方案查询
            List<TreatmentPlan> allPlans = repositoryFactory.getTreatmentRepository().findAllPlans();
            System.out.println("防治方案总数: " + allPlans.size());
            
            // 测试任务查询
            List<TreatmentTask> allTasks = repositoryFactory.getTreatmentRepository().findAllTasks();
            System.out.println("防治任务总数: " + allTasks.size());
            
            // 测试需要审批的方案
            List<TreatmentPlan> needingApproval = repositoryFactory.getTreatmentRepository().findPlansNeedingApproval();
            System.out.println("需要审批的方案数量: " + needingApproval.size());
            
            System.out.println("TreatmentRepository测试通过");
        } catch (Exception e) {
            System.err.println("TreatmentRepository测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试评估Repository
     */
    private void testEvaluationRepository() {
        System.out.println("测试EvaluationRepository...");
        
        try {
            // 测试查询功能
            List<EffectEvaluation> allEvaluations = repositoryFactory.getEvaluationRepository().findAll();
            System.out.println("效果评估总数: " + allEvaluations.size());
            
            // 测试高效果评估查询
            List<EffectEvaluation> highEffectiveness = repositoryFactory.getEvaluationRepository().findHighEffectivenessEvaluations();
            System.out.println("高效果评估数量: " + highEffectiveness.size());
            
            // 测试平均有效率计算
            Double averageRate = repositoryFactory.getEvaluationRepository().calculateAverageEffectivenessRate();
            System.out.println("平均有效率: " + averageRate + "%");
            
            System.out.println("EvaluationRepository测试通过");
        } catch (Exception e) {
            System.err.println("EvaluationRepository测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试预测Repository
     */
    private void testPredictionRepository() {
        System.out.println("测试PredictionRepository...");
        
        try {
            // 测试预测查询
            List<PestPrediction> allPredictions = repositoryFactory.getPredictionRepository().findAllPredictions();
            System.out.println("预测记录总数: " + allPredictions.size());
            
            // 测试预警查询
            List<PestAlert> allAlerts = repositoryFactory.getPredictionRepository().findAllAlerts();
            System.out.println("预警记录总数: " + allAlerts.size());
            
            // 测试高风险预测
            List<PestPrediction> highRiskPredictions = repositoryFactory.getPredictionRepository().findHighRiskPredictions();
            System.out.println("高风险预测数量: " + highRiskPredictions.size());
            
            System.out.println("PredictionRepository测试通过");
        } catch (Exception e) {
            System.err.println("PredictionRepository测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试森林资源Repository
     */
    private void testForestResourceRepository() {
        System.out.println("测试ForestResourceRepository...");
        
        try {
            // 测试查询功能
            List<ForestResource> allResources = repositoryFactory.getForestResourceRepository().findAll();
            System.out.println("森林资源总数: " + allResources.size());
            
            // 测试根区域查询
            List<ForestResource> rootAreas = repositoryFactory.getForestResourceRepository().findRootAreas();
            System.out.println("根区域数量: " + rootAreas.size());
            
            // 测试总面积计算
            Double totalArea = repositoryFactory.getForestResourceRepository().calculateTotalArea();
            System.out.println("总面积: " + totalArea + " 公顷");
            
            System.out.println("ForestResourceRepository测试通过");
        } catch (Exception e) {
            System.err.println("ForestResourceRepository测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试知识库Repository
     */
    private void testKnowledgeRepository() {
        System.out.println("测试KnowledgeRepository...");
        
        try {
            // 测试查询功能
            List<KnowledgeBase> allKnowledge = repositoryFactory.getKnowledgeRepository().findAll();
            System.out.println("知识库条目总数: " + allKnowledge.size());
            
            // 测试活跃知识查询
            List<KnowledgeBase> activeKnowledge = repositoryFactory.getKnowledgeRepository().findActiveKnowledge();
            System.out.println("活跃知识条目数量: " + activeKnowledge.size());
            
            // 测试热门知识查询
            List<KnowledgeBase> popularKnowledge = repositoryFactory.getKnowledgeRepository().findPopularKnowledge(5);
            System.out.println("热门知识条目数量: " + popularKnowledge.size());
            
            System.out.println("KnowledgeRepository测试通过");
        } catch (Exception e) {
            System.err.println("KnowledgeRepository测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 打印Repository统计信息
     */
    public void printRepositoryStatistics() {
        System.out.println("=== Repository统计信息 ===");
        
        RepositoryFactory.RepositoryStatistics stats = repositoryFactory.getStatistics();
        System.out.println(stats.toString());
        
        System.out.println("========================");
    }
    
    /**
     * 验证数据完整性
     */
    public boolean validateDataIntegrity() {
        System.out.println("验证数据完整性...");
        
        try {
            // 验证基本数据存在
            if (repositoryFactory.getUserRepository().count() == 0) {
                System.err.println("用户数据为空");
                return false;
            }
            
            if (repositoryFactory.getPestRepository().count() == 0) {
                System.err.println("病虫害数据为空");
                return false;
            }
            
            if (repositoryFactory.getPesticideRepository().count() == 0) {
                System.err.println("药剂数据为空");
                return false;
            }
            
            System.out.println("数据完整性验证通过");
            return true;
        } catch (Exception e) {
            System.err.println("数据完整性验证失败: " + e.getMessage());
            return false;
        }
    }
}