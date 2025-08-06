package com.forestpest.controller;

import com.forestpest.common.ApiResponse;
import com.forestpest.entity.PestPrediction;
import com.forestpest.entity.PestAlert;
import com.forestpest.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/prediction")
@CrossOrigin(origins = "*")
public class PredictionController extends BaseController {
    
    @Autowired
    private PredictionService predictionService;
    
    // ========== 预测管理接口 ==========
    

    @PostMapping
    public ApiResponse<PestPrediction> createPrediction(
            @RequestBody @Valid CreatePredictionRequest request) {
        
        try {
            PestPrediction prediction = predictionService.createPrediction(
                request.getPrediction(), request.getUserId());
            return success("创建预测成功", prediction);
        } catch (Exception e) {
            logger.error("创建预测失败", e);
            return error("创建预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预测详情
     */
    @GetMapping("/{predictionId}")
    public ApiResponse<PestPrediction> getPrediction(
            @PathVariable @NotBlank String predictionId) {
        
        try {
            PestPrediction prediction = predictionService.getPredictionById(predictionId);
            if (prediction == null) {
                return error("预测不存在");
            }
            return success(prediction);
        } catch (Exception e) {
            logger.error("获取预测失败", e);
            return error("获取预测失败: " + e.getMessage());
        }
    }
    

    @GetMapping
    public ApiResponse<List<PestPrediction>> getAllPredictions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            List<PestPrediction> predictions;
            if (page > 0 || size != 20) {
                predictions = predictionService.getPredictions(page, size);
            } else {
                predictions = predictionService.getAllPredictions();
            }
            return success(predictions);
        } catch (Exception e) {
            logger.error("获取预测列表失败", e);
            return error("获取预测列表失败: " + e.getMessage());
        }
    }
    
 
    @GetMapping("/user/{userId}")
    public ApiResponse<List<PestPrediction>> getUserPredictions(
            @PathVariable @NotBlank String userId) {
        
        try {
            List<PestPrediction> predictions = predictionService.getUserPredictions(userId);
            return success(predictions);
        } catch (Exception e) {
            logger.error("获取用户预测列表失败", e);
            return error("获取用户预测列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新预测
     */
    @PutMapping("/{predictionId}")
    public ApiResponse<PestPrediction> updatePrediction(
            @PathVariable @NotBlank String predictionId,
            @RequestBody @Valid UpdatePredictionRequest request) {
        
        try {
            PestPrediction prediction = predictionService.updatePrediction(
                predictionId, request.getPrediction(), request.getUserId());
            return success("更新预测成功", prediction);
        } catch (Exception e) {
            logger.error("更新预测失败", e);
            return error("更新预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除预测
     */
    @DeleteMapping("/{predictionId}")
    public ApiResponse<Boolean> deletePrediction(
            @PathVariable @NotBlank String predictionId,
            @RequestParam @NotBlank String userId) {
        
        try {
            boolean deleted = predictionService.deletePrediction(predictionId, userId);
            if (!deleted) {
                return error("删除失败，预测不存在或无权限");
            }
            return success("删除预测成功", true);
        } catch (Exception e) {
            logger.error("删除预测失败", e);
            return error("删除预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据病虫害ID获取预测
     */
    @GetMapping("/pest/{pestId}")
    public ApiResponse<List<PestPrediction>> getPredictionsByPest(
            @PathVariable @NotBlank String pestId) {
        
        try {
            List<PestPrediction> predictions = predictionService.getPredictionsByPestId(pestId);
            return success(predictions);
        } catch (Exception e) {
            logger.error("获取病虫害预测失败", e);
            return error("获取病虫害预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据目标区域获取预测
     */
    @GetMapping("/area/{targetArea}")
    public ApiResponse<List<PestPrediction>> getPredictionsByArea(
            @PathVariable @NotBlank String targetArea) {
        
        try {
            List<PestPrediction> predictions = predictionService.getPredictionsByArea(targetArea);
            return success(predictions);
        } catch (Exception e) {
            logger.error("获取区域预测失败", e);
            return error("获取区域预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据风险等级获取预测
     */
    @GetMapping("/risk/{riskLevel}")
    public ApiResponse<List<PestPrediction>> getPredictionsByRiskLevel(
            @PathVariable @NotBlank String riskLevel) {
        
        try {
            List<PestPrediction> predictions = predictionService.getPredictionsByRiskLevel(riskLevel);
            return success(predictions);
        } catch (Exception e) {
            logger.error("获取风险等级预测失败", e);
            return error("获取风险等级预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据日期范围获取预测
     */
    @GetMapping("/date-range")
    public ApiResponse<List<PestPrediction>> getPredictionsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<PestPrediction> predictions = predictionService.getPredictionsByDateRange(start, end);
            return success(predictions);
        } catch (Exception e) {
            logger.error("获取日期范围预测失败", e);
            return error("获取日期范围预测失败: " + e.getMessage());
        }
    }
    

    @GetMapping("/high-risk")
    public ApiResponse<List<PestPrediction>> getHighRiskPredictions() {
        
        try {
            List<PestPrediction> predictions = predictionService.getHighRiskPredictions();
            return success(predictions);
        } catch (Exception e) {
            logger.error("获取高风险预测失败", e);
            return error("获取高风险预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近的预测
     */
    @GetMapping("/recent")
    public ApiResponse<List<PestPrediction>> getRecentPredictions(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<PestPrediction> predictions = predictionService.getRecentPredictions(limit);
            return success(predictions);
        } catch (Exception e) {
            logger.error("获取最近预测失败", e);
            return error("获取最近预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索预测
     */
    @GetMapping("/search")
    public ApiResponse<List<PestPrediction>> searchPredictions(
            @RequestParam @NotBlank String keyword) {
        
        try {
            List<PestPrediction> predictions = predictionService.searchPredictions(keyword);
            return success(predictions);
        } catch (Exception e) {
            logger.error("搜索预测失败", e);
            return error("搜索预测失败: " + e.getMessage());
        }
    }
    
    // ========== 预警管理接口 ==========
    
    /**
     * 创建预警
     */
    @PostMapping("/alert")
    public ApiResponse<PestAlert> createAlert(
            @RequestBody @Valid CreateAlertRequest request) {
        
        try {
            PestAlert alert = predictionService.createAlert(
                request.getAlert(), request.getUserId());
            return success("创建预警成功", alert);
        } catch (Exception e) {
            logger.error("创建预警失败", e);
            return error("创建预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据预测创建预警
     */
    @PostMapping("/{predictionId}/alert")
    public ApiResponse<PestAlert> createAlertFromPrediction(
            @PathVariable @NotBlank String predictionId,
            @RequestBody @Valid CreateAlertFromPredictionRequest request) {
        
        try {
            PestAlert alert = predictionService.createAlertFromPrediction(
                predictionId, request.getUserId(), request.getAlertConfig());
            return success("根据预测创建预警成功", alert);
        } catch (Exception e) {
            logger.error("根据预测创建预警失败", e);
            return error("根据预测创建预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预警详情
     */
    @GetMapping("/alert/{alertId}")
    public ApiResponse<PestAlert> getAlert(
            @PathVariable @NotBlank String alertId) {
        
        try {
            PestAlert alert = predictionService.getAlertById(alertId);
            if (alert == null) {
                return error("预警不存在");
            }
            return success(alert);
        } catch (Exception e) {
            logger.error("获取预警失败", e);
            return error("获取预警失败: " + e.getMessage());
        }
    }
    

    @GetMapping("/alert")
    public ApiResponse<List<PestAlert>> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            List<PestAlert> alerts;
            if (page > 0 || size != 20) {
                alerts = predictionService.getAlerts(page, size);
            } else {
                alerts = predictionService.getAllAlerts();
            }
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取预警列表失败", e);
            return error("获取预警列表失败: " + e.getMessage());
        }
    }
    

    @GetMapping("/alert/user/{userId}")
    public ApiResponse<List<PestAlert>> getUserAlerts(
            @PathVariable @NotBlank String userId) {
        
        try {
            List<PestAlert> alerts = predictionService.getUserAlerts(userId);
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取用户预警列表失败", e);
            return error("获取用户预警列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新预警
     */
    @PutMapping("/alert/{alertId}")
    public ApiResponse<PestAlert> updateAlert(
            @PathVariable @NotBlank String alertId,
            @RequestBody @Valid UpdateAlertRequest request) {
        
        try {
            PestAlert alert = predictionService.updateAlert(
                alertId, request.getAlert(), request.getUserId());
            return success("更新预警成功", alert);
        } catch (Exception e) {
            logger.error("更新预警失败", e);
            return error("更新预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除预警
     */
    @DeleteMapping("/alert/{alertId}")
    public ApiResponse<Boolean> deleteAlert(
            @PathVariable @NotBlank String alertId,
            @RequestParam @NotBlank String userId) {
        
        try {
            boolean deleted = predictionService.deleteAlert(alertId, userId);
            if (!deleted) {
                return error("删除失败，预警不存在或无权限");
            }
            return success("删除预警成功", true);
        } catch (Exception e) {
            logger.error("删除预警失败", e);
            return error("删除预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据预测ID获取预警
     */
    @GetMapping("/{predictionId}/alerts")
    public ApiResponse<List<PestAlert>> getAlertsByPrediction(
            @PathVariable @NotBlank String predictionId) {
        
        try {
            List<PestAlert> alerts = predictionService.getAlertsByPredictionId(predictionId);
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取预测预警失败", e);
            return error("获取预测预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据预警等级获取预警
     */
    @GetMapping("/alert/level/{alertLevel}")
    public ApiResponse<List<PestAlert>> getAlertsByLevel(
            @PathVariable @NotBlank String alertLevel) {
        
        try {
            List<PestAlert> alerts = predictionService.getAlertsByLevel(alertLevel);
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取预警等级预警失败", e);
            return error("获取预警等级预警失败: " + e.getMessage());
        }
    }

    @GetMapping("/alert/status/{status}")
    public ApiResponse<List<PestAlert>> getAlertsByStatus(
            @PathVariable @NotBlank String status) {
        
        try {
            List<PestAlert> alerts = predictionService.getAlertsByStatus(status);
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取状态预警失败", e);
            return error("获取状态预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据目标区域获取预警
     */
    @GetMapping("/alert/area/{targetArea}")
    public ApiResponse<List<PestAlert>> getAlertsByArea(
            @PathVariable @NotBlank String targetArea) {
        
        try {
            List<PestAlert> alerts = predictionService.getAlertsByArea(targetArea);
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取区域预警失败", e);
            return error("获取区域预警失败: " + e.getMessage());
        }
    }
    

    @GetMapping("/alert/active")
    public ApiResponse<List<PestAlert>> getActiveAlerts() {
        
        try {
            List<PestAlert> alerts = predictionService.getActiveAlerts();
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取活跃预警失败", e);
            return error("获取活跃预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未处理的预警
     */
    @GetMapping("/alert/unhandled")
    public ApiResponse<List<PestAlert>> getUnhandledAlerts() {
        
        try {
            List<PestAlert> alerts = predictionService.getUnhandledAlerts();
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取未处理预警失败", e);
            return error("获取未处理预警失败: " + e.getMessage());
        }
    }
    
 
    @GetMapping("/alert/urgent")
    public ApiResponse<List<PestAlert>> getUrgentAlerts() {
        
        try {
            List<PestAlert> alerts = predictionService.getUrgentAlerts();
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取紧急预警失败", e);
            return error("获取紧急预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近的预警
     */
    @GetMapping("/alert/recent")
    public ApiResponse<List<PestAlert>> getRecentAlerts(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<PestAlert> alerts = predictionService.getRecentAlerts(limit);
            return success(alerts);
        } catch (Exception e) {
            logger.error("获取最近预警失败", e);
            return error("获取最近预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索预警
     */
    @GetMapping("/alert/search")
    public ApiResponse<List<PestAlert>> searchAlerts(
            @RequestParam @NotBlank String keyword) {
        
        try {
            List<PestAlert> alerts = predictionService.searchAlerts(keyword);
            return success(alerts);
        } catch (Exception e) {
            logger.error("搜索预警失败", e);
            return error("搜索预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 确认预警
     */
    @PostMapping("/alert/{alertId}/acknowledge")
    public ApiResponse<Boolean> acknowledgeAlert(
            @PathVariable @NotBlank String alertId,
            @RequestParam @NotBlank String userId) {
        
        try {
            boolean acknowledged = predictionService.acknowledgeAlert(alertId, userId);
            if (!acknowledged) {
                return error("确认失败，预警不存在");
            }
            return success("确认预警成功", true);
        } catch (Exception e) {
            logger.error("确认预警失败", e);
            return error("确认预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理预警
     */
    @PostMapping("/alert/{alertId}/handle")
    public ApiResponse<Boolean> handleAlert(
            @PathVariable @NotBlank String alertId,
            @RequestBody @Valid HandleAlertRequest request) {
        
        try {
            boolean handled = predictionService.handleAlert(
                alertId, request.getUserId(), request.getHandleResult());
            if (!handled) {
                return error("处理失败，预警不存在");
            }
            return success("处理预警成功", true);
        } catch (Exception e) {
            logger.error("处理预警失败", e);
            return error("处理预警失败: " + e.getMessage());
        }
    }
    
    // ========== 预测算法接口 ==========
    
    /**
     * 基于历史数据生成预测
     */
    @PostMapping("/generate/history")
    public ApiResponse<PestPrediction> generatePredictionFromHistory(
            @RequestBody @Valid GenerateFromHistoryRequest request) {
        
        try {
            PestPrediction prediction = predictionService.generatePredictionFromHistory(
                request.getPestId(), request.getTargetArea(), request.getParameters());
            return success("基于历史数据生成预测成功", prediction);
        } catch (Exception e) {
            logger.error("基于历史数据生成预测失败", e);
            return error("基于历史数据生成预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 基于天气数据生成预测
     */
    @PostMapping("/generate/weather")
    public ApiResponse<PestPrediction> generatePredictionFromWeather(
            @RequestBody @Valid GenerateFromWeatherRequest request) {
        
        try {
            PestPrediction prediction = predictionService.generatePredictionFromWeather(
                request.getPestId(), request.getTargetArea(), request.getWeatherData());
            return success("基于天气数据生成预测成功", prediction);
        } catch (Exception e) {
            logger.error("基于天气数据生成预测失败", e);
            return error("基于天气数据生成预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 基于环境因子生成预测
     */
    @PostMapping("/generate/environment")
    public ApiResponse<PestPrediction> generatePredictionFromEnvironment(
            @RequestBody @Valid GenerateFromEnvironmentRequest request) {
        
        try {
            PestPrediction prediction = predictionService.generatePredictionFromEnvironment(
                request.getPestId(), request.getTargetArea(), request.getEnvironmentData());
            return success("基于环境因子生成预测成功", prediction);
        } catch (Exception e) {
            logger.error("基于环境因子生成预测失败", e);
            return error("基于环境因子生成预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 综合预测模型
     */
    @PostMapping("/generate/comprehensive")
    public ApiResponse<PestPrediction> generateComprehensivePrediction(
            @RequestBody @Valid GenerateComprehensiveRequest request) {
        
        try {
            PestPrediction prediction = predictionService.generateComprehensivePrediction(
                request.getPestId(), request.getTargetArea(), request.getAllData());
            return success("综合预测生成成功", prediction);
        } catch (Exception e) {
            logger.error("综合预测生成失败", e);
            return error("综合预测生成失败: " + e.getMessage());
        }
    }
    

    @PostMapping("/{predictionId}/evaluate")
    public ApiResponse<Map<String, Object>> evaluatePredictionAccuracy(
            @PathVariable @NotBlank String predictionId,
            @RequestBody @Valid EvaluateAccuracyRequest request) {
        
        try {
            Map<String, Object> evaluation = predictionService.evaluatePredictionAccuracy(
                predictionId, request.getActualData());
            return success(evaluation);
        } catch (Exception e) {
            logger.error("评估预测准确性失败", e);
            return error("评估预测准确性失败: " + e.getMessage());
        }
    }
    
    // ========== 预警触发接口 ==========

    @PostMapping("/alert/check-triggers")
    public ApiResponse<List<PestAlert>> checkAlertTriggers() {
        
        try {
            List<PestAlert> triggeredAlerts = predictionService.checkAlertTriggers();
            return success(triggeredAlerts);
        } catch (Exception e) {
            logger.error("检查预警触发条件失败", e);
            return error("检查预警触发条件失败: " + e.getMessage());
        }
    }
    
    /**
     * 自动触发预警
     */
    @PostMapping("/alert/trigger-auto")
    public ApiResponse<List<PestAlert>> triggerAlertsAutomatically() {
        
        try {
            List<PestAlert> triggeredAlerts = predictionService.triggerAlertsAutomatically();
            return success("自动触发预警成功", triggeredAlerts);
        } catch (Exception e) {
            logger.error("自动触发预警失败", e);
            return error("自动触发预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据预测触发预警
     */
    @PostMapping("/{predictionId}/trigger-alert")
    public ApiResponse<PestAlert> triggerAlertFromPrediction(
            @PathVariable @NotBlank String predictionId,
            @RequestParam @NotBlank String triggerReason) {
        
        try {
            PestAlert alert = predictionService.triggerAlertFromPrediction(predictionId, triggerReason);
            if (alert == null) {
                return error("触发预警失败，预测不存在");
            }
            return success("触发预警成功", alert);
        } catch (Exception e) {
            logger.error("触发预警失败", e);
            return error("触发预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量触发预警
     */
    @PostMapping("/alert/batch-trigger")
    public ApiResponse<List<PestAlert>> batchTriggerAlerts(
            @RequestBody @Valid BatchTriggerAlertsRequest request) {
        
        try {
            List<PestAlert> alerts = predictionService.batchTriggerAlerts(
                request.getPredictionIds(), request.getTriggerReason());
            return success("批量触发预警成功", alerts);
        } catch (Exception e) {
            logger.error("批量触发预警失败", e);
            return error("批量触发预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置预警规则
     */
    @PostMapping("/alert/rule")
    public ApiResponse<Boolean> setAlertRule(
            @RequestBody @Valid SetAlertRuleRequest request) {
        
        try {
            boolean success = predictionService.setAlertRule(
                request.getRuleId(), request.getRuleConfig());
            return success("设置预警规则成功", success);
        } catch (Exception e) {
            logger.error("设置预警规则失败", e);
            return error("设置预警规则失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预警规则
     */
    @GetMapping("/alert/rule")
    public ApiResponse<List<Map<String, Object>>> getAlertRules() {
        
        try {
            List<Map<String, Object>> rules = predictionService.getAlertRules();
            return success(rules);
        } catch (Exception e) {
            logger.error("获取预警规则失败", e);
            return error("获取预警规则失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除预警规则
     */
    @DeleteMapping("/alert/rule/{ruleId}")
    public ApiResponse<Boolean> deleteAlertRule(
            @PathVariable @NotBlank String ruleId) {
        
        try {
            boolean deleted = predictionService.deleteAlertRule(ruleId);
            if (!deleted) {
                return error("删除失败，规则不存在");
            }
            return success("删除预警规则成功", true);
        } catch (Exception e) {
            logger.error("删除预警规则失败", e);
            return error("删除预警规则失败: " + e.getMessage());
        }
    }
    
    
    /**
     * 发送预警通知
     */
    @PostMapping("/alert/{alertId}/notify")
    public ApiResponse<Boolean> sendAlertNotification(
            @PathVariable @NotBlank String alertId,
            @RequestBody @Valid SendNotificationRequest request) {
        
        try {
            boolean sent = predictionService.sendAlertNotification(
                alertId, request.getRecipients());
            if (!sent) {
                return error("发送通知失败");
            }
            return success("发送预警通知成功", true);
        } catch (Exception e) {
            logger.error("发送预警通知失败", e);
            return error("发送预警通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量发送预警通知
     */
    @PostMapping("/alert/batch-notify")
    public ApiResponse<Map<String, Boolean>> batchSendAlertNotifications(
            @RequestBody @Valid BatchSendNotificationRequest request) {
        
        try {
            Map<String, Boolean> results = predictionService.batchSendAlertNotifications(
                request.getAlertIds(), request.getRecipients());
            return success(results);
        } catch (Exception e) {
            logger.error("批量发送预警通知失败", e);
            return error("批量发送预警通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取通知历史
     */
    @GetMapping("/alert/{alertId}/notification-history")
    public ApiResponse<List<Map<String, Object>>> getNotificationHistory(
            @PathVariable @NotBlank String alertId) {
        
        try {
            List<Map<String, Object>> history = predictionService.getNotificationHistory(alertId);
            return success(history);
        } catch (Exception e) {
            logger.error("获取通知历史失败", e);
            return error("获取通知历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置通知偏好
     */
    @PostMapping("/notification/preference")
    public ApiResponse<Boolean> setNotificationPreference(
            @RequestBody @Valid SetNotificationPreferenceRequest request) {
        
        try {
            boolean success = predictionService.setNotificationPreference(
                request.getUserId(), request.getPreferences());
            return success("设置通知偏好成功", success);
        } catch (Exception e) {
            logger.error("设置通知偏好失败", e);
            return error("设置通知偏好失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取通知偏好
     */
    @GetMapping("/notification/preference/{userId}")
    public ApiResponse<Map<String, Object>> getNotificationPreference(
            @PathVariable @NotBlank String userId) {
        
        try {
            Map<String, Object> preference = predictionService.getNotificationPreference(userId);
            return success(preference);
        } catch (Exception e) {
            logger.error("获取通知偏好失败", e);
            return error("获取通知偏好失败: " + e.getMessage());
        }
    }
    
    // ========== 统计分析接口 ==========
    
    /**
     * 获取预测统计
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getPredictionStatistics() {
        
        try {
            Map<String, Object> statistics = predictionService.getPredictionStatistics();
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取预测统计失败", e);
            return error("获取预测统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户预测统计
     */
    @GetMapping("/statistics/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserPredictionStatistics(
            @PathVariable @NotBlank String userId) {
        
        try {
            Map<String, Object> statistics = predictionService.getUserPredictionStatistics(userId);
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取用户预测统计失败", e);
            return error("获取用户预测统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预警统计
     */
    @GetMapping("/alert/statistics")
    public ApiResponse<Map<String, Object>> getAlertStatistics() {
        
        try {
            Map<String, Object> statistics = predictionService.getAlertStatistics();
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取预警统计失败", e);
            return error("获取预警统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户预警统计
     */
    @GetMapping("/alert/statistics/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserAlertStatistics(
            @PathVariable @NotBlank String userId) {
        
        try {
            Map<String, Object> statistics = predictionService.getUserAlertStatistics(userId);
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取用户预警统计失败", e);
            return error("获取用户预警统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/accuracy")
    public ApiResponse<Map<String, Object>> getPredictionAccuracyStatistics() {
        
        try {
            Map<String, Object> statistics = predictionService.getPredictionAccuracyStatistics();
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取预测准确性统计失败", e);
            return error("获取预测准确性统计失败" + e.getMessage());
        }
    }
    
    /**
     * 获取预警响应统计
     */
    @GetMapping("/alert/statistics/response")
    public ApiResponse<Map<String, Object>> getAlertResponseStatistics() {
        
        try {
            Map<String, Object> statistics = predictionService.getAlertResponseStatistics();
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取预警响应统计失败", e);
            return error("获取预警响应统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取风险等级分布
     */
    @GetMapping("/statistics/risk-distribution")
    public ApiResponse<Map<String, Long>> getRiskLevelDistribution() {
        
        try {
            Map<String, Long> distribution = predictionService.getRiskLevelDistribution();
            return success(distribution);
        } catch (Exception e) {
            logger.error("获取风险等级分布失败", e);
            return error("获取风险等级分布失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预警等级分布
     */
    @GetMapping("/alert/statistics/level-distribution")
    public ApiResponse<Map<String, Long>> getAlertLevelDistribution() {
        
        try {
            Map<String, Long> distribution = predictionService.getAlertLevelDistribution();
            return success(distribution);
        } catch (Exception e) {
            logger.error("获取预警等级分布失败", e);
            return error("获取预警等级分布失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取区域预警分布
     */
    @GetMapping("/alert/statistics/area-distribution")
    public ApiResponse<Map<String, Long>> getAreaAlertDistribution() {
        
        try {
            Map<String, Long> distribution = predictionService.getAreaAlertDistribution();
            return success(distribution);
        } catch (Exception e) {
            logger.error("获取区域预警分布失败", e);
            return error("获取区域预警分布失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预测趋势
     */
    @GetMapping("/trends")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getPredictionTrends(
            @RequestParam(defaultValue = "monthly") String period) {
        
        try {
            Map<String, List<Map<String, Object>>> trends = predictionService.getPredictionTrends(period);
            return success(trends);
        } catch (Exception e) {
            logger.error("获取预测趋势失败", e);
            return error("获取预测趋势失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预警趋势
     */
    @GetMapping("/alert/trends")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getAlertTrends(
            @RequestParam(defaultValue = "monthly") String period) {
        
        try {
            Map<String, List<Map<String, Object>>> trends = predictionService.getAlertTrends(period);
            return success(trends);
        } catch (Exception e) {
            logger.error("获取预警趋势失败", e);
            return error("获取预警趋势失败: " + e.getMessage());
        }
    }
    
    // ========== 模型管理接口 ==========
    
    /**
     * 获取预测模型列表
     */
    @GetMapping("/model")
    public ApiResponse<List<Map<String, Object>>> getPredictionModels() {
        
        try {
            List<Map<String, Object>> models = predictionService.getPredictionModels();
            return success(models);
        } catch (Exception e) {
            logger.error("获取预测模型列表失败", e);
            return error("获取预测模型列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预测模型详情
     */
    @GetMapping("/model/{modelId}")
    public ApiResponse<Map<String, Object>> getPredictionModelDetails(
            @PathVariable @NotBlank String modelId) {
        
        try {
            Map<String, Object> modelDetails = predictionService.getPredictionModelDetails(modelId);
            return success(modelDetails);
        } catch (Exception e) {
            logger.error("获取预测模型详情失败", e);
            return error("获取预测模型详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 训练预测模型
     */
    @PostMapping("/model/{modelId}/train")
    public ApiResponse<Boolean> trainPredictionModel(
            @PathVariable @NotBlank String modelId,
            @RequestBody @Valid TrainModelRequest request) {
        
        try {
            boolean success = predictionService.trainPredictionModel(
                modelId, request.getTrainingData());
            return success("训练预测模型成功", success);
        } catch (Exception e) {
            logger.error("训练预测模型失败", e);
            return error("训练预测模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 评估预测模型
     */
    @PostMapping("/model/{modelId}/evaluate")
    public ApiResponse<Map<String, Object>> evaluatePredictionModel(
            @PathVariable @NotBlank String modelId,
            @RequestBody @Valid EvaluateModelRequest request) {
        
        try {
            Map<String, Object> evaluation = predictionService.evaluatePredictionModel(
                modelId, request.getTestData());
            return success(evaluation);
        } catch (Exception e) {
            logger.error("评估预测模型失败", e);
            return error("评估预测模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 部署预测模型
     */
    @PostMapping("/model/{modelId}/deploy")
    public ApiResponse<Boolean> deployPredictionModel(
            @PathVariable @NotBlank String modelId) {
        
        try {
            boolean deployed = predictionService.deployPredictionModel(modelId);
            if (!deployed) {
                return error("部署失败，模型不存在");
            }
            return success("部署预测模型成功", true);
        } catch (Exception e) {
            logger.error("部署预测模型失败", e);
            return error("部署预测模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取模型性能指标
     */
    @GetMapping("/model/{modelId}/performance")
    public ApiResponse<Map<String, Object>> getModelPerformanceMetrics(
            @PathVariable @NotBlank String modelId) {
        
        try {
            Map<String, Object> metrics = predictionService.getModelPerformanceMetrics(modelId);
            return success(metrics);
        } catch (Exception e) {
            logger.error("获取模型性能指标失败", e);
            return error("获取模型性能指标失败: " + e.getMessage());
        }
    }
    
    /**
     * 比较预测模型
     */
    @PostMapping("/model/compare")
    public ApiResponse<Map<String, Object>> comparePredictionModels(
            @RequestBody @Valid CompareModelsRequest request) {
        
        try {
            Map<String, Object> comparison = predictionService.comparePredictionModels(
                request.getModelIds());
            return success(comparison);
        } catch (Exception e) {
            logger.error("比较预测模型失败", e);
            return error("比较预测模型失败: " + e.getMessage());
        }
    }
    
    // ========== 数据导出接口 ==========
    
    /**
     * 导出预测数据
     */
    @GetMapping("/export/data")
    public ApiResponse<String> exportPredictionData(
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(required = false) Map<String, Object> filters) {
        
        try {
            String exportData = predictionService.exportPredictionData(format, filters);
            return success(exportData);
        } catch (Exception e) {
            logger.error("导出预测数据失败", e);
            return error("导出预测数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出预警数据
     */
    @GetMapping("/alert/export/data")
    public ApiResponse<String> exportAlertData(
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(required = false) Map<String, Object> filters) {
        
        try {
            String exportData = predictionService.exportAlertData(format, filters);
            return success(exportData);
        } catch (Exception e) {
            logger.error("导出预警数据失败", e);
            return error("导出预警数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出预测报告
     */
    @GetMapping("/{predictionId}/export/report")
    public ApiResponse<String> exportPredictionReport(
            @PathVariable @NotBlank String predictionId,
            @RequestParam(defaultValue = "json") String format) {
        
        try {
            String report = predictionService.exportPredictionReport(predictionId, format);
            return success(report);
        } catch (Exception e) {
            logger.error("导出预测报告失败", e);
            return error("导出预测报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出预警报告
     */
    @GetMapping("/alert/{alertId}/export/report")
    public ApiResponse<String> exportAlertReport(
            @PathVariable @NotBlank String alertId,
            @RequestParam(defaultValue = "json") String format) {
        
        try {
            String report = predictionService.exportAlertReport(alertId, format);
            return success(report);
        } catch (Exception e) {
            logger.error("导出预警报告失败", e);
            return error("导出预警报告失败: " + e.getMessage());
        }
    }
    
    // ========== 配置管理接口 ==========
    
    /**
     * 获取预测配置
     */
    @GetMapping("/config")
    public ApiResponse<Map<String, Object>> getPredictionConfig() {
        
        try {
            Map<String, Object> config = predictionService.getPredictionConfig();
            return success(config);
        } catch (Exception e) {
            logger.error("获取预测配置失败", e);
            return error("获取预测配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新预测配置
     */
    @PutMapping("/config")
    public ApiResponse<Boolean> updatePredictionConfig(
            @RequestBody @Valid Map<String, Object> config) {
        
        try {
            boolean updated = predictionService.updatePredictionConfig(config);
            return success("更新预测配置成功", updated);
        } catch (Exception e) {
            logger.error("更新预测配置失败", e);
            return error("更新预测配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预警配置
     */
    @GetMapping("/alert/config")
    public ApiResponse<Map<String, Object>> getAlertConfig() {
        
        try {
            Map<String, Object> config = predictionService.getAlertConfig();
            return success(config);
        } catch (Exception e) {
            logger.error("获取预警配置失败", e);
            return error("获取预警配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新预警配置
     */
    @PutMapping("/alert/config")
    public ApiResponse<Boolean> updateAlertConfig(
            @RequestBody @Valid Map<String, Object> config) {
        
        try {
            boolean updated = predictionService.updateAlertConfig(config);
            return success("更新预警配置成功", updated);
        } catch (Exception e) {
            logger.error("更新预警配置失败", e);
            return error("更新预警配置失败: " + e.getMessage());
        }
    }
    

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> performHealthCheck() {
        
        try {
            Map<String, Object> health = predictionService.performHealthCheck();
            return success(health);
        } catch (Exception e) {
            logger.error("系统健康检查失败", e);
            return error("系统健康检查失败"+ e.getMessage());
        }
    }
    
    // ========== 请求对象==========
    
    /**
     * 创建预测请求
     */
    public static class CreatePredictionRequest {
        @Valid
        private PestPrediction prediction;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public PestPrediction getPrediction() {
            return prediction;
        }
        
        public void setPrediction(PestPrediction prediction) {
            this.prediction = prediction;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 更新预测请求
     */
    public static class UpdatePredictionRequest {
        @Valid
        private PestPrediction prediction;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public PestPrediction getPrediction() {
            return prediction;
        }
        
        public void setPrediction(PestPrediction prediction) {
            this.prediction = prediction;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 创建预警请求
     */
    public static class CreateAlertRequest {
        @Valid
        private PestAlert alert;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public PestAlert getAlert() {
            return alert;
        }
        
        public void setAlert(PestAlert alert) {
            this.alert = alert;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 根据预测创建预警请求
     */
    public static class CreateAlertFromPredictionRequest {
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private Map<String, Object> alertConfig;
        
        // Getters and Setters
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public Map<String, Object> getAlertConfig() {
            return alertConfig;
        }
        
        public void setAlertConfig(Map<String, Object> alertConfig) {
            this.alertConfig = alertConfig;
        }
    }
    
    /**
     * 更新预警请求
     */
    public static class UpdateAlertRequest {
        @Valid
        private PestAlert alert;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public PestAlert getAlert() {
            return alert;
        }
        
        public void setAlert(PestAlert alert) {
            this.alert = alert;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 处理预警请求
     */
    public static class HandleAlertRequest {
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        @NotBlank(message = "处理结果不能为空")
        private String handleResult;
        
        // Getters and Setters
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getHandleResult() {
            return handleResult;
        }
        
        public void setHandleResult(String handleResult) {
            this.handleResult = handleResult;
        }
    }
    
    /**
     * 基于历史数据生成预测请求
     */
    public static class GenerateFromHistoryRequest {
        @NotBlank(message = "病虫害ID不能为空")
        private String pestId;
        
        @NotBlank(message = "目标区域不能为空")
        private String targetArea;
        
        private Map<String, Object> parameters;
        
        // Getters and Setters
        public String getPestId() {
            return pestId;
        }
        
        public void setPestId(String pestId) {
            this.pestId = pestId;
        }
        
        public String getTargetArea() {
            return targetArea;
        }
        
        public void setTargetArea(String targetArea) {
            this.targetArea = targetArea;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
        
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
    
    /**
     * 基于天气数据生成预测请求
     */
    public static class GenerateFromWeatherRequest {
        @NotBlank(message = "病虫害ID不能为空")
        private String pestId;
        
        @NotBlank(message = "目标区域不能为空")
        private String targetArea;
        
        private Map<String, Object> weatherData;
        
        // Getters and Setters
        public String getPestId() {
            return pestId;
        }
        
        public void setPestId(String pestId) {
            this.pestId = pestId;
        }
        
        public String getTargetArea() {
            return targetArea;
        }
        
        public void setTargetArea(String targetArea) {
            this.targetArea = targetArea;
        }
        
        public Map<String, Object> getWeatherData() {
            return weatherData;
        }
        
        public void setWeatherData(Map<String, Object> weatherData) {
            this.weatherData = weatherData;
        }
    }
    
    /**
     * 基于环境因子生成预测请求
     */
    public static class GenerateFromEnvironmentRequest {
        @NotBlank(message = "病虫害ID不能为空")
        private String pestId;
        
        @NotBlank(message = "目标区域不能为空")
        private String targetArea;
        
        private Map<String, Object> environmentData;
        
        // Getters and Setters
        public String getPestId() {
            return pestId;
        }
        
        public void setPestId(String pestId) {
            this.pestId = pestId;
        }
        
        public String getTargetArea() {
            return targetArea;
        }
        
        public void setTargetArea(String targetArea) {
            this.targetArea = targetArea;
        }
        
        public Map<String, Object> getEnvironmentData() {
            return environmentData;
        }
        
        public void setEnvironmentData(Map<String, Object> environmentData) {
            this.environmentData = environmentData;
        }
    }
    
    /**
     * 综合预测请求
     */
    public static class GenerateComprehensiveRequest {
        @NotBlank(message = "病虫害ID不能为空")
        private String pestId;
        
        @NotBlank(message = "目标区域不能为空")
        private String targetArea;
        
        private Map<String, Object> allData;
        
        // Getters and Setters
        public String getPestId() {
            return pestId;
        }
        
        public void setPestId(String pestId) {
            this.pestId = pestId;
        }
        
        public String getTargetArea() {
            return targetArea;
        }
        
        public void setTargetArea(String targetArea) {
            this.targetArea = targetArea;
        }
        
        public Map<String, Object> getAllData() {
            return allData;
        }
        
        public void setAllData(Map<String, Object> allData) {
            this.allData = allData;
        }
    }
    
    /**
     * 评估准确性请
     */
    public static class EvaluateAccuracyRequest {
        private Map<String, Object> actualData;
        
        // Getters and Setters
        public Map<String, Object> getActualData() {
            return actualData;
        }
        
        public void setActualData(Map<String, Object> actualData) {
            this.actualData = actualData;
        }
    }
    
    /**
     * 批量触发预警请求
     */
    public static class BatchTriggerAlertsRequest {
        @NotEmpty(message = "预测ID列表不能为空")
        private List<String> predictionIds;
        
        @NotBlank(message = "触发原因不能为空")
        private String triggerReason;
        
        // Getters and Setters
        public List<String> getPredictionIds() {
            return predictionIds;
        }
        
        public void setPredictionIds(List<String> predictionIds) {
            this.predictionIds = predictionIds;
        }
        
        public String getTriggerReason() {
            return triggerReason;
        }
        
        public void setTriggerReason(String triggerReason) {
            this.triggerReason = triggerReason;
        }
    }
    
    /**
     * 设置预警规则请求
     */
    public static class SetAlertRuleRequest {
        @NotBlank(message = "规则ID不能为空")
        private String ruleId;
        
        private Map<String, Object> ruleConfig;
        
        // Getters and Setters
        public String getRuleId() {
            return ruleId;
        }
        
        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }
        
        public Map<String, Object> getRuleConfig() {
            return ruleConfig;
        }
        
        public void setRuleConfig(Map<String, Object> ruleConfig) {
            this.ruleConfig = ruleConfig;
        }
    }
    
    /**
     * 发送通知请求
     */
    public static class SendNotificationRequest {
        @NotEmpty(message = "接收者列表不能为空")
        private List<String> recipients;
        
        // Getters and Setters
        public List<String> getRecipients() {
            return recipients;
        }
        
        public void setRecipients(List<String> recipients) {
            this.recipients = recipients;
        }
    }
    
    /**
     * 批量发送通知请求
     */
    public static class BatchSendNotificationRequest {
        @NotEmpty(message = "预警ID列表不能为空")
        private List<String> alertIds;
        
        @NotEmpty(message = "接收者列表不能为空")
        private List<String> recipients;
        
        // Getters and Setters
        public List<String> getAlertIds() {
            return alertIds;
        }
        
        public void setAlertIds(List<String> alertIds) {
            this.alertIds = alertIds;
        }
        
        public List<String> getRecipients() {
            return recipients;
        }
        
        public void setRecipients(List<String> recipients) {
            this.recipients = recipients;
        }
    }
    
    /**
     * 设置通知偏好请求
     */
    public static class SetNotificationPreferenceRequest {
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private Map<String, Object> preferences;
        
        // Getters and Setters
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public Map<String, Object> getPreferences() {
            return preferences;
        }
        
        public void setPreferences(Map<String, Object> preferences) {
            this.preferences = preferences;
        }
    }
    
    /**
     * 训练模型请求
     */
    public static class TrainModelRequest {
        private Map<String, Object> trainingData;
        
        // Getters and Setters
        public Map<String, Object> getTrainingData() {
            return trainingData;
        }
        
        public void setTrainingData(Map<String, Object> trainingData) {
            this.trainingData = trainingData;
        }
    }
    
    /**
     * 评估模型请求
     */
    public static class EvaluateModelRequest {
        private Map<String, Object> testData;
        
        // Getters and Setters
        public Map<String, Object> getTestData() {
            return testData;
        }
        
        public void setTestData(Map<String, Object> testData) {
            this.testData = testData;
        }
    }
    
    /**
     * 比较模型请求
     */
    public static class CompareModelsRequest {
        @NotEmpty(message = "模型ID列表不能为空")
        private List<String> modelIds;
        
        // Getters and Setters
        public List<String> getModelIds() {
            return modelIds;
        }
        
        public void setModelIds(List<String> modelIds) {
            this.modelIds = modelIds;
        }
    }
}
