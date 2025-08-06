package com.forestpest.data.storage;

import com.forestpest.entity.EffectEvaluation;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 效果评估数据存储
 */
@Component
public class EvaluationStorage {
    
    private final Map<String, EffectEvaluation> evaluations = new ConcurrentHashMap<>();
    private final Map<String, List<String>> taskIdIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> pestIdIndex = new ConcurrentHashMap<>();
    
    public void save(EffectEvaluation evaluation) {
        evaluations.put(evaluation.getId(), evaluation);
        
        // 更新任务ID索引
        if (evaluation.getTaskId() != null) {
            taskIdIndex.computeIfAbsent(evaluation.getTaskId(), k -> new ArrayList<>()).add(evaluation.getId());
        }
        
        // 更新病虫害ID索引
        if (evaluation.getPestId() != null) {
            pestIdIndex.computeIfAbsent(evaluation.getPestId(), k -> new ArrayList<>()).add(evaluation.getId());
        }
    }
    
    public Optional<EffectEvaluation> findById(String id) {
        return Optional.ofNullable(evaluations.get(id));
    }
    
    public List<EffectEvaluation> findAll() {
        return new ArrayList<>(evaluations.values());
    }
    
    public List<EffectEvaluation> findByTaskId(String taskId) {
        List<String> ids = taskIdIndex.get(taskId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(evaluations::get)
                .filter(evaluation -> evaluation != null)
                .collect(Collectors.toList());
    }
    
    public List<EffectEvaluation> findByPestId(String pestId) {
        List<String> ids = pestIdIndex.get(pestId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(evaluations::get)
                .filter(evaluation -> evaluation != null)
                .collect(Collectors.toList());
    }
    
    public List<EffectEvaluation> findByEvaluatedBy(String evaluatedBy) {
        return evaluations.values().stream()
                .filter(evaluation -> evaluatedBy.equals(evaluation.getEvaluatedBy()))
                .collect(Collectors.toList());
    }
    
    public List<EffectEvaluation> findByEffectivenessRateRange(double minRate, double maxRate) {
        return evaluations.values().stream()
                .filter(evaluation -> evaluation.getEffectivenessRate() != null &&
                                    evaluation.getEffectivenessRate() >= minRate &&
                                    evaluation.getEffectivenessRate() <= maxRate)
                .collect(Collectors.toList());
    }
    
    public void deleteById(String id) {
        EffectEvaluation evaluation = evaluations.remove(id);
        if (evaluation != null) {
            // 清理索引
            if (evaluation.getTaskId() != null) {
                List<String> taskIds = taskIdIndex.get(evaluation.getTaskId());
                if (taskIds != null) {
                    taskIds.remove(id);
                }
            }
            
            if (evaluation.getPestId() != null) {
                List<String> pestIds = pestIdIndex.get(evaluation.getPestId());
                if (pestIds != null) {
                    pestIds.remove(id);
                }
            }
        }
    }
    
    public void clear() {
        evaluations.clear();
        taskIdIndex.clear();
        pestIdIndex.clear();
    }
    
    public int count() {
        return evaluations.size();
    }
}