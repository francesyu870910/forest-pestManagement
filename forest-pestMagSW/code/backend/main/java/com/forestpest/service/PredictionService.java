package com.forestpest.service;

import com.forestpest.entity.PestPrediction;
import com.forestpest.entity.PestAlert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 预测预警服务接口
 */
public interface PredictionService {
    
    // ========== 预测管理方法 ==========
    
    /**
     * 创建病虫害预测
     */
    PestPrediction createPrediction(PestPrediction prediction, String userId);
    
    /**
     * 根据ID获取预测
     */
    PestPrediction getPredictionById(String predictionId);
    
    /**
     * 获取所有预测列表
     */
    List<PestPrediction> getAllPredictions();
    
    /**
     * 获取用户创建的预测列表
     */
    List<PestPrediction> getUserPredictions(String userId);
    
    /**
     * 获取预测列表（分页）
     */
    List<PestPrediction> getPredictions(int page, int size);
    
    /**
     * 更新预测
     */
    PestPrediction updatePrediction(String predictionId, PestPrediction updatedPrediction, String userId);
    
    /**
     * 删除预测
     */
    boolean deletePrediction(String predictionId, String userId);
    
    /**
     * 根据病虫害ID获取预测
     */
    List<PestPrediction> getPredictionsByPestId(String pestId);
    
    /**
     * 根据目标区域获取预测
     */
    List<PestPrediction> getPredictionsByArea(String targetArea);
    
    /**
     * 根据风险等级获取预测
     */
    List<PestPrediction> getPredictionsByRiskLevel(String riskLevel);
    
    /**
     * 根据日期范围获取预测
     */
    List<PestPrediction> getPredictionsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取高风险预测
     */
    List<PestPrediction> getHighRiskPredictions();
    
    /**
     * 获取最近的预测
     */
    List<PestPrediction> getRecentPredictions(int limit);
    
    /**
     * 搜索预测
     */
    List<PestPrediction> searchPredictions(String keyword);
    
    // ========== 预警管理方法 ==========
    
    /**
     * 创建预警
     */
    PestAlert createAlert(PestAlert alert, String userId);
    
    /**
     * 根据预测创建预警
     */
    PestAlert createAlertFromPrediction(String predictionId, String userId, Map<String, Object> alertConfig);
    
    /**
     * 根据ID获取预警
     */
    PestAlert getAlertById(String alertId);
    
    /**
     * 获取所有预警列表
     */
    List<PestAlert> getAllAlerts();
    
    /**
     * 获取用户创建的预警列表
     */
    List<PestAlert> getUserAlerts(String userId);
    
    /**
     * 获取预警列表（分页）
     */
    List<PestAlert> getAlerts(int page, int size);
    
    /**
     * 更新预警
     */
    PestAlert updateAlert(String alertId, PestAlert updatedAlert, String userId);
    
    /**
     * 删除预警
     */
    boolean deleteAlert(String alertId, String userId);
    
    /**
     * 根据预测ID获取预警
     */
    List<PestAlert> getAlertsByPredictionId(String predictionId);
    
    /**
     * 根据预警等级获取预警
     */
    List<PestAlert> getAlertsByLevel(String alertLevel);
    
    /**
     * 根据状态获取预警
     */
    List<PestAlert> getAlertsByStatus(String status);
    
    /**
     * 根据目标区域获取预警
     */
    List<PestAlert> getAlertsByArea(String targetArea);
    
    /**
     * 获取活跃的预警
     */
    List<PestAlert> getActiveAlerts();
    
    /**
     * 获取未处理的预警
     */
    List<PestAlert> getUnhandledAlerts();
    
    /**
     * 获取紧急预警
     */
    List<PestAlert> getUrgentAlerts();
    
    /**
     * 获取最近的预警
     */
    List<PestAlert> getRecentAlerts(int limit);
    
    /**
     * 搜索预警
     */
    List<PestAlert> searchAlerts(String keyword);
    
    /**
     * 确认预警
     */
    boolean acknowledgeAlert(String alertId, String userId);
    
    /**
     * 处理预警
     */
    boolean handleAlert(String alertId, String userId, String handleResult);
    
    // ========== 预测算法方法 ==========
    
    /**
     * 基于历史数据生成预测
     */
    PestPrediction generatePredictionFromHistory(String pestId, String targetArea, Map<String, Object> parameters);
    
    /**
     * 基于天气数据生成预测
     */
    PestPrediction generatePredictionFromWeather(String pestId, String targetArea, Map<String, Object> weatherData);
    
    /**
     * 基于环境因子生成预测
     */
    PestPrediction generatePredictionFromEnvironment(String pestId, String targetArea, Map<String, Object> environmentData);
    
    /**
     * 综合预测模型
     */
    PestPrediction generateComprehensivePrediction(String pestId, String targetArea, Map<String, Object> allData);
    
    /**
     * 计算风险等级
     */
    String calculateRiskLevel(double probability, Map<String, Object> factors);
    
    /**
     * 计算发生概率
     */
    double calculateProbability(String pestId, String targetArea, Map<String, Object> factors);
    
    /**
     * 评估预测准确性
     */
    Map<String, Object> evaluatePredictionAccuracy(String predictionId, Map<String, Object> actualData);
    
    /**
     * 更新预测模型
     */
    boolean updatePredictionModel(String modelId, Map<String, Object> trainingData);
    
    // ========== 预警触发方法 ==========
    
    /**
     * 检查预警触发条件
     */
    List<PestAlert> checkAlertTriggers();
    
    /**
     * 自动触发预警
     */
    List<PestAlert> triggerAlertsAutomatically();
    
    /**
     * 根据预测触发预警
     */
    PestAlert triggerAlertFromPrediction(String predictionId, String triggerReason);
    
    /**
     * 批量触发预警
     */
    List<PestAlert> batchTriggerAlerts(List<String> predictionIds, String triggerReason);
    
    /**
     * 设置预警规则
     */
    boolean setAlertRule(String ruleId, Map<String, Object> ruleConfig);
    
    /**
     * 获取预警规则
     */
    List<Map<String, Object>> getAlertRules();
    
    /**
     * 删除预警规则
     */
    boolean deleteAlertRule(String ruleId);
    
    // ========== 通知发送方法 ==========
    
    /**
     * 发送预警通知
     */
    boolean sendAlertNotification(String alertId, List<String> recipients);
    
    /**
     * 批量发送预警通知
     */
    Map<String, Boolean> batchSendAlertNotifications(List<String> alertIds, List<String> recipients);
    
    /**
     * 发送邮件通知
     */
    boolean sendEmailNotification(String alertId, List<String> emails);
    
    /**
     * 发送短信通知
     */
    boolean sendSmsNotification(String alertId, List<String> phoneNumbers);
    
    /**
     * 发送系统内通知
     */
    boolean sendSystemNotification(String alertId, List<String> userIds);
    
    /**
     * 获取通知历史
     */
    List<Map<String, Object>> getNotificationHistory(String alertId);
    
    /**
     * 设置通知偏好
     */
    boolean setNotificationPreference(String userId, Map<String, Object> preferences);
    
    /**
     * 获取通知偏好
     */
    Map<String, Object> getNotificationPreference(String userId);
    
    // ========== 统计分析方法 ==========
    
    /**
     * 获取预测统计
     */
    Map<String, Object> getPredictionStatistics();
    
    /**
     * 获取用户预测统计
     */
    Map<String, Object> getUserPredictionStatistics(String userId);
    
    /**
     * 获取预警统计
     */
    Map<String, Object> getAlertStatistics();
    
    /**
     * 获取用户预警统计
     */
    Map<String, Object> getUserAlertStatistics(String userId);
    
    /**
     * 获取预测准确性统计
     */
    Map<String, Object> getPredictionAccuracyStatistics();
    
    /**
     * 获取预警响应统计
     */
    Map<String, Object> getAlertResponseStatistics();
    
    /**
     * 获取风险等级分布
     */
    Map<String, Long> getRiskLevelDistribution();
    
    /**
     * 获取预警等级分布
     */
    Map<String, Long> getAlertLevelDistribution();
    
    /**
     * 获取区域预警分布
     */
    Map<String, Long> getAreaAlertDistribution();
    
    /**
     * 获取预测趋势
     */
    Map<String, List<Map<String, Object>>> getPredictionTrends(String period);
    
    /**
     * 获取预警趋势
     */
    Map<String, List<Map<String, Object>>> getAlertTrends(String period);
    
    /**
     * 获取预测效果分析
     */
    Map<String, Object> getPredictionEffectivenessAnalysis();
    
    /**
     * 获取预警效果分析
     */
    Map<String, Object> getAlertEffectivenessAnalysis();
    
    // ========== 模型管理方法 ==========
    
    /**
     * 获取预测模型列表
     */
    List<Map<String, Object>> getPredictionModels();
    
    /**
     * 获取预测模型详情
     */
    Map<String, Object> getPredictionModelDetails(String modelId);
    
    /**
     * 训练预测模型
     */
    boolean trainPredictionModel(String modelId, Map<String, Object> trainingData);
    
    /**
     * 评估预测模型
     */
    Map<String, Object> evaluatePredictionModel(String modelId, Map<String, Object> testData);
    
    /**
     * 部署预测模型
     */
    boolean deployPredictionModel(String modelId);
    
    /**
     * 获取模型性能指标
     */
    Map<String, Object> getModelPerformanceMetrics(String modelId);
    
    /**
     * 比较预测模型
     */
    Map<String, Object> comparePredictionModels(List<String> modelIds);
    
    // ========== 数据导出方法 ==========
    
    /**
     * 导出预测数据
     */
    String exportPredictionData(String format, Map<String, Object> filters);
    
    /**
     * 导出预警数据
     */
    String exportAlertData(String format, Map<String, Object> filters);
    
    /**
     * 导出预测报告
     */
    String exportPredictionReport(String predictionId, String format);
    
    /**
     * 导出预警报告
     */
    String exportAlertReport(String alertId, String format);
    
    /**
     * 导出统计报告
     */
    String exportStatisticsReport(String reportType, String format);
    
    // ========== 配置管理方法 ==========
    
    /**
     * 获取预测配置
     */
    Map<String, Object> getPredictionConfig();
    
    /**
     * 更新预测配置
     */
    boolean updatePredictionConfig(Map<String, Object> config);
    
    /**
     * 获取预警配置
     */
    Map<String, Object> getAlertConfig();
    
    /**
     * 更新预警配置
     */
    boolean updateAlertConfig(Map<String, Object> config);
    
    /**
     * 重置配置为默认值
     */
    boolean resetConfigToDefault(String configType);
    
    /**
     * 验证配置有效性
     */
    Map<String, Object> validateConfig(Map<String, Object> config);
    
    // ========== 系统维护方法 ==========
    
    /**
     * 清理过期预测
     */
    int cleanupExpiredPredictions();
    
    /**
     * 清理过期预警
     */
    int cleanupExpiredAlerts();
    
    /**
     * 归档历史数据
     */
    boolean archiveHistoricalData(LocalDate beforeDate);
    
    /**
     * 系统健康检查
     */
    Map<String, Object> performHealthCheck();
    
    /**
     * 获取系统状态
     */
    Map<String, Object> getSystemStatus();
    
    /**
     * 重新初始化系统
     */
    boolean reinitializeSystem();
}