package com.forestpest.repository;

import com.forestpest.entity.PestPrediction;
import com.forestpest.entity.PestAlert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 预测预警数据访问接口
 */
public interface PredictionRepository {
    
    // ========== PestPrediction 相关方法 ==========
    
    /**
     * 保存预测记录
     */
    PestPrediction savePrediction(PestPrediction prediction);
    
    /**
     * 根据ID查找预测记录
     */
    Optional<PestPrediction> findPredictionById(String id);
    
    /**
     * 查找所有预测记录
     */
    List<PestPrediction> findAllPredictions();
    
    /**
     * 根据病虫害ID查找预测记录
     */
    List<PestPrediction> findPredictionsByPestId(String pestId);
    
    /**
     * 根据风险等级查找预测记录
     */
    List<PestPrediction> findPredictionsByRiskLevel(String riskLevel);
    
    /**
     * 根据目标区域查找预测记录
     */
    List<PestPrediction> findPredictionsByTargetArea(String targetArea);
    
    /**
     * 根据预测日期范围查找预测记录
     */
    List<PestPrediction> findPredictionsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据预测者查找预测记录
     */
    List<PestPrediction> findPredictionsByPredictedBy(String predictedBy);
    
    /**
     * 获取高风险预测记录
     */
    List<PestPrediction> findHighRiskPredictions();
    
    /**
     * 获取最近的预测记录
     */
    List<PestPrediction> findRecentPredictions(int limit);
    
    /**
     * 删除预测记录
     */
    void deletePredictionById(String id);
    
    /**
     * 统计预测记录数量
     */
    long countPredictions();
    
    // ========== PestAlert 相关方法 ==========
    
    /**
     * 保存预警记录
     */
    PestAlert saveAlert(PestAlert alert);
    
    /**
     * 根据ID查找预警记录
     */
    Optional<PestAlert> findAlertById(String id);
    
    /**
     * 查找所有预警记录
     */
    List<PestAlert> findAllAlerts();
    
    /**
     * 根据预测ID查找预警记录
     */
    List<PestAlert> findAlertsByPredictionId(String predictionId);
    
    /**
     * 根据预警等级查找预警记录
     */
    List<PestAlert> findAlertsByLevel(String alertLevel);
    
    /**
     * 根据状态查找预警记录
     */
    List<PestAlert> findAlertsByStatus(String status);
    
    /**
     * 根据目标区域查找预警记录
     */
    List<PestAlert> findAlertsByTargetArea(String targetArea);
    
    /**
     * 根据创建时间范围查找预警记录
     */
    List<PestAlert> findAlertsByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取活跃的预警记录
     */
    List<PestAlert> findActiveAlerts();
    
    /**
     * 获取未处理的预警记录
     */
    List<PestAlert> findUnhandledAlerts();
    
    /**
     * 获取最近的预警记录
     */
    List<PestAlert> findRecentAlerts(int limit);
    
    /**
     * 删除预警记录
     */
    void deleteAlertById(String id);
    
    /**
     * 统计预警记录数量
     */
    long countAlerts();
    
    // ========== 批量操作方法 ==========
    
    /**
     * 批量保存预测记录
     */
    List<PestPrediction> saveAllPredictions(Iterable<PestPrediction> predictions);
    
    /**
     * 批量保存预警记录
     */
    List<PestAlert> saveAllAlerts(Iterable<PestAlert> alerts);
    
    /**
     * 清空所有预测记录
     */
    void deleteAllPredictions();
    
    /**
     * 清空所有预警记录
     */
    void deleteAllAlerts();
    
    /**
     * 清空所有数据
     */
    void deleteAll();
    
    // ========== 复合查询方法 ==========
    
    /**
     * 根据关键词搜索预测记录
     */
    List<PestPrediction> searchPredictionsByKeyword(String keyword);
    
    /**
     * 根据关键词搜索预警记录
     */
    List<PestAlert> searchAlertsByKeyword(String keyword);
    
    /**
     * 获取需要关注的预测（高风险且最近）
     */
    List<PestPrediction> findPredictionsNeedingAttention();
    
    /**
     * 获取紧急预警（高等级且未处理）
     */
    List<PestAlert> findUrgentAlerts();
    
    /**
     * 统计各风险等级预测数量
     */
    java.util.Map<String, Long> countPredictionsByRiskLevel();
    
    /**
     * 统计各预警等级数量
     */
    java.util.Map<String, Long> countAlertsByLevel();
    
    /**
     * 统计各区域预警数量
     */
    java.util.Map<String, Long> countAlertsByArea();
}