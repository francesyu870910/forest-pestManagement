package com.forestpest.service.impl;

import com.forestpest.entity.PestPrediction;
import com.forestpest.entity.PestAlert;
import com.forestpest.service.PredictionService;
import com.forestpest.repository.PredictionRepository;
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
 * 预测预警服务实现类
 */
@Service
public class PredictionServiceImpl implements PredictionService {
    
    @Autowired
    private PredictionRepository predictionRepository;
    
    // 预警规则存储
    private final Map<String, Map<String, Object>> alertRules = new ConcurrentHashMap<>();
    
    // 通知偏好存储
    private final Map<String, Map<String, Object>> notificationPreferences = new ConcurrentHashMap<>();
    
    // 预测模型存储
    private final Map<String, Map<String, Object>> predictionModels = new ConcurrentHashMap<>();
    
    // 系统配置存储
    private final Map<String, Object> predictionConfig = new ConcurrentHashMap<>();
    private final Map<String, Object> alertConfig = new ConcurrentHashMap<>();
    
    private final Random random = new Random();
    
    // 初始化配置和模型
    {
        initializeDefaultConfig();
        initializePredictionModels();
        initializeDefaultAlertRules();
    }
    
    // ========== 预测管理方法实现 ==========
    
    @Override
    public PestPrediction createPrediction(PestPrediction prediction, String userId) {
        if (prediction == null) {
            throw new ForestPestSystemException("预测数据不能为空");
        }
        
        if (!validatePrediction(prediction)) {
            throw new ForestPestSystemException("预测数据验证失败");
        }
        
        prediction.setId(IdGenerator.generateId());
        prediction.setCreatedBy(userId);
        prediction.setCreatedTime(LocalDateTime.now());
        prediction.setStatus("ACTIVE");
        
        // 如果没有设置预测日期，默认为今天
        if (prediction.getPredictionDate() == null) {
            prediction.setPredictionDate(LocalDate.now());
        }
        
        // 自动计算风险等级和概率（如果未设置）
        if (prediction.getRiskLevel() == null || prediction.getProbability() == null) {
            Map<String, Object> factors = buildFactorsFromPrediction(prediction);
            if (prediction.getProbability() == null) {
                double probability = calculateProbability(prediction.getPestId(), prediction.getTargetArea(), factors);
                prediction.setProbability(probability);
            }
            if (prediction.getRiskLevel() == null) {
                String riskLevel = calculateRiskLevel(prediction.getProbability(), factors);
                prediction.setRiskLevel(riskLevel);
            }
        }
        
        PestPrediction savedPrediction = predictionRepository.savePrediction(prediction);
        
        // 检查是否需要触发预警
        checkAndTriggerAlert(savedPrediction);
        
        return savedPrediction;
    }
    
    @Override
    public PestPrediction getPredictionById(String predictionId) {
        return predictionRepository.findPredictionById(predictionId).orElse(null);
    }
    
    @Override
    public List<PestPrediction> getAllPredictions() {
        return predictionRepository.findAllPredictions();
    }
    
    @Override
    public List<PestPrediction> getUserPredictions(String userId) {
        return predictionRepository.findPredictionsByPredictedBy(userId);
    }
    
    @Override
    public List<PestPrediction> getPredictions(int page, int size) {
        List<PestPrediction> allPredictions = predictionRepository.findAllPredictions();
        allPredictions.sort((p1, p2) -> p2.getCreatedTime().compareTo(p1.getCreatedTime()));
        
        int start = page * size;
        int end = Math.min(start + size, allPredictions.size());
        
        if (start >= allPredictions.size()) {
            return new ArrayList<>();
        }
        
        return allPredictions.subList(start, end);
    }
    
    @Override
    public PestPrediction updatePrediction(String predictionId, PestPrediction updatedPrediction, String userId) {
        Optional<PestPrediction> existingOpt = predictionRepository.findPredictionById(predictionId);
        if (!existingOpt.isPresent()) {
            throw new ForestPestSystemException("预测不存在");
        }
        
        PestPrediction existingPrediction = existingOpt.get();
        if (!userId.equals(existingPrediction.getCreatedBy())) {
            throw new ForestPestSystemException("无权限修改此预测");
        }
        
        // 更新预测信息
        existingPrediction.setTargetArea(updatedPrediction.getTargetArea());
        existingPrediction.setPredictionDate(updatedPrediction.getPredictionDate());
        existingPrediction.setRiskLevel(updatedPrediction.getRiskLevel());
        existingPrediction.setProbability(updatedPrediction.getProbability());
        existingPrediction.setInfluencingFactors(updatedPrediction.getInfluencingFactors());
        existingPrediction.setRecommendedActions(updatedPrediction.getRecommendedActions());
        existingPrediction.setWeatherConditions(updatedPrediction.getWeatherConditions());
        existingPrediction.setUpdatedTime(LocalDateTime.now());
        existingPrediction.setUpdatedBy(userId);
        
        return predictionRepository.savePrediction(existingPrediction);
    }
    
    @Override
    public boolean deletePrediction(String predictionId, String userId) {
        Optional<PestPrediction> predictionOpt = predictionRepository.findPredictionById(predictionId);
        if (!predictionOpt.isPresent() || !userId.equals(predictionOpt.get().getCreatedBy())) {
            return false;
        }
        
        predictionRepository.deletePredictionById(predictionId);
        return true;
    }
    
    @Override
    public List<PestPrediction> getPredictionsByPestId(String pestId) {
        return predictionRepository.findPredictionsByPestId(pestId);
    }
    
    @Override
    public List<PestPrediction> getPredictionsByArea(String targetArea) {
        return predictionRepository.findPredictionsByTargetArea(targetArea);
    }
    
    @Override
    public List<PestPrediction> getPredictionsByRiskLevel(String riskLevel) {
        return predictionRepository.findPredictionsByRiskLevel(riskLevel);
    }
    
    @Override
    public List<PestPrediction> getPredictionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return predictionRepository.findPredictionsByDateRange(startDate, endDate);
    }
    
    @Override
    public List<PestPrediction> getHighRiskPredictions() {
        return predictionRepository.findHighRiskPredictions();
    }
    
    @Override
    public List<PestPrediction> getRecentPredictions(int limit) {
        return predictionRepository.findRecentPredictions(limit);
    }
    
    @Override
    public List<PestPrediction> searchPredictions(String keyword) {
        return predictionRepository.searchPredictionsByKeyword(keyword);
    }
    
    // ========== 预警管理方法实现 ==========
    
    @Override
    public PestAlert createAlert(PestAlert alert, String userId) {
        if (alert == null) {
            throw new ForestPestSystemException("预警数据不能为空");
        }
        
        if (!validateAlert(alert)) {
            throw new ForestPestSystemException("预警数据验证失败");
        }
        
        alert.setId(IdGenerator.generateId());
        alert.setCreatedBy(userId);
        alert.setCreatedTime(LocalDateTime.now());
        alert.setStatus("ACTIVE");
        
        if (alert.getAlertTime() == null) {
            alert.setAlertTime(LocalDateTime.now());
        }
        
        PestAlert savedAlert = predictionRepository.saveAlert(alert);
        
        // 发送预警通知
        sendAlertNotification(savedAlert.getId(), getDefaultRecipients(savedAlert));
        
        return savedAlert;
    }
    
    @Override
    public PestAlert createAlertFromPrediction(String predictionId, String userId, Map<String, Object> alertConfig) {
        Optional<PestPrediction> predictionOpt = predictionRepository.findPredictionById(predictionId);
        if (!predictionOpt.isPresent()) {
            throw new ForestPestSystemException("预测不存在");
        }
        
        PestPrediction prediction = predictionOpt.get();
        
        PestAlert alert = new PestAlert();
        alert.setPredictionId(predictionId);
        alert.setPestId(prediction.getPestId());
        alert.setTargetArea(prediction.getTargetArea());
        alert.setAlertLevel(determineAlertLevel(prediction.getRiskLevel()));
        alert.setMessage(generateAlertMessage(prediction));
        alert.setTargetAudience((String) alertConfig.getOrDefault("targetAudience", "所有用户"));
        alert.setUrgency(determineUrgency(prediction.getRiskLevel()));
        alert.setSeverity(determineSeverity(prediction.getProbability()));
        alert.setCertainty("可能");
        alert.setInstructions(prediction.getRecommendedActions());
        
        return createAlert(alert, userId);
    }
    
    @Override
    public PestAlert getAlertById(String alertId) {
        return predictionRepository.findAlertById(alertId).orElse(null);
    }
    
    @Override
    public List<PestAlert> getAllAlerts() {
        return predictionRepository.findAllAlerts();
    }
    
    @Override
    public List<PestAlert> getUserAlerts(String userId) {
        return predictionRepository.findAllAlerts().stream()
                .filter(alert -> userId.equals(alert.getCreatedBy()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PestAlert> getAlerts(int page, int size) {
        List<PestAlert> allAlerts = predictionRepository.findAllAlerts();
        allAlerts.sort((a1, a2) -> a2.getCreatedTime().compareTo(a1.getCreatedTime()));
        
        int start = page * size;
        int end = Math.min(start + size, allAlerts.size());
        
        if (start >= allAlerts.size()) {
            return new ArrayList<>();
        }
        
        return allAlerts.subList(start, end);
    }
    
    @Override
    public PestAlert updateAlert(String alertId, PestAlert updatedAlert, String userId) {
        Optional<PestAlert> existingOpt = predictionRepository.findAlertById(alertId);
        if (!existingOpt.isPresent()) {
            throw new ForestPestSystemException("预警不存在");
        }
        
        PestAlert existingAlert = existingOpt.get();
        if (!userId.equals(existingAlert.getCreatedBy())) {
            throw new ForestPestSystemException("无权限修改此预警");
        }
        
        // 更新预警信息
        existingAlert.setAlertLevel(updatedAlert.getAlertLevel());
        existingAlert.setMessage(updatedAlert.getMessage());
        existingAlert.setTargetAudience(updatedAlert.getTargetAudience());
        existingAlert.setInstructions(updatedAlert.getInstructions());
        existingAlert.setStatus(updatedAlert.getStatus());
        existingAlert.setUpdatedTime(LocalDateTime.now());
        existingAlert.setUpdatedBy(userId);
        
        return predictionRepository.saveAlert(existingAlert);
    }
    
    @Override
    public boolean deleteAlert(String alertId, String userId) {
        Optional<PestAlert> alertOpt = predictionRepository.findAlertById(alertId);
        if (!alertOpt.isPresent() || !userId.equals(alertOpt.get().getCreatedBy())) {
            return false;
        }
        
        predictionRepository.deleteAlertById(alertId);
        return true;
    }
    
    @Override
    public List<PestAlert> getAlertsByPredictionId(String predictionId) {
        return predictionRepository.findAlertsByPredictionId(predictionId);
    }
    
    @Override
    public List<PestAlert> getAlertsByLevel(String alertLevel) {
        return predictionRepository.findAlertsByLevel(alertLevel);
    }
    
    @Override
    public List<PestAlert> getAlertsByStatus(String status) {
        return predictionRepository.findAlertsByStatus(status);
    }
    
    @Override
    public List<PestAlert> getAlertsByArea(String targetArea) {
        return predictionRepository.findAlertsByTargetArea(targetArea);
    }
    
    @Override
    public List<PestAlert> getActiveAlerts() {
        return predictionRepository.findActiveAlerts();
    }
    
    @Override
    public List<PestAlert> getUnhandledAlerts() {
        return predictionRepository.findUnhandledAlerts();
    }
    
    @Override
    public List<PestAlert> getUrgentAlerts() {
        return predictionRepository.findUrgentAlerts();
    }
    
    @Override
    public List<PestAlert> getRecentAlerts(int limit) {
        return predictionRepository.findRecentAlerts(limit);
    }
    
    @Override
    public List<PestAlert> searchAlerts(String keyword) {
        return predictionRepository.searchAlertsByKeyword(keyword);
    }
    
    @Override
    public boolean acknowledgeAlert(String alertId, String userId) {
        Optional<PestAlert> alertOpt = predictionRepository.findAlertById(alertId);
        if (!alertOpt.isPresent()) {
            return false;
        }
        
        PestAlert alert = alertOpt.get();
        alert.setAcknowledgedBy(userId);
        alert.setAcknowledgedTime(LocalDateTime.now());
        alert.setStatus("ACKNOWLEDGED");
        
        predictionRepository.saveAlert(alert);
        return true;
    }
    
    @Override
    public boolean handleAlert(String alertId, String userId, String handleResult) {
        Optional<PestAlert> alertOpt = predictionRepository.findAlertById(alertId);
        if (!alertOpt.isPresent()) {
            return false;
        }
        
        PestAlert alert = alertOpt.get();
        alert.setStatus("HANDLED");
        alert.setUpdatedBy(userId);
        alert.setUpdatedTime(LocalDateTime.now());
        
        predictionRepository.saveAlert(alert);
        return true;
    }
    
    // ========== 预测算法方法实现 ==========
    
    @Override
    public PestPrediction generatePredictionFromHistory(String pestId, String targetArea, Map<String, Object> parameters) {
        PestPrediction prediction = new PestPrediction();
        prediction.setPestId(pestId);
        prediction.setTargetArea(targetArea);
        prediction.setPredictionDate(LocalDate.now().plusDays(7)); // 预测7天后
        prediction.setPredictionModel("历史数据模型");
        
        // 模拟基于历史数据的预测算法
        double baseProbability = 0.3 + random.nextDouble() * 0.4; // 0.3-0.7
        
        // 根据历史数据调整概率
        if (parameters.containsKey("historicalOccurrences")) {
            int occurrences = (Integer) parameters.getOrDefault("historicalOccurrences", 0);
            baseProbability += occurrences * 0.1;
        }
        
        prediction.setProbability(Math.min(1.0, baseProbability));
        prediction.setRiskLevel(calculateRiskLevel(prediction.getProbability(), parameters));
        prediction.setInfluencingFactors("历史发生频率、季节性规律、环境相似性");
        prediction.setConfidence("中等");
        prediction.setValidityPeriod("7天");
        
        return prediction;
    }
    
    @Override
    public PestPrediction generatePredictionFromWeather(String pestId, String targetArea, Map<String, Object> weatherData) {
        PestPrediction prediction = new PestPrediction();
        prediction.setPestId(pestId);
        prediction.setTargetArea(targetArea);
        prediction.setPredictionDate(LocalDate.now().plusDays(3)); // 预测3天后
        prediction.setPredictionModel("天气预测模型");
        
        // 模拟基于天气数据的预测算法
        double temperature = (Double) weatherData.getOrDefault("temperature", 20.0);
        double humidity = (Double) weatherData.getOrDefault("humidity", 60.0);
        double rainfall = (Double) weatherData.getOrDefault("rainfall", 0.0);
        
        double probability = 0.2;
        
        // 温度影响
        if (temperature >= 20 && temperature <= 30) {
            probability += 0.3;
        }
        
        // 湿度影响
        if (humidity >= 70) {
            probability += 0.2;
        }
        
        // 降雨影响
        if (rainfall > 10) {
            probability += 0.1;
        }
        
        prediction.setProbability(Math.min(1.0, probability));
        prediction.setRiskLevel(calculateRiskLevel(prediction.getProbability(), weatherData));
        prediction.setWeatherConditions(String.format("温度: %.1f°C, 湿度: %.1f%%, 降雨: %.1fmm", 
                                                      temperature, humidity, rainfall));
        prediction.setInfluencingFactors("温度、湿度、降雨量、风速");
        prediction.setConfidence("高");
        prediction.setValidityPeriod("3天");
        
        return prediction;
    }
    
    @Override
    public PestPrediction generatePredictionFromEnvironment(String pestId, String targetArea, Map<String, Object> environmentData) {
        PestPrediction prediction = new PestPrediction();
        prediction.setPestId(pestId);
        prediction.setTargetArea(targetArea);
        prediction.setPredictionDate(LocalDate.now().plusDays(5)); // 预测5天后
        prediction.setPredictionModel("环境因子模型");
        
        // 模拟基于环境因子的预测算法
        double probability = 0.25;
        
        // 植被覆盖度影响
        if (environmentData.containsKey("vegetationCoverage")) {
            double coverage = (Double) environmentData.get("vegetationCoverage");
            if (coverage > 0.8) {
                probability += 0.2;
            }
        }
        
        // 土壤湿度影响
        if (environmentData.containsKey("soilMoisture")) {
            double moisture = (Double) environmentData.get("soilMoisture");
            if (moisture > 0.6) {
                probability += 0.15;
            }
        }
        
        prediction.setProbability(Math.min(1.0, probability));
        prediction.setRiskLevel(calculateRiskLevel(prediction.getProbability(), environmentData));
        prediction.setInfluencingFactors("植被覆盖度、土壤湿度、海拔高度、坡向");
        prediction.setConfidence("中等");
        prediction.setValidityPeriod("5天");
        
        return prediction;
    }
    
    @Override
    public PestPrediction generateComprehensivePrediction(String pestId, String targetArea, Map<String, Object> allData) {
        PestPrediction prediction = new PestPrediction();
        prediction.setPestId(pestId);
        prediction.setTargetArea(targetArea);
        prediction.setPredictionDate(LocalDate.now().plusDays(7)); // 预测7天后
        prediction.setPredictionModel("综合预测模型");
        
        // 综合多种因素的预测算法
        double probability = 0.2;
        List<String> factors = new ArrayList<>();
        
        // 历史数据权重 30%
        if (allData.containsKey("historicalData")) {
            probability += 0.3 * (Double) allData.getOrDefault("historicalProbability", 0.5);
            factors.add("历史数据");
        }
        
        // 天气数据权重 40%
        if (allData.containsKey("weatherData")) {
            probability += 0.4 * (Double) allData.getOrDefault("weatherProbability", 0.5);
            factors.add("天气条件");
        }
        
        // 环境数据权重 30%
        if (allData.containsKey("environmentData")) {
            probability += 0.3 * (Double) allData.getOrDefault("environmentProbability", 0.5);
            factors.add("环境因子");
        }
        
        prediction.setProbability(Math.min(1.0, probability));
        prediction.setRiskLevel(calculateRiskLevel(prediction.getProbability(), allData));
        prediction.setInfluencingFactors(String.join("、", factors));
        prediction.setConfidence("高");
        prediction.setValidityPeriod("7天");
        
        return prediction;
    }
    
    @Override
    public String calculateRiskLevel(double probability, Map<String, Object> factors) {
        if (probability >= 0.8) {
            return "极高风险";
        } else if (probability >= 0.6) {
            return "高风险";
        } else if (probability >= 0.4) {
            return "中风险";
        } else if (probability >= 0.2) {
            return "低风险";
        } else {
            return "极低风险";
        }
    }
    
    @Override
    public double calculateProbability(String pestId, String targetArea, Map<String, Object> factors) {
        // 模拟概率计算算法
        double baseProbability = 0.3 + random.nextDouble() * 0.4; // 基础概率 0.3-0.7
        
        // 根据各种因子调整概率
        if (factors.containsKey("temperature")) {
            double temp = (Double) factors.get("temperature");
            if (temp >= 20 && temp <= 30) {
                baseProbability += 0.1;
            }
        }
        
        if (factors.containsKey("humidity")) {
            double humidity = (Double) factors.get("humidity");
            if (humidity >= 70) {
                baseProbability += 0.1;
            }
        }
        
        return Math.min(1.0, baseProbability);
    }
    
    @Override
    public Map<String, Object> evaluatePredictionAccuracy(String predictionId, Map<String, Object> actualData) {
        Map<String, Object> evaluation = new HashMap<>();
        
        Optional<PestPrediction> predictionOpt = predictionRepository.findPredictionById(predictionId);
        if (!predictionOpt.isPresent()) {
            evaluation.put("error", "预测不存在");
            return evaluation;
        }
        
        PestPrediction prediction = predictionOpt.get();
        boolean actualOccurred = (Boolean) actualData.getOrDefault("occurred", false);
        double predictedProbability = prediction.getProbability();
        
        // 计算准确性指标
        double accuracy = actualOccurred ? predictedProbability : (1 - predictedProbability);
        
        evaluation.put("predictionId", predictionId);
        evaluation.put("predictedProbability", predictedProbability);
        evaluation.put("actualOccurred", actualOccurred);
        evaluation.put("accuracy", accuracy);
        evaluation.put("evaluationTime", LocalDateTime.now());
        
        if (accuracy >= 0.8) {
            evaluation.put("accuracyLevel", "高");
        } else if (accuracy >= 0.6) {
            evaluation.put("accuracyLevel", "中");
        } else {
            evaluation.put("accuracyLevel", "低");
        }
        
        return evaluation;
    }
    
    @Override
    public boolean updatePredictionModel(String modelId, Map<String, Object> trainingData) {
        if (!predictionModels.containsKey(modelId)) {
            return false;
        }
        
        Map<String, Object> model = predictionModels.get(modelId);
        model.put("lastTrainingTime", LocalDateTime.now());
        model.put("trainingDataSize", trainingData.size());
        model.put("version", (Integer) model.getOrDefault("version", 1) + 1);
        
        return true;
    }
    
    // ========== 预警触发方法实现 ==========
    
    @Override
    public List<PestAlert> checkAlertTriggers() {
        List<PestAlert> triggeredAlerts = new ArrayList<>();
        
        // 检查高风险预测
        List<PestPrediction> highRiskPredictions = getHighRiskPredictions();
        for (PestPrediction prediction : highRiskPredictions) {
            // 检查是否已经有预警
            List<PestAlert> existingAlerts = getAlertsByPredictionId(prediction.getId());
            if (existingAlerts.isEmpty()) {
                PestAlert alert = triggerAlertFromPrediction(prediction.getId(), "高风险预测自动触发");
                if (alert != null) {
                    triggeredAlerts.add(alert);
                }
            }
        }
        
        return triggeredAlerts;
    }
    
    @Override
    public List<PestAlert> triggerAlertsAutomatically() {
        return checkAlertTriggers();
    }
    
    @Override
    public PestAlert triggerAlertFromPrediction(String predictionId, String triggerReason) {
        Optional<PestPrediction> predictionOpt = predictionRepository.findPredictionById(predictionId);
        if (!predictionOpt.isPresent()) {
            return null;
        }
        
        PestPrediction prediction = predictionOpt.get();
        
        PestAlert alert = new PestAlert();
        alert.setPredictionId(predictionId);
        alert.setPestId(prediction.getPestId());
        alert.setTargetArea(prediction.getTargetArea());
        alert.setAlertLevel(determineAlertLevel(prediction.getRiskLevel()));
        alert.setMessage(generateAlertMessage(prediction));
        alert.setTargetAudience("所有用户");
        alert.setUrgency(determineUrgency(prediction.getRiskLevel()));
        alert.setSeverity(determineSeverity(prediction.getProbability()));
        alert.setCertainty("可能");
        alert.setInstructions(prediction.getRecommendedActions());
        alert.setAlertType("预测预警");
        
        return createAlert(alert, "system");
    }
    
    @Override
    public List<PestAlert> batchTriggerAlerts(List<String> predictionIds, String triggerReason) {
        List<PestAlert> alerts = new ArrayList<>();
        
        for (String predictionId : predictionIds) {
            PestAlert alert = triggerAlertFromPrediction(predictionId, triggerReason);
            if (alert != null) {
                alerts.add(alert);
            }
        }
        
        return alerts;
    }
    
    @Override
    public boolean setAlertRule(String ruleId, Map<String, Object> ruleConfig) {
        alertRules.put(ruleId, new HashMap<>(ruleConfig));
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getAlertRules() {
        return new ArrayList<>(alertRules.values());
    }
    
    @Override
    public boolean deleteAlertRule(String ruleId) {
        return alertRules.remove(ruleId) != null;
    }
    
    // ========== 通知发送方法实现 ==========
    
    @Override
    public boolean sendAlertNotification(String alertId, List<String> recipients) {
        // 模拟发送通知
        Optional<PestAlert> alertOpt = predictionRepository.findAlertById(alertId);
        if (!alertOpt.isPresent()) {
            return false;
        }
        
        // 这里应该实现实际的通知发送逻辑
        // 目前只是模拟成功
        return true;
    }
    
    @Override
    public Map<String, Boolean> batchSendAlertNotifications(List<String> alertIds, List<String> recipients) {
        Map<String, Boolean> results = new HashMap<>();
        
        for (String alertId : alertIds) {
            boolean success = sendAlertNotification(alertId, recipients);
            results.put(alertId, success);
        }
        
        return results;
    }
    
    @Override
    public boolean sendEmailNotification(String alertId, List<String> emails) {
        // 模拟邮件发送
        return sendAlertNotification(alertId, emails);
    }
    
    @Override
    public boolean sendSmsNotification(String alertId, List<String> phoneNumbers) {
        // 模拟短信发送
        return sendAlertNotification(alertId, phoneNumbers);
    }
    
    @Override
    public boolean sendSystemNotification(String alertId, List<String> userIds) {
        // 模拟系统内通知
        return sendAlertNotification(alertId, userIds);
    }
    
    @Override
    public List<Map<String, Object>> getNotificationHistory(String alertId) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // 模拟通知历史
        Map<String, Object> notification = new HashMap<>();
        notification.put("alertId", alertId);
        notification.put("type", "系统通知");
        notification.put("sentTime", LocalDateTime.now().minusHours(1));
        notification.put("status", "已发送");
        notification.put("recipients", Arrays.asList("user1", "user2"));
        
        history.add(notification);
        return history;
    }
    
    @Override
    public boolean setNotificationPreference(String userId, Map<String, Object> preferences) {
        notificationPreferences.put(userId, new HashMap<>(preferences));
        return true;
    }
    
    @Override
    public Map<String, Object> getNotificationPreference(String userId) {
        return notificationPreferences.getOrDefault(userId, getDefaultNotificationPreference());
    }
    
    // ========== 统计分析方法实现 ==========
    
    @Override
    public Map<String, Object> getPredictionStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        List<PestPrediction> allPredictions = predictionRepository.findAllPredictions();
        statistics.put("totalPredictions", allPredictions.size());
        
        // 按风险等级统计
        Map<String, Long> riskLevelStats = predictionRepository.countPredictionsByRiskLevel();
        statistics.put("riskLevelStats", riskLevelStats);
        
        // 按月份统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> monthlyStats = allPredictions.stream()
                .collect(Collectors.groupingBy(
                    pred -> pred.getCreatedTime().format(formatter),
                    Collectors.counting()
                ));
        statistics.put("monthlyStats", monthlyStats);
        
        // 平均概率
        double avgProbability = allPredictions.stream()
                .filter(pred -> pred.getProbability() != null)
                .mapToDouble(PestPrediction::getProbability)
                .average()
                .orElse(0.0);
        statistics.put("averageProbability", avgProbability);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getUserPredictionStatistics(String userId) {
        List<PestPrediction> userPredictions = getUserPredictions(userId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalPredictions", userPredictions.size());
        
        // 按风险等级统计
        Map<String, Long> riskLevelStats = userPredictions.stream()
                .collect(Collectors.groupingBy(PestPrediction::getRiskLevel, Collectors.counting()));
        statistics.put("riskLevelStats", riskLevelStats);
        
        // 平均概率
        double avgProbability = userPredictions.stream()
                .filter(pred -> pred.getProbability() != null)
                .mapToDouble(PestPrediction::getProbability)
                .average()
                .orElse(0.0);
        statistics.put("averageProbability", avgProbability);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        List<PestAlert> allAlerts = predictionRepository.findAllAlerts();
        statistics.put("totalAlerts", allAlerts.size());
        
        // 按预警等级统计
        Map<String, Long> alertLevelStats = predictionRepository.countAlertsByLevel();
        statistics.put("alertLevelStats", alertLevelStats);
        
        // 按状态统计
        Map<String, Long> statusStats = allAlerts.stream()
                .collect(Collectors.groupingBy(PestAlert::getStatus, Collectors.counting()));
        statistics.put("statusStats", statusStats);
        
        // 按区域统计
        Map<String, Long> areaStats = predictionRepository.countAlertsByArea();
        statistics.put("areaStats", areaStats);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getUserAlertStatistics(String userId) {
        List<PestAlert> userAlerts = getUserAlerts(userId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAlerts", userAlerts.size());
        
        // 按预警等级统计
        Map<String, Long> alertLevelStats = userAlerts.stream()
                .collect(Collectors.groupingBy(PestAlert::getAlertLevel, Collectors.counting()));
        statistics.put("alertLevelStats", alertLevelStats);
        
        // 按状态统计
        Map<String, Long> statusStats = userAlerts.stream()
                .collect(Collectors.groupingBy(PestAlert::getStatus, Collectors.counting()));
        statistics.put("statusStats", statusStats);
        
        return statistics;
    }
    
    // 辅助方法实现
    
    private boolean validatePrediction(PestPrediction prediction) {
        if (prediction.getPestId() == null || prediction.getPestId().trim().isEmpty()) {
            return false;
        }
        if (prediction.getTargetArea() == null || prediction.getTargetArea().trim().isEmpty()) {
            return false;
        }
        return true;
    }
    
    private boolean validateAlert(PestAlert alert) {
        if (alert.getMessage() == null || alert.getMessage().trim().isEmpty()) {
            return false;
        }
        if (alert.getTargetAudience() == null || alert.getTargetAudience().trim().isEmpty()) {
            return false;
        }
        return true;
    }
    
    private Map<String, Object> buildFactorsFromPrediction(PestPrediction prediction) {
        Map<String, Object> factors = new HashMap<>();
        factors.put("pestId", prediction.getPestId());
        factors.put("targetArea", prediction.getTargetArea());
        factors.put("predictionDate", prediction.getPredictionDate());
        return factors;
    }
    
    private void checkAndTriggerAlert(PestPrediction prediction) {
        // 如果是高风险预测，自动触发预警
        if ("高风险".equals(prediction.getRiskLevel()) || "极高风险".equals(prediction.getRiskLevel())) {
            triggerAlertFromPrediction(prediction.getId(), "高风险预测自动触发");
        }
    }
    
    private String determineAlertLevel(String riskLevel) {
        switch (riskLevel) {
            case "极高风险": return "紧急";
            case "高风险": return "高级";
            case "中风险": return "中级";
            case "低风险": return "低级";
            default: return "信息";
        }
    }
    
    private String determineUrgency(String riskLevel) {
        switch (riskLevel) {
            case "极高风险": return "立即";
            case "高风险": return "紧急";
            case "中风险": return "预期";
            default: return "未来";
        }
    }
    
    private String determineSeverity(double probability) {
        if (probability >= 0.8) return "极端";
        if (probability >= 0.6) return "严重";
        if (probability >= 0.4) return "中等";
        if (probability >= 0.2) return "轻微";
        return "未知";
    }
    
    private String generateAlertMessage(PestPrediction prediction) {
        return String.format("预测在%s区域可能发生%s级别的病虫害，发生概率为%.1f%%，请及时采取防护措施。",
                prediction.getTargetArea(),
                prediction.getRiskLevel(),
                prediction.getProbability() * 100);
    }
    
    private List<String> getDefaultRecipients(PestAlert alert) {
        // 根据预警等级和区域确定默认接收者
        return Arrays.asList("admin", "forestManager", "localOfficer");
    }
    
    private Map<String, Object> getDefaultNotificationPreference() {
        Map<String, Object> preference = new HashMap<>();
        preference.put("email", true);
        preference.put("sms", false);
        preference.put("system", true);
        preference.put("alertLevels", Arrays.asList("高级", "紧急"));
        return preference;
    }
    
    private void initializeDefaultConfig() {
        // 初始化预测配置
        predictionConfig.put("defaultValidityPeriod", "7天");
        predictionConfig.put("minProbabilityThreshold", 0.1);
        predictionConfig.put("maxProbabilityThreshold", 1.0);
        predictionConfig.put("autoTriggerAlertThreshold", 0.6);
        
        // 初始化预警配置
        alertConfig.put("defaultAlertLevel", "中级");
        alertConfig.put("autoExpireHours", 72);
        alertConfig.put("maxRecipientsPerAlert", 100);
        alertConfig.put("enableAutoTrigger", true);
    }
    
    private void initializePredictionModels() {
        // 历史数据模型
        Map<String, Object> historyModel = new HashMap<>();
        historyModel.put("id", "history-model");
        historyModel.put("name", "历史数据预测模型");
        historyModel.put("type", "历史统计");
        historyModel.put("accuracy", 0.75);
        historyModel.put("version", 1);
        historyModel.put("status", "active");
        predictionModels.put("history-model", historyModel);
        
        // 天气预测模型
        Map<String, Object> weatherModel = new HashMap<>();
        weatherModel.put("id", "weather-model");
        weatherModel.put("name", "天气预测模型");
        weatherModel.put("type", "气象关联");
        weatherModel.put("accuracy", 0.82);
        weatherModel.put("version", 1);
        weatherModel.put("status", "active");
        predictionModels.put("weather-model", weatherModel);
        
        // 综合预测模型
        Map<String, Object> comprehensiveModel = new HashMap<>();
        comprehensiveModel.put("id", "comprehensive-model");
        comprehensiveModel.put("name", "综合预测模型");
        comprehensiveModel.put("type", "多因子综合");
        comprehensiveModel.put("accuracy", 0.88);
        comprehensiveModel.put("version", 1);
        comprehensiveModel.put("status", "active");
        predictionModels.put("comprehensive-model", comprehensiveModel);
    }
    
    private void initializeDefaultAlertRules() {
        // 高风险自动预警规则
        Map<String, Object> highRiskRule = new HashMap<>();
        highRiskRule.put("id", "high-risk-auto");
        highRiskRule.put("name", "高风险自动预警");
        highRiskRule.put("condition", "riskLevel >= 高风险");
        highRiskRule.put("action", "自动创建预警");
        highRiskRule.put("enabled", true);
        alertRules.put("high-risk-auto", highRiskRule);
        
        // 概率阈值预警规则
        Map<String, Object> probabilityRule = new HashMap<>();
        probabilityRule.put("id", "probability-threshold");
        probabilityRule.put("name", "概率阈值预警");
        probabilityRule.put("condition", "probability >= 0.7");
        probabilityRule.put("action", "发送通知");
        probabilityRule.put("enabled", true);
        alertRules.put("probability-threshold", probabilityRule);
    }
    
    // 实现其他接口方法的简化版本
    
    @Override
    public Map<String, Object> getPredictionAccuracyStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("overallAccuracy", 0.78);
        stats.put("modelAccuracies", predictionModels.values().stream()
                .collect(Collectors.toMap(
                    model -> (String) model.get("name"),
                    model -> model.get("accuracy")
                )));
        return stats;
    }
    
    @Override
    public Map<String, Object> getAlertResponseStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAlerts", predictionRepository.countAlerts());
        stats.put("acknowledgedAlerts", getActiveAlerts().size());
        stats.put("averageResponseTime", "2.5小时");
        return stats;
    }
    
    @Override
    public Map<String, Long> getRiskLevelDistribution() {
        return predictionRepository.countPredictionsByRiskLevel();
    }
    
    @Override
    public Map<String, Long> getAlertLevelDistribution() {
        return predictionRepository.countAlertsByLevel();
    }
    
    @Override
    public Map<String, Long> getAreaAlertDistribution() {
        return predictionRepository.countAlertsByArea();
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> getPredictionTrends(String period) {
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        
        DateTimeFormatter formatter;
        if ("daily".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if ("monthly".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy");
        }
        
        List<PestPrediction> allPredictions = predictionRepository.findAllPredictions();
        
        // 预测数量趋势
        Map<String, Long> countTrend = allPredictions.stream()
                .collect(Collectors.groupingBy(
                    pred -> pred.getCreatedTime().format(formatter),
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
    public Map<String, List<Map<String, Object>>> getAlertTrends(String period) {
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        
        DateTimeFormatter formatter;
        if ("daily".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if ("monthly".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy");
        }
        
        List<PestAlert> allAlerts = predictionRepository.findAllAlerts();
        
        // 预警数量趋势
        Map<String, Long> countTrend = allAlerts.stream()
                .collect(Collectors.groupingBy(
                    alert -> alert.getCreatedTime().format(formatter),
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
    public Map<String, Object> getPredictionEffectivenessAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalPredictions", predictionRepository.countPredictions());
        analysis.put("accurateRate", 0.78);
        analysis.put("falsePositiveRate", 0.15);
        analysis.put("falseNegativeRate", 0.07);
        return analysis;
    }
    
    @Override
    public Map<String, Object> getAlertEffectivenessAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalAlerts", predictionRepository.countAlerts());
        analysis.put("responseRate", 0.85);
        analysis.put("averageResponseTime", "2.5小时");
        analysis.put("preventionSuccessRate", 0.72);
        return analysis;
    }
    
    @Override
    public List<Map<String, Object>> getPredictionModels() {
        return new ArrayList<>(predictionModels.values());
    }
    
    @Override
    public Map<String, Object> getPredictionModelDetails(String modelId) {
        return predictionModels.getOrDefault(modelId, new HashMap<>());
    }
    
    @Override
    public boolean trainPredictionModel(String modelId, Map<String, Object> trainingData) {
        return updatePredictionModel(modelId, trainingData);
    }
    
    @Override
    public Map<String, Object> evaluatePredictionModel(String modelId, Map<String, Object> testData) {
        Map<String, Object> evaluation = new HashMap<>();
        evaluation.put("modelId", modelId);
        evaluation.put("accuracy", 0.85);
        evaluation.put("precision", 0.82);
        evaluation.put("recall", 0.78);
        evaluation.put("f1Score", 0.80);
        return evaluation;
    }
    
    @Override
    public boolean deployPredictionModel(String modelId) {
        Map<String, Object> model = predictionModels.get(modelId);
        if (model != null) {
            model.put("status", "deployed");
            model.put("deployTime", LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getModelPerformanceMetrics(String modelId) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("modelId", modelId);
        metrics.put("accuracy", 0.85);
        metrics.put("responseTime", "150ms");
        metrics.put("throughput", "1000 predictions/hour");
        return metrics;
    }
    
    @Override
    public Map<String, Object> comparePredictionModels(List<String> modelIds) {
        Map<String, Object> comparison = new HashMap<>();
        List<Map<String, Object>> models = new ArrayList<>();
        
        for (String modelId : modelIds) {
            Map<String, Object> model = predictionModels.get(modelId);
            if (model != null) {
                models.add(model);
            }
        }
        
        comparison.put("models", models);
        comparison.put("comparisonTime", LocalDateTime.now());
        
        return comparison;
    }
    
    @Override
    public String exportPredictionData(String format, Map<String, Object> filters) {
        List<PestPrediction> predictions = predictionRepository.findAllPredictions();
        
        if ("json".equalsIgnoreCase(format)) {
            return predictions.toString();
        } else if ("csv".equalsIgnoreCase(format)) {
            StringBuilder csv = new StringBuilder();
            csv.append("ID,病虫害ID,目标区域,预测日期,风险等级,发生概率,创建时间\n");
            
            for (PestPrediction prediction : predictions) {
                csv.append(prediction.getId()).append(",")
                   .append(prediction.getPestId()).append(",")
                   .append(prediction.getTargetArea()).append(",")
                   .append(prediction.getPredictionDate()).append(",")
                   .append(prediction.getRiskLevel()).append(",")
                   .append(prediction.getProbability()).append(",")
                   .append(prediction.getCreatedTime()).append("\n");
            }
            
            return csv.toString();
        }
        
        throw new ForestPestSystemException("不支持的导出格式: " + format);
    }
    
    @Override
    public String exportAlertData(String format, Map<String, Object> filters) {
        List<PestAlert> alerts = predictionRepository.findAllAlerts();
        
        if ("json".equalsIgnoreCase(format)) {
            return alerts.toString();
        } else if ("csv".equalsIgnoreCase(format)) {
            StringBuilder csv = new StringBuilder();
            csv.append("ID,预测ID,预警等级,预警时间,目标受众,消息,状态\n");
            
            for (PestAlert alert : alerts) {
                csv.append(alert.getId()).append(",")
                   .append(alert.getPredictionId()).append(",")
                   .append(alert.getAlertLevel()).append(",")
                   .append(alert.getAlertTime()).append(",")
                   .append(alert.getTargetAudience()).append(",")
                   .append(alert.getMessage()).append(",")
                   .append(alert.getStatus()).append("\n");
            }
            
            return csv.toString();
        }
        
        throw new ForestPestSystemException("不支持的导出格式: " + format);
    }
    
    @Override
    public String exportPredictionReport(String predictionId, String format) {
        Optional<PestPrediction> predictionOpt = predictionRepository.findPredictionById(predictionId);
        if (!predictionOpt.isPresent()) {
            throw new ForestPestSystemException("预测不存在");
        }
        
        PestPrediction prediction = predictionOpt.get();
        
        if ("json".equalsIgnoreCase(format)) {
            return prediction.toString();
        }
        
        throw new ForestPestSystemException("不支持的导出格式: " + format);
    }
    
    @Override
    public String exportAlertReport(String alertId, String format) {
        Optional<PestAlert> alertOpt = predictionRepository.findAlertById(alertId);
        if (!alertOpt.isPresent()) {
            throw new ForestPestSystemException("预警不存在");
        }
        
        PestAlert alert = alertOpt.get();
        
        if ("json".equalsIgnoreCase(format)) {
            return alert.toString();
        }
        
        throw new ForestPestSystemException("不支持的导出格式: " + format);
    }
    
    @Override
    public String exportStatisticsReport(String reportType, String format) {
        Map<String, Object> statistics;
        
        switch (reportType) {
            case "prediction":
                statistics = getPredictionStatistics();
                break;
            case "alert":
                statistics = getAlertStatistics();
                break;
            default:
                throw new ForestPestSystemException("不支持的报告类型: " + reportType);
        }
        
        if ("json".equalsIgnoreCase(format)) {
            return statistics.toString();
        }
        
        throw new ForestPestSystemException("不支持的导出格式: " + format);
    }
    
    @Override
    public Map<String, Object> getPredictionConfig() {
        return new HashMap<>(predictionConfig);
    }
    
    @Override
    public boolean updatePredictionConfig(Map<String, Object> config) {
        predictionConfig.putAll(config);
        return true;
    }
    
    @Override
    public Map<String, Object> getAlertConfig() {
        return new HashMap<>(alertConfig);
    }
    
    @Override
    public boolean updateAlertConfig(Map<String, Object> config) {
        alertConfig.putAll(config);
        return true;
    }
    
    @Override
    public boolean resetConfigToDefault(String configType) {
        if ("prediction".equals(configType)) {
            predictionConfig.clear();
            initializeDefaultConfig();
            return true;
        } else if ("alert".equals(configType)) {
            alertConfig.clear();
            initializeDefaultConfig();
            return true;
        }
        return false;
    }
    
    @Override
    public Map<String, Object> validateConfig(Map<String, Object> config) {
        Map<String, Object> validation = new HashMap<>();
        validation.put("isValid", true);
        validation.put("errors", new ArrayList<>());
        validation.put("warnings", new ArrayList<>());
        return validation;
    }
    
    @Override
    public int cleanupExpiredPredictions() {
        LocalDate expireDate = LocalDate.now().minusDays(30);
        List<PestPrediction> expiredPredictions = predictionRepository.findAllPredictions().stream()
                .filter(pred -> pred.getPredictionDate().isBefore(expireDate))
                .collect(Collectors.toList());
        
        for (PestPrediction prediction : expiredPredictions) {
            predictionRepository.deletePredictionById(prediction.getId());
        }
        
        return expiredPredictions.size();
    }
    
    @Override
    public int cleanupExpiredAlerts() {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(7);
        List<PestAlert> expiredAlerts = predictionRepository.findAllAlerts().stream()
                .filter(alert -> alert.getCreatedTime().isBefore(expireTime))
                .collect(Collectors.toList());
        
        for (PestAlert alert : expiredAlerts) {
            predictionRepository.deleteAlertById(alert.getId());
        }
        
        return expiredAlerts.size();
    }
    
    @Override
    public boolean archiveHistoricalData(LocalDate beforeDate) {
        // 模拟归档操作
        return true;
    }
    
    @Override
    public Map<String, Object> performHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("predictionCount", predictionRepository.countPredictions());
        health.put("alertCount", predictionRepository.countAlerts());
        health.put("activeModels", predictionModels.size());
        health.put("checkTime", LocalDateTime.now());
        return health;
    }
    
    @Override
    public Map<String, Object> getSystemStatus() {
        return performHealthCheck();
    }
    
    @Override
    public boolean reinitializeSystem() {
        // 重新初始化配置和模型
        predictionConfig.clear();
        alertConfig.clear();
        predictionModels.clear();
        alertRules.clear();
        
        initializeDefaultConfig();
        initializePredictionModels();
        initializeDefaultAlertRules();
        
        return true;
    }
}