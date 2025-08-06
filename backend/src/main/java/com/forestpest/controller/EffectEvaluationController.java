package com.forestpest.controller;

import com.forestpest.common.ApiResponse;
import com.forestpest.entity.EffectEvaluation;
import com.forestpest.entity.EvaluationData;
import com.forestpest.service.EffectEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * 效果评估控制
 */
@RestController
@RequestMapping("/api/evaluation")
@CrossOrigin(origins = "*")
public class EffectEvaluationController extends BaseController {
    
    @Autowired
    private EffectEvaluationService effectEvaluationService;
    
    /**
     * 创建效果评估
     */
    @PostMapping
    public ApiResponse<EffectEvaluation> createEvaluation(
            @RequestBody @Valid CreateEvaluationRequest request) {
        
        try {
            EffectEvaluation evaluation = effectEvaluationService.createEvaluation(
                request.getEvaluation(), request.getUserId());
            return success("创建效果评估成功", evaluation);
        } catch (Exception e) {
            logger.error("创建效果评估失败", e);
            return error("创建效果评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取效果评估详情
     */
    @GetMapping("/{evaluationId}")
    public ApiResponse<EffectEvaluation> getEvaluation(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            EffectEvaluation evaluation = effectEvaluationService.getEvaluationById(evaluationId);
            if (evaluation == null) {
                return error("效果评估不存在");
            }
            return success(evaluation);
        } catch (Exception e) {
            logger.error("获取效果评估失败", e);
            return error("获取效果评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的效果评估
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<EffectEvaluation>> getUserEvaluations(
            @PathVariable @NotBlank String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            List<EffectEvaluation> evaluations;
            if (page > 0 || size != 20) {
                evaluations = effectEvaluationService.getEvaluations(page, size);
                // 过滤用户的评估
                evaluations = evaluations.stream()
                    .filter(eval -> userId.equals(eval.getEvaluatedBy()))
                    .collect(java.util.stream.Collectors.toList());
            } else {
                evaluations = effectEvaluationService.getUserEvaluations(userId);
            }
            return success(evaluations);
        } catch (Exception e) {
            logger.error("获取用户效果评估列表失败", e);
            return error("获取用户效果评估列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取防治方案的效果评估列表
     */
    @GetMapping("/plan/{planId}")
    public ApiResponse<List<EffectEvaluation>> getPlanEvaluations(
            @PathVariable @NotBlank String planId) {
        
        try {
            List<EffectEvaluation> evaluations = effectEvaluationService.getEvaluationsByPlan(planId);
            return success(evaluations);
        } catch (Exception e) {
            logger.error("获取方案效果评估列表失败", e);
            return error("获取方案效果评估列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新效果评估
     */
    @PutMapping("/{evaluationId}")
    public ApiResponse<EffectEvaluation> updateEvaluation(
            @PathVariable @NotBlank String evaluationId,
            @RequestBody @Valid UpdateEvaluationRequest request) {
        
        try {
            EffectEvaluation evaluation = effectEvaluationService.updateEvaluation(
                evaluationId, request.getEvaluation(), request.getUserId());
            return success("更新效果评估成功", evaluation);
        } catch (Exception e) {
            logger.error("更新效果评估失败", e);
            return error("更新效果评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除效果评估
     */
    @DeleteMapping("/{evaluationId}")
    public ApiResponse<Boolean> deleteEvaluation(
            @PathVariable @NotBlank String evaluationId,
            @RequestParam @NotBlank String userId) {
        
        try {
            boolean deleted = effectEvaluationService.deleteEvaluation(evaluationId, userId);
            if (!deleted) {
                return error("删除失败，评估不存在或无权限");
            }
            return success("删除效果评估成功", true);
        } catch (Exception e) {
            logger.error("删除效果评估失败", e);
            return error("删除效果评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 录入评估数据
     */
    @PostMapping("/{evaluationId}/data")
    public ApiResponse<EvaluationData> recordEvaluationData(
            @PathVariable @NotBlank String evaluationId,
            @RequestBody @Valid RecordDataRequest request) {
        
        try {
            EvaluationData data = effectEvaluationService.recordEvaluationData(
                evaluationId, request.getData(), request.getUserId());
            return success("录入评估数据成功", data);
        } catch (Exception e) {
            logger.error("录入评估数据失败", e);
            return error("录入评估数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量录入评估数据
     */
    @PostMapping("/{evaluationId}/data/batch")
    public ApiResponse<List<EvaluationData>> batchRecordEvaluationData(
            @PathVariable @NotBlank String evaluationId,
            @RequestBody @Valid BatchRecordDataRequest request) {
        
        try {
            List<EvaluationData> dataList = effectEvaluationService.batchRecordEvaluationData(
                evaluationId, request.getDataList(), request.getUserId());
            return success("批量录入评估数据成功", dataList);
        } catch (Exception e) {
            logger.error("批量录入评估数据失败", e);
            return error("批量录入评估数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取评估数据列表
     */
    @GetMapping("/{evaluationId}/data")
    public ApiResponse<List<EvaluationData>> getEvaluationData(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            List<EvaluationData> dataList = effectEvaluationService.getEvaluationData(evaluationId);
            return success(dataList);
        } catch (Exception e) {
            logger.error("获取评估数据失败", e);
            return error("获取评估数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 计算防治效果指标
     */
    @GetMapping("/{evaluationId}/indicators")
    public ApiResponse<Map<String, Object>> calculateEffectIndicators(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            Map<String, Object> indicators = effectEvaluationService.calculateEffectIndicators(evaluationId);
            return success(indicators);
        } catch (Exception e) {
            logger.error("计算效果指标失败", e);
            return error("计算效果指标失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成效果评估报告
     */
    @GetMapping("/{evaluationId}/report")
    public ApiResponse<Map<String, Object>> generateEvaluationReport(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            Map<String, Object> report = effectEvaluationService.generateEvaluationReport(evaluationId);
            return success(report);
        } catch (Exception e) {
            logger.error("生成评估报告失败", e);
            return error("生成评估报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成多维度评估报告
     */
    @GetMapping("/{evaluationId}/report/multi-dimensional")
    public ApiResponse<Map<String, Object>> generateMultiDimensionalReport(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            Map<String, Object> report = effectEvaluationService.generateMultiDimensionalReport(evaluationId);
            return success(report);
        } catch (Exception e) {
            logger.error("生成多维度评估报告失败", e);
            return error("生成多维度评估报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 对比不同方案的效果
     */
    @PostMapping("/compare")
    public ApiResponse<Map<String, Object>> compareEvaluations(
            @RequestBody @Valid CompareEvaluationsRequest request) {
        
        try {
            Map<String, Object> comparison = effectEvaluationService.compareEvaluations(request.getEvaluationIds());
            return success(comparison);
        } catch (Exception e) {
            logger.error("对比评估失败", e);
            return error("对比评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取效果评估统计
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getEvaluationStatistics() {
        
        try {
            Map<String, Object> statistics = effectEvaluationService.getEvaluationStatistics();
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取评估统计失败", e);
            return error("获取评估统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户效果评估统计
     */
    @GetMapping("/statistics/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserEvaluationStatistics(
            @PathVariable @NotBlank String userId) {
        
        try {
            Map<String, Object> statistics = effectEvaluationService.getUserEvaluationStatistics(userId);
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取用户评估统计失败", e);
            return error("获取用户评估统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取防治方案效果统计
     */
    @GetMapping("/statistics/plan/{planId}")
    public ApiResponse<Map<String, Object>> getPlanEffectStatistics(
            @PathVariable @NotBlank String planId) {
        
        try {
            Map<String, Object> statistics = effectEvaluationService.getPlanEffectStatistics(planId);
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取方案效果统计失败", e);
            return error("获取方案效果统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取效果趋势分析
     */
    @GetMapping("/trends")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getEffectTrends(
            @RequestParam(defaultValue = "monthly") String period) {
        
        try {
            Map<String, List<Map<String, Object>>> trends = effectEvaluationService.getEffectTrends(period);
            return success(trends);
        } catch (Exception e) {
            logger.error("获取效果趋势失败", e);
            return error("获取效果趋势失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取效果评估历史
     */
    @GetMapping("/{evaluationId}/history")
    public ApiResponse<List<Map<String, Object>>> getEvaluationHistory(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            List<Map<String, Object>> history = effectEvaluationService.getEvaluationHistory(evaluationId);
            return success(history);
        } catch (Exception e) {
            logger.error("获取评估历史失败", e);
            return error("获取评估历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出效果评估数据
     */
    @GetMapping("/{evaluationId}/export/data")
    public ApiResponse<String> exportEvaluationData(
            @PathVariable @NotBlank String evaluationId,
            @RequestParam(defaultValue = "json") String format) {
        
        try {
            String exportData = effectEvaluationService.exportEvaluationData(evaluationId, format);
            return success(exportData);
        } catch (Exception e) {
            logger.error("导出评估数据失败", e);
            return error("导出评估数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出评估报告
     */
    @GetMapping("/{evaluationId}/export/report")
    public ApiResponse<String> exportEvaluationReport(
            @PathVariable @NotBlank String evaluationId,
            @RequestParam(defaultValue = "json") String format) {
        
        try {
            String exportData = effectEvaluationService.exportEvaluationReport(evaluationId, format);
            return success(exportData);
        } catch (Exception e) {
            logger.error("导出评估报告失败", e);
            return error("导出评估报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取评估模板
     */
    @GetMapping("/templates")
    public ApiResponse<List<Map<String, Object>>> getEvaluationTemplates() {
        
        try {
            List<Map<String, Object>> templates = effectEvaluationService.getEvaluationTemplates();
            return success(templates);
        } catch (Exception e) {
            logger.error("获取评估模板失败", e);
            return error("获取评估模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据模板创建评估
     */
    @PostMapping("/from-template")
    public ApiResponse<EffectEvaluation> createEvaluationFromTemplate(
            @RequestBody @Valid CreateFromTemplateRequest request) {
        
        try {
            EffectEvaluation evaluation = effectEvaluationService.createEvaluationFromTemplate(
                request.getTemplateId(), request.getPlanId(), 
                request.getUserId(), request.getParameters());
            return success("根据模板创建评估成功", evaluation);
        } catch (Exception e) {
            logger.error("根据模板创建评估失败", e);
            return error("根据模板创建评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取评估指标定义
     */
    @GetMapping("/indicators")
    public ApiResponse<List<Map<String, Object>>> getEvaluationIndicators() {
        
        try {
            List<Map<String, Object>> indicators = effectEvaluationService.getEvaluationIndicators();
            return success(indicators);
        } catch (Exception e) {
            logger.error("获取评估指标失败", e);
            return error("获取评估指标失败: " + e.getMessage());
        }
    }
    
    /**
     * 自定义评估指标
     */
    @PostMapping("/indicators/custom")
    public ApiResponse<Boolean> addCustomIndicator(
            @RequestBody @Valid AddCustomIndicatorRequest request) {
        
        try {
            boolean success = effectEvaluationService.addCustomIndicator(
                request.getIndicator(), request.getUserId());
            return success("添加自定义指标成功", success);
        } catch (Exception e) {
            logger.error("添加自定义指标失败", e);
            return error("添加自定义指标失败: " + e.getMessage());
        }
    }
    
    /**
     * 计算综合效果得分
     */
    @GetMapping("/{evaluationId}/score")
    public ApiResponse<Double> calculateOverallEffectScore(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            double score = effectEvaluationService.calculateOverallEffectScore(evaluationId);
            return success(score);
        } catch (Exception e) {
            logger.error("计算综合效果得分失败", e);
            return error("计算综合效果得分失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取效果等级评定
     */
    @GetMapping("/{evaluationId}/rating")
    public ApiResponse<String> getEffectRating(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            String rating = effectEvaluationService.getEffectRating(evaluationId);
            return success(rating);
        } catch (Exception e) {
            logger.error("获取效果等级评定失败", e);
            return error("获取效果等级评定失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取改进建议
     */
    @GetMapping("/{evaluationId}/suggestions")
    public ApiResponse<List<String>> getImprovementSuggestions(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            List<String> suggestions = effectEvaluationService.getImprovementSuggestions(evaluationId);
            return success(suggestions);
        } catch (Exception e) {
            logger.error("获取改进建议失败", e);
            return error("获取改进建议失败: " + e.getMessage());
        }
    }
    
    /**
     * 预测效果趋势
     */
    @GetMapping("/{evaluationId}/predict")
    public ApiResponse<Map<String, Object>> predictEffectTrend(
            @PathVariable @NotBlank String evaluationId,
            @RequestParam(defaultValue = "30") int days) {
        
        try {
            Map<String, Object> prediction = effectEvaluationService.predictEffectTrend(evaluationId, days);
            return success(prediction);
        } catch (Exception e) {
            logger.error("预测效果趋势失败", e);
            return error("预测效果趋势失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最佳实践案例
     */
    @GetMapping("/best-practices")
    public ApiResponse<List<Map<String, Object>>> getBestPracticeCases(
            @RequestParam @NotBlank String pestType) {
        
        try {
            List<Map<String, Object>> cases = effectEvaluationService.getBestPracticeCases(pestType);
            return success(cases);
        } catch (Exception e) {
            logger.error("获取最佳实践案例失败", e);
            return error("获取最佳实践案例失败: " + e.getMessage());
        }
    }
    
    /**
     * 效果评估审核
     */
    @PostMapping("/{evaluationId}/review")
    public ApiResponse<Boolean> reviewEvaluation(
            @PathVariable @NotBlank String evaluationId,
            @RequestBody @Valid ReviewEvaluationRequest request) {
        
        try {
            boolean success = effectEvaluationService.reviewEvaluation(
                evaluationId, request.getReviewerId(), 
                request.getReviewResult(), request.getComments());
            return success("审核评估成功", success);
        } catch (Exception e) {
            logger.error("审核评估失败", e);
            return error("审核评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取待审核的评估列表
     */
    @GetMapping("/pending-review")
    public ApiResponse<List<EffectEvaluation>> getPendingReviewEvaluations() {
        
        try {
            List<EffectEvaluation> evaluations = effectEvaluationService.getPendingReviewEvaluations();
            return success(evaluations);
        } catch (Exception e) {
            logger.error("获取待审核评估列表失败", e);
            return error("获取待审核评估列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取已审核的评估列表
     */
    @GetMapping("/reviewed/{reviewerId}")
    public ApiResponse<List<EffectEvaluation>> getReviewedEvaluations(
            @PathVariable @NotBlank String reviewerId) {
        
        try {
            List<EffectEvaluation> evaluations = effectEvaluationService.getReviewedEvaluations(reviewerId);
            return success(evaluations);
        } catch (Exception e) {
            logger.error("获取已审核评估列表失败", e);
            return error("获取已审核评估列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置评估提醒
     */
    @PostMapping("/reminder")
    public ApiResponse<Boolean> setEvaluationReminder(
            @RequestBody @Valid SetReminderRequest request) {
        
        try {
            boolean success = effectEvaluationService.setEvaluationReminder(
                request.getPlanId(), request.getUserId(), request.getReminderConfig());
            return success("设置评估提醒成功", success);
        } catch (Exception e) {
            logger.error("设置评估提醒失败", e);
            return error("设置评估提醒失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取评估提醒列表
     */
    @GetMapping("/reminder/{userId}")
    public ApiResponse<List<Map<String, Object>>> getEvaluationReminders(
            @PathVariable @NotBlank String userId) {
        
        try {
            List<Map<String, Object>> reminders = effectEvaluationService.getEvaluationReminders(userId);
            return success(reminders);
        } catch (Exception e) {
            logger.error("获取评估提醒列表失败", e);
            return error("获取评估提醒列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消评估提醒
     */
    @DeleteMapping("/reminder/{reminderId}")
    public ApiResponse<Boolean> cancelEvaluationReminder(
            @PathVariable @NotBlank String reminderId,
            @RequestParam @NotBlank String userId) {
        
        try {
            boolean success = effectEvaluationService.cancelEvaluationReminder(reminderId, userId);
            return success("取消评估提醒成功", success);
        } catch (Exception e) {
            logger.error("取消评估提醒失败", e);
            return error("取消评估提醒失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取效果评估质量分析
     */
    @GetMapping("/quality-analysis")
    public ApiResponse<Map<String, Object>> getEvaluationQualityAnalysis() {
        
        try {
            Map<String, Object> analysis = effectEvaluationService.getEvaluationQualityAnalysis();
            return success(analysis);
        } catch (Exception e) {
            logger.error("获取评估质量分析失败", e);
            return error("获取评估质量分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取评估完成率统计
     */
    @GetMapping("/completion-rate")
    public ApiResponse<Map<String, Object>> getEvaluationCompletionRate() {
        
        try {
            Map<String, Object> completionRate = effectEvaluationService.getEvaluationCompletionRate();
            return success(completionRate);
        } catch (Exception e) {
            logger.error("获取评估完成率统计失败", e);
            return error("获取评估完成率统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取效果改善趋势
     */
    @GetMapping("/improvement-trends")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getEffectImprovementTrends(
            @RequestParam(defaultValue = "monthly") String period) {
        
        try {
            Map<String, List<Map<String, Object>>> trends = effectEvaluationService.getEffectImprovementTrends(period);
            return success(trends);
        } catch (Exception e) {
            logger.error("获取效果改善趋势失败", e);
            return error("获取效果改善趋势失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取成本效益分析
     */
    @GetMapping("/{evaluationId}/cost-benefit")
    public ApiResponse<Map<String, Object>> getCostBenefitAnalysis(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            Map<String, Object> analysis = effectEvaluationService.getCostBenefitAnalysis(evaluationId);
            return success(analysis);
        } catch (Exception e) {
            logger.error("获取成本效益分析失败", e);
            return error("获取成本效益分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取环境影响评估
     */
    @GetMapping("/{evaluationId}/environmental-impact")
    public ApiResponse<Map<String, Object>> getEnvironmentalImpactAssessment(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            Map<String, Object> assessment = effectEvaluationService.getEnvironmentalImpactAssessment(evaluationId);
            return success(assessment);
        } catch (Exception e) {
            logger.error("获取环境影响评估失败", e);
            return error("获取环境影响评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取可持续性评估
     */
    @GetMapping("/{evaluationId}/sustainability")
    public ApiResponse<Map<String, Object>> getSustainabilityAssessment(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            Map<String, Object> assessment = effectEvaluationService.getSustainabilityAssessment(evaluationId);
            return success(assessment);
        } catch (Exception e) {
            logger.error("获取可持续性评估失败", e);
            return error("获取可持续性评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取风险评估
     */
    @GetMapping("/{evaluationId}/risk")
    public ApiResponse<Map<String, Object>> getRiskAssessment(
            @PathVariable @NotBlank String evaluationId) {
        
        try {
            Map<String, Object> assessment = effectEvaluationService.getRiskAssessment(evaluationId);
            return success(assessment);
        } catch (Exception e) {
            logger.error("获取风险评估失败", e);
            return error("获取风险评估失败: " + e.getMessage());
        }
    }
    
    // ========== 请求对象类 ==========
    
    /**
     * 创建评估请求
     */
    public static class CreateEvaluationRequest {
        @Valid
        private EffectEvaluation evaluation;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public EffectEvaluation getEvaluation() {
            return evaluation;
        }
        
        public void setEvaluation(EffectEvaluation evaluation) {
            this.evaluation = evaluation;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 更新评估请求
     */
    public static class UpdateEvaluationRequest {
        @Valid
        private EffectEvaluation evaluation;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public EffectEvaluation getEvaluation() {
            return evaluation;
        }
        
        public void setEvaluation(EffectEvaluation evaluation) {
            this.evaluation = evaluation;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 录入数据请求
     */
    public static class RecordDataRequest {
        @Valid
        private EvaluationData data;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public EvaluationData getData() {
            return data;
        }
        
        public void setData(EvaluationData data) {
            this.data = data;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 批量录入数据请求
     */
    public static class BatchRecordDataRequest {
        @NotEmpty(message = "数据列表不能为空")
        private List<EvaluationData> dataList;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public List<EvaluationData> getDataList() {
            return dataList;
        }
        
        public void setDataList(List<EvaluationData> dataList) {
            this.dataList = dataList;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 对比评估请求
     */
    public static class CompareEvaluationsRequest {
        @NotEmpty(message = "评估ID列表不能为空")
        private List<String> evaluationIds;
        
        // Getters and Setters
        public List<String> getEvaluationIds() {
            return evaluationIds;
        }
        
        public void setEvaluationIds(List<String> evaluationIds) {
            this.evaluationIds = evaluationIds;
        }
    }
    
    /**
     * 从模板创建请�?
     */
    public static class CreateFromTemplateRequest {
        @NotBlank(message = "模板ID不能为空")
        private String templateId;
        
        @NotBlank(message = "方案ID不能为空")
        private String planId;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private Map<String, Object> parameters;
        
        // Getters and Setters
        public String getTemplateId() {
            return templateId;
        }
        
        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
        
        public String getPlanId() {
            return planId;
        }
        
        public void setPlanId(String planId) {
            this.planId = planId;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
        
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
    
    /**
     * 添加自定义指标请求
     */
    public static class AddCustomIndicatorRequest {
        private Map<String, Object> indicator;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Map<String, Object> getIndicator() {
            return indicator;
        }
        
        public void setIndicator(Map<String, Object> indicator) {
            this.indicator = indicator;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 审核评估请求
     */
    public static class ReviewEvaluationRequest {
        @NotBlank(message = "审核人ID不能为空")
        private String reviewerId;
        
        @NotBlank(message = "审核结果不能为空")
        private String reviewResult;
        
        private String comments;
        
        // Getters and Setters
        public String getReviewerId() {
            return reviewerId;
        }
        
        public void setReviewerId(String reviewerId) {
            this.reviewerId = reviewerId;
        }
        
        public String getReviewResult() {
            return reviewResult;
        }
        
        public void setReviewResult(String reviewResult) {
            this.reviewResult = reviewResult;
        }
        
        public String getComments() {
            return comments;
        }
        
        public void setComments(String comments) {
            this.comments = comments;
        }
    }
    
    /**
     * 设置提醒请求
     */
    public static class SetReminderRequest {
        @NotBlank(message = "方案ID不能为空")
        private String planId;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private Map<String, Object> reminderConfig;
        
        // Getters and Setters
        public String getPlanId() {
            return planId;
        }
        
        public void setPlanId(String planId) {
            this.planId = planId;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public Map<String, Object> getReminderConfig() {
            return reminderConfig;
        }
        
        public void setReminderConfig(Map<String, Object> reminderConfig) {
            this.reminderConfig = reminderConfig;
        }
    }
}
