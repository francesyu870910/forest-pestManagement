package com.forestpest.data.storage;

import com.forestpest.entity.PestPrediction;
import com.forestpest.entity.PestAlert;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 预测预警数据存储
 */
@Component
public class PredictionStorage {
    
    private final Map<String, PestPrediction> predictions = new ConcurrentHashMap<>();
    private final Map<String, PestAlert> alerts = new ConcurrentHashMap<>();
    private final Map<String, List<String>> pestIdIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> predictionAlertIndex = new ConcurrentHashMap<>();
    
    // PestPrediction methods
    public void savePrediction(PestPrediction prediction) {
        predictions.put(prediction.getId(), prediction);
        
        // 更新病虫害ID索引
        pestIdIndex.computeIfAbsent(prediction.getPestId(), k -> new ArrayList<>()).add(prediction.getId());
    }
    
    public Optional<PestPrediction> findPredictionById(String id) {
        return Optional.ofNullable(predictions.get(id));
    }
    
    public List<PestPrediction> findAllPredictions() {
        return new ArrayList<>(predictions.values());
    }
    
    public List<PestPrediction> findPredictionsByPestId(String pestId) {
        List<String> ids = pestIdIndex.get(pestId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(predictions::get)
                .filter(prediction -> prediction != null)
                .collect(Collectors.toList());
    }
    
    public List<PestPrediction> findPredictionsByRiskLevel(String riskLevel) {
        return predictions.values().stream()
                .filter(prediction -> riskLevel.equals(prediction.getRiskLevel()))
                .collect(Collectors.toList());
    }
    
    public List<PestPrediction> findPredictionsByTargetArea(String targetArea) {
        return predictions.values().stream()
                .filter(prediction -> targetArea.equals(prediction.getTargetArea()))
                .collect(Collectors.toList());
    }
    
    public List<PestPrediction> findPredictionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return predictions.values().stream()
                .filter(prediction -> !prediction.getPredictionDate().isBefore(startDate) &&
                                    !prediction.getPredictionDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    public void deletePredictionById(String id) {
        PestPrediction prediction = predictions.remove(id);
        if (prediction != null) {
            // 清理索引
            List<String> pestIds = pestIdIndex.get(prediction.getPestId());
            if (pestIds != null) {
                pestIds.remove(id);
            }
            
            // 删除相关预警
            List<String> alertIds = predictionAlertIndex.get(id);
            if (alertIds != null) {
                alertIds.forEach(alerts::remove);
                predictionAlertIndex.remove(id);
            }
        }
    }
    
    // PestAlert methods
    public void saveAlert(PestAlert alert) {
        alerts.put(alert.getId(), alert);
        
        // 更新预测预警索引
        if (alert.getPredictionId() != null) {
            predictionAlertIndex.computeIfAbsent(alert.getPredictionId(), k -> new ArrayList<>()).add(alert.getId());
        }
    }
    
    public Optional<PestAlert> findAlertById(String id) {
        return Optional.ofNullable(alerts.get(id));
    }
    
    public List<PestAlert> findAllAlerts() {
        return new ArrayList<>(alerts.values());
    }
    
    public List<PestAlert> findAlertsByPredictionId(String predictionId) {
        List<String> ids = predictionAlertIndex.get(predictionId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(alerts::get)
                .filter(alert -> alert != null)
                .collect(Collectors.toList());
    }
    
    public List<PestAlert> findAlertsByLevel(String alertLevel) {
        return alerts.values().stream()
                .filter(alert -> alertLevel.equals(alert.getAlertLevel()))
                .collect(Collectors.toList());
    }
    
    public List<PestAlert> findAlertsByStatus(String status) {
        return alerts.values().stream()
                .filter(alert -> status.equals(alert.getStatus()))
                .collect(Collectors.toList());
    }
    
    public void deleteAlertById(String id) {
        PestAlert alert = alerts.remove(id);
        if (alert != null) {
            // 清理索引
            if (alert.getPredictionId() != null) {
                List<String> alertIds = predictionAlertIndex.get(alert.getPredictionId());
                if (alertIds != null) {
                    alertIds.remove(id);
                }
            }
        }
    }
    
    public void clear() {
        predictions.clear();
        alerts.clear();
        pestIdIndex.clear();
        predictionAlertIndex.clear();
    }
    
    public int predictionCount() {
        return predictions.size();
    }
    
    public int alertCount() {
        return alerts.size();
    }
}