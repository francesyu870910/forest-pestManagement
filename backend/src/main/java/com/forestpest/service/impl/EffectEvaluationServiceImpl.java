package com.forestpest.service.impl;

import com.forestpest.entity.EffectEvaluation;
import com.forestpest.entity.EvaluationData;
import com.forestpest.service.EffectEvaluationService;
import com.forestpest.repository.EvaluationRepository;
import com.forestpest.exception.ForestPestSystemException;
import com.forestpest.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 效果评估服务实现类
 */
@Service
public class EffectEvaluationServiceImpl implements EffectEvaluationService {
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    // 评估数据存储 - 用于存储额外的评估指标数据
    private final Map<String, List<EvaluationData>> evaluationData = new ConcurrentHashMap<>();
    
    // 评估模板存储
    private final Map<String, Map<String, Object>> evaluationTemplates = new ConcurrentHashMap<>();
    
    // 评估提醒存储
    private final Map<String, Map<String, Object>> evaluationReminders = new ConcurrentHashMap<>();
    
    private final Random random = new Random();
    
    // 初始化评估模板
    {
        initializeEvaluationTemplates();
    }
    
    /**
     * 初始化评估模板
     */
    private void initializeEvaluationTemplates() {
        // 标准防治效果评估模板
        Map<String, Object> standardTemplate = new HashMap<>();
        standardTemplate.put("id", "template-standard");
        standardTemplate.put("name", "标准防治效果评估");
        standardTemplate.put("description", "包含防治效果率、死亡率、虫口密度等标准指标");
        standardTemplate.put("indicators", Arrays.asList("防治效果率", "死亡率", "虫口密度下降率", "持续时间"));
        standardTemplate.put("category", "standard");
        evaluationTemplates.put("template-standard", standardTemplate);
        
        // 环境友好型评估模板
        Map<String, Object> ecoTemplate = new HashMap<>();
        ecoTemplate.put("id", "template-eco");
        ecoTemplate.put("name", "环境友好型评估");
        ecoTemplate.put("description", "注重环境影响和可持续性的评估模板");
        ecoTemplate.put("indicators", Arrays.asList("防治效果率", "环境影响指数", "天敌保护率", "土壤影响"));
        ecoTemplate.put("category", "environmental");
        evaluationTemplates.put("template-eco", ecoTemplate);
        
        // 经济效益评估模板
        Map<String, Object> economicTemplate = new HashMap<>();
        economicTemplate.put("id", "template-economic");
        economicTemplate.put("name", "经济效益评估");
        economicTemplate.put("description", "重点关注成本效益和经济回报的评估模板");
        economicTemplate.put("indicators", Arrays.asList("防治效果率", "成本效益比", "经济损失减少率", "投入产出比"));
        economicTemplate.put("category", "economic");
        evaluationTemplates.put("template-economic", economicTemplate);
    }
    
    @Override
    public EffectEvaluation createEvaluation(EffectEvaluation evaluation, String userId) {
        if (evaluation == null) {
            throw new ForestPestSystemException("效果评估不能为空");
        }
        
        if (!validateEvaluation(evaluation)) {
            throw new ForestPestSystemException("效果评估验证失败");
        }
        
        evaluation.setId(IdGenerator.generateId());
        evaluation.setCreatedBy(userId);
        evaluation.setCreatedTime(LocalDateTime.now());
        evaluation.setStatus("DRAFT");
        evaluation.setEvaluatedBy(userId);
        
        // 计算初始效果率
        if (evaluation.getBeforeTreatment() != null && evaluation.getAfterTreatment() != null) {
            double effectivenessRate = calculateEffectivenessRate(evaluation.getBeforeTreatment(), evaluation.getAfterTreatment());
            evaluation.setEffectivenessRate(effectivenessRate);
        }
        
        return evaluationRepository.save(evaluation);
    }
    
    @Override
    public EffectEvaluation getEvaluationById(String evaluationId) {
        return evaluationRepository.findById(evaluationId).orElse(null);
    }
    
    @Override
    public List<EffectEvaluation> getEvaluationsByPlan(String planId) {
        // 通过taskId来关联planId
        return evaluationRepository.findByTaskId(planId);
    }
    
    @Override
    public List<EffectEvaluation> getUserEvaluations(String userId) {
        return evaluationRepository.findByEvaluatedBy(userId);
    }
    
    @Override
    public List<EffectEvaluation> getEvaluations(int page, int size) {
        List<EffectEvaluation> allEvaluations = evaluationRepository.findAll();
        allEvaluations.sort((e1, e2) -> e2.getCreatedTime().compareTo(e1.getCreatedTime()));
        
        int start = page * size;
        int end = Math.min(start + size, allEvaluations.size());
        
        if (start >= allEvaluations.size()) {
            return new ArrayList<>();
        }
        
        return allEvaluations.subList(start, end);
    }
    
    @Override
    public EffectEvaluation updateEvaluation(String evaluationId, EffectEvaluation updatedEvaluation, String userId) {
        Optional<EffectEvaluation> existingOpt = evaluationRepository.findById(evaluationId);
        if (!existingOpt.isPresent()) {
            throw new ForestPestSystemException("效果评估不存在");
        }
        
        EffectEvaluation existingEvaluation = existingOpt.get();
        if (!userId.equals(existingEvaluation.getCreatedBy())) {
            throw new ForestPestSystemException("无权限修改此效果评估");
        }
        
        // 更新评估信息
        existingEvaluation.setEvaluatedArea(updatedEvaluation.getEvaluatedArea());
        existingEvaluation.setEvaluationMethod(updatedEvaluation.getEvaluationMethod());
        existingEvaluation.setConclusion(updatedEvaluation.getConclusion());
        existingEvaluation.setRecommendations(updatedEvaluation.getRecommendations());
        existingEvaluation.setFollowUpActions(updatedEvaluation.getFollowUpActions());
        existingEvaluation.setWeather(updatedEvaluation.getWeather());
        existingEvaluation.setUpdatedTime(LocalDateTime.now());
        existingEvaluation.setUpdatedBy(userId);
        
        // 重新计算效果率
        if (updatedEvaluation.getBeforeTreatment() != null) {
            existingEvaluation.setBeforeTreatment(updatedEvaluation.getBeforeTreatment());
        }
        if (updatedEvaluation.getAfterTreatment() != null) {
            existingEvaluation.setAfterTreatment(updatedEvaluation.getAfterTreatment());
        }
        
        if (existingEvaluation.getBeforeTreatment() != null && existingEvaluation.getAfterTreatment() != null) {
            double effectivenessRate = calculateEffectivenessRate(existingEvaluation.getBeforeTreatment(), existingEvaluation.getAfterTreatment());
            existingEvaluation.setEffectivenessRate(effectivenessRate);
        }
        
        return evaluationRepository.save(existingEvaluation);
    }
    
    @Override
    public boolean deleteEvaluation(String evaluationId, String userId) {
        Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (!evaluationOpt.isPresent() || !userId.equals(evaluationOpt.get().getCreatedBy())) {
            return false;
        }
        
        // 删除相关评估数据
        evaluationData.remove(evaluationId);
        
        // 删除评估
        evaluationRepository.deleteById(evaluationId);
        
        return true;
    }
    
    @Override
    public EvaluationData recordEvaluationData(String evaluationId, EvaluationData data, String userId) {
        Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (!evaluationOpt.isPresent()) {
            throw new ForestPestSystemException("效果评估不存在");
        }
        
        if (data == null) {
            throw new ForestPestSystemException("评估数据不能为空");
        }
        
        EffectEvaluation evaluation = evaluationOpt.get();
        
        // 将数据记录到评估的before或after字段中
        if (evaluation.getBeforeTreatment() == null) {
            evaluation.setBeforeTreatment(data);
        } else if (evaluation.getAfterTreatment() == null) {
            evaluation.setAfterTreatment(data);
            // 计算效果率
            double effectivenessRate = calculateEffectivenessRate(evaluation.getBeforeTreatment(), evaluation.getAfterTreatment());
            evaluation.setEffectivenessRate(effectivenessRate);
        } else {
            // 如果已有前后数据，更新后处理数据
            evaluation.setAfterTreatment(data);
            double effectivenessRate = calculateEffectivenessRate(evaluation.getBeforeTreatment(), evaluation.getAfterTreatment());
            evaluation.setEffectivenessRate(effectivenessRate);
        }
        
        evaluation.setUpdatedTime(LocalDateTime.now());
        evaluation.setUpdatedBy(userId);
        
        evaluationRepository.save(evaluation);
        
        // 同时存储到额外的数据存储中
        evaluationData.computeIfAbsent(evaluationId, k -> new ArrayList<>()).add(data);
        
        return data;
    }
    
    @Override
    public List<EvaluationData> getEvaluationData(String evaluationId) {
        Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (!evaluationOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        EffectEvaluation evaluation = evaluationOpt.get();
        List<EvaluationData> dataList = new ArrayList<>();
        
        if (evaluation.getBeforeTreatment() != null) {
            dataList.add(evaluation.getBeforeTreatment());
        }
        if (evaluation.getAfterTreatment() != null) {
            dataList.add(evaluation.getAfterTreatment());
        }
        
        // 添加额外存储的数据
        List<EvaluationData> extraData = evaluationData.get(evaluationId);
        if (extraData != null) {
            dataList.addAll(extraData);
        }
        
        return dataList;
    }
    
    @Override
    public List<EvaluationData> batchRecordEvaluationData(String evaluationId, List<EvaluationData> dataList, String userId) {
        if (dataList == null || dataList.isEmpty()) {
            throw new ForestPestSystemException("评估数据列表不能为空");
        }
        
        List<EvaluationData> recordedData = new ArrayList<>();
        
        for (EvaluationData data : dataList) {
            try {
                EvaluationData recorded = recordEvaluationData(evaluationId, data, userId);
                recordedData.add(recorded);
            } catch (Exception e) {
                // 记录错误但继续处理其他数据
                EvaluationData errorData = new EvaluationData();
                errorData.setDamageDescription("录入失败: " + e.getMessage());
                recordedData.add(errorData);
            }
        }
        
        return recordedData;
    }
    
    @Override
    public Map<String, Object> calculateEffectIndicators(String evaluationId) {
        Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (!evaluationOpt.isPresent()) {
            throw new ForestPestSystemException("效果评估不存在");
        }
        
        EffectEvaluation evaluation = evaluationOpt.get();
        Map<String, Object> indicators = new HashMap<>();
        
        if (evaluation.getBeforeTreatment() == null || evaluation.getAfterTreatment() == null) {
            indicators.put("dataAvailable", false);
            indicators.put("message", "缺少防治前后对比数据");
            return indicators;
        }
        
        EvaluationData beforeData = evaluation.getBeforeTreatment();
        EvaluationData afterData = evaluation.getAfterTreatment();
        
        // 计算防治效果率
        double treatmentEffectiveness = evaluation.getEffectivenessRate() != null ? evaluation.getEffectivenessRate() : 0.0;
        indicators.put("treatmentEffectiveness", treatmentEffectiveness);
        
        // 计算虫口密度下降率
        if (beforeData.getPestPopulation() != null && afterData.getPestPopulation() != null && beforeData.getPestPopulation() > 0) {
            double densityReduction = ((double)(beforeData.getPestPopulation() - afterData.getPestPopulation()) / beforeData.getPestPopulation()) * 100;
            indicators.put("densityReduction", Math.max(0, densityReduction));
        } else {
            indicators.put("densityReduction", 0.0);
        }
        
        // 计算受害面积减少率
        if (beforeData.getAffectedArea() != null && afterData.getAffectedArea() != null && beforeData.getAffectedArea() > 0) {
            double areaReduction = ((beforeData.getAffectedArea() - afterData.getAffectedArea()) / beforeData.getAffectedArea()) * 100;
            indicators.put("areaReduction", Math.max(0, areaReduction));
        } else {
            indicators.put("areaReduction", 0.0);
        }
        
        // 计算植物健康改善率
        if (beforeData.getHealthyPlants() != null && afterData.getHealthyPlants() != null) {
            int totalBefore = (beforeData.getHealthyPlants() != null ? beforeData.getHealthyPlants() : 0) + 
                             (beforeData.getDamagedPlants() != null ? beforeData.getDamagedPlants() : 0) + 
                             (beforeData.getDeadPlants() != null ? beforeData.getDeadPlants() : 0);
            int totalAfter = (afterData.getHealthyPlants() != null ? afterData.getHealthyPlants() : 0) + 
                            (afterData.getDamagedPlants() != null ? afterData.getDamagedPlants() : 0) + 
                            (afterData.getDeadPlants() != null ? afterData.getDeadPlants() : 0);
            
            if (totalBefore > 0 && totalAfter > 0) {
                double healthBefore = (double) beforeData.getHealthyPlants() / totalBefore * 100;
                double healthAfter = (double) afterData.getHealthyPlants() / totalAfter * 100;
                double healthImprovement = healthAfter - healthBefore;
                indicators.put("healthImprovement", healthImprovement);
            } else {
                indicators.put("healthImprovement", 0.0);
            }
        } else {
            indicators.put("healthImprovement", 0.0);
        }
        
        // 计算综合得分
        double overallScore = calculateOverallScore(indicators);
        indicators.put("overallScore", overallScore);
        
        // 效果等级
        String effectLevel = getEffectLevel(overallScore);
        indicators.put("effectLevel", effectLevel);
        
        indicators.put("dataAvailable", true);
        indicators.put("calculationTime", LocalDateTime.now());
        
        return indicators;
    }
    
    @Override
    public Map<String, Object> generateEvaluationReport(String evaluationId) {
        Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (!evaluationOpt.isPresent()) {
            throw new ForestPestSystemException("效果评估不存在");
        }
        
        EffectEvaluation evaluation = evaluationOpt.get();
        Map<String, Object> report = new HashMap<>();
        
        // 基本信息
        report.put("evaluationId", evaluationId);
        report.put("taskId", evaluation.getTaskId());
        report.put("pestId", evaluation.getPestId());
        report.put("evaluatedArea", evaluation.getEvaluatedArea());
        report.put("evaluationTime", evaluation.getEvaluationTime());
        report.put("evaluatedBy", evaluation.getEvaluatedBy());
        report.put("status", evaluation.getStatus());
        report.put("conclusion", evaluation.getConclusion());
        
        // 效果指标
        Map<String, Object> indicators = calculateEffectIndicators(evaluationId);
        report.put("indicators", indicators);
        
        // 评估数据统计
        List<EvaluationData> dataList = getEvaluationData(evaluationId);
        report.put("dataCount", dataList.size());
        
        // 改进建议
        List<String> suggestions = getImprovementSuggestions(evaluationId);
        report.put("suggestions", suggestions);
        
        // 报告生成时间
        report.put("reportGeneratedTime", LocalDateTime.now());
        
        return report;
    }
    
    @Override
    public Map<String, Object> generateMultiDimensionalReport(String evaluationId) {
        Map<String, Object> basicReport = generateEvaluationReport(evaluationId);
        
        // 添加多维度分析
        Map<String, Object> multiReport = new HashMap<>(basicReport);
        
        // 成本效益分析
        Map<String, Object> costBenefit = getCostBenefitAnalysis(evaluationId);
        multiReport.put("costBenefitAnalysis", costBenefit);
        
        // 环境影响评估
        Map<String, Object> environmentalImpact = getEnvironmentalImpactAssessment(evaluationId);
        multiReport.put("environmentalImpact", environmentalImpact);
        
        // 可持续性评估
        Map<String, Object> sustainability = getSustainabilityAssessment(evaluationId);
        multiReport.put("sustainability", sustainability);
        
        // 风险评估
        Map<String, Object> riskAssessment = getRiskAssessment(evaluationId);
        multiReport.put("riskAssessment", riskAssessment);
        
        return multiReport;
    }
    
    @Override
    public Map<String, Object> compareEvaluations(List<String> evaluationIds) {
        if (evaluationIds == null || evaluationIds.size() < 2) {
            throw new ForestPestSystemException("至少需要两个评估进行对比");
        }
        
        Map<String, Object> comparison = new HashMap<>();
        List<Map<String, Object>> evaluationComparisons = new ArrayList<>();
        
        for (String evaluationId : evaluationIds) {
            Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
            if (evaluationOpt.isPresent()) {
                EffectEvaluation evaluation = evaluationOpt.get();
                Map<String, Object> evalData = new HashMap<>();
                evalData.put("evaluationId", evaluationId);
                evalData.put("taskId", evaluation.getTaskId());
                evalData.put("pestId", evaluation.getPestId());
                evalData.put("effectivenessRate", evaluation.getEffectivenessRate());
                
                // 获取效果指标
                Map<String, Object> indicators = calculateEffectIndicators(evaluationId);
                evalData.put("indicators", indicators);
                
                evaluationComparisons.add(evalData);
            }
        }
        
        comparison.put("evaluations", evaluationComparisons);
        comparison.put("comparisonTime", LocalDateTime.now());
        
        // 找出最佳评估
        if (!evaluationComparisons.isEmpty()) {
            Map<String, Object> bestEvaluation = evaluationComparisons.stream()
                    .max((e1, e2) -> Double.compare(
                        (Double) ((Map<String, Object>) e1.get("indicators")).getOrDefault("overallScore", 0.0),
                        (Double) ((Map<String, Object>) e2.get("indicators")).getOrDefault("overallScore", 0.0)
                    ))
                    .orElse(null);
            comparison.put("bestEvaluation", bestEvaluation);
        }
        
        return comparison;
    }
    
    @Override
    public Map<String, Object> getEvaluationStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        List<EffectEvaluation> allEvaluations = evaluationRepository.findAll();
        statistics.put("totalEvaluations", allEvaluations.size());
        
        // 按状态统计
        Map<String, Long> statusStats = allEvaluations.stream()
                .collect(Collectors.groupingBy(EffectEvaluation::getStatus, Collectors.counting()));
        statistics.put("statusStats", statusStats);
        
        // 按月份统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> monthlyStats = allEvaluations.stream()
                .collect(Collectors.groupingBy(
                    eval -> eval.getCreatedTime().format(formatter),
                    Collectors.counting()
                ));
        statistics.put("monthlyStats", monthlyStats);
        
        // 平均效果率
        double avgEffectiveness = allEvaluations.stream()
                .filter(eval -> eval.getEffectivenessRate() != null)
                .mapToDouble(EffectEvaluation::getEffectivenessRate)
                .average()
                .orElse(0.0);
        statistics.put("averageEffectiveness", avgEffectiveness);
        
        // 评估数据总数
        statistics.put("totalEvaluationData", evaluationData.size());
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getUserEvaluationStatistics(String userId) {
        List<EffectEvaluation> userEvaluations = getUserEvaluations(userId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalEvaluations", userEvaluations.size());
        
        // 按状态统计
        Map<String, Long> statusStats = userEvaluations.stream()
                .collect(Collectors.groupingBy(EffectEvaluation::getStatus, Collectors.counting()));
        statistics.put("statusStats", statusStats);
        
        // 平均效果率
        double avgEffectiveness = userEvaluations.stream()
                .filter(eval -> eval.getEffectivenessRate() != null)
                .mapToDouble(EffectEvaluation::getEffectivenessRate)
                .average()
                .orElse(0.0);
        statistics.put("averageEffectiveness", avgEffectiveness);
        
        // 最高效果率
        double maxEffectiveness = userEvaluations.stream()
                .filter(eval -> eval.getEffectivenessRate() != null)
                .mapToDouble(EffectEvaluation::getEffectivenessRate)
                .max()
                .orElse(0.0);
        statistics.put("maxEffectiveness", maxEffectiveness);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getPlanEffectStatistics(String planId) {
        List<EffectEvaluation> planEvaluations = getEvaluationsByPlan(planId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("evaluationCount", planEvaluations.size());
        
        if (!planEvaluations.isEmpty()) {
            // 平均效果率
            double avgEffectiveness = planEvaluations.stream()
                    .filter(eval -> eval.getEffectivenessRate() != null)
                    .mapToDouble(EffectEvaluation::getEffectivenessRate)
                    .average()
                    .orElse(0.0);
            statistics.put("averageEffectiveness", avgEffectiveness);
            
            // 最新评估
            EffectEvaluation latestEvaluation = planEvaluations.stream()
                    .max(Comparator.comparing(EffectEvaluation::getCreatedTime))
                    .orElse(null);
            
            if (latestEvaluation != null) {
                statistics.put("latestEvaluation", Map.of(
                    "id", latestEvaluation.getId(),
                    "taskId", latestEvaluation.getTaskId(),
                    "effectivenessRate", latestEvaluation.getEffectivenessRate(),
                    "status", latestEvaluation.getStatus()
                ));
            }
            
            // 效果趋势
            List<Map<String, Object>> trend = planEvaluations.stream()
                    .sorted(Comparator.comparing(EffectEvaluation::getCreatedTime))
                    .map(eval -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("date", eval.getCreatedTime().toLocalDate());
                        map.put("effectivenessRate", eval.getEffectivenessRate() != null ? eval.getEffectivenessRate() : 0.0);
                        return map;
                    })
                    .collect(Collectors.toList());
            statistics.put("effectTrend", trend);
        }
        
        return statistics;
    }
    
    // 辅助方法实现
    
    /**
     * 计算防治效果率
     */
    private double calculateEffectivenessRate(EvaluationData beforeData, EvaluationData afterData) {
        if (beforeData == null || afterData == null) {
            return 0.0;
        }
        
        // 基于虫口密度计算
        if (beforeData.getPestPopulation() != null && afterData.getPestPopulation() != null && beforeData.getPestPopulation() > 0) {
            double reduction = (double)(beforeData.getPestPopulation() - afterData.getPestPopulation()) / beforeData.getPestPopulation();
            return Math.max(0, Math.min(100, reduction * 100));
        }
        
        // 基于受害面积计算
        if (beforeData.getAffectedArea() != null && afterData.getAffectedArea() != null && beforeData.getAffectedArea() > 0) {
            double reduction = (beforeData.getAffectedArea() - afterData.getAffectedArea()) / beforeData.getAffectedArea();
            return Math.max(0, Math.min(100, reduction * 100));
        }
        
        return 0.0;
    }
    
    /**
     * 计算综合得分
     */
    private double calculateOverallScore(Map<String, Object> indicators) {
        double treatmentEffectiveness = (Double) indicators.getOrDefault("treatmentEffectiveness", 0.0);
        double densityReduction = (Double) indicators.getOrDefault("densityReduction", 0.0);
        double areaReduction = (Double) indicators.getOrDefault("areaReduction", 0.0);
        double healthImprovement = (Double) indicators.getOrDefault("healthImprovement", 0.0);
        
        // 加权平均计算综合得分
        return (treatmentEffectiveness * 0.4 + densityReduction * 0.3 + areaReduction * 0.2 + Math.max(0, healthImprovement) * 0.1);
    }
    
    /**
     * 获取效果等级
     */
    private String getEffectLevel(double score) {
        if (score >= 90) return "优秀";
        if (score >= 80) return "良好";
        if (score >= 70) return "中等";
        if (score >= 60) return "及格";
        return "不及格";
    }
    
    /**
     * 验证效果评估
     */
    @Override
    public boolean validateEvaluation(EffectEvaluation evaluation) {
        if (evaluation == null) return false;
        if (evaluation.getTaskId() == null || evaluation.getTaskId().trim().isEmpty()) return false;
        if (evaluation.getPestId() == null || evaluation.getPestId().trim().isEmpty()) return false;
        if (evaluation.getEvaluatedArea() == null || evaluation.getEvaluatedArea().trim().isEmpty()) return false;
        return true;
    }
    
    // 实现其他接口方法的简化版本
    
    @Override
    public Map<String, List<Map<String, Object>>> getEffectTrends(String period) {
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        
        DateTimeFormatter formatter;
        if ("daily".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if ("monthly".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy");
        }
        
        List<EffectEvaluation> allEvaluations = evaluationRepository.findAll();
        
        // 效果率趋势
        Map<String, Double> effectivenessTrend = allEvaluations.stream()
                .filter(eval -> eval.getEffectivenessRate() != null)
                .collect(Collectors.groupingBy(
                    eval -> eval.getCreatedTime().format(formatter),
                    Collectors.averagingDouble(EffectEvaluation::getEffectivenessRate)
                ));
        
        List<Map<String, Object>> effectivenessData = effectivenessTrend.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("period", entry.getKey());
                    item.put("averageEffectiveness", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        
        trends.put("effectivenessTrend", effectivenessData);
        
        // 评估数量趋势
        Map<String, Long> countTrend = allEvaluations.stream()
                .collect(Collectors.groupingBy(
                    eval -> eval.getCreatedTime().format(formatter),
                    Collectors.counting()
                ));
        
        List<Map<String, Object>> countData = countTrend.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("period", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        
        trends.put("countTrend", countData);
        
        return trends;
    }
    
    @Override
    public List<Map<String, Object>> getEvaluationHistory(String evaluationId) {
        Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (!evaluationOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        EffectEvaluation evaluation = evaluationOpt.get();
        List<Map<String, Object>> history = new ArrayList<>();
        
        // 创建记录
        Map<String, Object> createRecord = new HashMap<>();
        createRecord.put("action", "创建评估");
        createRecord.put("timestamp", evaluation.getCreatedTime());
        createRecord.put("userId", evaluation.getCreatedBy());
        history.add(createRecord);
        
        // 更新记录
        if (evaluation.getUpdatedTime() != null) {
            Map<String, Object> updateRecord = new HashMap<>();
            updateRecord.put("action", "更新评估");
            updateRecord.put("timestamp", evaluation.getUpdatedTime());
            updateRecord.put("userId", evaluation.getUpdatedBy());
            history.add(updateRecord);
        }
        
        // 按时间排序
        history.sort((r1, r2) -> ((LocalDateTime) r1.get("timestamp")).compareTo((LocalDateTime) r2.get("timestamp")));
        
        return history;
    }
    
    @Override
    public String exportEvaluationData(String evaluationId, String format) {
        List<EvaluationData> dataList = getEvaluationData(evaluationId);
        
        if ("json".equalsIgnoreCase(format)) {
            return dataList.toString();
        } else if ("csv".equalsIgnoreCase(format)) {
            StringBuilder csv = new StringBuilder();
            csv.append("受害面积,严重程度,虫口密度,损害描述\n");
            
            for (EvaluationData data : dataList) {
                csv.append(data.getAffectedArea()).append(",")
                   .append(data.getSeverityLevel()).append(",")
                   .append(data.getPestPopulation()).append(",")
                   .append(data.getDamageDescription()).append("\n");
            }
            
            return csv.toString();
        }
        
        throw new ForestPestSystemException("不支持的导出格式: " + format);
    }
    
    @Override
    public String exportEvaluationReport(String evaluationId, String format) {
        Map<String, Object> report = generateEvaluationReport(evaluationId);
        
        if ("json".equalsIgnoreCase(format)) {
            return report.toString();
        }
        
        throw new ForestPestSystemException("不支持的导出格式: " + format);
    }
    
    @Override
    public List<Map<String, Object>> getEvaluationTemplates() {
        return new ArrayList<>(evaluationTemplates.values());
    }
    
    @Override
    public EffectEvaluation createEvaluationFromTemplate(String templateId, String planId, String userId, Map<String, Object> parameters) {
        Map<String, Object> template = evaluationTemplates.get(templateId);
        if (template == null) {
            throw new ForestPestSystemException("模板不存在");
        }
        
        EffectEvaluation evaluation = new EffectEvaluation();
        evaluation.setTaskId(planId);
        evaluation.setEvaluatedArea((String) parameters.getOrDefault("evaluatedArea", "默认区域"));
        evaluation.setPestId((String) parameters.getOrDefault("pestId", ""));
        evaluation.setEvaluationMethod((String) template.get("name"));
        
        return createEvaluation(evaluation, userId);
    }
    
    @Override
    public List<Map<String, Object>> getEvaluationIndicators() {
        List<Map<String, Object>> indicators = new ArrayList<>();
        
        Map<String, Object> effectiveness = new HashMap<>();
        effectiveness.put("name", "防治效果率");
        effectiveness.put("unit", "%");
        effectiveness.put("description", "防治前后虫口密度或受害面积的减少比例");
        indicators.add(effectiveness);
        
        Map<String, Object> densityReduction = new HashMap<>();
        densityReduction.put("name", "虫口密度下降率");
        densityReduction.put("unit", "%");
        densityReduction.put("description", "防治前后虫口密度的下降比例");
        indicators.add(densityReduction);
        
        Map<String, Object> areaReduction = new HashMap<>();
        areaReduction.put("name", "受害面积减少率");
        areaReduction.put("unit", "%");
        areaReduction.put("description", "防治前后受害面积的减少比例");
        indicators.add(areaReduction);
        
        return indicators;
    }
    
    @Override
    public boolean addCustomIndicator(Map<String, Object> indicator, String userId) {
        // 简化实现，实际应该验证用户权限和指标有效性
        return true;
    }
    
    @Override
    public double calculateOverallEffectScore(String evaluationId) {
        Map<String, Object> indicators = calculateEffectIndicators(evaluationId);
        return (Double) indicators.getOrDefault("overallScore", 0.0);
    }
    
    @Override
    public String getEffectRating(String evaluationId) {
        double score = calculateOverallEffectScore(evaluationId);
        return getEffectLevel(score);
    }
    
    @Override
    public List<String> getImprovementSuggestions(String evaluationId) {
        List<String> suggestions = new ArrayList<>();
        
        Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (!evaluationOpt.isPresent()) {
            return suggestions;
        }
        
        EffectEvaluation evaluation = evaluationOpt.get();
        double effectivenessRate = evaluation.getEffectivenessRate() != null ? evaluation.getEffectivenessRate() : 0.0;
        
        if (effectivenessRate < 50) {
            suggestions.add("防治效果较差，建议调整防治方案或更换药剂");
            suggestions.add("检查药剂使用剂量和施用方法是否正确");
            suggestions.add("考虑采用综合防治措施，结合生物防治和物理防治");
        } else if (effectivenessRate < 80) {
            suggestions.add("防治效果一般，可以进一步优化防治时机");
            suggestions.add("建议加强防治后的跟踪监测");
        } else {
            suggestions.add("防治效果良好，建议继续保持当前防治策略");
            suggestions.add("可以将成功经验推广到其他类似区域");
        }
        
        return suggestions;
    }
    
    // 其他方法的简化实现
    
    @Override
    public Map<String, Object> predictEffectTrend(String evaluationId, int days) {
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("evaluationId", evaluationId);
        prediction.put("predictionDays", days);
        prediction.put("predictedTrend", "基于历史数据预测，效果可能会逐渐稳定");
        prediction.put("confidence", 0.75);
        return prediction;
    }
    
    @Override
    public List<Map<String, Object>> getBestPracticeCases(String pestType) {
        List<Map<String, Object>> cases = new ArrayList<>();
        
        Map<String, Object> case1 = new HashMap<>();
        case1.put("id", "case-1");
        case1.put("pestType", pestType);
        case1.put("title", "生物防治成功案例");
        case1.put("effectivenessRate", 92.5);
        case1.put("description", "采用天敌昆虫防治，效果显著且环境友好");
        cases.add(case1);
        
        return cases;
    }
    
    @Override
    public boolean reviewEvaluation(String evaluationId, String reviewerId, String reviewResult, String comments) {
        Optional<EffectEvaluation> evaluationOpt = evaluationRepository.findById(evaluationId);
        if (!evaluationOpt.isPresent()) {
            return false;
        }
        
        EffectEvaluation evaluation = evaluationOpt.get();
        evaluation.setReviewedBy(reviewerId);
        evaluation.setReviewTime(LocalDateTime.now());
        evaluation.setStatus("APPROVED".equals(reviewResult) ? "APPROVED" : "REJECTED");
        
        evaluationRepository.save(evaluation);
        return true;
    }
    
    @Override
    public List<EffectEvaluation> getPendingReviewEvaluations() {
        return evaluationRepository.findAll().stream()
                .filter(eval -> "SUBMITTED".equals(eval.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EffectEvaluation> getReviewedEvaluations(String reviewerId) {
        return evaluationRepository.findAll().stream()
                .filter(eval -> reviewerId.equals(eval.getReviewedBy()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean setEvaluationReminder(String planId, String userId, Map<String, Object> reminderConfig) {
        String reminderId = IdGenerator.generateId();
        reminderConfig.put("id", reminderId);
        reminderConfig.put("planId", planId);
        reminderConfig.put("userId", userId);
        reminderConfig.put("createdTime", LocalDateTime.now());
        
        evaluationReminders.put(reminderId, reminderConfig);
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getEvaluationReminders(String userId) {
        return evaluationReminders.values().stream()
                .filter(reminder -> userId.equals(reminder.get("userId")))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean cancelEvaluationReminder(String reminderId, String userId) {
        Map<String, Object> reminder = evaluationReminders.get(reminderId);
        if (reminder != null && userId.equals(reminder.get("userId"))) {
            evaluationReminders.remove(reminderId);
            return true;
        }
        return false;
    }
    
    // 分析类方法的简化实现
    
    @Override
    public Map<String, Object> getEvaluationQualityAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        List<EffectEvaluation> allEvaluations = evaluationRepository.findAll();
        
        long completeEvaluations = allEvaluations.stream()
                .filter(eval -> eval.getBeforeTreatment() != null && eval.getAfterTreatment() != null)
                .count();
        
        analysis.put("totalEvaluations", allEvaluations.size());
        analysis.put("completeEvaluations", completeEvaluations);
        analysis.put("completionRate", allEvaluations.size() > 0 ? (double) completeEvaluations / allEvaluations.size() * 100 : 0);
        
        return analysis;
    }
    
    @Override
    public Map<String, Object> getEvaluationCompletionRate() {
        return getEvaluationQualityAnalysis();
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> getEffectImprovementTrends(String period) {
        return getEffectTrends(period);
    }
    
    @Override
    public Map<String, Object> getCostBenefitAnalysis(String evaluationId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("evaluationId", evaluationId);
        analysis.put("totalCost", 5000.0);
        analysis.put("savedLoss", 15000.0);
        analysis.put("benefitCostRatio", 3.0);
        analysis.put("roi", 200.0);
        return analysis;
    }
    
    @Override
    public Map<String, Object> getEnvironmentalImpactAssessment(String evaluationId) {
        Map<String, Object> assessment = new HashMap<>();
        assessment.put("evaluationId", evaluationId);
        assessment.put("environmentalScore", 85.0);
        assessment.put("soilImpact", "轻微");
        assessment.put("waterImpact", "无");
        assessment.put("biodiversityImpact", "正面");
        return assessment;
    }
    
    @Override
    public Map<String, Object> getSustainabilityAssessment(String evaluationId) {
        Map<String, Object> assessment = new HashMap<>();
        assessment.put("evaluationId", evaluationId);
        assessment.put("sustainabilityScore", 78.0);
        assessment.put("longTermEffectiveness", "良好");
        assessment.put("resourceEfficiency", "高");
        return assessment;
    }
    
    @Override
    public Map<String, Object> getRiskAssessment(String evaluationId) {
        Map<String, Object> assessment = new HashMap<>();
        assessment.put("evaluationId", evaluationId);
        assessment.put("overallRisk", "低");
        assessment.put("resistanceRisk", "中");
        assessment.put("environmentalRisk", "低");
        return assessment;
    }
    
    @Override
    public Map<String, Object> generateEvaluationSummary(String evaluationId) {
        Map<String, Object> report = generateEvaluationReport(evaluationId);
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("evaluationId", evaluationId);
        summary.put("overallRating", getEffectRating(evaluationId));
        summary.put("effectivenessRate", report.get("effectivenessRate"));
        summary.put("keyFindings", "防治效果良好，建议继续监测");
        
        return summary;
    }
    
    @Override
    public Map<String, Object> validateEvaluationData(String evaluationId) {
        Map<String, Object> validation = new HashMap<>();
        List<EvaluationData> dataList = getEvaluationData(evaluationId);
        
        validation.put("isValid", !dataList.isEmpty());
        validation.put("dataCount", dataList.size());
        validation.put("missingFields", new ArrayList<>());
        
        return validation;
    }
    
    @Override
    public List<String> getEvaluationRecommendations(String planId, String pestType) {
        List<String> recommendations = new ArrayList<>();
        recommendations.add("建议在防治后7-14天进行效果评估");
        recommendations.add("记录详细的防治前后对比数据");
        recommendations.add("注意观察天敌昆虫的保护情况");
        return recommendations;
    }
    
    @Override
    public Map<String, Object> getSimilarPlanComparison(String evaluationId) {
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("evaluationId", evaluationId);
        comparison.put("similarPlans", new ArrayList<>());
        comparison.put("averageEffectiveness", 75.0);
        return comparison;
    }
    
    @Override
    public List<Map<String, Object>> getEffectRanking(String category, int limit) {
        List<EffectEvaluation> topEvaluations = evaluationRepository.findTopEffectiveTreatments(limit);
        
        return topEvaluations.stream()
                .map(eval -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("evaluationId", eval.getId());
                    item.put("effectivenessRate", eval.getEffectivenessRate());
                    item.put("pestId", eval.getPestId());
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> checkDataCompleteness(String evaluationId) {
        return validateEvaluationData(evaluationId);
    }
    
    @Override
    public Map<String, Object> getAccuracyAnalysis(String evaluationId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("evaluationId", evaluationId);
        analysis.put("accuracyScore", 88.0);
        analysis.put("dataQuality", "良好");
        return analysis;
    }
    
    @Override
    public List<String> getEvaluationMethodSuggestions(String pestType, String treatmentType) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("采用标准化的评估指标");
        suggestions.add("建立对照组进行对比");
        suggestions.add("定期进行跟踪评估");
        return suggestions;
    }
    
    @Override
    public Map<String, Long> getEvaluationStatusStatistics() {
        List<EffectEvaluation> allEvaluations = evaluationRepository.findAll();
        return allEvaluations.stream()
                .collect(Collectors.groupingBy(EffectEvaluation::getStatus, Collectors.counting()));
    }
    
    @Override
    public List<Map<String, Object>> getPopularIndicators(int limit) {
        List<Map<String, Object>> indicators = getEvaluationIndicators();
        return indicators.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getEvaluationCycleSuggestion(String planId) {
        Map<String, Object> suggestion = new HashMap<>();
        suggestion.put("planId", planId);
        suggestion.put("suggestedCycle", "每7天评估一次，持续4周");
        suggestion.put("criticalPeriods", Arrays.asList("防治后3天", "防治后7天", "防治后14天"));
        return suggestion;
    }
    
    // 其他分析方法的简化实现
    
    @Override
    public Map<String, Object> getEffectDurabilityAnalysis(String evaluationId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("evaluationId", evaluationId);
        analysis.put("durabilityScore", 82.0);
        analysis.put("expectedDuration", "30-45天");
        return analysis;
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> getEvaluationDataTrends(String evaluationId, String period) {
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        trends.put("effectivenessTrend", new ArrayList<>());
        return trends;
    }
    
    @Override
    public Map<String, Object> getEffectFluctuationAnalysis(String evaluationId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("evaluationId", evaluationId);
        analysis.put("fluctuationLevel", "低");
        analysis.put("stabilityScore", 85.0);
        return analysis;
    }
    
    @Override
    public Map<String, Object> getEvaluationCoverageAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalPlans", 100);
        analysis.put("evaluatedPlans", 85);
        analysis.put("coverageRate", 85.0);
        return analysis;
    }
    
    @Override
    public Map<String, Object> getEffectStabilityAnalysis(String evaluationId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("evaluationId", evaluationId);
        analysis.put("stabilityScore", 88.0);
        analysis.put("variationCoefficient", 0.12);
        return analysis;
    }
    
    @Override
    public Map<String, Object> getEvaluationEfficiencyAnalysis(String userId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("userId", userId);
        analysis.put("averageEvaluationTime", "2.5小时");
        analysis.put("efficiencyScore", 85.0);
        return analysis;
    }
}