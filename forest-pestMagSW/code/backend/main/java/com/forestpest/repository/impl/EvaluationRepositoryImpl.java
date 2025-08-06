package com.forestpest.repository.impl;

import com.forestpest.entity.EffectEvaluation;
import com.forestpest.repository.EvaluationRepository;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * 效果评估数据访问实现类
 */
@Repository
public class EvaluationRepositoryImpl implements EvaluationRepository {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Override
    public EffectEvaluation save(EffectEvaluation evaluation) {
        if (evaluation.getId() == null) {
            evaluation.setId(dataStorage.generateId());
        }
        evaluation.setUpdatedTime(LocalDateTime.now());
        dataStorage.getEvaluationStorage().save(evaluation);
        return evaluation;
    }
    
    @Override
    public Optional<EffectEvaluation> findById(String id) {
        return dataStorage.getEvaluationStorage().findById(id);
    }
    
    @Override
    public List<EffectEvaluation> findAll() {
        return dataStorage.getEvaluationStorage().findAll();
    }
    
    @Override
    public void deleteById(String id) {
        dataStorage.getEvaluationStorage().deleteById(id);
    }
    
    @Override
    public void delete(EffectEvaluation evaluation) {
        if (evaluation != null && evaluation.getId() != null) {
            deleteById(evaluation.getId());
        }
    }
    
    @Override
    public boolean existsById(String id) {
        return dataStorage.getEvaluationStorage().findById(id).isPresent();
    }
    
    @Override
    public long count() {
        return dataStorage.getEvaluationStorage().count();
    }
    
    @Override
    public List<EffectEvaluation> saveAll(Iterable<EffectEvaluation> evaluations) {
        List<EffectEvaluation> savedEvaluations = new java.util.ArrayList<>();
        for (EffectEvaluation evaluation : evaluations) {
            savedEvaluations.add(save(evaluation));
        }
        return savedEvaluations;
    }
    
    @Override
    public void deleteAll(Iterable<EffectEvaluation> evaluations) {
        for (EffectEvaluation evaluation : evaluations) {
            delete(evaluation);
        }
    }
    
    @Override
    public void deleteAll() {
        dataStorage.getEvaluationStorage().clear();
    }
    
    @Override
    public List<EffectEvaluation> findByTaskId(String taskId) {
        return dataStorage.getEvaluationStorage().findByTaskId(taskId);
    }
    
    @Override
    public List<EffectEvaluation> findByPestId(String pestId) {
        return dataStorage.getEvaluationStorage().findByPestId(pestId);
    }
    
    @Override
    public List<EffectEvaluation> findByEvaluatedBy(String evaluatedBy) {
        return dataStorage.getEvaluationStorage().findByEvaluatedBy(evaluatedBy);
    }
    
    @Override
    public List<EffectEvaluation> findByEvaluationTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return findAll().stream()
                .filter(evaluation -> evaluation.getCreatedTime() != null &&
                                    !evaluation.getCreatedTime().isBefore(startTime) &&
                                    !evaluation.getCreatedTime().isAfter(endTime))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EffectEvaluation> findByEffectivenessRateBetween(Double minRate, Double maxRate) {
        return dataStorage.getEvaluationStorage().findByEffectivenessRateRange(minRate, maxRate);
    }
    
    @Override
    public List<EffectEvaluation> findHighEffectivenessEvaluations() {
        return findByEffectivenessRateBetween(80.0, 100.0);
    }
    
    @Override
    public List<EffectEvaluation> findLowEffectivenessEvaluations() {
        return findByEffectivenessRateBetween(0.0, 50.0);
    }
    
    @Override
    public List<EffectEvaluation> findByTargetArea(String targetArea) {
        return findAll().stream()
                .filter(evaluation -> targetArea.equals(evaluation.getEvaluatedArea()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EffectEvaluation> findByUsedPesticide(String pesticideId) {
        return findAll().stream()
                .filter(evaluation -> pesticideId.equals(evaluation.getPestId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EffectEvaluation> findRecentEvaluations(int limit) {
        return findAll().stream()
                .sorted(Comparator.comparing(EffectEvaluation::getCreatedTime, 
                                           Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EffectEvaluation> searchByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return findAll().stream()
                .filter(evaluation ->
                    (evaluation.getEvaluatedArea() != null && evaluation.getEvaluatedArea().toLowerCase().contains(lowerKeyword)) ||
                    (evaluation.getConclusion() != null && evaluation.getConclusion().toLowerCase().contains(lowerKeyword)) ||
                    (evaluation.getRecommendations() != null && evaluation.getRecommendations().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public Double calculateAverageEffectivenessRate() {
        List<EffectEvaluation> evaluations = findAll();
        if (evaluations.isEmpty()) {
            return 0.0;
        }
        
        return evaluations.stream()
                .filter(evaluation -> evaluation.getEffectivenessRate() != null)
                .mapToDouble(EffectEvaluation::getEffectivenessRate)
                .average()
                .orElse(0.0);
    }
    
    @Override
    public Map<String, Double> calculateAverageEffectivenessRateByPest() {
        return findAll().stream()
                .filter(evaluation -> evaluation.getPestId() != null && evaluation.getEffectivenessRate() != null)
                .collect(Collectors.groupingBy(
                    EffectEvaluation::getPestId,
                    Collectors.averagingDouble(EffectEvaluation::getEffectivenessRate)
                ));
    }
    
    @Override
    public Map<String, Double> calculateAverageEffectivenessRateByPesticide() {
        return findAll().stream()
                .filter(evaluation -> evaluation.getPestId() != null && 
                                    evaluation.getEffectivenessRate() != null)
                .collect(Collectors.groupingBy(
                    EffectEvaluation::getPestId,
                    Collectors.averagingDouble(EffectEvaluation::getEffectivenessRate)
                ));
    }
    
    @Override
    public Map<String, Long> countEvaluationsByMonth() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return findAll().stream()
                .filter(evaluation -> evaluation.getCreatedTime() != null)
                .collect(Collectors.groupingBy(
                    evaluation -> evaluation.getCreatedTime().format(formatter),
                    Collectors.counting()
                ));
    }
    
    @Override
    public List<EffectEvaluation> findTopEffectiveTreatments(int limit) {
        return findAll().stream()
                .filter(evaluation -> evaluation.getEffectivenessRate() != null)
                .sorted(Comparator.comparing(EffectEvaluation::getEffectivenessRate, 
                                           Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EffectEvaluation> findTreatmentsNeedingImprovement() {
        return findLowEffectivenessEvaluations();
    }
    
    // 内部辅助类
    private static class AbstractMap {
        public static class SimpleEntry<K, V> implements Map.Entry<K, V> {
            private final K key;
            private V value;
            
            public SimpleEntry(K key, V value) {
                this.key = key;
                this.value = value;
            }
            
            @Override
            public K getKey() {
                return key;
            }
            
            @Override
            public V getValue() {
                return value;
            }
            
            @Override
            public V setValue(V value) {
                V oldValue = this.value;
                this.value = value;
                return oldValue;
            }
        }
    }
}