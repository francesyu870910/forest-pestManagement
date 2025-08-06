package com.forestpest.service;

import com.forestpest.entity.Pesticide;
import com.forestpest.entity.PesticideUsageRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 药剂管理服务接口
 */
public interface PesticideManagementService {
    
    /**
     * 获取药剂库存列表
     */
    List<Pesticide> getPesticideInventory();
    
    /**
     * 获取药剂库存列表（分页）
     */
    List<Pesticide> getPesticideInventory(int page, int size);
    
    /**
     * 根据ID获取药剂详情
     */
    Pesticide getPesticideById(String pesticideId);
    
    /**
     * 根据名称搜索药剂
     */
    List<Pesticide> searchPesticidesByName(String name);
    
    /**
     * 根据类型获取药剂列表
     */
    List<Pesticide> getPesticidesByType(String type);
    
    /**
     * 根据有效成分搜索药剂
     */
    List<Pesticide> searchPesticidesByActiveIngredient(String activeIngredient);
    
    /**
     * 添加新药剂
     */
    Pesticide addPesticide(Pesticide pesticide, String userId);
    
    /**
     * 更新药剂信息
     */
    Pesticide updatePesticide(String pesticideId, Pesticide updatedPesticide, String userId);
    
    /**
     * 删除药剂
     */
    boolean deletePesticide(String pesticideId, String userId);
    
    /**
     * 药剂入库操作
     */
    Pesticide addStock(String pesticideId, int quantity, String userId, String notes);
    
    /**
     * 药剂出库操作
     */
    Pesticide removeStock(String pesticideId, int quantity, String userId, String notes);
    
    /**
     * 药剂调拨操作
     */
    boolean transferStock(String pesticideId, int quantity, String fromLocation, String toLocation, String userId);
    
    /**
     * 记录药剂使用
     */
    PesticideUsageRecord recordUsage(PesticideUsageRecord usageRecord, String userId);
    
    /**
     * 获取药剂使用记录
     */
    List<PesticideUsageRecord> getUsageRecords(String pesticideId);
    
    /**
     * 获取用户的药剂使用记录
     */
    List<PesticideUsageRecord> getUserUsageRecords(String userId);
    
    /**
     * 获取药剂使用记录（分页）
     */
    List<PesticideUsageRecord> getUsageRecords(String pesticideId, int page, int size);
    
    /**
     * 获取指定时间范围的使用记录
     */
    List<PesticideUsageRecord> getUsageRecordsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取库存预警列表
     */
    List<Pesticide> getLowStockPesticides();
    
    /**
     * 获取过期预警列表
     */
    List<Pesticide> getExpiringPesticides();
    
    /**
     * 获取已过期药剂列表
     */
    List<Pesticide> getExpiredPesticides();
    
    /**
     * 设置库存预警阈值
     */
    boolean setStockAlert(String pesticideId, int minStock, String userId);
    
    /**
     * 获取药剂使用统计
     */
    Map<String, Object> getUsageStatistics();
    
    /**
     * 获取用户药剂使用统计
     */
    Map<String, Object> getUserUsageStatistics(String userId);
    
    /**
     * 获取药剂成本分析
     */
    Map<String, Object> getCostAnalysis();
    
    /**
     * 获取药剂成本分析（按时间范围）
     */
    Map<String, Object> getCostAnalysisByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取库存价值统计
     */
    Map<String, Object> getInventoryValueStatistics();
    
    /**
     * 获取药剂安全信息
     */
    Map<String, Object> getPesticideSafetyInfo(String pesticideId);
    
    /**
     * 更新药剂安全信息
     */
    boolean updatePesticideSafetyInfo(String pesticideId, Map<String, Object> safetyInfo, String userId);
    
    /**
     * 获取药剂使用建议
     */
    List<String> getUsageSuggestions(String pesticideId, String pestType);
    
    /**
     * 检查药剂兼容性
     */
    Map<String, Object> checkPesticideCompatibility(List<String> pesticideIds);
    
    /**
     * 获取药剂混配建议
     */
    List<Map<String, Object>> getMixingSuggestions(String targetPest);
    
    /**
     * 计算药剂用量
     */
    Map<String, Object> calculateDosage(String pesticideId, double area, String cropType);
    
    /**
     * 获取药剂库存报告
     */
    Map<String, Object> getInventoryReport();
    
    /**
     * 获取药剂使用趋势
     */
    Map<String, List<Map<String, Object>>> getUsageTrends(String period);
    
    /**
     * 导出库存数据
     */
    String exportInventoryData(String format);
    
    /**
     * 导出使用记录
     */
    String exportUsageRecords(LocalDate startDate, LocalDate endDate, String format);
    
    /**
     * 批量更新库存
     */
    List<Pesticide> batchUpdateStock(List<Map<String, Object>> stockUpdates, String userId);
    
    /**
     * 获取药剂供应商信息
     */
    List<Map<String, Object>> getSupplierInfo(String pesticideId);
    
    /**
     * 添加药剂供应商
     */
    boolean addSupplier(String pesticideId, Map<String, Object> supplierInfo, String userId);
    
    /**
     * 获取药剂采购建议
     */
    List<Map<String, Object>> getPurchaseSuggestions();
    
    /**
     * 创建采购订单
     */
    Map<String, Object> createPurchaseOrder(List<Map<String, Object>> orderItems, String userId);
    
    /**
     * 获取采购订单列表
     */
    List<Map<String, Object>> getPurchaseOrders(String userId);
    
    /**
     * 更新采购订单状态
     */
    boolean updatePurchaseOrderStatus(String orderId, String status, String userId);
    
    /**
     * 获取药剂质量检测记录
     */
    List<Map<String, Object>> getQualityTestRecords(String pesticideId);
    
    /**
     * 添加质量检测记录
     */
    boolean addQualityTestRecord(String pesticideId, Map<String, Object> testRecord, String userId);
    
    /**
     * 获取药剂存储条件要求
     */
    Map<String, Object> getStorageRequirements(String pesticideId);
    
    /**
     * 检查存储条件合规性
     */
    Map<String, Object> checkStorageCompliance(String location);
    
    /**
     * 获取药剂处置建议
     */
    List<String> getDisposalSuggestions(String pesticideId);
    
    /**
     * 记录药剂处置
     */
    boolean recordDisposal(String pesticideId, int quantity, String reason, String method, String userId);
    
    /**
     * 获取药剂处置记录
     */
    List<Map<String, Object>> getDisposalRecords();
    
    /**
     * 获取药剂效果评估
     */
    Map<String, Object> getEffectivenessEvaluation(String pesticideId);
    
    /**
     * 添加药剂效果评估
     */
    boolean addEffectivenessEvaluation(String pesticideId, Map<String, Object> evaluation, String userId);
    
    /**
     * 获取药剂抗性监测数据
     */
    Map<String, Object> getResistanceMonitoring(String pesticideId);
    
    /**
     * 更新抗性监测数据
     */
    boolean updateResistanceMonitoring(String pesticideId, Map<String, Object> monitoringData, String userId);
    
    /**
     * 获取药剂轮换建议
     */
    List<String> getRotationSuggestions(String currentPesticideId);
    
    /**
     * 验证药剂信息
     */
    boolean validatePesticide(Pesticide pesticide);
    
    /**
     * 获取药剂分类统计
     */
    Map<String, Long> getPesticideTypeStatistics();
    
    /**
     * 获取热门药剂排行
     */
    List<Map<String, Object>> getPopularPesticides(int limit);
    
    /**
     * 获取药剂库存周转率
     */
    Map<String, Double> getInventoryTurnoverRate();
    
    /**
     * 预测药剂需求
     */
    Map<String, Object> predictPesticideDemand(String pesticideId, int days);
    
    /**
     * 获取药剂价格趋势
     */
    Map<String, List<Map<String, Object>>> getPriceTrends(String pesticideId, String period);
    
    /**
     * 设置药剂价格预警
     */
    boolean setPriceAlert(String pesticideId, double maxPrice, String userId);
    
    /**
     * 获取价格预警列表
     */
    List<Map<String, Object>> getPriceAlerts(String userId);
}