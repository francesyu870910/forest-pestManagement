package com.forestpest.service;

import com.forestpest.entity.EffectEvaluation;
import com.forestpest.entity.EvaluationData;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 效果评估服务接口
 */
public interface EffectEvaluationService {
    
    /**
     * 创建效果评估
     */
    EffectEvaluation createEvaluation(EffectEvaluation evaluation, String userId);
    
    /**
     * 根据ID获取效果评估
     */
    EffectEvaluation getEvaluationById(String evaluationId);
    
    /**
     * 获取防治方案的效果评估列表
     */
    List<EffectEvaluation> getEvaluationsByPlan(String planId);
    
    /**
     * 获取用户创建的效果评估列表
     */
    List<EffectEvaluation> getUserEvaluations(String userId);
    
    /**
     * 获取效果评估列表（分页）
     */
    List<EffectEvaluation> getEvaluations(int page, int size);
    
    /**
     * 更新效果评估
     */
    EffectEvaluation updateEvaluation(String evaluationId, EffectEvaluation updatedEvaluation, String userId);
    
    /**
     * 删除效果评估
     */
    boolean deleteEvaluation(String evaluationId, String userId);
    
    /**
     * 录入评估数据
     */
    EvaluationData recordEvaluationData(String evaluationId, EvaluationData data, String userId);
    
    /**
     * 获取评估数据列表
     */
    List<EvaluationData> getEvaluationData(String evaluationId);
    
    /**
     * 批量录入评估数据
     */
    List<EvaluationData> batchRecordEvaluationData(String evaluationId, List<EvaluationData> dataList, String userId);
    
    /**
     * 计算防治效果指标
     */
    Map<String, Object> calculateEffectIndicators(String evaluationId);
    
    /**
     * 生成效果评估报告
     */
    Map<String, Object> generateEvaluationReport(String evaluationId);
    
    /**
     * 生成多维度评估报告
     */
    Map<String, Object> generateMultiDimensionalReport(String evaluationId);
    
    /**
     * 对比不同方案的效果
     */
    Map<String, Object> compareEvaluations(List<String> evaluationIds);
    
    /**
     * 获取效果评估统计
     */
    Map<String, Object> getEvaluationStatistics();
    
    /**
     * 获取用户效果评估统计
     */
    Map<String, Object> getUserEvaluationStatistics(String userId);
    
    /**
     * 获取防治方案效果统计
     */
    Map<String, Object> getPlanEffectStatistics(String planId);
    
    /**
     * 获取效果趋势分析
     */
    Map<String, List<Map<String, Object>>> getEffectTrends(String period);
    
    /**
     * 获取效果评估历史
     */
    List<Map<String, Object>> getEvaluationHistory(String evaluationId);
    
    /**
     * 导出效果评估数据
     */
    String exportEvaluationData(String evaluationId, String format);
    
    /**
     * 导出评估报告
     */
    String exportEvaluationReport(String evaluationId, String format);
    
    /**
     * 获取评估模板
     */
    List<Map<String, Object>> getEvaluationTemplates();
    
    /**
     * 根据模板创建评估
     */
    EffectEvaluation createEvaluationFromTemplate(String templateId, String planId, String userId, Map<String, Object> parameters);
    
    /**
     * 获取评估指标定义
     */
    List<Map<String, Object>> getEvaluationIndicators();
    
    /**
     * 自定义评估指标
     */
    boolean addCustomIndicator(Map<String, Object> indicator, String userId);
    
    /**
     * 计算综合效果得分
     */
    double calculateOverallEffectScore(String evaluationId);
    
    /**
     * 获取效果等级评定
     */
    String getEffectRating(String evaluationId);
    
    /**
     * 获取改进建议
     */
    List<String> getImprovementSuggestions(String evaluationId);
    
    /**
     * 预测效果趋势
     */
    Map<String, Object> predictEffectTrend(String evaluationId, int days);
    
    /**
     * 获取最佳实践案例
     */
    List<Map<String, Object>> getBestPracticeCases(String pestType);
    
    /**
     * 效果评估审核
     */
    boolean reviewEvaluation(String evaluationId, String reviewerId, String reviewResult, String comments);
    
    /**
     * 获取待审核的评估列表
     */
    List<EffectEvaluation> getPendingReviewEvaluations();
    
    /**
     * 获取已审核的评估列表
     */
    List<EffectEvaluation> getReviewedEvaluations(String reviewerId);
    
    /**
     * 设置评估提醒
     */
    boolean setEvaluationReminder(String planId, String userId, Map<String, Object> reminderConfig);
    
    /**
     * 获取评估提醒列表
     */
    List<Map<String, Object>> getEvaluationReminders(String userId);
    
    /**
     * 取消评估提醒
     */
    boolean cancelEvaluationReminder(String reminderId, String userId);
    
    /**
     * 获取效果评估质量分析
     */
    Map<String, Object> getEvaluationQualityAnalysis();
    
    /**
     * 获取评估完成率统计
     */
    Map<String, Object> getEvaluationCompletionRate();
    
    /**
     * 获取效果改善趋势
     */
    Map<String, List<Map<String, Object>>> getEffectImprovementTrends(String period);
    
    /**
     * 获取成本效益分析
     */
    Map<String, Object> getCostBenefitAnalysis(String evaluationId);
    
    /**
     * 获取环境影响评估
     */
    Map<String, Object> getEnvironmentalImpactAssessment(String evaluationId);
    
    /**
     * 获取可持续性评估
     */
    Map<String, Object> getSustainabilityAssessment(String evaluationId);
    
    /**
     * 获取风险评估
     */
    Map<String, Object> getRiskAssessment(String evaluationId);
    
    /**
     * 生成效果评估摘要
     */
    Map<String, Object> generateEvaluationSummary(String evaluationId);
    
    /**
     * 获取评估数据验证结果
     */
    Map<String, Object> validateEvaluationData(String evaluationId);
    
    /**
     * 获取评估建议
     */
    List<String> getEvaluationRecommendations(String planId, String pestType);
    
    /**
     * 获取同类方案效果对比
     */
    Map<String, Object> getSimilarPlanComparison(String evaluationId);
    
    /**
     * 获取效果评估排行榜
     */
    List<Map<String, Object>> getEffectRanking(String category, int limit);
    
    /**
     * 获取评估数据完整性检查
     */
    Map<String, Object> checkDataCompleteness(String evaluationId);
    
    /**
     * 获取效果评估准确性分析
     */
    Map<String, Object> getAccuracyAnalysis(String evaluationId);
    
    /**
     * 获取评估方法建议
     */
    List<String> getEvaluationMethodSuggestions(String pestType, String treatmentType);
    
    /**
     * 验证效果评估
     */
    boolean validateEvaluation(EffectEvaluation evaluation);
    
    /**
     * 获取评估状态统计
     */
    Map<String, Long> getEvaluationStatusStatistics();
    
    /**
     * 获取热门评估指标
     */
    List<Map<String, Object>> getPopularIndicators(int limit);
    
    /**
     * 获取评估周期建议
     */
    Map<String, Object> getEvaluationCycleSuggestion(String planId);
    
    /**
     * 获取效果持续性分析
     */
    Map<String, Object> getEffectDurabilityAnalysis(String evaluationId);
    
    /**
     * 获取评估数据趋势
     */
    Map<String, List<Map<String, Object>>> getEvaluationDataTrends(String evaluationId, String period);
    
    /**
     * 获取效果波动分析
     */
    Map<String, Object> getEffectFluctuationAnalysis(String evaluationId);
    
    /**
     * 获取评估覆盖率分析
     */
    Map<String, Object> getEvaluationCoverageAnalysis();
    
    /**
     * 获取效果稳定性分析
     */
    Map<String, Object> getEffectStabilityAnalysis(String evaluationId);
    
    /**
     * 获取评估效率分析
     */
    Map<String, Object> getEvaluationEfficiencyAnalysis(String userId);
}