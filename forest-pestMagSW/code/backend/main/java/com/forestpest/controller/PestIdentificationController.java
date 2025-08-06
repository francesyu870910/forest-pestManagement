package com.forestpest.controller;

import com.forestpest.common.ApiResponse;
import com.forestpest.entity.IdentificationResult;
import com.forestpest.entity.Pest;
import com.forestpest.service.PestIdentificationService;
import com.forestpest.util.ImageProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 病虫害识别控制器
 */
@RestController
@RequestMapping("/api/identification")
@CrossOrigin(origins = "*")
public class PestIdentificationController {
    
    @Autowired
    private PestIdentificationService pestIdentificationService;
    
    /**
     * 基于图片识别病虫
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<IdentificationResult> identifyByImage(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("userId") @NotBlank String userId) {
        
        // 验证图片文件
        ImageProcessingUtil.validateImageFile(imageFile);
        
        IdentificationResult result = pestIdentificationService.identifyByImage(imageFile, userId);
        return ApiResponse.success(result);
    }
    
    /**
     * 基于症状描述识别病虫
     */
    @PostMapping("/symptoms")
    public ApiResponse<List<IdentificationResult>> identifyBySymptoms(
            @RequestBody @Valid SymptomsRequest request) {
        
        List<IdentificationResult> results = pestIdentificationService.identifyBySymptoms(
            request.getSymptoms(), request.getUserId());
        return ApiResponse.success(results);
    }
    
    /**
     * 基于多个条件识别病虫
     */
    @PostMapping("/conditions")
    public ApiResponse<List<IdentificationResult>> identifyByMultipleConditions(
            @RequestBody @Valid MultipleConditionsRequest request) {
        
        List<IdentificationResult> results = pestIdentificationService.identifyByMultipleConditions(
            request.getSymptoms(), 
            request.getHostPlant(), 
            request.getSeason(), 
            request.getRegion(),
            request.getUserId());
        return ApiResponse.success(results);
    }
    
    /**
     * 获取识别历史记录
     */
    @GetMapping("/history/{userId}")
    public ApiResponse<List<IdentificationResult>> getIdentificationHistory(
            @PathVariable @NotBlank String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<IdentificationResult> history;
        if (page > 0 || size != 20) {
            history = pestIdentificationService.getIdentificationHistory(userId, page, size);
        } else {
            history = pestIdentificationService.getIdentificationHistory(userId);
        }
        return ApiResponse.success(history);
    }
    
    /**
     * 根据ID获取识别结果详情
     */
    @GetMapping("/result/{resultId}")
    public ApiResponse<IdentificationResult> getIdentificationResult(
            @PathVariable @NotBlank String resultId) {
        
        IdentificationResult result = pestIdentificationService.getIdentificationResult(resultId);
        if (result == null) {
            return ApiResponse.error("识别结果不存在");
        }
        return ApiResponse.success(result);
    }
    
    /**
     * 删除识别记录
     */
    @DeleteMapping("/result/{resultId}")
    public ApiResponse<Boolean> deleteIdentificationResult(
            @PathVariable @NotBlank String resultId,
            @RequestParam @NotBlank String userId) {
        
        boolean deleted = pestIdentificationService.deleteIdentificationResult(resultId, userId);
        if (!deleted) {
            return ApiResponse.error("删除失败，记录不存在或无权限");
        }
        return ApiResponse.success(true);
    }
    
    /**
     * 获取用户识别统计信息
     */
    @GetMapping("/statistics/{userId}")
    public ApiResponse<Map<String, Object>> getIdentificationStatistics(
            @PathVariable @NotBlank String userId) {
        
        Map<String, Object> statistics = pestIdentificationService.getIdentificationStatistics(userId);
        return ApiResponse.success(statistics);
    }
    
    /**
     * 获取系统识别统计信息
     */
    @GetMapping("/statistics/system")
    public ApiResponse<Map<String, Object>> getSystemIdentificationStatistics() {
        Map<String, Object> statistics = pestIdentificationService.getSystemIdentificationStatistics();
        return ApiResponse.success(statistics);
    }
    
    /**
     * 更新识别结果的用户反馈
     */
    @PutMapping("/feedback/{resultId}")
    public ApiResponse<IdentificationResult> updateUserFeedback(
            @PathVariable @NotBlank String resultId,
            @RequestBody @Valid FeedbackRequest request) {
        
        IdentificationResult result = pestIdentificationService.updateUserFeedback(
            resultId, request.getUserId(), request.isCorrect(), request.getFeedback());
        return ApiResponse.success(result);
    }
    
    /**
     * 获取识别准确率统计
     */
    @GetMapping("/accuracy")
    public ApiResponse<Map<String, Double>> getAccuracyStatistics() {
        Map<String, Double> accuracy = pestIdentificationService.getAccuracyStatistics();
        return ApiResponse.success(accuracy);
    }
    
    /**
     * 根据寄主植物获取可能的病虫害
     */
    @GetMapping("/pests/host-plant")
    public ApiResponse<List<Pest>> getPossiblePestsByHostPlant(
            @RequestParam @NotBlank String hostPlant) {
        
        List<Pest> pests = pestIdentificationService.getPossiblePestsByHostPlant(hostPlant);
        return ApiResponse.success(pests);
    }
    
    /**
     * 根据季节获取常见病虫害
     */
    @GetMapping("/pests/season")
    public ApiResponse<List<Pest>> getCommonPestsBySeason(
            @RequestParam @NotBlank String season) {
        
        List<Pest> pests = pestIdentificationService.getCommonPestsBySeason(season);
        return ApiResponse.success(pests);
    }
    
    /**
     * 根据地区获取常见病虫害
     */
    @GetMapping("/pests/region")
    public ApiResponse<List<Pest>> getCommonPestsByRegion(
            @RequestParam @NotBlank String region) {
        
        List<Pest> pests = pestIdentificationService.getCommonPestsByRegion(region);
        return ApiResponse.success(pests);
    }
    
    /**
     * 搜索相似的识别记录
     */
    @GetMapping("/similar/{resultId}")
    public ApiResponse<List<IdentificationResult>> findSimilarIdentifications(
            @PathVariable @NotBlank String resultId) {
        
        List<IdentificationResult> similar = pestIdentificationService.findSimilarIdentifications(resultId);
        return ApiResponse.success(similar);
    }
    
    /**
     * 批量处理图片识别
     */
    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<IdentificationResult>> batchIdentifyByImages(
            @RequestParam("images") List<MultipartFile> imageFiles,
            @RequestParam("userId") @NotBlank String userId) {
        
        // 验证每个图片文件
        for (MultipartFile imageFile : imageFiles) {
            ImageProcessingUtil.validateImageFile(imageFile);
        }
        
        List<IdentificationResult> results = pestIdentificationService.batchIdentifyByImages(imageFiles, userId);
        return ApiResponse.success(results);
    }
    
    /**
     * 获取识别建议
     */
    @GetMapping("/suggestions")
    public ApiResponse<List<String>> getIdentificationSuggestions(
            @RequestParam @NotBlank String partialSymptom) {
        
        List<String> suggestions = pestIdentificationService.getIdentificationSuggestions(partialSymptom);
        return ApiResponse.success(suggestions);
    }
    
    /**
     * 导出识别历史
     */
    @GetMapping("/export/{userId}")
    public ApiResponse<String> exportIdentificationHistory(
            @PathVariable @NotBlank String userId,
            @RequestParam(defaultValue = "json") String format) {
        
        String exportData = pestIdentificationService.exportIdentificationHistory(userId, format);
        return ApiResponse.success(exportData);
    }
    
    /**
     * 获取热门识别病虫害
     */
    @GetMapping("/popular")
    public ApiResponse<List<Map<String, Object>>> getPopularIdentifiedPests(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> popular = pestIdentificationService.getPopularIdentifiedPests(limit);
        return ApiResponse.success(popular);
    }
    
    /**
     * 获取识别趋势数据
     */
    @GetMapping("/trends")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getIdentificationTrends(
            @RequestParam(defaultValue = "monthly") String period) {
        
        Map<String, List<Map<String, Object>>> trends = pestIdentificationService.getIdentificationTrends(period);
        return ApiResponse.success(trends);
    }
    
    // ========== 请求对象类 ==========
    
    /**
     * 症状识别请求
     */
    public static class SymptomsRequest {
        @NotEmpty(message = "症状列表不能为空")
        private List<String> symptoms;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public List<String> getSymptoms() {
            return symptoms;
        }
        
        public void setSymptoms(List<String> symptoms) {
            this.symptoms = symptoms;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 多条件识别请求
     */
    public static class MultipleConditionsRequest {
        private List<String> symptoms;
        private String hostPlant;
        private String season;
        private String region;
        
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        // Getters and Setters
        public List<String> getSymptoms() {
            return symptoms;
        }
        
        public void setSymptoms(List<String> symptoms) {
            this.symptoms = symptoms;
        }
        
        public String getHostPlant() {
            return hostPlant;
        }
        
        public void setHostPlant(String hostPlant) {
            this.hostPlant = hostPlant;
        }
        
        public String getSeason() {
            return season;
        }
        
        public void setSeason(String season) {
            this.season = season;
        }
        
        public String getRegion() {
            return region;
        }
        
        public void setRegion(String region) {
            this.region = region;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * 用户反馈请求
     */
    public static class FeedbackRequest {
        @NotBlank(message = "用户ID不能为空")
        private String userId;
        
        @NotNull(message = "反馈结果不能为空")
        private Boolean correct;
        
        private String feedback;
        
        // Getters and Setters
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public boolean isCorrect() {
            return correct != null && correct;
        }
        
        public void setCorrect(Boolean correct) {
            this.correct = correct;
        }
        
        public String getFeedback() {
            return feedback;
        }
        
        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
    }
}
