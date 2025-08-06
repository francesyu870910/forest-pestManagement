package com.forestpest.repository.impl;

import com.forestpest.entity.PestPrediction;
import com.forestpest.entity.PestAlert;
import com.forestpest.repository.PredictionRepository;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * 预测预警数据访问实现类
 */
@Repository
public class PredictionRepositoryImpl implements PredictionRepository {
    
    @Autowired
    private DataStorage dataStorage;
    
    // ========== PestPrediction 相关方法实现 ==========
    
    @Override
    public PestPrediction savePrediction(PestPrediction prediction) {
        if (prediction.getId() == null) {
            prediction.setId(dataStorage.generateId());
        }
        prediction.setUpdatedTime(LocalDateTime.now());
        dataStorage.getPredictionStorage().savePrediction(prediction);
        return prediction;
    }
    
    @Override
    public Optional<PestPrediction> findPredictionById(String id) {
        return dataStorage.getPredictionStorage().findPredictionById(id);
    }
    
    @Override
    public List<PestPrediction> findAllPredictions() {
        return dataStorage.getPredictionStorage().findAllPredictions();
    }
    
    @Override
    public List<PestPrediction> findPredictionsByPestId(String pestId) {
        return dataStorage.getPredictionStorage().findPredictionsByPestId(pestId);
    }
    
    @Override
    public List<PestPrediction> findPredictionsByRiskLevel(String riskLevel) {
        return dataStorage.getPredictionStorage().findPredictionsByRiskLevel(riskLevel);
    }
    
    @Override
    public List<PestPrediction> findPredictionsByTargetArea(String targetArea) {
        return dataStorage.getPredictionStorage().findPredictionsByTargetArea(targetArea);
    }
    
    @Override
    public List<PestPrediction> findPredictionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return dataStorage.getPredictionStorage().findPredictionsByDateRange(startDate, endDate);
    }
    
    @Override
    public List<PestPrediction> findPredictionsByPredictedBy(String predictedBy) {
        return findAllPredictions().stream()
                .filter(prediction -> predictedBy.equals(prediction.getCreatedBy()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestPrediction> findHighRiskPredictions() {
        return findAllPredictions().stream()
                .filter(prediction -> "高风险".equals(prediction.getRiskLevel()) || 
                                    "极高风险".equals(prediction.getRiskLevel()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestPrediction> findRecentPredictions(int limit) {
        return findAllPredictions().stream()
                .sorted(Comparator.comparing(PestPrediction::getCreatedTime, 
                                           Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deletePredictionById(String id) {
        dataStorage.getPredictionStorage().deletePredictionById(id);
    }
    
    @Override
    public long countPredictions() {
        return dataStorage.getPredictionStorage().predictionCount();
    }
    
    // ========== PestAlert 相关方法实现 ==========
    
    @Override
    public PestAlert saveAlert(PestAlert alert) {
        if (alert.getId() == null) {
            alert.setId(dataStorage.generateId());
        }
        alert.setUpdatedTime(LocalDateTime.now());
        dataStorage.getPredictionStorage().saveAlert(alert);
        return alert;
    }
    
    @Override
    public Optional<PestAlert> findAlertById(String id) {
        return dataStorage.getPredictionStorage().findAlertById(id);
    }
    
    @Override
    public List<PestAlert> findAllAlerts() {
        return dataStorage.getPredictionStorage().findAllAlerts();
    }
    
    @Override
    public List<PestAlert> findAlertsByPredictionId(String predictionId) {
        return dataStorage.getPredictionStorage().findAlertsByPredictionId(predictionId);
    }
    
    @Override
    public List<PestAlert> findAlertsByLevel(String alertLevel) {
        return dataStorage.getPredictionStorage().findAlertsByLevel(alertLevel);
    }
    
    @Override
    public List<PestAlert> findAlertsByStatus(String status) {
        return dataStorage.getPredictionStorage().findAlertsByStatus(status);
    }
    
    @Override
    public List<PestAlert> findAlertsByTargetArea(String targetArea) {
        return findAllAlerts().stream()
                .filter(alert -> targetArea.equals(alert.getTargetArea()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestAlert> findAlertsByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return findAllAlerts().stream()
                .filter(alert -> alert.getCreatedTime() != null &&
                               !alert.getCreatedTime().isBefore(startTime) &&
                               !alert.getCreatedTime().isAfter(endTime))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestAlert> findActiveAlerts() {
        return findAlertsByStatus("ACTIVE");
    }
    
    @Override
    public List<PestAlert> findUnhandledAlerts() {
        return findAllAlerts().stream()
                .filter(alert -> "未处理".equals(alert.getStatus()) || "PENDING".equals(alert.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestAlert> findRecentAlerts(int limit) {
        return findAllAlerts().stream()
                .sorted(Comparator.comparing(PestAlert::getCreatedTime, 
                                           Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteAlertById(String id) {
        dataStorage.getPredictionStorage().deleteAlertById(id);
    }
    
    @Override
    public long countAlerts() {
        return dataStorage.getPredictionStorage().alertCount();
    }
    
    // ========== 批量操作方法实现 ==========
    
    @Override
    public List<PestPrediction> saveAllPredictions(Iterable<PestPrediction> predictions) {
        List<PestPrediction> savedPredictions = new java.util.ArrayList<>();
        for (PestPrediction prediction : predictions) {
            savedPredictions.add(savePrediction(prediction));
        }
        return savedPredictions;
    }
    
    @Override
    public List<PestAlert> saveAllAlerts(Iterable<PestAlert> alerts) {
        List<PestAlert> savedAlerts = new java.util.ArrayList<>();
        for (PestAlert alert : alerts) {
            savedAlerts.add(saveAlert(alert));
        }
        return savedAlerts;
    }
    
    @Override
    public void deleteAllPredictions() {
        findAllPredictions().forEach(prediction -> deletePredictionById(prediction.getId()));
    }
    
    @Override
    public void deleteAllAlerts() {
        findAllAlerts().forEach(alert -> deleteAlertById(alert.getId()));
    }
    
    @Override
    public void deleteAll() {
        dataStorage.getPredictionStorage().clear();
    }
    
    // ========== 复合查询方法实现 ==========
    
    @Override
    public List<PestPrediction> searchPredictionsByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return findAllPredictions().stream()
                .filter(prediction ->
                    (prediction.getTargetArea() != null && prediction.getTargetArea().toLowerCase().contains(lowerKeyword)) ||
                    (prediction.getInfluencingFactors() != null && prediction.getInfluencingFactors().toLowerCase().contains(lowerKeyword)) ||
                    (prediction.getRiskLevel() != null && prediction.getRiskLevel().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestAlert> searchAlertsByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return findAllAlerts().stream()
                .filter(alert ->
                    (alert.getAlertLevel() != null && alert.getAlertLevel().toLowerCase().contains(lowerKeyword)) ||
                    (alert.getMessage() != null && alert.getMessage().toLowerCase().contains(lowerKeyword)) ||
                    (alert.getTargetArea() != null && alert.getTargetArea().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestPrediction> findPredictionsNeedingAttention() {
        LocalDate recentDate = LocalDate.now().minusDays(7); // 最近7天
        return findHighRiskPredictions().stream()
                .filter(prediction -> prediction.getPredictionDate() != null &&
                                    !prediction.getPredictionDate().isBefore(recentDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestAlert> findUrgentAlerts() {
        return findAllAlerts().stream()
                .filter(alert -> ("高级".equals(alert.getAlertLevel()) || "紧急".equals(alert.getAlertLevel())) &&
                               ("未处理".equals(alert.getStatus()) || "PENDING".equals(alert.getStatus())))
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Long> countPredictionsByRiskLevel() {
        return findAllPredictions().stream()
                .filter(prediction -> prediction.getRiskLevel() != null)
                .collect(Collectors.groupingBy(PestPrediction::getRiskLevel, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countAlertsByLevel() {
        return findAllAlerts().stream()
                .filter(alert -> alert.getAlertLevel() != null)
                .collect(Collectors.groupingBy(PestAlert::getAlertLevel, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countAlertsByArea() {
        return findAllAlerts().stream()
                .filter(alert -> alert.getTargetArea() != null)
                .collect(Collectors.groupingBy(PestAlert::getTargetArea, Collectors.counting()));
    }
}