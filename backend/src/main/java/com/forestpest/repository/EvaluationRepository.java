package com.forestpest.repository;

import com.forestpest.entity.EffectEvaluation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 效果评估数据访问接口
 */
public interface EvaluationRepository extends BaseRepository<EffectEvaluation, String> {
    
    /**
     * 根据任务ID查找评估记录
     */
    List<EffectEvaluation> findByTaskId(String taskId);
    
    /**
     * 根据病虫害ID查找评估记录
     */
    List<EffectEvaluation> findByPestId(String pestId);
    
    /**
     * 根据评估人查找评估记录
     */
    List<EffectEvaluation> findByEvaluatedBy(String evaluatedBy);
    
    /**
     * 根据评估时间范围查找评估记录
     */
    List<EffectEvaluation> findByEvaluationTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据有效率范围查找评估记录
     */
    List<EffectEvaluation> findByEffectivenessRateBetween(Double minRate, Double maxRate);
    
    /**
     * 查找高效果评估记录（有效率>=80%）
     */
    List<EffectEvaluation> findHighEffectivenessEvaluations();
    
    /**
     * 查找低效果评估记录（有效率<50%）
     */
    List<EffectEvaluation> findLowEffectivenessEvaluations();
    
    /**
     * 根据目标区域查找评估记录
     */
    List<EffectEvaluation> findByTargetArea(String targetArea);
    
    /**
     * 根据使用药剂查找评估记录
     */
    List<EffectEvaluation> findByUsedPesticide(String pesticideId);
    
    /**
     * 获取最近的评估记录
     */
    List<EffectEvaluation> findRecentEvaluations(int limit);
    
    /**
     * 根据关键词搜索评估记录
     */
    List<EffectEvaluation> searchByKeyword(String keyword);
    
    /**
     * 统计平均有效率
     */
    Double calculateAverageEffectivenessRate();
    
    /**
     * 根据病虫害统计平均有效率
     */
    java.util.Map<String, Double> calculateAverageEffectivenessRateByPest();
    
    /**
     * 根据药剂统计平均有效率
     */
    java.util.Map<String, Double> calculateAverageEffectivenessRateByPesticide();
    
    /**
     * 统计评估数量按月份分组
     */
    java.util.Map<String, Long> countEvaluationsByMonth();
    
    /**
     * 获取效果最好的防治方案
     */
    List<EffectEvaluation> findTopEffectiveTreatments(int limit);
    
    /**
     * 获取需要改进的防治方案
     */
    List<EffectEvaluation> findTreatmentsNeedingImprovement();
}