package com.forestpest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestpest.entity.Pesticide;
import com.forestpest.entity.PesticideUsageRecord;
import com.forestpest.service.PesticideManagementService;
import com.forestpest.controller.PesticideController.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 药剂管理控制器测试类
 */
@WebMvcTest(PesticideController.class)
class PesticideControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PesticideManagementService pesticideManagementService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
        testUsageRecord.setCost(500.0);
    }
    
    @Test
    void testGetPesticideInventory_Success() throws Exception {
        // Arrange
        List<Pesticide> inventory = Arrays.asList(testPesticide);
        when(pesticideManagementService.getPesticideInventory()).thenReturn(inventory);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testPesticide.getId()))
                .andExpect(jsonPath("$.data[0].name").value(testPesticide.getName()));
        
        verify(pesticideManagementService).getPesticideInventory();
    }
    
    @Test
    void testGetPesticideInventory_WithPagination() throws Exception {
        // Arrange
        List<Pesticide> inventory = Arrays.asList(testPesticide);
        when(pesticideManagementService.getPesticideInventory(1, 10)).thenReturn(inventory);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/inventory")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pesticideManagementService).getPesticideInventory(1, 10);
    }
    
    @Test
    void testGetPesticideById_Success() throws Exception {
        // Arrange
        when(pesticideManagementService.getPesticideById(testPesticide.getId()))
            .thenReturn(testPesticide);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/{pesticideId}", testPesticide.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPesticide.getId()))
                .andExpect(jsonPath("$.data.name").value(testPesticide.getName()));
        
        verify(pesticideManagementService).getPesticideById(testPesticide.getId());
    }
    
    @Test
    void testGetPesticideById_NotFound() throws Exception {
        // Arrange
        when(pesticideManagementService.getPesticideById("non-existent"))
            .thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/{pesticideId}", "non-existent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("药剂不存在"));
        
        verify(pesticideManagementService).getPesticideById("non-existent");
    }
    
    @Test
    void testSearchPesticidesByName_Success() throws Exception {
        // Arrange
        List<Pesticide> searchResults = Arrays.asList(testPesticide);
        when(pesticideManagementService.searchPesticidesByName("测试"))
            .thenReturn(searchResults);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/search")
                .param("name", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value(testPesticide.getName()));
        
        verify(pesticideManagementService).searchPesticidesByName("测试");
    }
    
    @Test
    void testGetPesticidesByType_Success() throws Exception {
        // Arrange
        List<Pesticide> pesticides = Arrays.asList(testPesticide);
        when(pesticideManagementService.getPesticidesByType("杀虫剂"))
            .thenReturn(pesticides);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/type/{type}", "杀虫剂"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("杀虫剂"));
        
        verify(pesticideManagementService).getPesticidesByType("杀虫剂");
    }
    
    @Test
    void testSearchPesticidesByActiveIngredient_Success() throws Exception {
        // Arrange
        List<Pesticide> pesticides = Arrays.asList(testPesticide);
        when(pesticideManagementService.searchPesticidesByActiveIngredient("吡虫啉"))
            .thenReturn(pesticides);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/search/ingredient")
                .param("activeIngredient", "吡虫啉"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pesticideManagementService).searchPesticidesByActiveIngredient("吡虫啉");
    }
    
    @Test
    void testAddPesticide_Success() throws Exception {
        // Arrange
        AddPesticideRequest request = new AddPesticideRequest();
        request.setPesticide(testPesticide);
        request.setUserId(testUserId);
        
        when(pesticideManagementService.addPesticide(any(Pesticide.class), eq(testUserId)))
            .thenReturn(testPesticide);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPesticide.getId()));
        
        verify(pesticideManagementService).addPesticide(any(Pesticide.class), eq(testUserId));
    }
    
    @Test
    void testUpdatePesticide_Success() throws Exception {
        // Arrange
        UpdatePesticideRequest request = new UpdatePesticideRequest();
        request.setPesticide(testPesticide);
        request.setUserId(testUserId);
        
        when(pesticideManagementService.updatePesticide(eq(testPesticide.getId()), any(Pesticide.class), eq(testUserId)))
            .thenReturn(testPesticide);
        
        // Act & Assert
        mockMvc.perform(put("/api/pesticide/{pesticideId}", testPesticide.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPesticide.getId()));
        
        verify(pesticideManagementService).updatePesticide(eq(testPesticide.getId()), any(Pesticide.class), eq(testUserId));
    }
    
    @Test
    void testDeletePesticide_Success() throws Exception {
        // Arrange
        when(pesticideManagementService.deletePesticide(testPesticide.getId(), testUserId))
            .thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(delete("/api/pesticide/{pesticideId}", testPesticide.getId())
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(pesticideManagementService).deletePesticide(testPesticide.getId(), testUserId);
    }
    
    @Test
    void testDeletePesticide_Failed() throws Exception {
        // Arrange
        when(pesticideManagementService.deletePesticide(testPesticide.getId(), testUserId))
            .thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(delete("/api/pesticide/{pesticideId}", testPesticide.getId())
                .param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败，药剂不存在或有库存"));
        
        verify(pesticideManagementService).deletePesticide(testPesticide.getId(), testUserId);
    }
    
    @Test
    void testAddStock_Success() throws Exception {
        // Arrange
        StockOperationRequest request = new StockOperationRequest();
        request.setQuantity(50);
        request.setUserId(testUserId);
        request.setNotes("入库测试");
        
        testPesticide.setCurrentStock(150);
        when(pesticideManagementService.addStock(testPesticide.getId(), 50, testUserId, "入库测试"))
            .thenReturn(testPesticide);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide/{pesticideId}/stock/add", testPesticide.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.currentStock").value(150));
        
        verify(pesticideManagementService).addStock(testPesticide.getId(), 50, testUserId, "入库测试");
    }
    
    @Test
    void testRemoveStock_Success() throws Exception {
        // Arrange
        StockOperationRequest request = new StockOperationRequest();
        request.setQuantity(30);
        request.setUserId(testUserId);
        request.setNotes("出库测试");
        
        testPesticide.setCurrentStock(70);
        when(pesticideManagementService.removeStock(testPesticide.getId(), 30, testUserId, "出库测试"))
            .thenReturn(testPesticide);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide/{pesticideId}/stock/remove", testPesticide.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.currentStock").value(70));
        
        verify(pesticideManagementService).removeStock(testPesticide.getId(), 30, testUserId, "出库测试");
    }
    
    @Test
    void testTransferStock_Success() throws Exception {
        // Arrange
        TransferStockRequest request = new TransferStockRequest();
        request.setQuantity(20);
        request.setFromLocation("仓库A");
        request.setToLocation("仓库B");
        request.setUserId(testUserId);
        
        when(pesticideManagementService.transferStock(testPesticide.getId(), 20, "仓库A", "仓库B", testUserId))
            .thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide/{pesticideId}/stock/transfer", testPesticide.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(pesticideManagementService).transferStock(testPesticide.getId(), 20, "仓库A", "仓库B", testUserId);
    }
    
    @Test
    void testRecordUsage_Success() throws Exception {
        // Arrange
        RecordUsageRequest request = new RecordUsageRequest();
        request.setUsageRecord(testUsageRecord);
        request.setUserId(testUserId);
        
        when(pesticideManagementService.recordUsage(any(PesticideUsageRecord.class), eq(testUserId)))
            .thenReturn(testUsageRecord);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide/usage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testUsageRecord.getId()))
                .andExpect(jsonPath("$.data.quantityUsed").value(10));
        
        verify(pesticideManagementService).recordUsage(any(PesticideUsageRecord.class), eq(testUserId));
    }
    
    @Test
    void testGetUsageRecords_Success() throws Exception {
        // Arrange
        List<PesticideUsageRecord> records = Arrays.asList(testUsageRecord);
        when(pesticideManagementService.getUsageRecords(testPesticide.getId()))
            .thenReturn(records);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/{pesticideId}/usage", testPesticide.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testUsageRecord.getId()));
        
        verify(pesticideManagementService).getUsageRecords(testPesticide.getId());
    }
    
    @Test
    void testGetUsageRecords_WithPagination() throws Exception {
        // Arrange
        List<PesticideUsageRecord> records = Arrays.asList(testUsageRecord);
        when(pesticideManagementService.getUsageRecords(testPesticide.getId(), 1, 10))
            .thenReturn(records);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/{pesticideId}/usage", testPesticide.getId())
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pesticideManagementService).getUsageRecords(testPesticide.getId(), 1, 10);
    }
    
    @Test
    void testGetUserUsageRecords_Success() throws Exception {
        // Arrange
        List<PesticideUsageRecord> records = Arrays.asList(testUsageRecord);
        when(pesticideManagementService.getUserUsageRecords(testUserId))
            .thenReturn(records);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/usage/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(testUserId));
        
        verify(pesticideManagementService).getUserUsageRecords(testUserId);
    }
    
    @Test
    void testGetUsageRecordsByDateRange_Success() throws Exception {
        // Arrange
        List<PesticideUsageRecord> records = Arrays.asList(testUsageRecord);
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        when(pesticideManagementService.getUsageRecordsByDateRange(startDate, endDate))
            .thenReturn(records);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/usage/date-range")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pesticideManagementService).getUsageRecordsByDateRange(startDate, endDate);
    }
    
    @Test
    void testGetLowStockPesticides_Success() throws Exception {
        // Arrange
        List<Pesticide> lowStock = Arrays.asList(testPesticide);
        when(pesticideManagementService.getLowStockPesticides())
            .thenReturn(lowStock);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/alerts/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testPesticide.getId()));
        
        verify(pesticideManagementService).getLowStockPesticides();
    }
    
    @Test
    void testGetExpiringPesticides_Success() throws Exception {
        // Arrange
        List<Pesticide> expiring = Arrays.asList(testPesticide);
        when(pesticideManagementService.getExpiringPesticides())
            .thenReturn(expiring);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/alerts/expiring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pesticideManagementService).getExpiringPesticides();
    }
    
    @Test
    void testGetExpiredPesticides_Success() throws Exception {
        // Arrange
        List<Pesticide> expired = Arrays.asList(testPesticide);
        when(pesticideManagementService.getExpiredPesticides())
            .thenReturn(expired);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/alerts/expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(pesticideManagementService).getExpiredPesticides();
    }
    
    @Test
    void testSetStockAlert_Success() throws Exception {
        // Arrange
        SetAlertRequest request = new SetAlertRequest();
        request.setMinStock(20);
        request.setUserId(testUserId);
        
        when(pesticideManagementService.setStockAlert(testPesticide.getId(), 20, testUserId))
            .thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide/{pesticideId}/alert", testPesticide.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(pesticideManagementService).setStockAlert(testPesticide.getId(), 20, testUserId);
    }
    
    @Test
    void testGetUsageStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsageRecords", 10);
        statistics.put("totalCost", 5000.0);
        
        when(pesticideManagementService.getUsageStatistics())
            .thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/statistics/usage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsageRecords").value(10))
                .andExpect(jsonPath("$.data.totalCost").value(5000.0));
        
        verify(pesticideManagementService).getUsageStatistics();
    }
    
    @Test
    void testGetUserUsageStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRecords", 5);
        statistics.put("totalCost", 2500.0);
        
        when(pesticideManagementService.getUserUsageStatistics(testUserId))
            .thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/statistics/usage/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRecords").value(5))
                .andExpect(jsonPath("$.data.totalCost").value(2500.0));
        
        verify(pesticideManagementService).getUserUsageStatistics(testUserId);
    }
    
    @Test
    void testGetCostAnalysis_Success() throws Exception {
        // Arrange
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalCost", 10000.0);
        analysis.put("averageCost", 500.0);
        
        when(pesticideManagementService.getCostAnalysis())
            .thenReturn(analysis);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/analysis/cost"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCost").value(10000.0))
                .andExpect(jsonPath("$.data.averageCost").value(500.0));
        
        verify(pesticideManagementService).getCostAnalysis();
    }
    
    @Test
    void testGetCostAnalysis_WithDateRange() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalCost", 5000.0);
        
        when(pesticideManagementService.getCostAnalysisByDateRange(startDate, endDate))
            .thenReturn(analysis);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/analysis/cost")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCost").value(5000.0));
        
        verify(pesticideManagementService).getCostAnalysisByDateRange(startDate, endDate);
    }
    
    @Test
    void testGetInventoryValueStatistics_Success() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalValue", 50000.0);
        statistics.put("totalItems", 1000);
        
        when(pesticideManagementService.getInventoryValueStatistics())
            .thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/statistics/inventory-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalValue").value(50000.0))
                .andExpect(jsonPath("$.data.totalItems").value(1000));
        
        verify(pesticideManagementService).getInventoryValueStatistics();
    }
    
    @Test
    void testGetPesticideSafetyInfo_Success() throws Exception {
        // Arrange
        Map<String, Object> safetyInfo = new HashMap<>();
        safetyInfo.put("toxicityLevel", "低毒");
        safetyInfo.put("safetyInterval", 7);
        safetyInfo.put("protectiveEquipment", Arrays.asList("防护服", "防护手套"));
        
        when(pesticideManagementService.getPesticideSafetyInfo(testPesticide.getId()))
            .thenReturn(safetyInfo);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/{pesticideId}/safety", testPesticide.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.toxicityLevel").value("低毒"))
                .andExpect(jsonPath("$.data.safetyInterval").value(7));
        
        verify(pesticideManagementService).getPesticideSafetyInfo(testPesticide.getId());
    }
    
    @Test
    void testUpdatePesticideSafetyInfo_Success() throws Exception {
        // Arrange
        UpdateSafetyInfoRequest request = new UpdateSafetyInfoRequest();
        Map<String, Object> safetyInfo = new HashMap<>();
        safetyInfo.put("toxicityLevel", "中毒");
        request.setSafetyInfo(safetyInfo);
        request.setUserId(testUserId);
        
        when(pesticideManagementService.updatePesticideSafetyInfo(testPesticide.getId(), safetyInfo, testUserId))
            .thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(put("/api/pesticide/{pesticideId}/safety", testPesticide.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(pesticideManagementService).updatePesticideSafetyInfo(testPesticide.getId(), safetyInfo, testUserId);
    }
    
    @Test
    void testGetUsageSuggestions_Success() throws Exception {
        // Arrange
        List<String> suggestions = Arrays.asList(
            "建议在害虫活动高峰期使用",
            "注意轮换使用不同作用机理的药剂"
        );
        
        when(pesticideManagementService.getUsageSuggestions(testPesticide.getId(), "害虫"))
            .thenReturn(suggestions);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/{pesticideId}/suggestions", testPesticide.getId())
                .param("pestType", "害虫"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
        
        verify(pesticideManagementService).getUsageSuggestions(testPesticide.getId(), "害虫");
    }
    
    @Test
    void testCheckPesticideCompatibility_Success() throws Exception {
        // Arrange
        CompatibilityCheckRequest request = new CompatibilityCheckRequest();
        request.setPesticideIds(Arrays.asList("pesticide-001", "pesticide-002"));
        
        Map<String, Object> compatibility = new HashMap<>();
        compatibility.put("compatible", true);
        compatibility.put("message", "药剂可以混用");
        
        when(pesticideManagementService.checkPesticideCompatibility(request.getPesticideIds()))
            .thenReturn(compatibility);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide/compatibility/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.compatible").value(true))
                .andExpect(jsonPath("$.data.message").value("药剂可以混用"));
        
        verify(pesticideManagementService).checkPesticideCompatibility(request.getPesticideIds());
    }
    
    @Test
    void testGetMixingSuggestions_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> suggestions = Arrays.asList(
            Map.of("combination", Arrays.asList("杀虫剂A", "杀菌剂B"), "ratio", "1:1")
        );
        
        when(pesticideManagementService.getMixingSuggestions("蚜虫"))
            .thenReturn(suggestions);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/mixing/suggestions")
                .param("targetPest", "蚜虫"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].ratio").value("1:1"));
        
        verify(pesticideManagementService).getMixingSuggestions("蚜虫");
    }
    
    @Test
    void testCalculateDosage_Success() throws Exception {
        // Arrange
        CalculateDosageRequest request = new CalculateDosageRequest();
        request.setPesticideId(testPesticide.getId());
        request.setArea(10.0);
        request.setCropType("果树");
        
        Map<String, Object> dosage = new HashMap<>();
        dosage.put("totalDosage", 1200.0);
        dosage.put("waterVolume", 60000.0);
        dosage.put("estimatedCost", 60.0);
        
        when(pesticideManagementService.calculateDosage(testPesticide.getId(), 10.0, "果树"))
            .thenReturn(dosage);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide/dosage/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalDosage").value(1200.0))
                .andExpect(jsonPath("$.data.estimatedCost").value(60.0));
        
        verify(pesticideManagementService).calculateDosage(testPesticide.getId(), 10.0, "果树");
    }
    
    @Test
    void testGetInventoryReport_Success() throws Exception {
        // Arrange
        Map<String, Object> report = new HashMap<>();
        report.put("totalPesticides", 50);
        report.put("totalStock", 5000);
        report.put("totalValue", 250000.0);
        report.put("lowStockCount", 5);
        
        when(pesticideManagementService.getInventoryReport())
            .thenReturn(report);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/report/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPesticides").value(50))
                .andExpect(jsonPath("$.data.totalStock").value(5000))
                .andExpect(jsonPath("$.data.totalValue").value(250000.0));
        
        verify(pesticideManagementService).getInventoryReport();
    }
    
    @Test
    void testGetUsageTrends_Success() throws Exception {
        // Arrange
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        List<Map<String, Object>> usageTrend = Arrays.asList(
            Map.of("period", "2024-01", "usage", 1000.0)
        );
        trends.put("usageTrend", usageTrend);
        
        when(pesticideManagementService.getUsageTrends("monthly"))
            .thenReturn(trends);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/trends/usage")
                .param("period", "monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.usageTrend").isArray())
                .andExpect(jsonPath("$.data.usageTrend[0].period").value("2024-01"));
        
        verify(pesticideManagementService).getUsageTrends("monthly");
    }
    
    @Test
    void testExportInventoryData_Success() throws Exception {
        // Arrange
        String exportData = "exported inventory data";
        when(pesticideManagementService.exportInventoryData("json"))
            .thenReturn(exportData);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/export/inventory")
                .param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(exportData));
        
        verify(pesticideManagementService).exportInventoryData("json");
    }
    
    @Test
    void testExportUsageRecords_Success() throws Exception {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        String exportData = "exported usage records";
        
        when(pesticideManagementService.exportUsageRecords(startDate, endDate, "csv"))
            .thenReturn(exportData);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/export/usage")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("format", "csv"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(exportData));
        
        verify(pesticideManagementService).exportUsageRecords(startDate, endDate, "csv");
    }
    
    @Test
    void testBatchUpdateStock_Success() throws Exception {
        // Arrange
        BatchUpdateStockRequest request = new BatchUpdateStockRequest();
        List<Map<String, Object>> updates = Arrays.asList(
            Map.of("pesticideId", testPesticide.getId(), "operation", "add", "quantity", 50)
        );
        request.setStockUpdates(updates);
        request.setUserId(testUserId);
        
        List<Pesticide> updatedPesticides = Arrays.asList(testPesticide);
        when(pesticideManagementService.batchUpdateStock(updates, testUserId))
            .thenReturn(updatedPesticides);
        
        // Act & Assert
        mockMvc.perform(post("/api/pesticide/stock/batch-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testPesticide.getId()));
        
        verify(pesticideManagementService).batchUpdateStock(updates, testUserId);
    }
    
    @Test
    void testGetSupplierInfo_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> suppliers = Arrays.asList(
            Map.of("id", "supplier-001", "name", "供应商A", "price", 50.0)
        );
        
        when(pesticideManagementService.getSupplierInfo(testPesticide.getId()))
            .thenReturn(suppliers);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/{pesticideId}/suppliers", testPesticide.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("供应商A"));
        
        verify(pesticideManagementService).getSupplierInfo(testPesticide.getId());
    }
    
    @Test
    void testGetPesticideTypeStatistics_Success() throws Exception {
        // Arrange
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("杀虫剂", 20L);
        statistics.put("杀菌剂", 15L);
        statistics.put("除草剂", 10L);
        
        when(pesticideManagementService.getPesticideTypeStatistics())
            .thenReturn(statistics);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/statistics/type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.杀虫剂").value(20))
                .andExpect(jsonPath("$.data.杀菌剂").value(15))
                .andExpect(jsonPath("$.data.除草剂").value(10));
        
        verify(pesticideManagementService).getPesticideTypeStatistics();
    }
    
    @Test
    void testGetPopularPesticides_Success() throws Exception {
        // Arrange
        List<Map<String, Object>> popular = Arrays.asList(
            Map.of("pesticideId", testPesticide.getId(), "pesticideName", testPesticide.getName(), "usageCount", 25L)
        );
        
        when(pesticideManagementService.getPopularPesticides(10))
            .thenReturn(popular);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/popular")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].pesticideName").value(testPesticide.getName()))
                .andExpect(jsonPath("$.data[0].usageCount").value(25));
        
        verify(pesticideManagementService).getPopularPesticides(10);
    }
    
    @Test
    void testPredictPesticideDemand_Success() throws Exception {
        // Arrange
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("pesticideId", testPesticide.getId());
        prediction.put("predictedDemand", 300.0);
        prediction.put("confidence", 0.8);
        prediction.put("stockSufficient", false);
        
        when(pesticideManagementService.predictPesticideDemand(testPesticide.getId(), 30))
            .thenReturn(prediction);
        
        // Act & Assert
        mockMvc.perform(get("/api/pesticide/{pesticideId}/demand/predict", testPesticide.getId())
                .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pesticideId").value(testPesticide.getId()))
                .andExpect(jsonPath("$.data.predictedDemand").value(300.0))
                .andExpect(jsonPath("$.data.confidence").value(0.8))
                .andExpect(jsonPath("$.data.stockSufficient").value(false));
        
        verify(pesticideManagementService).predictPesticideDemand(testPesticide.getId(), 30);
    }
}