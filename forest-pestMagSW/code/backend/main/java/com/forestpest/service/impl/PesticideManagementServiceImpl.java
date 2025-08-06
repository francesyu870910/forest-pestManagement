package com.forestpest.service.impl;

import com.forestpest.entity.Pesticide;
import com.forestpest.entity.PesticideUsageRecord;
import com.forestpest.service.PesticideManagementService;
import com.forestpest.data.storage.DataStorage;
import com.forestpest.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 药剂管理服务实现类
 */
@Service
public class PesticideManagementServiceImpl implements PesticideManagementService {
    
    @Autowired
    private DataStorage dataStorage;
    
    // 药剂库存存储
    private final Map<String, Pesticide> pesticideInventory = new ConcurrentHashMap<>();
    
    // 药剂使用记录存储
    private final Map<String, PesticideUsageRecord> usageRecords = new ConcurrentHashMap<>();
    
    // 药剂使用记录索引
    private final Map<String, List<String>> pesticideUsageIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userUsageIndex = new ConcurrentHashMap<>();
    
    // 库存预警设置
    private final Map<String, Integer> stockAlerts = new ConcurrentHashMap<>();
    
    // 采购订单存储
    private final Map<String, Map<String, Object>> purchaseOrders = new ConcurrentHashMap<>();
    
    // 质量检测记录
    private final Map<String, List<Map<String, Object>>> qualityTestRecords = new ConcurrentHashMap<>();
    
    // 处置记录
    private final List<Map<String, Object>> disposalRecords = new ArrayList<>();
    
    private final Random random = new Random();
    
    public PesticideManagementServiceImpl() {
        // 初始化默认药剂数据
        initializeDefaultPesticides();
    }
    
    /**
     * 初始化默认药剂数据
     */
    private void initializeDefaultPesticides() {
        try {
            // 创建一些默认的药剂数据
            createDefaultPesticide("1", "敌敌畏", "有机磷杀虫剂", "液体", 100, 50.0, "2025-12-31");
            createDefaultPesticide("2", "氯氰菊酯", "拟除虫菊酯杀虫剂", "乳油", 80, 120.0, "2025-10-15");
            createDefaultPesticide("3", "多菌灵", "苯并咪唑类杀菌剂", "可湿性粉剂", 60, 80.0, "2025-11-20");
            createDefaultPesticide("4", "草甘膦", "有机磷除草剂", "水剂", 200, 35.0, "2026-01-10");
            createDefaultPesticide("5", "吡虫啉", "新烟碱类杀虫剂", "颗粒剂", 150, 200.0, "2025-09-30");
        } catch (Exception e) {
            // 如果初始化失败，记录错误但不抛出异常
            System.err.println("初始化默认药剂数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建默认药剂
     */
    private void createDefaultPesticide(String id, String name, String category, String specification, 
                                      int stockQuantity, double unitPrice, String expiryDate) {
        try {
            Pesticide pesticide = new Pesticide();
            pesticide.setId(id);
            pesticide.setName(name);
            pesticide.setActiveIngredient(name); // 简化处理
            pesticide.setCategory(category);
            pesticide.setSpecification(specification);
            pesticide.setStockQuantity(stockQuantity);
            pesticide.setUnitPrice(BigDecimal.valueOf(unitPrice));
            pesticide.setExpiryDate(LocalDate.parse(expiryDate));
            pesticide.setSupplier("默认供应商");
            pesticide.setSafetyLevel("中等");
            
            pesticideInventory.put(id, pesticide);
        } catch (Exception e) {
            System.err.println("创建默认药剂失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Pesticide> getPesticideInventory() {
        return new ArrayList<>(pesticideInventory.values());
    }
    
    @Override
    public List<Pesticide> getPesticideInventory(int page, int size) {
        List<Pesticide> allPesticides = getPesticideInventory();
        
        int start = page * size;
        int end = Math.min(start + size, allPesticides.size());
        
        if (start >= allPesticides.size()) {
            return new ArrayList<>();
        }
        
        return allPesticides.subList(start, end);
    }
    
    @Override
    public Pesticide getPesticideById(String pesticideId) {
        return pesticideInventory.get(pesticideId);
    }
    
    @Override
    public List<Pesticide> searchPesticidesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getPesticideInventory();
        }
        
        String lowerName = name.toLowerCase();
        return pesticideInventory.values().stream()
                .filter(pesticide -> pesticide.getName().toLowerCase().contains(lowerName) ||
                                   (pesticide.getManufacturer() != null && pesticide.getManufacturer().toLowerCase().contains(lowerName)))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> getPesticidesByType(String type) {
        return pesticideInventory.values().stream()
                .filter(pesticide -> type.equals(pesticide.getCategory()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> searchPesticidesByActiveIngredient(String activeIngredient) {
        if (activeIngredient == null || activeIngredient.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerIngredient = activeIngredient.toLowerCase();
        return pesticideInventory.values().stream()
                .filter(pesticide -> pesticide.getActiveIngredient().toLowerCase().contains(lowerIngredient))
                .collect(Collectors.toList());
    }
    
    @Override
    public Pesticide addPesticide(Pesticide pesticide, String userId) {
        if (pesticide == null) {
            throw new BusinessException("药剂信息不能为空");
        }
        
        if (!validatePesticide(pesticide)) {
            throw new BusinessException("药剂信息验证失败");
        }
        
        pesticide.setId(dataStorage.generateId());
        pesticide.setCreatedBy(userId);
        pesticide.setCreatedTime(LocalDateTime.now());
        pesticide.setUpdatedTime(LocalDateTime.now());
        
        pesticideInventory.put(pesticide.getId(), pesticide);
        
        return pesticide;
    }
    
    @Override
    public Pesticide updatePesticide(String pesticideId, Pesticide updatedPesticide, String userId) {
        Pesticide existingPesticide = pesticideInventory.get(pesticideId);
        if (existingPesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        // 更新基本信息
        existingPesticide.setName(updatedPesticide.getName());
        existingPesticide.setName(updatedPesticide.getName());
        existingPesticide.setActiveIngredient(updatedPesticide.getActiveIngredient());
        existingPesticide.setSpecification(updatedPesticide.getSpecification());
        existingPesticide.setCategory(updatedPesticide.getCategory());
        existingPesticide.setUsageInstructions(updatedPesticide.getUsageInstructions());
        existingPesticide.setManufacturer(updatedPesticide.getManufacturer());
        existingPesticide.setUnitPrice(updatedPesticide.getUnitPrice());
        existingPesticide.setUpdatedTime(LocalDateTime.now());
        existingPesticide.setUpdatedBy(userId);
        
        return existingPesticide;
    }
    
    @Override
    public boolean deletePesticide(String pesticideId, String userId) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            return false;
        }
        
        // 检查是否有库存
        if (pesticide.getStockQuantity() > 0) {
            throw new BusinessException("有库存的药剂不能删除");
        }
        
        pesticideInventory.remove(pesticideId);
        stockAlerts.remove(pesticideId);
        
        return true;
    }
    
    @Override
    public Pesticide addStock(String pesticideId, int quantity, String userId, String notes) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        if (quantity <= 0) {
            throw new BusinessException("入库数量必须大于0");
        }
        
        pesticide.setStockQuantity(pesticide.getStockQuantity() + quantity);
        pesticide.setUpdatedTime(LocalDateTime.now());
        pesticide.setUpdatedBy(userId);
        
        // 记录库存变动
        recordStockChange(pesticideId, quantity, "入库", userId, notes);
        
        return pesticide;
    }
    
    @Override
    public Pesticide removeStock(String pesticideId, int quantity, String userId, String notes) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        if (quantity <= 0) {
            throw new BusinessException("出库数量必须大于0");
        }
        
        if (pesticide.getStockQuantity() < quantity) {
            throw new BusinessException("库存不足，当前库存：" + pesticide.getStockQuantity());
        }
        
        pesticide.setStockQuantity(pesticide.getStockQuantity() - quantity);
        pesticide.setUpdatedTime(LocalDateTime.now());
        pesticide.setUpdatedBy(userId);
        
        // 记录库存变动
        recordStockChange(pesticideId, -quantity, "出库", userId, notes);
        
        return pesticide;
    }
    
    @Override
    public boolean transferStock(String pesticideId, int quantity, String fromLocation, String toLocation, String userId) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        if (quantity <= 0) {
            throw new BusinessException("调拨数量必须大于0");
        }
        
        // 简化的调拨逻辑，实际应该涉及不同仓库的库存管理
        String notes = String.format("从%s调拨到%s", fromLocation, toLocation);
        recordStockChange(pesticideId, 0, "调拨", userId, notes);
        
        return true;
    }
    
    @Override
    public PesticideUsageRecord recordUsage(PesticideUsageRecord usageRecord, String userId) {
        if (usageRecord == null) {
            throw new BusinessException("使用记录不能为空");
        }
        
        // 验证药剂是否存在
        Pesticide pesticide = pesticideInventory.get(usageRecord.getPesticideId());
        if (pesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        // 检查库存是否足够
        if (pesticide.getStockQuantity() < usageRecord.getUsedQuantity()) {
            throw new BusinessException("库存不足，无法记录使用");
        }
        
        usageRecord.setId(dataStorage.generateId());
        usageRecord.setCreatedBy(userId);
        usageRecord.setUsageTime(LocalDateTime.now());
        
        // 计算成本 - 注意：PesticideUsageRecord实体中没有cost字段，这里注释掉
        // double cost = usageRecord.getUsedQuantity() * pesticide.getUnitPrice().doubleValue();
        // usageRecord.setCost(cost);
        
        // 保存使用记录
        usageRecords.put(usageRecord.getId(), usageRecord);
        
        // 更新索引
        pesticideUsageIndex.computeIfAbsent(usageRecord.getPesticideId(), k -> new ArrayList<>())
                .add(usageRecord.getId());
        userUsageIndex.computeIfAbsent(userId, k -> new ArrayList<>())
                .add(usageRecord.getId());
        
        // 减少库存
        pesticide.setStockQuantity(pesticide.getStockQuantity() - usageRecord.getUsedQuantity());
        pesticide.setUpdatedTime(LocalDateTime.now());
        
        return usageRecord;
    }
    
    @Override
    public List<PesticideUsageRecord> getUsageRecords(String pesticideId) {
        List<String> recordIds = pesticideUsageIndex.get(pesticideId);
        if (recordIds == null) {
            return new ArrayList<>();
        }
        
        return recordIds.stream()
                .map(usageRecords::get)
                .filter(Objects::nonNull)
                .sorted((r1, r2) -> r2.getUsageTime().compareTo(r1.getUsageTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PesticideUsageRecord> getUserUsageRecords(String userId) {
        List<String> recordIds = userUsageIndex.get(userId);
        if (recordIds == null) {
            return new ArrayList<>();
        }
        
        return recordIds.stream()
                .map(usageRecords::get)
                .filter(Objects::nonNull)
                .sorted((r1, r2) -> r2.getUsageTime().compareTo(r1.getUsageTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PesticideUsageRecord> getUsageRecords(String pesticideId, int page, int size) {
        List<PesticideUsageRecord> allRecords = getUsageRecords(pesticideId);
        
        int start = page * size;
        int end = Math.min(start + size, allRecords.size());
        
        if (start >= allRecords.size()) {
            return new ArrayList<>();
        }
        
        return allRecords.subList(start, end);
    }
    
    @Override
    public List<PesticideUsageRecord> getUsageRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return usageRecords.values().stream()
                .filter(record -> {
                    LocalDate usageDate = record.getUsageTime().toLocalDate();
                    return !usageDate.isBefore(startDate) && !usageDate.isAfter(endDate);
                })
                .sorted((r1, r2) -> r2.getUsageTime().compareTo(r1.getUsageTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> getLowStockPesticides() {
        return pesticideInventory.values().stream()
                .filter(pesticide -> {
                    int minStock = stockAlerts.getOrDefault(pesticide.getId(), 10);
                    return pesticide.getStockQuantity() <= minStock;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> getExpiringPesticides() {
        LocalDate warningDate = LocalDate.now().plusDays(30); // 30天内过期
        
        return pesticideInventory.values().stream()
                .filter(pesticide -> pesticide.getExpiryDate() != null &&
                                   !pesticide.getExpiryDate().isAfter(warningDate) &&
                                   pesticide.getExpiryDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> getExpiredPesticides() {
        LocalDate today = LocalDate.now();
        
        return pesticideInventory.values().stream()
                .filter(pesticide -> pesticide.getExpiryDate() != null &&
                                   pesticide.getExpiryDate().isBefore(today))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean setStockAlert(String pesticideId, int minStock, String userId) {
        if (pesticideInventory.containsKey(pesticideId)) {
            stockAlerts.put(pesticideId, minStock);
            return true;
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getUsageStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        List<PesticideUsageRecord> allRecords = new ArrayList<>(usageRecords.values());
        statistics.put("totalUsageRecords", allRecords.size());
        
        // 按药剂类型统计使用量
        Map<String, Double> usageByType = new HashMap<>();
        Map<String, Double> costByType = new HashMap<>();
        
        for (PesticideUsageRecord record : allRecords) {
            Pesticide pesticide = pesticideInventory.get(record.getPesticideId());
            if (pesticide != null) {
                String type = pesticide.getCategory();
                usageByType.put(type, usageByType.getOrDefault(type, 0.0) + record.getUsedQuantity());
                // 注意：PesticideUsageRecord没有getCost方法，使用默认值
                costByType.put(type, costByType.getOrDefault(type, 0.0) + (record.getUsedQuantity() * (pesticide.getUnitPrice() != null ? pesticide.getUnitPrice().doubleValue() : 0.0)));
            }
        }
        
        statistics.put("usageByType", usageByType);
        statistics.put("costByType", costByType);
        
        // 总成本 - 由于PesticideUsageRecord没有getCost方法，计算总数量
        double totalQuantity = allRecords.stream().mapToDouble(PesticideUsageRecord::getUsedQuantity).sum();
        statistics.put("totalQuantity", totalQuantity);
        
        // 按月份统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> monthlyUsage = allRecords.stream()
                .collect(Collectors.groupingBy(
                    record -> record.getUsageTime().format(formatter),
                    Collectors.counting()
                ));
        statistics.put("monthlyUsage", monthlyUsage);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getUserUsageStatistics(String userId) {
        List<PesticideUsageRecord> userRecords = getUserUsageRecords(userId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRecords", userRecords.size());
        
        // 总使用量
        double totalQuantity = userRecords.stream().mapToDouble(PesticideUsageRecord::getUsedQuantity).sum();
        // 注意：PesticideUsageRecord没有getCost方法，设
        double totalCost = 0.0;
        
        statistics.put("totalQuantity", totalQuantity);
        statistics.put("totalCost", totalCost);
        
        // 最常用的药
        Map<String, Long> pesticideUsage = userRecords.stream()
                .collect(Collectors.groupingBy(PesticideUsageRecord::getPesticideId, Collectors.counting()));
        
        String mostUsedPesticideId = pesticideUsage.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        
        if (mostUsedPesticideId != null) {
            Pesticide mostUsedPesticide = pesticideInventory.get(mostUsedPesticideId);
            statistics.put("mostUsedPesticide", mostUsedPesticide != null ? mostUsedPesticide.getName() : "未知");
        }
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getCostAnalysis() {
        return getCostAnalysisByDateRange(LocalDate.now().minusMonths(12), LocalDate.now());
    }
    
    @Override
    public Map<String, Object> getCostAnalysisByDateRange(LocalDate startDate, LocalDate endDate) {
        List<PesticideUsageRecord> records = getUsageRecordsByDateRange(startDate, endDate);
        
        Map<String, Object> analysis = new HashMap<>();
        
        // 总数量（替代成本
        double totalQuantity = records.stream().mapToDouble(PesticideUsageRecord::getUsedQuantity).sum();
        analysis.put("totalQuantity", totalQuantity);
        
        // 平均数量
        double averageQuantity = records.isEmpty() ? 0.0 : totalQuantity / records.size();
        analysis.put("averageQuantity", averageQuantity);
        
        // 按药剂分组的使用
        Map<String, Double> quantityByPesticide = records.stream()
                .collect(Collectors.groupingBy(
                    PesticideUsageRecord::getPesticideId,
                    Collectors.summingDouble(PesticideUsageRecord::getUsedQuantity)
                ));
        
        // 转换为药剂名
        Map<String, Double> quantityByPesticideName = new HashMap<>();
        for (Map.Entry<String, Double> entry : quantityByPesticide.entrySet()) {
            Pesticide pesticide = pesticideInventory.get(entry.getKey());
            String name = pesticide != null ? pesticide.getName() : "未知药剂";
            quantityByPesticideName.put(name, entry.getValue());
        }
        
        analysis.put("quantityByPesticide", quantityByPesticideName);
        
        // 使用量最高的药剂
        String highestQuantityPesticideId = quantityByPesticide.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        
        if (highestQuantityPesticideId != null) {
            Pesticide pesticide = pesticideInventory.get(highestQuantityPesticideId);
            analysis.put("highestQuantityPesticide", pesticide != null ? pesticide.getName() : "未知");
            analysis.put("highestQuantity", quantityByPesticide.get(highestQuantityPesticideId));
        }
        
        return analysis;
    }
    
    @Override
    public Map<String, Object> getInventoryValueStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        double totalValue = 0.0;
        int totalItems = 0;
        Map<String, Double> valueByType = new HashMap<>();
        
        for (Pesticide pesticide : pesticideInventory.values()) {
            double itemValue = pesticide.getStockQuantity() * (pesticide.getUnitPrice() != null ? pesticide.getUnitPrice().doubleValue() : 0.0);
            totalValue += itemValue;
            totalItems += pesticide.getStockQuantity();
            
            String type = pesticide.getCategory();
            valueByType.put(type, valueByType.getOrDefault(type, 0.0) + itemValue);
        }
        
        statistics.put("totalValue", totalValue);
        statistics.put("totalItems", totalItems);
        statistics.put("valueByType", valueByType);
        statistics.put("averageUnitValue", totalItems > 0 ? totalValue / totalItems : 0.0);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getPesticideSafetyInfo(String pesticideId) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        Map<String, Object> safetyInfo = new HashMap<>();
        safetyInfo.put("toxicityLevel", pesticide.getToxicityLevel());
        safetyInfo.put("safetyLevel", pesticide.getSafetyLevel());
        safetyInfo.put("storageConditions", pesticide.getStorageConditions());
        
        // 模拟安全信息
        safetyInfo.put("protectiveEquipment", Arrays.asList("防护服", "防护手套", "防护眼镜", "防毒面具"));
        safetyInfo.put("firstAidMeasures", Arrays.asList(
            "皮肤接触：立即脱去污染的衣着，用肥皂水和清水彻底冲洗皮肤",
            "眼睛接触：提起眼睑，用流动清水或生理盐水冲洗",
            "吸入：迅速脱离现场至空气新鲜处，保持呼吸道通畅",
            "食入：饮足量温水，催吐，就医"
        ));
        safetyInfo.put("storageConditions", Arrays.asList("阴凉", "干燥", "通风", "远离火源"));
        safetyInfo.put("disposalMethod", "按照危险废物处理规定进行处置");
        
        return safetyInfo;
    }
    
    @Override
    public boolean updatePesticideSafetyInfo(String pesticideId, Map<String, Object> safetyInfo, String userId) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            return false;
        }
        
        // 更新安全信息
        if (safetyInfo.containsKey("toxicityLevel")) {
            pesticide.setToxicityLevel((String) safetyInfo.get("toxicityLevel"));
        }
        if (safetyInfo.containsKey("safetyLevel")) {
            pesticide.setSafetyLevel((String) safetyInfo.get("safetyLevel"));
        }
        if (safetyInfo.containsKey("storageConditions")) {
            pesticide.setStorageConditions((String) safetyInfo.get("storageConditions"));
        }
        
        pesticide.setUpdatedTime(LocalDateTime.now());
        pesticide.setUpdatedBy(userId);
        
        return true;
    }
    
    @Override
    public List<String> getUsageSuggestions(String pesticideId, String pestType) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            return new ArrayList<>();
        }
        
        List<String> suggestions = new ArrayList<>();
        
        // 基于药剂类型的建议
        switch (pesticide.getCategory() != null ? pesticide.getCategory() : "未知") {
            case "杀虫剂":
                suggestions.add("建议在害虫活动高峰期使用，通常为清晨或傍晚");
                suggestions.add("注意轮换使用不同作用机理的药剂，避免产生抗性");
                suggestions.add("根据害虫发育阶段选择合适的用药时机");
                break;
            case "杀菌剂":
                suggestions.add("预防性用药效果更佳，在病害发生前或初期使用");
                suggestions.add("雨前用药，确保药剂有足够时间被植物吸收");
                suggestions.add("注意药剂的持效期，及时补充用药");
                break;
            case "除草剂":
                suggestions.add("在杂草幼苗期使用效果最佳");
                suggestions.add("避免在风大天气施药，防止药剂飘移");
                suggestions.add("注意土壤湿度，适当的土壤湿度有利于药效发挥");
                break;
        }
        
        // 基于毒性等级的建议
        if ("高毒".equals(pesticide.getToxicityLevel())) {
            suggestions.add("使用时必须穿戴完整的防护设备");
            suggestions.add("避免在风大天气使用，防止药剂飘散");
            suggestions.add("施药后立即清洗设备和防护用品");
        }
        
        // 基于安全等级的建议
        if (pesticide.getSafetyLevel() != null) {
            suggestions.add("安全等级：" + pesticide.getSafetyLevel() + "，请按照相应防护要求使用");
            suggestions.add("严格遵守安全操作规程，确保人员安全");
        }
        
        return suggestions;
    }
    
    @Override
    public Map<String, Object> checkPesticideCompatibility(List<String> pesticideIds) {
        Map<String, Object> compatibility = new HashMap<>();
        
        if (pesticideIds == null || pesticideIds.size() < 2) {
            compatibility.put("compatible", false);
            compatibility.put("message", "至少需要两种药剂进行兼容性检查");
            return compatibility;
        }
        
        List<Pesticide> pesticides = pesticideIds.stream()
                .map(pesticideInventory::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        if (pesticides.size() != pesticideIds.size()) {
            compatibility.put("compatible", false);
            compatibility.put("message", "部分药剂不存在");
            return compatibility;
        }
        
        // 简化的兼容性检查逻辑
        boolean hasAcidic = pesticides.stream().anyMatch(p -> "酸性".equals(p.getUsageInstructions()));
        boolean hasAlkaline = pesticides.stream().anyMatch(p -> "碱性".equals(p.getUsageInstructions()));
        
        if (hasAcidic && hasAlkaline) {
            compatibility.put("compatible", false);
            compatibility.put("message", "酸性和碱性药剂不能混用");
            compatibility.put("warnings", Arrays.asList("可能发生化学反应", "药效降低", "产生有害物质"));
        } else {
            compatibility.put("compatible", true);
            compatibility.put("message", "药剂可以混用");
            compatibility.put("suggestions", Arrays.asList(
                "先进行小面积试验",
                "现配现用，不要长时间存放",
                "注意混配顺序，一般先加水，再加药剂"
            ));
        }
        
        return compatibility;
    }
    
    @Override
    public List<Map<String, Object>> getMixingSuggestions(String targetPest) {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        // 模拟混配建议
        Map<String, Object> suggestion1 = new HashMap<>();
        suggestion1.put("combination", Arrays.asList("杀虫剂A", "杀菌剂B"));
        suggestion1.put("ratio", "1:1");
        suggestion1.put("effectiveness", 0.9);
        suggestion1.put("notes", "适用于同时防治害虫和病害");
        suggestions.add(suggestion1);
        
        Map<String, Object> suggestion2 = new HashMap<>();
        suggestion2.put("combination", Arrays.asList("杀虫剂C", "增效剂D"));
        suggestion2.put("ratio", "10:1");
        suggestion2.put("effectiveness", 0.85);
        suggestion2.put("notes", "增效剂可提高杀虫剂效果");
        suggestions.add(suggestion2);
        
        return suggestions;
    }
    
    @Override
    public Map<String, Object> calculateDosage(String pesticideId, double area, String cropType) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        Map<String, Object> dosage = new HashMap<>();
        
        // 模拟用量计算
        double baseRate = 100.0; // 基础用量 ml/亩
        double adjustmentFactor = 1.0;
        
        // 根据作物类型调整
        switch (cropType) {
            case "果树":
                adjustmentFactor = 1.2;
                break;
            case "蔬菜":
                adjustmentFactor = 0.8;
                break;
            case "粮食作物":
                adjustmentFactor = 1.0;
                break;
            default:
                adjustmentFactor = 1.0;
        }
        
        double totalDosage = baseRate * adjustmentFactor * area;
        double waterVolume = totalDosage * 50; // 稀释倍数
        
        dosage.put("pesticideName", pesticide.getName());
        dosage.put("area", area);
        dosage.put("cropType", cropType);
        dosage.put("dosagePerUnit", baseRate * adjustmentFactor);
        dosage.put("totalDosage", totalDosage);
        dosage.put("waterVolume", waterVolume);
        dosage.put("concentration", totalDosage / waterVolume * 100);
        dosage.put("estimatedCost", totalDosage * pesticide.getUnitPrice().doubleValue() / 1000);
        
        return dosage;
    }
    
    @Override
    public Map<String, Object> getInventoryReport() {
        Map<String, Object> report = new HashMap<>();
        
        List<Pesticide> inventory = getPesticideInventory();
        report.put("totalPesticides", inventory.size());
        
        // 库存统计
        int totalStock = inventory.stream().mapToInt(Pesticide::getStockQuantity).sum();
        report.put("totalStock", totalStock);
        
        // 库存价
        double totalValue = inventory.stream()
                .mapToDouble(p -> p.getStockQuantity() * (p.getUnitPrice() != null ? p.getUnitPrice().doubleValue() : 0.0))
                .sum();
        report.put("totalValue", totalValue);
        
        // 低库存预
        List<Pesticide> lowStock = getLowStockPesticides();
        report.put("lowStockCount", lowStock.size());
        report.put("lowStockPesticides", lowStock.stream()
                .map(p -> Map.of("name", p.getName(), "currentStock", p.getStockQuantity()))
                .collect(Collectors.toList()));
        
        // 过期预警
        List<Pesticide> expiring = getExpiringPesticides();
        report.put("expiringCount", expiring.size());
        report.put("expiringPesticides", expiring.stream()
                .map(p -> Map.of("name", p.getName(), "expiryDate", p.getExpiryDate()))
                .collect(Collectors.toList()));
        
        // 已过
        List<Pesticide> expired = getExpiredPesticides();
        report.put("expiredCount", expired.size());
        
        // 按类型统计
        Map<String, Long> typeStats = inventory.stream()
                .collect(Collectors.groupingBy(Pesticide::getCategory, Collectors.counting()));
        report.put("typeStatistics", typeStats);
        
        report.put("reportTime", LocalDateTime.now());
        
        return report;
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> getUsageTrends(String period) {
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        
        DateTimeFormatter formatter;
        if ("daily".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if ("monthly".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy");
        }
        
        // 使用量趋势
        Map<String, Double> usageTrend = usageRecords.values().stream()
                .collect(Collectors.groupingBy(
                    record -> record.getUsageTime().format(formatter),
                    Collectors.summingDouble(record -> record.getUsedQuantity().doubleValue())
                ));
        
        List<Map<String, Object>> usageData = usageTrend.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("period", entry.getKey());
                    item.put("usage", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        
        trends.put("usageTrend", usageData);
        
        // 成本趋势
        Map<String, Double> costTrend = usageRecords.values().stream()
                .collect(Collectors.groupingBy(
                    record -> record.getUsageTime().format(formatter),
                    Collectors.summingDouble(record -> 0.0)
                ));
        
        List<Map<String, Object>> costData = costTrend.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("period", entry.getKey());
                    item.put("cost", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        
        trends.put("costTrend", costData);
        
        return trends;
    }
    
    @Override
    public String exportInventoryData(String format) {
        List<Pesticide> inventory = getPesticideInventory();
        
        if ("json".equalsIgnoreCase(format)) {
            return inventory.toString();
        } else if ("csv".equalsIgnoreCase(format)) {
            StringBuilder csv = new StringBuilder();
            csv.append("药剂名称,商品名,有效成分,规格,类型,当前库存,单价,过期日期\n");
            
            for (Pesticide pesticide : inventory) {
                csv.append(pesticide.getName()).append(",")
                   .append(pesticide.getName()).append(",")
                   .append(pesticide.getActiveIngredient()).append(",")
                   .append(pesticide.getSpecification()).append(",")
                   .append(pesticide.getCategory()).append(",")
                   .append(pesticide.getStockQuantity()).append(",")
                   .append(pesticide.getUnitPrice()).append(",")
                   .append(pesticide.getExpiryDate()).append("\n");
            }
            
            return csv.toString();
        }
        
        throw new BusinessException("不支持的导出格式: " + format);
    }
    
    @Override
    public String exportUsageRecords(LocalDate startDate, LocalDate endDate, String format) {
        List<PesticideUsageRecord> records = getUsageRecordsByDateRange(startDate, endDate);
        
        if ("json".equalsIgnoreCase(format)) {
            return records.toString();
        } else if ("csv".equalsIgnoreCase(format)) {
            StringBuilder csv = new StringBuilder();
            csv.append("使用时间,药剂名称,使用数量,目标区域,使用人员,任务ID\n");
            
            for (PesticideUsageRecord record : records) {
                Pesticide pesticide = pesticideInventory.get(record.getPesticideId());
                String pesticideName = pesticide != null ? pesticide.getName() : "未知";
                
                csv.append(record.getUsageTime()).append(",")
                   .append(pesticideName).append(",")
                   .append(record.getUsedQuantity()).append(",")
                   .append(record.getTargetArea()).append(",")
                   .append(record.getUsedBy()).append(",")
                   .append(record.getTaskId()).append("\n");
            }
            
            return csv.toString();
        }
        
        throw new BusinessException("不支持的导出格式: " + format);
    }
    
    @Override
    public List<Pesticide> batchUpdateStock(List<Map<String, Object>> stockUpdates, String userId) {
        if (stockUpdates == null || stockUpdates.isEmpty()) {
            throw new BusinessException("更新列表不能为空");
        }
        
        if (stockUpdates.size() > 50) {
            throw new BusinessException("批量更新最多支持50个药剂");
        }
        
        List<Pesticide> updatedPesticides = new ArrayList<>();
        
        for (Map<String, Object> update : stockUpdates) {
            try {
                String pesticideId = (String) update.get("pesticideId");
                String operation = (String) update.get("operation"); // "add" or "remove"
                Integer quantity = (Integer) update.get("quantity");
                String notes = (String) update.get("notes");
                
                if ("add".equals(operation)) {
                    Pesticide updated = addStock(pesticideId, quantity, userId, notes);
                    updatedPesticides.add(updated);
                } else if ("remove".equals(operation)) {
                    Pesticide updated = removeStock(pesticideId, quantity, userId, notes);
                    updatedPesticides.add(updated);
                }
            } catch (Exception e) {
                // 记录错误但继续处理其他更
                continue;
            }
        }
        
        return updatedPesticides;
    }
    
    @Override
    public List<Map<String, Object>> getSupplierInfo(String pesticideId) {
        // 模拟供应商信
        List<Map<String, Object>> suppliers = new ArrayList<>();
        
        Map<String, Object> supplier1 = new HashMap<>();
        supplier1.put("id", "supplier-001");
        supplier1.put("name", "农药供应商A");
        supplier1.put("contact", "张经理");
        supplier1.put("phone", "138****1234");
        supplier1.put("price", 50.0);
        supplier1.put("minOrder", 100);
        supplier1.put("deliveryTime", 3);
        suppliers.add(supplier1);
        
        Map<String, Object> supplier2 = new HashMap<>();
        supplier2.put("id", "supplier-002");
        supplier2.put("name", "农药供应商B");
        supplier2.put("contact", "李经理");
        supplier2.put("phone", "139****5678");
        supplier2.put("price", 48.0);
        supplier2.put("minOrder", 200);
        supplier2.put("deliveryTime", 5);
        suppliers.add(supplier2);
        
        return suppliers;
    }
    
    @Override
    public boolean addSupplier(String pesticideId, Map<String, Object> supplierInfo, String userId) {
        // 模拟添加供应
        return pesticideInventory.containsKey(pesticideId);
    }
    
    @Override
    public List<Map<String, Object>> getPurchaseSuggestions() {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        // 基于低库存生成采购建
        List<Pesticide> lowStock = getLowStockPesticides();
        
        for (Pesticide pesticide : lowStock) {
            Map<String, Object> suggestion = new HashMap<>();
            suggestion.put("pesticideId", pesticide.getId());
            suggestion.put("pesticideName", pesticide.getName());
            suggestion.put("currentStock", pesticide.getStockQuantity());
            suggestion.put("suggestedQuantity", 500); // 建议采购量
            suggestion.put("estimatedCost", 500 * pesticide.getUnitPrice().doubleValue());
            suggestion.put("priority", pesticide.getStockQuantity() == 0 ? "紧急" : "一般");
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }
    
    @Override
    public Map<String, Object> createPurchaseOrder(List<Map<String, Object>> orderItems, String userId) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BusinessException("订单项目不能为空");
        }
        
        Map<String, Object> order = new HashMap<>();
        String orderId = dataStorage.generateId();
        
        order.put("orderId", orderId);
        order.put("userId", userId);
        order.put("orderTime", LocalDateTime.now());
        order.put("status", "待确认");
        order.put("items", orderItems);
        
        // 计算总总金额
        double totalAmount = orderItems.stream()
                .mapToDouble(item -> {
                    Integer quantity = (Integer) item.get("quantity");
                    Double price = (Double) item.get("price");
                    return quantity * price;
                })
                .sum();
        
        order.put("totalAmount", totalAmount);
        
        purchaseOrders.put(orderId, order);
        
        return order;
    }
    
    @Override
    public List<Map<String, Object>> getPurchaseOrders(String userId) {
        return purchaseOrders.values().stream()
                .filter(order -> userId.equals(order.get("userId")))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean updatePurchaseOrderStatus(String orderId, String status, String userId) {
        Map<String, Object> order = purchaseOrders.get(orderId);
        if (order != null && userId.equals(order.get("userId"))) {
            order.put("status", status);
            order.put("updateTime", LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    @Override
    public List<Map<String, Object>> getQualityTestRecords(String pesticideId) {
        return qualityTestRecords.getOrDefault(pesticideId, new ArrayList<>());
    }
    
    @Override
    public boolean addQualityTestRecord(String pesticideId, Map<String, Object> testRecord, String userId) {
        if (!pesticideInventory.containsKey(pesticideId)) {
            return false;
        }
        
        testRecord.put("testTime", LocalDateTime.now());
        testRecord.put("testerId", userId);
        
        qualityTestRecords.computeIfAbsent(pesticideId, k -> new ArrayList<>()).add(testRecord);
        
        return true;
    }
    
    @Override
    public Map<String, Object> getStorageRequirements(String pesticideId) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        Map<String, Object> requirements = new HashMap<>();
        requirements.put("temperature", "5-25°C");
        requirements.put("humidity", "≤80%");
        requirements.put("ventilation", "良好通风");
        requirements.put("lighting", "避光保存");
        requirements.put("separation", "与食品、饲料分开存放");
        requirements.put("fireProof", "远离火源、热源");
        requirements.put("childProof", "儿童不能接触的地方");
        
        return requirements;
    }
    
    @Override
    public Map<String, Object> checkStorageCompliance(String location) {
        Map<String, Object> compliance = new HashMap<>();
        
        // 模拟合规性检测
        compliance.put("location", location);
        compliance.put("compliant", true);
        compliance.put("checkTime", LocalDateTime.now());
        compliance.put("issues", new ArrayList<>());
        compliance.put("suggestions", Arrays.asList(
            "定期检查温湿度",
            "保持通风良好",
            "定期清洁存储区域"
        ));
        
        return compliance;
    }
    
    @Override
    public List<String> getDisposalSuggestions(String pesticideId) {
        List<String> suggestions = new ArrayList<>();
        
        suggestions.add("联系当地环保部门了解处置要求");
        suggestions.add("不得随意倾倒或焚烧");
        suggestions.add("包装容器需要三次清洗");
        suggestions.add("委托有资质的危废处置单位处理");
        suggestions.add("做好处置记录和台账");
        
        return suggestions;
    }
    
    @Override
    public boolean recordDisposal(String pesticideId, int quantity, String reason, String method, String userId) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            return false;
        }
        
        Map<String, Object> disposal = new HashMap<>();
        disposal.put("pesticideId", pesticideId);
        disposal.put("pesticideName", pesticide.getName());
        disposal.put("quantity", quantity);
        disposal.put("reason", reason);
        disposal.put("method", method);
        disposal.put("disposalTime", LocalDateTime.now());
        disposal.put("userId", userId);
        
        disposalRecords.add(disposal);
        
        // 减少库存
        if (pesticide.getStockQuantity() >= quantity) {
            pesticide.setStockQuantity(pesticide.getStockQuantity() - quantity);
        }
        
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getDisposalRecords() {
        return new ArrayList<>(disposalRecords);
    }
    
    @Override
    public Map<String, Object> getEffectivenessEvaluation(String pesticideId) {
        // 模拟效果评估数据
        Map<String, Object> evaluation = new HashMap<>();
        evaluation.put("pesticideId", pesticideId);
        evaluation.put("overallRating", 4.2);
        evaluation.put("effectivenessScore", 0.85);
        evaluation.put("speedOfAction", 0.8);
        evaluation.put("durability", 0.9);
        evaluation.put("safetyRating", 0.75);
        evaluation.put("evaluationCount", 15);
        evaluation.put("lastUpdated", LocalDateTime.now());
        
        return evaluation;
    }
    
    @Override
    public boolean addEffectivenessEvaluation(String pesticideId, Map<String, Object> evaluation, String userId) {
        if (!pesticideInventory.containsKey(pesticideId)) {
            return false;
        }
        
        evaluation.put("evaluationTime", LocalDateTime.now());
        evaluation.put("evaluatorId", userId);
        
        // 这里应该保存到评估记录中，简化处理
        return true;
    }
    
    @Override
    public Map<String, Object> getResistanceMonitoring(String pesticideId) {
        // 模拟抗性监测数据
        Map<String, Object> monitoring = new HashMap<>();
        monitoring.put("pesticideId", pesticideId);
        monitoring.put("resistanceLevel", "低");
        monitoring.put("resistanceIndex", 1.2);
        monitoring.put("monitoringDate", LocalDate.now());
        monitoring.put("testLocations", Arrays.asList("测试点A", "测试点B", "测试点C"));
        monitoring.put("recommendations", Arrays.asList(
            "继续监测抗性发展",
            "适当轮换使用其他药剂",
            "控制使用频次"
        ));
        
        return monitoring;
    }
    
    @Override
    public boolean updateResistanceMonitoring(String pesticideId, Map<String, Object> monitoringData, String userId) {
        if (!pesticideInventory.containsKey(pesticideId)) {
            return false;
        }
        
        monitoringData.put("updateTime", LocalDateTime.now());
        monitoringData.put("updatedBy", userId);
        
        // 这里应该保存监测数据，简化处理
        return true;
    }
    
    @Override
    public List<String> getRotationSuggestions(String currentPesticideId) {
        Pesticide currentPesticide = pesticideInventory.get(currentPesticideId);
        if (currentPesticide == null) {
            return new ArrayList<>();
        }
        
        // 查找相同类型但不同作用机理的药剂
        return pesticideInventory.values().stream()
                .filter(p -> !p.getId().equals(currentPesticideId))
                .filter(p -> p.getCategory().equals(currentPesticide.getCategory()))
                .map(p -> p.getName() + " (作用机理不同，可轮换使用)")
                .limit(5)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean validatePesticide(Pesticide pesticide) {
        if (pesticide == null) {
            return false;
        }
        
        // 验证必要字段
        if (pesticide.getName() == null || pesticide.getName().trim().isEmpty()) {
            return false;
        }
        
        if (pesticide.getActiveIngredient() == null || pesticide.getActiveIngredient().trim().isEmpty()) {
            return false;
        }
        
        if (pesticide.getCategory() == null || pesticide.getCategory().trim().isEmpty()) {
            return false;
        }
        
        if (pesticide.getUnitPrice() == null || pesticide.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public Map<String, Long> getPesticideTypeStatistics() {
        return pesticideInventory.values().stream()
                .collect(Collectors.groupingBy(Pesticide::getCategory, Collectors.counting()));
    }
    
    @Override
    public List<Map<String, Object>> getPopularPesticides(int limit) {
        // 基于使用记录统计热门药剂
        Map<String, Long> usageCount = usageRecords.values().stream()
                .collect(Collectors.groupingBy(PesticideUsageRecord::getPesticideId, Collectors.counting()));
        
        return usageCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    Pesticide pesticide = pesticideInventory.get(entry.getKey());
                    item.put("pesticideId", entry.getKey());
                    item.put("pesticideName", pesticide != null ? pesticide.getName() : "未知");
                    item.put("usageCount", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Double> getInventoryTurnoverRate() {
        Map<String, Double> turnoverRates = new HashMap<>();
        
        for (Pesticide pesticide : pesticideInventory.values()) {
            // 简化的周转率计量
            List<PesticideUsageRecord> records = getUsageRecords(pesticide.getId());
            double totalUsage = records.stream().mapToDouble(record -> record.getUsedQuantity().doubleValue()).sum();
            double averageInventory = (pesticide.getStockQuantity() + totalUsage) / 2.0;
            
            double turnoverRate = averageInventory > 0 ? totalUsage / averageInventory : 0.0;
            turnoverRates.put(pesticide.getName(), turnoverRate);
        }
        
        return turnoverRates;
    }
    
    @Override
    public Map<String, Object> predictPesticideDemand(String pesticideId, int days) {
        Pesticide pesticide = pesticideInventory.get(pesticideId);
        if (pesticide == null) {
            throw new BusinessException("药剂不存在");
        }
        
        List<PesticideUsageRecord> records = getUsageRecords(pesticideId);
        
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("pesticideId", pesticideId);
        prediction.put("pesticideName", pesticide.getName());
        prediction.put("predictionDays", days);
        
        if (records.isEmpty()) {
            prediction.put("predictedDemand", 0.0);
            prediction.put("confidence", 0.0);
        } else {
            // 简化的需求预测
            double dailyAverage = records.stream()
                    .mapToDouble(record -> record.getUsedQuantity().doubleValue())
                    .average()
                    .orElse(0.0);
            
            double predictedDemand = dailyAverage * days;
            prediction.put("predictedDemand", predictedDemand);
            prediction.put("confidence", 0.7);
            
            // 库存充足性分析
            boolean sufficient = pesticide.getStockQuantity() >= predictedDemand;
            prediction.put("stockSufficient", sufficient);
            
            if (!sufficient) {
                prediction.put("shortfall", predictedDemand - pesticide.getStockQuantity());
            }
        }
        
        prediction.put("currentStock", pesticide.getStockQuantity());
        prediction.put("predictionTime", LocalDateTime.now());
        
        return prediction;
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> getPriceTrends(String pesticideId, String period) {
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        
        // 模拟价格趋势数据
        List<Map<String, Object>> priceData = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            Map<String, Object> item = new HashMap<>();
            LocalDate date = now.minusMonths(i);
            item.put("period", date.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            item.put("price", 45.0 + random.nextDouble() * 10); // 45-55之间的价�?
            priceData.add(item);
        }
        
        trends.put("priceTrend", priceData);
        
        return trends;
    }
    
    @Override
    public boolean setPriceAlert(String pesticideId, double maxPrice, String userId) {
        if (!pesticideInventory.containsKey(pesticideId)) {
            return false;
        }
        
        // 这里应该保存价格预警设置，简化处理
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getPriceAlerts(String userId) {
        // 模拟价格预警列表
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        Map<String, Object> alert = new HashMap<>();
        alert.put("alertId", "alert-001");
        alert.put("pesticideId", "pesticide-001");
        alert.put("pesticideName", "杀虫剂A");
        alert.put("maxPrice", 50.0);
        alert.put("currentPrice", 52.0);
        alert.put("triggered", true);
        alert.put("alertTime", LocalDateTime.now());
        alerts.add(alert);
        
        return alerts;
    }
    
    // ========== 私有辅助方法 ==========
    
    /**
     * 记录库存变动
     */
    private void recordStockChange(String pesticideId, int quantity, String operation, String userId, String notes) {
        // 这里可以记录详细的库存变动历史
        // 简化处理，实际应该有专门的库存变动记录
    }
}
