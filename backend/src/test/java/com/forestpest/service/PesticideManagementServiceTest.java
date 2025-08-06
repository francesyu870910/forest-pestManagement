package com.forestpest.service;

import com.forestpest.entity.Pesticide;
import com.forestpest.entity.PesticideUsageRecord;
import com.forestpest.exception.BusinessException;
import com.forestpest.service.impl.PesticideManagementServiceImpl;
import com.forestpest.data.storage.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 药剂管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
class PesticideManagementServiceTest {
    
    @Mock
    private DataStorage dataStorage;
    
    @InjectMocks
    private PesticideManagementServiceImpl pesticideManagementService;
    
    private Pesticide testPesticide;
    private PesticideUsageRecord testUsageRecord;
    private String testUserId;
    
    @BeforeEach
    void setUp() {
        testUserId = "test-user-123";
        
        // 创建测试药剂
        testPesticide = new Pesticide();
        testPesticide.setId("pesticide-001");
        testPesticide.setName("测试杀虫剂");
        testPesticide.setCommercialName("测试杀虫剂A");
        testPesticide.setActiveIngredient("吡虫啉");
        testPesticide.setConcentration("20%");
        testPesticide.setType("杀虫剂");
        testPesticide.setFormulation("可湿性粉剂");
        testPesticide.setManufacturer("测试厂商");
        testPesticide.setCurrentStock(100);
        testPesticide.setUnitPrice(50.0);
        testPesticide.setExpiryDate(LocalDate.now().plusYears(2));
        testPesticide.setToxicityLevel("低毒");
        testPesticide.setSafetyInterval(7);
        testPesticide.setMaxResidueLimit(0.5);
        
        // 创建测试使用记录
        testUsageRecord = new PesticideUsageRecord();
        testUsageRecord.setId("usage-001");
        testUsageRecord.setPesticideId(testPesticide.getId());
        testUsageRecord.setQuantityUsed(10);
        testUsageRecord.setPurpose("防治害虫");
        testUsageRecord.setUsageArea("果园A区");
        testUsageRecord.setUsageTime(LocalDateTime.now());
        testUsageRecord.setUserId(testUserId);
        
        // Mock DataStorage
        when(dataStorage.generateId()).thenReturn("generated-id-" + System.currentTimeMillis());
    }
    
    @Test
    void testGetPesticideInventory_Success() {
        // Act
        List<Pesticide> inventory = pesticideManagementService.getPesticideInventory();
        
        // Assert
        assertNotNull(inventory);
        assertFalse(inventory.isEmpty());
        // 验证默认初始化的药剂存在
        assertTrue(inventory.stream().anyMatch(p -> p.getName().contains("杀虫剂")));
        assertTrue(inventory.stream().anyMatch(p -> p.getName().contains("杀菌剂")));
        assertTrue(inventory.stream().anyMatch(p -> p.getName().contains("除草剂")));
    }
    
    @Test
    void testGetPesticideInventory_WithPagination() {
        // Act
        List<Pesticide> page1 = pesticideManagementService.getPesticideInventory(0, 2);
        List<Pesticide> page2 = pesticideManagementService.getPesticideInventory(1, 2);
        
        // Assert
        assertNotNull(page1);
        assertNotNull(page2);
        assertTrue(page1.size() <= 2);
        assertTrue(page2.size() <= 2);
    }
    
    @Test
    void testAddPesticide_Success() {
        // Act
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Assert
        assertNotNull(addedPesticide);
        assertNotNull(addedPesticide.getId());
        assertEquals(testUserId, addedPesticide.getCreatedBy());
        assertNotNull(addedPesticide.getCreatedTime());
        assertEquals(testPesticide.getName(), addedPesticide.getName());
        
        // 验证药剂已添加到库存中
        Pesticide retrieved = pesticideManagementService.getPesticideById(addedPesticide.getId());
        assertNotNull(retrieved);
        assertEquals(addedPesticide.getName(), retrieved.getName());
    }
    
    @Test
    void testAddPesticide_NullPesticide() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.addPesticide(null, testUserId));
        assertEquals("药剂信息不能为空", exception.getMessage());
    }
    
    @Test
    void testAddPesticide_InvalidPesticide() {
        // Arrange
        Pesticide invalidPesticide = new Pesticide();
        // 缺少必要字段
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.addPesticide(invalidPesticide, testUserId));
        assertEquals("药剂信息验证失败", exception.getMessage());
    }
    
    @Test
    void testUpdatePesticide_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        Pesticide updatedInfo = new Pesticide();
        updatedInfo.setName("更新的杀虫剂");
        updatedInfo.setCommercialName("更新的商品名");
        updatedInfo.setActiveIngredient("新的有效成分");
        updatedInfo.setConcentration("25%");
        updatedInfo.setType("杀虫剂");
        updatedInfo.setFormulation("乳油");
        updatedInfo.setManufacturer("新厂商");
        updatedInfo.setUnitPrice(60.0);
        
        // Act
        Pesticide updated = pesticideManagementService.updatePesticide(addedPesticide.getId(), updatedInfo, testUserId);
        
        // Assert
        assertNotNull(updated);
        assertEquals("更新的杀虫剂", updated.getName());
        assertEquals("更新的商品名", updated.getCommercialName());
        assertEquals("新的有效成分", updated.getActiveIngredient());
        assertEquals("25%", updated.getConcentration());
        assertEquals("乳油", updated.getFormulation());
        assertEquals("新厂商", updated.getManufacturer());
        assertEquals(60.0, updated.getUnitPrice());
        assertNotNull(updated.getUpdatedTime());
        assertEquals(testUserId, updated.getUpdatedBy());
    }
    
    @Test
    void testUpdatePesticide_NotFound() {
        // Arrange
        Pesticide updatedInfo = new Pesticide();
        updatedInfo.setName("更新的杀虫剂");
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.updatePesticide("non-existent", updatedInfo, testUserId));
        assertEquals("药剂不存在", exception.getMessage());
    }
    
    @Test
    void testDeletePesticide_Success() {
        // Arrange
        testPesticide.setCurrentStock(0); // 设置为无库存
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        boolean deleted = pesticideManagementService.deletePesticide(addedPesticide.getId(), testUserId);
        
        // Assert
        assertTrue(deleted);
        assertNull(pesticideManagementService.getPesticideById(addedPesticide.getId()));
    }
    
    @Test
    void testDeletePesticide_HasStock() {
        // Arrange
        testPesticide.setCurrentStock(100); // 有库存
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.deletePesticide(addedPesticide.getId(), testUserId));
        assertEquals("有库存的药剂不能删除", exception.getMessage());
    }
    
    @Test
    void testSearchPesticidesByName_Success() {
        // Arrange
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<Pesticide> results = pesticideManagementService.searchPesticidesByName("测试");
        
        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getName().contains("测试")));
    }
    
    @Test
    void testSearchPesticidesByName_EmptyQuery() {
        // Act
        List<Pesticide> results = pesticideManagementService.searchPesticidesByName("");
        
        // Assert
        assertNotNull(results);
        // 应该返回所有药剂
        assertFalse(results.isEmpty());
    }
    
    @Test
    void testGetPesticidesByType_Success() {
        // Arrange
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<Pesticide> results = pesticideManagementService.getPesticidesByType("杀虫剂");
        
        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(p -> "杀虫剂".equals(p.getType())));
    }
    
    @Test
    void testSearchPesticidesByActiveIngredient_Success() {
        // Arrange
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<Pesticide> results = pesticideManagementService.searchPesticidesByActiveIngredient("吡虫啉");
        
        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getActiveIngredient().contains("吡虫啉")));
    }
    
    @Test
    void testAddStock_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        int originalStock = addedPesticide.getCurrentStock();
        int addQuantity = 50;
        
        // Act
        Pesticide updated = pesticideManagementService.addStock(addedPesticide.getId(), addQuantity, testUserId, "入库测试");
        
        // Assert
        assertNotNull(updated);
        assertEquals(originalStock + addQuantity, updated.getCurrentStock());
        assertNotNull(updated.getUpdatedTime());
        assertEquals(testUserId, updated.getUpdatedBy());
    }
    
    @Test
    void testAddStock_InvalidQuantity() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.addStock(addedPesticide.getId(), 0, testUserId, "测试"));
        assertEquals("入库数量必须大于0", exception.getMessage());
    }
    
    @Test
    void testAddStock_PesticideNotFound() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.addStock("non-existent", 50, testUserId, "测试"));
        assertEquals("药剂不存在", exception.getMessage());
    }
    
    @Test
    void testRemoveStock_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        int originalStock = addedPesticide.getCurrentStock();
        int removeQuantity = 30;
        
        // Act
        Pesticide updated = pesticideManagementService.removeStock(addedPesticide.getId(), removeQuantity, testUserId, "出库测试");
        
        // Assert
        assertNotNull(updated);
        assertEquals(originalStock - removeQuantity, updated.getCurrentStock());
        assertNotNull(updated.getUpdatedTime());
        assertEquals(testUserId, updated.getUpdatedBy());
    }
    
    @Test
    void testRemoveStock_InsufficientStock() {
        // Arrange
        testPesticide.setCurrentStock(10);
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.removeStock(addedPesticide.getId(), 20, testUserId, "测试"));
        assertTrue(exception.getMessage().contains("库存不足"));
    }
    
    @Test
    void testTransferStock_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        boolean success = pesticideManagementService.transferStock(
            addedPesticide.getId(), 20, "仓库A", "仓库B", testUserId);
        
        // Assert
        assertTrue(success);
    }
    
    @Test
    void testRecordUsage_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        int originalStock = addedPesticide.getCurrentStock();
        
        // Act
        PesticideUsageRecord recorded = pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Assert
        assertNotNull(recorded);
        assertNotNull(recorded.getId());
        assertEquals(testUserId, recorded.getUserId());
        assertNotNull(recorded.getUsageTime());
        assertEquals(testUsageRecord.getQuantityUsed() * addedPesticide.getUnitPrice(), recorded.getCost());
        
        // 验证库存减少
        Pesticide updatedPesticide = pesticideManagementService.getPesticideById(addedPesticide.getId());
        assertEquals(originalStock - testUsageRecord.getQuantityUsed(), updatedPesticide.getCurrentStock());
    }
    
    @Test
    void testRecordUsage_NullRecord() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.recordUsage(null, testUserId));
        assertEquals("使用记录不能为空", exception.getMessage());
    }
    
    @Test
    void testRecordUsage_PesticideNotFound() {
        // Arrange
        testUsageRecord.setPesticideId("non-existent");
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.recordUsage(testUsageRecord, testUserId));
        assertEquals("药剂不存在", exception.getMessage());
    }
    
    @Test
    void testRecordUsage_InsufficientStock() {
        // Arrange
        testPesticide.setCurrentStock(5);
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        testUsageRecord.setQuantityUsed(10); // 超过库存
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.recordUsage(testUsageRecord, testUserId));
        assertEquals("库存不足，无法记录使用", exception.getMessage());
    }
    
    @Test
    void testGetUsageRecords_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Act
        List<PesticideUsageRecord> records = pesticideManagementService.getUsageRecords(addedPesticide.getId());
        
        // Assert
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(testUsageRecord.getPurpose(), records.get(0).getPurpose());
    }
    
    @Test
    void testGetUserUsageRecords_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Act
        List<PesticideUsageRecord> records = pesticideManagementService.getUserUsageRecords(testUserId);
        
        // Assert
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(testUserId, records.get(0).getUserId());
    }
    
    @Test
    void testGetUsageRecordsByDateRange_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        
        // Act
        List<PesticideUsageRecord> records = pesticideManagementService.getUsageRecordsByDateRange(startDate, endDate);
        
        // Assert
        assertNotNull(records);
        assertEquals(1, records.size());
    }
    
    @Test
    void testGetLowStockPesticides_Success() {
        // Arrange
        testPesticide.setCurrentStock(5); // 低于默认预警值
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<Pesticide> lowStock = pesticideManagementService.getLowStockPesticides();
        
        // Assert
        assertNotNull(lowStock);
        assertTrue(lowStock.stream().anyMatch(p -> p.getId().equals(addedPesticide.getId())));
    }
    
    @Test
    void testGetExpiringPesticides_Success() {
        // Arrange
        testPesticide.setExpiryDate(LocalDate.now().plusDays(15)); // 15天后过期
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<Pesticide> expiring = pesticideManagementService.getExpiringPesticides();
        
        // Assert
        assertNotNull(expiring);
        assertTrue(expiring.stream().anyMatch(p -> p.getId().equals(addedPesticide.getId())));
    }
    
    @Test
    void testGetExpiredPesticides_Success() {
        // Arrange
        testPesticide.setExpiryDate(LocalDate.now().minusDays(1)); // 已过期
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<Pesticide> expired = pesticideManagementService.getExpiredPesticides();
        
        // Assert
        assertNotNull(expired);
        assertTrue(expired.stream().anyMatch(p -> p.getId().equals(addedPesticide.getId())));
    }
    
    @Test
    void testSetStockAlert_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        boolean success = pesticideManagementService.setStockAlert(addedPesticide.getId(), 20, testUserId);
        
        // Assert
        assertTrue(success);
    }
    
    @Test
    void testSetStockAlert_PesticideNotFound() {
        // Act
        boolean success = pesticideManagementService.setStockAlert("non-existent", 20, testUserId);
        
        // Assert
        assertFalse(success);
    }
    
    @Test
    void testGetUsageStatistics_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Act
        Map<String, Object> statistics = pesticideManagementService.getUsageStatistics();
        
        // Assert
        assertNotNull(statistics);
        assertTrue(statistics.containsKey("totalUsageRecords"));
        assertTrue(statistics.containsKey("usageByType"));
        assertTrue(statistics.containsKey("costByType"));
        assertTrue(statistics.containsKey("totalCost"));
        assertTrue(statistics.containsKey("monthlyUsage"));
        
        assertEquals(1, statistics.get("totalUsageRecords"));
    }
    
    @Test
    void testGetUserUsageStatistics_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Act
        Map<String, Object> statistics = pesticideManagementService.getUserUsageStatistics(testUserId);
        
        // Assert
        assertNotNull(statistics);
        assertTrue(statistics.containsKey("totalRecords"));
        assertTrue(statistics.containsKey("totalQuantity"));
        assertTrue(statistics.containsKey("totalCost"));
        
        assertEquals(1, statistics.get("totalRecords"));
        assertEquals(10.0, (Double) statistics.get("totalQuantity"));
    }
    
    @Test
    void testGetCostAnalysis_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Act
        Map<String, Object> analysis = pesticideManagementService.getCostAnalysis();
        
        // Assert
        assertNotNull(analysis);
        assertTrue(analysis.containsKey("totalCost"));
        assertTrue(analysis.containsKey("averageCost"));
        assertTrue(analysis.containsKey("costByPesticide"));
        
        double expectedCost = testUsageRecord.getQuantityUsed() * addedPesticide.getUnitPrice();
        assertEquals(expectedCost, (Double) analysis.get("totalCost"));
    }
    
    @Test
    void testGetInventoryValueStatistics_Success() {
        // Arrange
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        Map<String, Object> statistics = pesticideManagementService.getInventoryValueStatistics();
        
        // Assert
        assertNotNull(statistics);
        assertTrue(statistics.containsKey("totalValue"));
        assertTrue(statistics.containsKey("totalItems"));
        assertTrue(statistics.containsKey("valueByType"));
        assertTrue(statistics.containsKey("averageUnitValue"));
        
        assertTrue((Double) statistics.get("totalValue") > 0);
        assertTrue((Integer) statistics.get("totalItems") > 0);
    }
    
    @Test
    void testGetPesticideSafetyInfo_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        Map<String, Object> safetyInfo = pesticideManagementService.getPesticideSafetyInfo(addedPesticide.getId());
        
        // Assert
        assertNotNull(safetyInfo);
        assertTrue(safetyInfo.containsKey("toxicityLevel"));
        assertTrue(safetyInfo.containsKey("safetyInterval"));
        assertTrue(safetyInfo.containsKey("maxResidueLimit"));
        assertTrue(safetyInfo.containsKey("protectiveEquipment"));
        assertTrue(safetyInfo.containsKey("firstAidMeasures"));
        
        assertEquals(testPesticide.getToxicityLevel(), safetyInfo.get("toxicityLevel"));
        assertEquals(testPesticide.getSafetyInterval(), safetyInfo.get("safetyInterval"));
    }
    
    @Test
    void testGetPesticideSafetyInfo_NotFound() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.getPesticideSafetyInfo("non-existent"));
        assertEquals("药剂不存在", exception.getMessage());
    }
    
    @Test
    void testUpdatePesticideSafetyInfo_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        Map<String, Object> safetyInfo = new HashMap<>();
        safetyInfo.put("toxicityLevel", "中毒");
        safetyInfo.put("safetyInterval", 14);
        safetyInfo.put("maxResidueLimit", 1.0);
        
        // Act
        boolean success = pesticideManagementService.updatePesticideSafetyInfo(addedPesticide.getId(), safetyInfo, testUserId);
        
        // Assert
        assertTrue(success);
        
        // 验证更新
        Pesticide updated = pesticideManagementService.getPesticideById(addedPesticide.getId());
        assertEquals("中毒", updated.getToxicityLevel());
        assertEquals(14, updated.getSafetyInterval().intValue());
        assertEquals(1.0, updated.getMaxResidueLimit());
    }
    
    @Test
    void testGetUsageSuggestions_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<String> suggestions = pesticideManagementService.getUsageSuggestions(addedPesticide.getId(), "害虫");
        
        // Assert
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("害虫活动高峰期")));
    }
    
    @Test
    void testCheckPesticideCompatibility_Compatible() {
        // Arrange
        Pesticide pesticide1 = new Pesticide();
        pesticide1.setName("药剂1");
        pesticide1.setActiveIngredient("成分1");
        pesticide1.setType("杀虫剂");
        pesticide1.setFormulation("中性");
        pesticide1.setUnitPrice(50.0);
        Pesticide added1 = pesticideManagementService.addPesticide(pesticide1, testUserId);
        
        Pesticide pesticide2 = new Pesticide();
        pesticide2.setName("药剂2");
        pesticide2.setActiveIngredient("成分2");
        pesticide2.setType("杀菌剂");
        pesticide2.setFormulation("中性");
        pesticide2.setUnitPrice(45.0);
        Pesticide added2 = pesticideManagementService.addPesticide(pesticide2, testUserId);
        
        List<String> pesticideIds = Arrays.asList(added1.getId(), added2.getId());
        
        // Act
        Map<String, Object> compatibility = pesticideManagementService.checkPesticideCompatibility(pesticideIds);
        
        // Assert
        assertNotNull(compatibility);
        assertTrue(compatibility.containsKey("compatible"));
        assertTrue(compatibility.containsKey("message"));
        assertTrue((Boolean) compatibility.get("compatible"));
    }
    
    @Test
    void testCheckPesticideCompatibility_Incompatible() {
        // Arrange
        Pesticide pesticide1 = new Pesticide();
        pesticide1.setName("酸性药剂");
        pesticide1.setActiveIngredient("成分1");
        pesticide1.setType("杀虫剂");
        pesticide1.setFormulation("酸性");
        pesticide1.setUnitPrice(50.0);
        Pesticide added1 = pesticideManagementService.addPesticide(pesticide1, testUserId);
        
        Pesticide pesticide2 = new Pesticide();
        pesticide2.setName("碱性药剂");
        pesticide2.setActiveIngredient("成分2");
        pesticide2.setType("杀菌剂");
        pesticide2.setFormulation("碱性");
        pesticide2.setUnitPrice(45.0);
        Pesticide added2 = pesticideManagementService.addPesticide(pesticide2, testUserId);
        
        List<String> pesticideIds = Arrays.asList(added1.getId(), added2.getId());
        
        // Act
        Map<String, Object> compatibility = pesticideManagementService.checkPesticideCompatibility(pesticideIds);
        
        // Assert
        assertNotNull(compatibility);
        assertFalse((Boolean) compatibility.get("compatible"));
        assertTrue(compatibility.get("message").toString().contains("酸性和碱性药剂不能混用"));
    }
    
    @Test
    void testGetMixingSuggestions_Success() {
        // Act
        List<Map<String, Object>> suggestions = pesticideManagementService.getMixingSuggestions("蚜虫");
        
        // Assert
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        
        Map<String, Object> firstSuggestion = suggestions.get(0);
        assertTrue(firstSuggestion.containsKey("combination"));
        assertTrue(firstSuggestion.containsKey("ratio"));
        assertTrue(firstSuggestion.containsKey("effectiveness"));
        assertTrue(firstSuggestion.containsKey("notes"));
    }
    
    @Test
    void testCalculateDosage_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        Map<String, Object> dosage = pesticideManagementService.calculateDosage(addedPesticide.getId(), 10.0, "果树");
        
        // Assert
        assertNotNull(dosage);
        assertTrue(dosage.containsKey("pesticideName"));
        assertTrue(dosage.containsKey("area"));
        assertTrue(dosage.containsKey("cropType"));
        assertTrue(dosage.containsKey("dosagePerUnit"));
        assertTrue(dosage.containsKey("totalDosage"));
        assertTrue(dosage.containsKey("waterVolume"));
        assertTrue(dosage.containsKey("concentration"));
        assertTrue(dosage.containsKey("estimatedCost"));
        
        assertEquals(addedPesticide.getName(), dosage.get("pesticideName"));
        assertEquals(10.0, dosage.get("area"));
        assertEquals("果树", dosage.get("cropType"));
    }
    
    @Test
    void testCalculateDosage_PesticideNotFound() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.calculateDosage("non-existent", 10.0, "果树"));
        assertEquals("药剂不存在", exception.getMessage());
    }
    
    @Test
    void testGetInventoryReport_Success() {
        // Arrange
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        Map<String, Object> report = pesticideManagementService.getInventoryReport();
        
        // Assert
        assertNotNull(report);
        assertTrue(report.containsKey("totalPesticides"));
        assertTrue(report.containsKey("totalStock"));
        assertTrue(report.containsKey("totalValue"));
        assertTrue(report.containsKey("lowStockCount"));
        assertTrue(report.containsKey("expiringCount"));
        assertTrue(report.containsKey("expiredCount"));
        assertTrue(report.containsKey("typeStatistics"));
        assertTrue(report.containsKey("reportTime"));
        
        assertTrue((Integer) report.get("totalPesticides") > 0);
    }
    
    @Test
    void testGetUsageTrends_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Act
        Map<String, List<Map<String, Object>>> trends = pesticideManagementService.getUsageTrends("monthly");
        
        // Assert
        assertNotNull(trends);
        assertTrue(trends.containsKey("usageTrend"));
        assertTrue(trends.containsKey("costTrend"));
        
        List<Map<String, Object>> usageTrend = trends.get("usageTrend");
        assertNotNull(usageTrend);
        assertFalse(usageTrend.isEmpty());
        
        Map<String, Object> firstItem = usageTrend.get(0);
        assertTrue(firstItem.containsKey("period"));
        assertTrue(firstItem.containsKey("usage"));
    }
    
    @Test
    void testExportInventoryData_JSON() {
        // Arrange
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        String exportData = pesticideManagementService.exportInventoryData("json");
        
        // Assert
        assertNotNull(exportData);
        assertFalse(exportData.isEmpty());
    }
    
    @Test
    void testExportInventoryData_CSV() {
        // Arrange
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        String exportData = pesticideManagementService.exportInventoryData("csv");
        
        // Assert
        assertNotNull(exportData);
        assertTrue(exportData.contains("药剂名称,商品名,有效成分"));
        assertTrue(exportData.contains(testPesticide.getName()));
    }
    
    @Test
    void testExportInventoryData_UnsupportedFormat() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.exportInventoryData("xml"));
        assertEquals("不支持的导出格式: xml", exception.getMessage());
    }
    
    @Test
    void testExportUsageRecords_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        
        // Act
        String exportData = pesticideManagementService.exportUsageRecords(startDate, endDate, "csv");
        
        // Assert
        assertNotNull(exportData);
        assertTrue(exportData.contains("使用时间,药剂名称,使用数量"));
        assertTrue(exportData.contains(testPesticide.getName()));
    }
    
    @Test
    void testBatchUpdateStock_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        List<Map<String, Object>> updates = new ArrayList<>();
        Map<String, Object> update1 = new HashMap<>();
        update1.put("pesticideId", addedPesticide.getId());
        update1.put("operation", "add");
        update1.put("quantity", 50);
        update1.put("notes", "批量入库");
        updates.add(update1);
        
        // Act
        List<Pesticide> updated = pesticideManagementService.batchUpdateStock(updates, testUserId);
        
        // Assert
        assertNotNull(updated);
        assertEquals(1, updated.size());
        assertEquals(testPesticide.getCurrentStock() + 50, updated.get(0).getCurrentStock());
    }
    
    @Test
    void testBatchUpdateStock_TooManyUpdates() {
        // Arrange
        List<Map<String, Object>> tooManyUpdates = new ArrayList<>();
        for (int i = 0; i < 51; i++) {
            Map<String, Object> update = new HashMap<>();
            update.put("pesticideId", "pesticide-" + i);
            update.put("operation", "add");
            update.put("quantity", 10);
            tooManyUpdates.add(update);
        }
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.batchUpdateStock(tooManyUpdates, testUserId));
        assertEquals("批量更新最多支持50个药剂", exception.getMessage());
    }
    
    @Test
    void testGetSupplierInfo_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<Map<String, Object>> suppliers = pesticideManagementService.getSupplierInfo(addedPesticide.getId());
        
        // Assert
        assertNotNull(suppliers);
        assertFalse(suppliers.isEmpty());
        
        Map<String, Object> firstSupplier = suppliers.get(0);
        assertTrue(firstSupplier.containsKey("id"));
        assertTrue(firstSupplier.containsKey("name"));
        assertTrue(firstSupplier.containsKey("contact"));
        assertTrue(firstSupplier.containsKey("price"));
    }
    
    @Test
    void testAddSupplier_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        Map<String, Object> supplierInfo = new HashMap<>();
        supplierInfo.put("name", "新供应商");
        supplierInfo.put("contact", "联系人");
        supplierInfo.put("phone", "138****1234");
        
        // Act
        boolean success = pesticideManagementService.addSupplier(addedPesticide.getId(), supplierInfo, testUserId);
        
        // Assert
        assertTrue(success);
    }
    
    @Test
    void testGetPurchaseSuggestions_Success() {
        // Arrange
        testPesticide.setCurrentStock(5); // 低库存
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        List<Map<String, Object>> suggestions = pesticideManagementService.getPurchaseSuggestions();
        
        // Assert
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        
        Map<String, Object> firstSuggestion = suggestions.get(0);
        assertTrue(firstSuggestion.containsKey("pesticideId"));
        assertTrue(firstSuggestion.containsKey("pesticideName"));
        assertTrue(firstSuggestion.containsKey("currentStock"));
        assertTrue(firstSuggestion.containsKey("suggestedQuantity"));
        assertTrue(firstSuggestion.containsKey("priority"));
    }
    
    @Test
    void testCreatePurchaseOrder_Success() {
        // Arrange
        List<Map<String, Object>> orderItems = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("pesticideId", "pesticide-001");
        item.put("quantity", 100);
        item.put("price", 50.0);
        orderItems.add(item);
        
        // Act
        Map<String, Object> order = pesticideManagementService.createPurchaseOrder(orderItems, testUserId);
        
        // Assert
        assertNotNull(order);
        assertTrue(order.containsKey("orderId"));
        assertTrue(order.containsKey("userId"));
        assertTrue(order.containsKey("orderTime"));
        assertTrue(order.containsKey("status"));
        assertTrue(order.containsKey("items"));
        assertTrue(order.containsKey("totalAmount"));
        
        assertEquals(testUserId, order.get("userId"));
        assertEquals("待确认", order.get("status"));
        assertEquals(5000.0, (Double) order.get("totalAmount"));
    }
    
    @Test
    void testCreatePurchaseOrder_EmptyItems() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.createPurchaseOrder(new ArrayList<>(), testUserId));
        assertEquals("订单项目不能为空", exception.getMessage());
    }
    
    @Test
    void testValidatePesticide_Valid() {
        // Act
        boolean isValid = pesticideManagementService.validatePesticide(testPesticide);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void testValidatePesticide_InvalidName() {
        // Arrange
        testPesticide.setName("");
        
        // Act
        boolean isValid = pesticideManagementService.validatePesticide(testPesticide);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testValidatePesticide_InvalidPrice() {
        // Arrange
        testPesticide.setUnitPrice(0.0);
        
        // Act
        boolean isValid = pesticideManagementService.validatePesticide(testPesticide);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testGetPesticideTypeStatistics_Success() {
        // Arrange
        pesticideManagementService.addPesticide(testPesticide, testUserId);
        
        // Act
        Map<String, Long> statistics = pesticideManagementService.getPesticideTypeStatistics();
        
        // Assert
        assertNotNull(statistics);
        assertTrue(statistics.containsKey("杀虫剂"));
        assertTrue(statistics.get("杀虫剂") > 0);
    }
    
    @Test
    void testGetPopularPesticides_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Act
        List<Map<String, Object>> popular = pesticideManagementService.getPopularPesticides(5);
        
        // Assert
        assertNotNull(popular);
        assertFalse(popular.isEmpty());
        
        Map<String, Object> firstItem = popular.get(0);
        assertTrue(firstItem.containsKey("pesticideId"));
        assertTrue(firstItem.containsKey("pesticideName"));
        assertTrue(firstItem.containsKey("usageCount"));
        assertEquals(1L, firstItem.get("usageCount"));
    }
    
    @Test
    void testPredictPesticideDemand_Success() {
        // Arrange
        Pesticide addedPesticide = pesticideManagementService.addPesticide(testPesticide, testUserId);
        testUsageRecord.setPesticideId(addedPesticide.getId());
        pesticideManagementService.recordUsage(testUsageRecord, testUserId);
        
        // Act
        Map<String, Object> prediction = pesticideManagementService.predictPesticideDemand(addedPesticide.getId(), 30);
        
        // Assert
        assertNotNull(prediction);
        assertTrue(prediction.containsKey("pesticideId"));
        assertTrue(prediction.containsKey("pesticideName"));
        assertTrue(prediction.containsKey("predictionDays"));
        assertTrue(prediction.containsKey("predictedDemand"));
        assertTrue(prediction.containsKey("confidence"));
        assertTrue(prediction.containsKey("currentStock"));
        assertTrue(prediction.containsKey("stockSufficient"));
        
        assertEquals(addedPesticide.getId(), prediction.get("pesticideId"));
        assertEquals(30, prediction.get("predictionDays"));
    }
    
    @Test
    void testPredictPesticideDemand_PesticideNotFound() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> pesticideManagementService.predictPesticideDemand("non-existent", 30));
        assertEquals("药剂不存在", exception.getMessage());
    }
}