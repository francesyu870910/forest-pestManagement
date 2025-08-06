package com.forestpest.controller;

import com.forestpest.service.DataInitializationService;
import com.forestpest.data.manager.DataRelationshipManager;
import com.forestpest.util.DataExportUtil;
import com.forestpest.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * 数据管理控制器
 * 提供数据初始化、重置和统计的API接口
 */
@RestController
@RequestMapping("/api/data-management")
@CrossOrigin(origins = "*")
public class DataManagementController {
    
    @Autowired
    private DataInitializationService dataInitializationService;
    
    @Autowired
    private DataRelationshipManager relationshipManager;
    
    @Autowired
    private DataExportUtil dataExportUtil;
    
    /**
     * 初始化演示数据
     */
    @PostMapping("/initialize")
    public Result<String> initializeData() {
        try {
            dataInitializationService.initializeAllData();
            return Result.success("演示数据初始化成功");
        } catch (Exception e) {
            return Result.error("数据初始化失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置所有数据
     */
    @PostMapping("/reset")
    public Result<String> resetData() {
        try {
            dataInitializationService.resetAllData();
            return Result.success("数据重置成功");
        } catch (Exception e) {
            return Result.error("数据重置失败: " + e.getMessage());
        }
    }
    
    /**
     * 重新生成所有数据
     */
    @PostMapping("/regenerate")
    public Result<String> regenerateData() {
        try {
            dataInitializationService.regenerateAllData();
            return Result.success("数据重新生成成功");
        } catch (Exception e) {
            return Result.error("数据重新生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取数据统计信息
     */
    @GetMapping("/statistics")
    public Result<DataInitializationService.DataStatistics> getDataStatistics() {
        try {
            DataInitializationService.DataStatistics stats = dataInitializationService.getDataStatistics();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取数据统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查数据初始化状态
     */
    @GetMapping("/status")
    public Result<Boolean> checkDataStatus() {
        try {
            boolean isInitialized = dataInitializationService.isDataInitialized();
            return Result.success(isInitialized);
        } catch (Exception e) {
            return Result.error("检查数据状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证数据关联关系
     */
    @GetMapping("/validate")
    public Result<DataRelationshipManager.ValidationResult> validateDataRelationships() {
        try {
            DataRelationshipManager.ValidationResult result = relationshipManager.validateAllRelationships();
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("验证数据关联关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 修复数据关联关系
     */
    @PostMapping("/fix-relationships")
    public Result<String> fixDataRelationships() {
        try {
            relationshipManager.fixRelationships();
            return Result.success("数据关联关系修复完成");
        } catch (Exception e) {
            return Result.error("修复数据关联关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出指定类型的数据为JSON
     */
    @GetMapping("/export/{dataType}")
    public ResponseEntity<String> exportData(@PathVariable String dataType) {
        try {
            String jsonData = dataExportUtil.getDataAsJsonString(dataType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", dataType + ".json");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(jsonData);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"导出数据失败: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * 获取支持的数据类型列表
     */
    @GetMapping("/data-types")
    public Result<String[]> getSupportedDataTypes() {
        String[] dataTypes = {
            "users", "pests", "pesticides", "treatment_plans", 
            "treatment_tasks", "forest_resources", "knowledge_base"
        };
        return Result.success(dataTypes);
    }
}