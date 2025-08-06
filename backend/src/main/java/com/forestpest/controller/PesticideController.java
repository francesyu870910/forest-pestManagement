package com.forestpest.controller;

import com.forestpest.common.ApiResponse;
import com.forestpest.entity.Pesticide;
import com.forestpest.entity.PesticideUsageRecord;
import com.forestpest.service.PesticideManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 药剂管理控制
 */
@RestController
@RequestMapping("/api/pesticide")
@CrossOrigin(origins = "*")
public class PesticideController {
    
    @Autowired
    private PesticideManagementService pesticideManagementService;
    
    /**
     * 获取药剂库存列表
     */
    @GetMapping("/inventory")
    public ApiResponse<List<Pesticide>> getPesticideInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<Pesticide> inventory;
        if (page > 0 || size != 20) {
            inventory = pesticideManagementService.getPesticideInventory(page, size);
        } else {
            inventory = pesticideManagementService.getPesticideInventory();
        }
        return ApiResponse.success(inventory);
    }
    
    /**
     * 根据ID获取药剂详情
     */
    @GetMapping("/{pesticideId}")
    public ApiResponse<Pesticide> getPesticideById(
            @PathVariable @NotBlank String pesticideId) {
        
        Pesticide pesticide = pesticideManagementService.getPesticideById(pesticideId);
        if (pesticide == null) {
            return ApiResponse.error("药剂不存在");
        }
        return ApiResponse.success(pesticide);
    }
    
    /**
     * 根据名称搜索药剂
     */
    @GetMapping("/search")
    public ApiResponse<List<Pesticide>> searchPesticidesByName(
            @RequestParam @NotBlank String name) {
        
        List<Pesticide> pesticides = pesticideManagementService.searchPesticidesByName(name);
        return ApiResponse.success(pesticides);
    }
    
    /**
     * 根据类型获取药剂列表
     */
    @GetMapping("/type/{type}")
    public ApiResponse<List<Pesticide>> getPesticidesByType(
            @PathVariable @NotBlank String type) {
        
        List<Pesticide> pesticides = pesticideManagementService.getPesticidesByType(type);
        return ApiResponse.success(pesticides);
    }
    
    /**
     * 根据有效成分搜索药剂
     */
    @GetMapping("/search/ingredient")
    public ApiResponse<List<Pesticide>> searchPesticidesByActiveIngredient(
            @RequestParam @NotBlank String activeIngredient) {
        
        List<Pesticide> pesticides = pesticideManagementService.searchPesticidesByActiveIngredient(activeIngredient);
        return ApiResponse.success(pesticides);
    }
    
    /**
     * 添加新药
     */
    @PostMapping
    public ApiResponse<Pesticide> addPesticide(
            @RequestBody @Valid AddPesticideRequest request) {
        
        Pesticide pesticide = pesticideManagementService.addPesticide(request.getPesticide(), request.getUserId());
        return ApiResponse.success(pesticide);
    }
    
    /**
     * 更新药剂信息
     */
    @PutMapping("/{pesticideId}")
    public ApiResponse<Pesticide> updatePesticide(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid UpdatePesticideRequest request) {
        
        Pesticide pesticide = pesticideManagementService.updatePesticide(
            pesticideId, request.getPesticide(), request.getUserId());
        return ApiResponse.success(pesticide);
    }
    
    /**
     * 删除药剂
     */
    @DeleteMapping("/{pesticideId}")
    public ApiResponse<Boolean> deletePesticide(
            @PathVariable @NotBlank String pesticideId,
            @RequestParam @NotBlank String userId) {
        
        boolean deleted = pesticideManagementService.deletePesticide(pesticideId, userId);
        if (!deleted) {
            return ApiResponse.error("删除失败，药剂不存在或有库存");
        }
        return ApiResponse.success(true);
    }
    
    /**
     * 药剂入库操作
     */
    @PostMapping("/{pesticideId}/stock/add")
    public ApiResponse<Pesticide> addStock(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid StockOperationRequest request) {
        
        Pesticide pesticide = pesticideManagementService.addStock(
            pesticideId, request.getQuantity(), request.getUserId(), request.getNotes());
        return ApiResponse.success(pesticide);
    }
    
    /**
     * 药剂出库操作
     */
    @PostMapping("/{pesticideId}/stock/remove")
    public ApiResponse<Pesticide> removeStock(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid StockOperationRequest request) {
        
        Pesticide pesticide = pesticideManagementService.removeStock(
            pesticideId, request.getQuantity(), request.getUserId(), request.getNotes());
        return ApiResponse.success(pesticide);
    }
    
    /**
     * 药剂调拨操作
     */
    @PostMapping("/{pesticideId}/stock/transfer")
    public ApiResponse<Boolean> transferStock(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid TransferStockRequest request) {
        
        boolean success = pesticideManagementService.transferStock(
            pesticideId, request.getQuantity(), request.getFromLocation(), 
            request.getToLocation(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 记录药剂使用
     */
    @PostMapping("/usage")
    public ApiResponse<PesticideUsageRecord> recordUsage(
            @RequestBody @Valid RecordUsageRequest request) {
        
        PesticideUsageRecord record = pesticideManagementService.recordUsage(
            request.getUsageRecord(), request.getUserId());
        return ApiResponse.success(record);
    }
    
    /**
     * 获取药剂使用记录
     */
    @GetMapping("/{pesticideId}/usage")
    public ApiResponse<List<PesticideUsageRecord>> getUsageRecords(
            @PathVariable @NotBlank String pesticideId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<PesticideUsageRecord> records;
        if (page > 0 || size != 20) {
            records = pesticideManagementService.getUsageRecords(pesticideId, page, size);
        } else {
            records = pesticideManagementService.getUsageRecords(pesticideId);
        }
        return ApiResponse.success(records);
    }
    
    /**
     * 获取用户的药剂使用记
     */
    @GetMapping("/usage/user/{userId}")
    public ApiResponse<List<PesticideUsageRecord>> getUserUsageRecords(
            @PathVariable @NotBlank String userId) {
        
        List<PesticideUsageRecord> records = pesticideManagementService.getUserUsageRecords(userId);
        return ApiResponse.success(records);
    }
    
    /**
     * 获取指定时间范围的使用记
     */
    @GetMapping("/usage/date-range")
    public ApiResponse<List<PesticideUsageRecord>> getUsageRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<PesticideUsageRecord> records = pesticideManagementService.getUsageRecordsByDateRange(startDate, endDate);
        return ApiResponse.success(records);
    }
    
    /**
     * 获取库存预警列表
     */
    @GetMapping("/alerts/low-stock")
    public ApiResponse<List<Pesticide>> getLowStockPesticides() {
        List<Pesticide> lowStock = pesticideManagementService.getLowStockPesticides();
        return ApiResponse.success(lowStock);
    }
    
    /**
     * 获取过期预警列表
     */
    @GetMapping("/alerts/expiring")
    public ApiResponse<List<Pesticide>> getExpiringPesticides() {
        List<Pesticide> expiring = pesticideManagementService.getExpiringPesticides();
        return ApiResponse.success(expiring);
    }
    
    /**
     * 获取已过期药剂列
     */
    @GetMapping("/alerts/expired")
    public ApiResponse<List<Pesticide>> getExpiredPesticides() {
        List<Pesticide> expired = pesticideManagementService.getExpiredPesticides();
        return ApiResponse.success(expired);
    }
    
    /**
     * 设置库存预警�?
     */
    @PostMapping("/{pesticideId}/alert")
    public ApiResponse<Boolean> setStockAlert(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid SetAlertRequest request) {
        
        boolean success = pesticideManagementService.setStockAlert(
            pesticideId, request.getMinStock(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取药剂使用统计
     */
    @GetMapping("/statistics/usage")
    public ApiResponse<Map<String, Object>> getUsageStatistics() {
        Map<String, Object> statistics = pesticideManagementService.getUsageStatistics();
        return ApiResponse.success(statistics);
    }
    
    /**
     * 获取用户药剂使用统计
     */
    @GetMapping("/statistics/usage/{userId}")
    public ApiResponse<Map<String, Object>> getUserUsageStatistics(
            @PathVariable @NotBlank String userId) {
        
        Map<String, Object> statistics = pesticideManagementService.getUserUsageStatistics(userId);
        return ApiResponse.success(statistics);
    }
    
    /**
     * 获取药剂成本分析
     */
    @GetMapping("/analysis/cost")
    public ApiResponse<Map<String, Object>> getCostAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Object> analysis;
        if (startDate != null && endDate != null) {
            analysis = pesticideManagementService.getCostAnalysisByDateRange(startDate, endDate);
        } else {
            analysis = pesticideManagementService.getCostAnalysis();
        }
        return ApiResponse.success(analysis);
    }
    
    /**
     * 获取库存价值统
     */
    @GetMapping("/statistics/inventory-value")
    public ApiResponse<Map<String, Object>> getInventoryValueStatistics() {
        Map<String, Object> statistics = pesticideManagementService.getInventoryValueStatistics();
        return ApiResponse.success(statistics);
    }
    
    /**
     * 获取药剂安全信息
     */
    @GetMapping("/{pesticideId}/safety")
    public ApiResponse<Map<String, Object>> getPesticideSafetyInfo(
            @PathVariable @NotBlank String pesticideId) {
        
        Map<String, Object> safetyInfo = pesticideManagementService.getPesticideSafetyInfo(pesticideId);
        return ApiResponse.success(safetyInfo);
    }
    
    /**
     * 更新药剂安全信息
     */
    @PutMapping("/{pesticideId}/safety")
    public ApiResponse<Boolean> updatePesticideSafetyInfo(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid UpdateSafetyInfoRequest request) {
        
        boolean success = pesticideManagementService.updatePesticideSafetyInfo(
            pesticideId, request.getSafetyInfo(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取药剂使用建议
     */
    @GetMapping("/{pesticideId}/suggestions")
    public ApiResponse<List<String>> getUsageSuggestions(
            @PathVariable @NotBlank String pesticideId,
            @RequestParam(required = false) String pestType) {
        
        List<String> suggestions = pesticideManagementService.getUsageSuggestions(pesticideId, pestType);
        return ApiResponse.success(suggestions);
    }
    
    /**
     * 检查药剂兼�?
     */
    @PostMapping("/compatibility/check")
    public ApiResponse<Map<String, Object>> checkPesticideCompatibility(
            @RequestBody @Valid CompatibilityCheckRequest request) {
        
        Map<String, Object> compatibility = pesticideManagementService.checkPesticideCompatibility(request.getPesticideIds());
        return ApiResponse.success(compatibility);
    }
    
    /**
     * 获取药剂混配建议
     */
    @GetMapping("/mixing/suggestions")
    public ApiResponse<List<Map<String, Object>>> getMixingSuggestions(
            @RequestParam @NotBlank String targetPest) {
        
        List<Map<String, Object>> suggestions = pesticideManagementService.getMixingSuggestions(targetPest);
        return ApiResponse.success(suggestions);
    }
    
    /**
     * 计算药剂用量
     */
    @PostMapping("/dosage/calculate")
    public ApiResponse<Map<String, Object>> calculateDosage(
            @RequestBody @Valid CalculateDosageRequest request) {
        
        Map<String, Object> dosage = pesticideManagementService.calculateDosage(
            request.getPesticideId(), request.getArea(), request.getCropType());
        return ApiResponse.success(dosage);
    }
    
    /**
     * 获取药剂库存报告
     */
    @GetMapping("/report/inventory")
    public ApiResponse<Map<String, Object>> getInventoryReport() {
        Map<String, Object> report = pesticideManagementService.getInventoryReport();
        return ApiResponse.success(report);
    }
    
    /**
     * 获取药剂使用趋势
     */
    @GetMapping("/trends/usage")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getUsageTrends(
            @RequestParam(defaultValue = "monthly") String period) {
        
        Map<String, List<Map<String, Object>>> trends = pesticideManagementService.getUsageTrends(period);
        return ApiResponse.success(trends);
    }
    
    /**
     * 导出库存数据
     */
    @GetMapping("/export/inventory")
    public ApiResponse<String> exportInventoryData(
            @RequestParam(defaultValue = "json") String format) {
        
        String exportData = pesticideManagementService.exportInventoryData(format);
        return ApiResponse.success(exportData);
    }
    
    /**
     * 导出使用记录
     */
    @GetMapping("/export/usage")
    public ApiResponse<String> exportUsageRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "json") String format) {
        
        String exportData = pesticideManagementService.exportUsageRecords(startDate, endDate, format);
        return ApiResponse.success(exportData);
    }
    
    /**
     * 批量更新库存
     */
    @PostMapping("/stock/batch-update")
    public ApiResponse<List<Pesticide>> batchUpdateStock(
            @RequestBody @Valid BatchUpdateStockRequest request) {
        
        List<Pesticide> updatedPesticides = pesticideManagementService.batchUpdateStock(
            request.getStockUpdates(), request.getUserId());
        return ApiResponse.success(updatedPesticides);
    }
    
    /**
     * 获取药剂供应�?
     */
    @GetMapping("/{pesticideId}/suppliers")
    public ApiResponse<List<Map<String, Object>>> getSupplierInfo(
            @PathVariable @NotBlank String pesticideId) {
        
        List<Map<String, Object>> suppliers = pesticideManagementService.getSupplierInfo(pesticideId);
        return ApiResponse.success(suppliers);
    }
    
    /**
     * 添加药剂供应�?
     */
    @PostMapping("/{pesticideId}/suppliers")
    public ApiResponse<Boolean> addSupplier(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid AddSupplierRequest request) {
        
        boolean success = pesticideManagementService.addSupplier(
            pesticideId, request.getSupplierInfo(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取药剂采购建议
     */
    @GetMapping("/purchase/suggestions")
    public ApiResponse<List<Map<String, Object>>> getPurchaseSuggestions() {
        List<Map<String, Object>> suggestions = pesticideManagementService.getPurchaseSuggestions();
        return ApiResponse.success(suggestions);
    }
    
    /**
     * 创建采购订单
     */
    @PostMapping("/purchase/order")
    public ApiResponse<Map<String, Object>> createPurchaseOrder(
            @RequestBody @Valid CreatePurchaseOrderRequest request) {
        
        Map<String, Object> order = pesticideManagementService.createPurchaseOrder(
            request.getOrderItems(), request.getUserId());
        return ApiResponse.success(order);
    }
    
    /**
     * 获取采购订单列表
     */
    @GetMapping("/purchase/orders/{userId}")
    public ApiResponse<List<Map<String, Object>>> getPurchaseOrders(
            @PathVariable @NotBlank String userId) {
        
        List<Map<String, Object>> orders = pesticideManagementService.getPurchaseOrders(userId);
        return ApiResponse.success(orders);
    }
    
    /**
     * 更新采购订单状�?
     */
    @PutMapping("/purchase/order/{orderId}/status")
    public ApiResponse<Boolean> updatePurchaseOrderStatus(
            @PathVariable @NotBlank String orderId,
            @RequestBody @Valid UpdateOrderStatusRequest request) {
        
        boolean success = pesticideManagementService.updatePurchaseOrderStatus(
            orderId, request.getStatus(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取药剂质量检测记�?
     */
    @GetMapping("/{pesticideId}/quality-tests")
    public ApiResponse<List<Map<String, Object>>> getQualityTestRecords(
            @PathVariable @NotBlank String pesticideId) {
        
        List<Map<String, Object>> records = pesticideManagementService.getQualityTestRecords(pesticideId);
        return ApiResponse.success(records);
    }
    
    /**
     * 添加质量检测记�?
     */
    @PostMapping("/{pesticideId}/quality-tests")
    public ApiResponse<Boolean> addQualityTestRecord(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid AddQualityTestRequest request) {
        
        boolean success = pesticideManagementService.addQualityTestRecord(
            pesticideId, request.getTestRecord(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取药剂存储条件要求
     */
    @GetMapping("/{pesticideId}/storage-requirements")
    public ApiResponse<Map<String, Object>> getStorageRequirements(
            @PathVariable @NotBlank String pesticideId) {
        
        Map<String, Object> requirements = pesticideManagementService.getStorageRequirements(pesticideId);
        return ApiResponse.success(requirements);
    }
    
    /**
     * 检查存储条件合�?
     */
    @GetMapping("/storage/compliance")
    public ApiResponse<Map<String, Object>> checkStorageCompliance(
            @RequestParam @NotBlank String location) {
        
        Map<String, Object> compliance = pesticideManagementService.checkStorageCompliance(location);
        return ApiResponse.success(compliance);
    }
    
    /**
     * 获取药剂处置建议
     */
    @GetMapping("/{pesticideId}/disposal/suggestions")
    public ApiResponse<List<String>> getDisposalSuggestions(
            @PathVariable @NotBlank String pesticideId) {
        
        List<String> suggestions = pesticideManagementService.getDisposalSuggestions(pesticideId);
        return ApiResponse.success(suggestions);
    }
    
    /**
     * 记录药剂处置
     */
    @PostMapping("/{pesticideId}/disposal")
    public ApiResponse<Boolean> recordDisposal(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid RecordDisposalRequest request) {
        
        boolean success = pesticideManagementService.recordDisposal(
            pesticideId, request.getQuantity(), request.getReason(), 
            request.getMethod(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取药剂处置记录
     */
    @GetMapping("/disposal/records")
    public ApiResponse<List<Map<String, Object>>> getDisposalRecords() {
        List<Map<String, Object>> records = pesticideManagementService.getDisposalRecords();
        return ApiResponse.success(records);
    }
    
    /**
     * 获取药剂效果评估
     */
    @GetMapping("/{pesticideId}/effectiveness")
    public ApiResponse<Map<String, Object>> getEffectivenessEvaluation(
            @PathVariable @NotBlank String pesticideId) {
        
        Map<String, Object> evaluation = pesticideManagementService.getEffectivenessEvaluation(pesticideId);
        return ApiResponse.success(evaluation);
    }
    
    /**
     * 添加药剂效果评估
     */
    @PostMapping("/{pesticideId}/effectiveness")
    public ApiResponse<Boolean> addEffectivenessEvaluation(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid AddEvaluationRequest request) {
        
        boolean success = pesticideManagementService.addEffectivenessEvaluation(
            pesticideId, request.getEvaluation(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取药剂抗性监测数据
     */
    @GetMapping("/{pesticideId}/resistance")
    public ApiResponse<Map<String, Object>> getResistanceMonitoring(
            @PathVariable @NotBlank String pesticideId) {
        
        Map<String, Object> monitoring = pesticideManagementService.getResistanceMonitoring(pesticideId);
        return ApiResponse.success(monitoring);
    }
    
    /**
     * 更新抗性监测数据
     */
    @PutMapping("/{pesticideId}/resistance")
    public ApiResponse<Boolean> updateResistanceMonitoring(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid UpdateResistanceRequest request) {
        
        boolean success = pesticideManagementService.updateResistanceMonitoring(
            pesticideId, request.getMonitoringData(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取药剂轮换建议
     */
    @GetMapping("/{pesticideId}/rotation/suggestions")
    public ApiResponse<List<String>> getRotationSuggestions(
            @PathVariable @NotBlank String pesticideId) {
        
        List<String> suggestions = pesticideManagementService.getRotationSuggestions(pesticideId);
        return ApiResponse.success(suggestions);
    }
    
    /**
     * 获取药剂分类统计
     */
    @GetMapping("/statistics/type")
    public ApiResponse<Map<String, Long>> getPesticideTypeStatistics() {
        Map<String, Long> statistics = pesticideManagementService.getPesticideTypeStatistics();
        return ApiResponse.success(statistics);
    }
    
    /**
     * 获取热门药剂排行
     */
    @GetMapping("/popular")
    public ApiResponse<List<Map<String, Object>>> getPopularPesticides(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> popular = pesticideManagementService.getPopularPesticides(limit);
        return ApiResponse.success(popular);
    }
    
    /**
     * 获取药剂库存周转
     */
    @GetMapping("/statistics/turnover")
    public ApiResponse<Map<String, Double>> getInventoryTurnoverRate() {
        Map<String, Double> turnoverRate = pesticideManagementService.getInventoryTurnoverRate();
        return ApiResponse.success(turnoverRate);
    }
    
    /**
     * 预测药剂需求
     */
    @GetMapping("/{pesticideId}/demand/predict")
    public ApiResponse<Map<String, Object>> predictPesticideDemand(
            @PathVariable @NotBlank String pesticideId,
            @RequestParam(defaultValue = "30") int days) {
        
        Map<String, Object> prediction = pesticideManagementService.predictPesticideDemand(pesticideId, days);
        return ApiResponse.success(prediction);
    }
    
    /**
     * 获取药剂价格趋势
     */
    @GetMapping("/{pesticideId}/price/trends")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getPriceTrends(
            @PathVariable @NotBlank String pesticideId,
            @RequestParam(defaultValue = "monthly") String period) {
        
        Map<String, List<Map<String, Object>>> trends = pesticideManagementService.getPriceTrends(pesticideId, period);
        return ApiResponse.success(trends);
    }
    
    /**
     * 设置药剂价格预警
     */
    @PostMapping("/{pesticideId}/price/alert")
    public ApiResponse<Boolean> setPriceAlert(
            @PathVariable @NotBlank String pesticideId,
            @RequestBody @Valid SetPriceAlertRequest request) {
        
        boolean success = pesticideManagementService.setPriceAlert(
            pesticideId, request.getMaxPrice(), request.getUserId());
        return ApiResponse.success(success);
    }
    
    /**
     * 获取价格预警列表
     */
    @GetMapping("/price/alerts/{userId}")
    public ApiResponse<List<Map<String, Object>>> getPriceAlerts(
            @PathVariable @NotBlank String userId) {
        
        List<Map<String, Object>> alerts = pesticideManagementService.getPriceAlerts(userId);
        return ApiResponse.success(alerts);
    }
    
    
    /**
     * 添加药剂请求
     */
    public static class AddPesticideRequest {
        @Valid
        private Pesticide pesticide;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Pesticide getPesticide() {
            return pesticide;
        }
        
        public void setPesticide(Pesticide pesticide) {
            this.pesticide = pesticide;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 更新药剂请求
     */
    public static class UpdatePesticideRequest {
        @Valid
        private Pesticide pesticide;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Pesticide getPesticide() {
            return pesticide;
        }
        
        public void setPesticide(Pesticide pesticide) {
            this.pesticide = pesticide;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 库存操作请求
     */
    public static class StockOperationRequest {
        @Positive(message = "数量必须大于0")
        private Integer quantity;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        private String notes;
        
        // Getters and Setters
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
    
    /**
     * 调拨库存请求
     */
    public static class TransferStockRequest {
        @Positive(message = "数量必须大于0")
        private Integer quantity;
        
        @NotBlank(message = "源位置不能为空")
        private String fromLocation;
        
        @NotBlank(message = "目标位置不能为空")
        private String toLocation;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public String getFromLocation() {
            return fromLocation;
        }
        
        public void setFromLocation(String fromLocation) {
            this.fromLocation = fromLocation;
        }
        
        public String getToLocation() {
            return toLocation;
        }
        
        public void setToLocation(String toLocation) {
            this.toLocation = toLocation;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 记录使用请求
     */
    public static class RecordUsageRequest {
        @Valid
        private PesticideUsageRecord usageRecord;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public PesticideUsageRecord getUsageRecord() {
            return usageRecord;
        }
        
        public void setUsageRecord(PesticideUsageRecord usageRecord) {
            this.usageRecord = usageRecord;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 设置预警请求
     */
    public static class SetAlertRequest {
        @Positive(message = "最小库存必须大于零")
        private Integer minStock;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Integer getMinStock() {
            return minStock;
        }
        
        public void setMinStock(Integer minStock) {
            this.minStock = minStock;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 更新安全信息请求
     */
    public static class UpdateSafetyInfoRequest {
        private Map<String, Object> safetyInfo;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Map<String, Object> getSafetyInfo() {
            return safetyInfo;
        }
        
        public void setSafetyInfo(Map<String, Object> safetyInfo) {
            this.safetyInfo = safetyInfo;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 兼容性检查请求
     */
    public static class CompatibilityCheckRequest {
        @NotEmpty(message = "药剂ID列表不能为空")
        private List<String> pesticideIds;
        
        // Getters and Setters
        public List<String> getPesticideIds() {
            return pesticideIds;
        }
        
        public void setPesticideIds(List<String> pesticideIds) {
            this.pesticideIds = pesticideIds;
        }
    }
    
    /**
     * 计算用量请求
     */
    public static class CalculateDosageRequest {
        @NotBlank(message = "药剂ID不能为空")
        private String pesticideId;
        
        @Positive(message = "面积必须大于0")
        private Double area;
        
        @NotBlank(message = "作物类型不能为空")
        private String cropType;
        
        // Getters and Setters
        public String getPesticideId() {
            return pesticideId;
        }
        
        public void setPesticideId(String pesticideId) {
            this.pesticideId = pesticideId;
        }
        
        public Double getArea() {
            return area;
        }
        
        public void setArea(Double area) {
            this.area = area;
        }
        
        public String getCropType() {
            return cropType;
        }
        
        public void setCropType(String cropType) {
            this.cropType = cropType;
        }
    }
    
    /**
     * 批量更新库存请求
     */
    public static class BatchUpdateStockRequest {
        @NotEmpty(message = "更新列表不能为空")
        private List<Map<String, Object>> stockUpdates;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public List<Map<String, Object>> getStockUpdates() {
            return stockUpdates;
        }
        
        public void setStockUpdates(List<Map<String, Object>> stockUpdates) {
            this.stockUpdates = stockUpdates;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 添加供应商请求
     */
    public static class AddSupplierRequest {
        private Map<String, Object> supplierInfo;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Map<String, Object> getSupplierInfo() {
            return supplierInfo;
        }
        
        public void setSupplierInfo(Map<String, Object> supplierInfo) {
            this.supplierInfo = supplierInfo;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 创建采购订单请求
     */
    public static class CreatePurchaseOrderRequest {
        @NotEmpty(message = "订单项目不能为空")
        private List<Map<String, Object>> orderItems;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public List<Map<String, Object>> getOrderItems() {
            return orderItems;
        }
        
        public void setOrderItems(List<Map<String, Object>> orderItems) {
            this.orderItems = orderItems;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 更新订单状态请求
     */
    public static class UpdateOrderStatusRequest {
        @NotBlank(message = "状态不能为空")
        private String status;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 添加质量检测请求
     */
    public static class AddQualityTestRequest {
        private Map<String, Object> testRecord;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Map<String, Object> getTestRecord() {
            return testRecord;
        }
        
        public void setTestRecord(Map<String, Object> testRecord) {
            this.testRecord = testRecord;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 记录处置请求
     */
    public static class RecordDisposalRequest {
        @Positive(message = "数量必须大于0")
        private Integer quantity;
        
        @NotBlank(message = "处置原因不能为空")
        private String reason;
        
        @NotBlank(message = "处置方法不能为空")
        private String method;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        public String getMethod() {
            return method;
        }
        
        public void setMethod(String method) {
            this.method = method;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 添加评估请求
     */
    public static class AddEvaluationRequest {
        private Map<String, Object> evaluation;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Map<String, Object> getEvaluation() {
            return evaluation;
        }
        
        public void setEvaluation(Map<String, Object> evaluation) {
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
     * 更新抗性监测请求
     */
    public static class UpdateResistanceRequest {
        private Map<String, Object> monitoringData;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Map<String, Object> getMonitoringData() {
            return monitoringData;
        }
        
        public void setMonitoringData(Map<String, Object> monitoringData) {
            this.monitoringData = monitoringData;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 设置价格预警请求
     */
    public static class SetPriceAlertRequest {
        @Positive(message = "最高价格必须大于零")
        private Double maxPrice;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public Double getMaxPrice() {
            return maxPrice;
        }
        
        public void setMaxPrice(Double maxPrice) {
            this.maxPrice = maxPrice;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
