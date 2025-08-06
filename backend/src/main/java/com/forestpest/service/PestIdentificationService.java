package com.forestpest.service;

import com.forestpest.entity.IdentificationResult;
import com.forestpest.entity.Pest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 病虫害识别服务接口
 */
public interface PestIdentificationService {
    
    /**
     * 基于图片识别病虫害
     */
    IdentificationResult identifyByImage(MultipartFile imageFile, String userId);
    
    /**
     * 基于症状描述识别病虫害
     */
    List<IdentificationResult> identifyBySymptoms(List<String> symptoms, String userId);
    
    /**
     * 基于多个条件识别病虫害
     */
    List<IdentificationResult> identifyByMultipleConditions(
        List<String> symptoms, 
        String hostPlant, 
        String season, 
        String region,
        String userId
    );
    
    /**
     * 获取识别历史记录
     */
    List<IdentificationResult> getIdentificationHistory(String userId);
    
    /**
     * 获取识别历史记录（分页）
     */
    List<IdentificationResult> getIdentificationHistory(String userId, int page, int size);
    
    /**
     * 根据ID获取识别结果
     */
    IdentificationResult getIdentificationResult(String resultId);
    
    /**
     * 删除识别记录
     */
    boolean deleteIdentificationResult(String resultId, String userId);
    
    /**
     * 获取识别统计信息
     */
    Map<String, Object> getIdentificationStatistics(String userId);
    
    /**
     * 获取系统识别统计信息（管理员）
     */
    Map<String, Object> getSystemIdentificationStatistics();
    
    /**
     * 更新识别结果的用户反馈
     */
    IdentificationResult updateUserFeedback(String resultId, String userId, boolean isCorrect, String feedback);
    
    /**
     * 获取识别准确率统计
     */
    Map<String, Double> getAccuracyStatistics();
    
    /**
     * 根据寄主植物获取可能的病虫害
     */
    List<Pest> getPossiblePestsByHostPlant(String hostPlant);
    
    /**
     * 根据季节获取常见病虫害
     */
    List<Pest> getCommonPestsBySeason(String season);
    
    /**
     * 根据地区获取常见病虫害
     */
    List<Pest> getCommonPestsByRegion(String region);
    
    /**
     * 搜索相似的识别记录
     */
    List<IdentificationResult> findSimilarIdentifications(String resultId);
    
    /**
     * 批量处理图片识别
     */
    List<IdentificationResult> batchIdentifyByImages(List<MultipartFile> imageFiles, String userId);
    
    /**
     * 获取识别建议
     */
    List<String> getIdentificationSuggestions(String partialSymptom);
    
    /**
     * 验证识别结果
     */
    boolean validateIdentificationResult(IdentificationResult result);
    
    /**
     * 导出识别历史
     */
    String exportIdentificationHistory(String userId, String format);
    
    /**
     * 获取热门识别病虫害
     */
    List<Map<String, Object>> getPopularIdentifiedPests(int limit);
    
    /**
     * 获取识别趋势数据
     */
    Map<String, List<Map<String, Object>>> getIdentificationTrends(String period);
}