package com.forestpest.service.impl;

import com.forestpest.entity.IdentificationResult;
import com.forestpest.entity.Pest;
import com.forestpest.repository.PestRepository;
import com.forestpest.service.PestIdentificationService;
import com.forestpest.data.storage.DataStorage;
import com.forestpest.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 病虫害识别服务实现类
 */
@Service
public class PestIdentificationServiceImpl implements PestIdentificationService {
    
    @Autowired
    private PestRepository pestRepository;
    
    @Autowired
    private DataStorage dataStorage;
    
    // 识别结果存储
    private final Map<String, IdentificationResult> identificationResults = new ConcurrentHashMap<>();
    
    // 用户识别历史索引
    private final Map<String, List<String>> userIdentificationHistory = new ConcurrentHashMap<>();
    
    private final Random random = new Random();
    
    @Override
    public IdentificationResult identifyByImage(MultipartFile imageFile, String userId) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new BusinessException("图片文件不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证图片格式
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("文件格式不正确，请上传图片文件");
        }
        
        // 验证文件大小（限制为10MB）
        if (imageFile.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException("图片文件大小不能超过10MB");
        }
        
        // 模拟图片识别算法
        IdentificationResult result = simulateImageIdentification(imageFile, userId);
        
        // 保存识别结果
        saveIdentificationResult(result, userId);
        
        return result;
    }
    
    @Override
    public List<IdentificationResult> identifyBySymptoms(List<String> symptoms, String userId) {
        if (symptoms == null || symptoms.isEmpty()) {
            throw new BusinessException("症状描述不能为空");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        List<IdentificationResult> results = new ArrayList<>();
        
        // 获取所有病虫害
        List<Pest> allPests = pestRepository.findAll();
        
        // 基于症状匹配病虫害
        for (Pest pest : allPests) {
            double matchScore = calculateSymptomMatchScore(symptoms, pest);
            if (matchScore > 0.3) { // 匹配度阈值
                IdentificationResult result = createIdentificationResult(pest, matchScore, userId);
                result.setIdentificationMethod("症状匹配");
                result.setInputSymptoms(symptoms);
                results.add(result);
            }
        }
        
        // 按匹配度排序
        results.sort((r1, r2) -> Double.compare(r2.getConfidence(), r1.getConfidence()));
        
        // 限制返回结果数量
        if (results.size() > 5) {
            results = results.subList(0, 5);
        }
        
        // 保存识别结果
        for (IdentificationResult result : results) {
            saveIdentificationResult(result, userId);
        }
        
        return results;
    }
    
    @Override
    public List<IdentificationResult> identifyByMultipleConditions(
            List<String> symptoms, String hostPlant, String season, String region, String userId) {
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }
        
        List<IdentificationResult> results = new ArrayList<>();
        List<Pest> allPests = pestRepository.findAll();
        
        for (Pest pest : allPests) {
            double totalScore = 0.0;
            int factorCount = 0;
            
            // 症状匹配
            if (symptoms != null && !symptoms.isEmpty()) {
                totalScore += calculateSymptomMatchScore(symptoms, pest) * 0.4;
                factorCount++;
            }
            
            // 寄主植物匹配
            if (hostPlant != null && !hostPlant.trim().isEmpty()) {
                double hostScore = calculateHostPlantMatchScore(hostPlant, pest);
                totalScore += hostScore * 0.3;
                factorCount++;
            }
            
            // 季节匹配
            if (season != null && !season.trim().isEmpty()) {
                double seasonScore = calculateSeasonMatchScore(season, pest);
                totalScore += seasonScore * 0.2;
                factorCount++;
            }
            
            // 地区匹配
            if (region != null && !region.trim().isEmpty()) {
                double regionScore = calculateRegionMatchScore(region, pest);
                totalScore += regionScore * 0.1;
                factorCount++;
            }
            
            if (factorCount > 0) {
                double averageScore = totalScore / factorCount;
                if (averageScore > 0.3) {
                    IdentificationResult result = createIdentificationResult(pest, averageScore, userId);
                    result.setIdentificationMethod("综合条件匹配");
                    result.setInputSymptoms(symptoms);
                    result.setLocation(region);
                    result.setNotes("Host plant: " + hostPlant + ", Season: " + season);
                    results.add(result);
                }
            }
        }
        
        // 按匹配度排序并限制结果数量
        results.sort((r1, r2) -> Double.compare(r2.getConfidence(), r1.getConfidence()));
        if (results.size() > 10) {
            results = results.subList(0, 10);
        }
        
        // 保存识别结果
        for (IdentificationResult result : results) {
            saveIdentificationResult(result, userId);
        }
        
        return results;
    } 
   
    @Override
    public List<IdentificationResult> getIdentificationHistory(String userId) {
        List<String> resultIds = userIdentificationHistory.get(userId);
        if (resultIds == null) {
            return new ArrayList<>();
        }
        
        return resultIds.stream()
                .map(identificationResults::get)
                .filter(Objects::nonNull)
                .sorted((r1, r2) -> r2.getIdentificationTime().compareTo(r1.getIdentificationTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<IdentificationResult> getIdentificationHistory(String userId, int page, int size) {
        List<IdentificationResult> allHistory = getIdentificationHistory(userId);
        
        int start = page * size;
        int end = Math.min(start + size, allHistory.size());
        
        if (start >= allHistory.size()) {
            return new ArrayList<>();
        }
        
        return allHistory.subList(start, end);
    }
    
    @Override
    public IdentificationResult getIdentificationResult(String resultId) {
        return identificationResults.get(resultId);
    }
    
    @Override
    public boolean deleteIdentificationResult(String resultId, String userId) {
        IdentificationResult result = identificationResults.get(resultId);
        if (result == null || !userId.equals(result.getUserId())) {
            return false;
        }
        
        identificationResults.remove(resultId);
        
        List<String> userHistory = userIdentificationHistory.get(userId);
        if (userHistory != null) {
            userHistory.remove(resultId);
        }
        
        return true;
    }
    
    @Override
    public Map<String, Object> getIdentificationStatistics(String userId) {
        List<IdentificationResult> userHistory = getIdentificationHistory(userId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalIdentifications", userHistory.size());
        
        // 按方法统计
        Map<String, Long> methodStats = userHistory.stream()
                .collect(Collectors.groupingBy(
                    IdentificationResult::getIdentificationMethod,
                    Collectors.counting()
                ));
        statistics.put("byMethod", methodStats);
        
        // 按月份统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> monthlyStats = userHistory.stream()
                .collect(Collectors.groupingBy(
                    result -> result.getIdentificationTime().format(formatter),
                    Collectors.counting()
                ));
        statistics.put("byMonth", monthlyStats);
        
        // 平均置信度
        double avgConfidence = userHistory.stream()
                .mapToDouble(IdentificationResult::getConfidence)
                .average()
                .orElse(0.0);
        statistics.put("averageConfidence", avgConfidence);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getSystemIdentificationStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        List<IdentificationResult> allResults = new ArrayList<>(identificationResults.values());
        statistics.put("totalIdentifications", allResults.size());
        statistics.put("totalUsers", userIdentificationHistory.size());
        
        // 最受欢迎的病虫害
        Map<String, Long> pestPopularity = allResults.stream()
                .collect(Collectors.groupingBy(
                    IdentificationResult::getPestId,
                    Collectors.counting()
                ));
        statistics.put("popularPests", pestPopularity);
        
        // 识别方法统计
        Map<String, Long> methodStats = allResults.stream()
                .collect(Collectors.groupingBy(
                    IdentificationResult::getIdentificationMethod,
                    Collectors.counting()
                ));
        statistics.put("methodStats", methodStats);
        
        return statistics;
    }
    
    @Override
    public IdentificationResult updateUserFeedback(String resultId, String userId, boolean isCorrect, String feedback) {
        IdentificationResult result = identificationResults.get(resultId);
        if (result == null || !userId.equals(result.getUserId())) {
            throw new BusinessException("识别结果不存在或无权限");
        }
        
        result.setNotes(feedback);
        result.setVerificationStatus(isCorrect ? "VERIFIED" : "REJECTED");
        result.setUpdatedTime(LocalDateTime.now());
        
        return result;
    }
    
    @Override
    public Map<String, Double> getAccuracyStatistics() {
        List<IdentificationResult> resultsWithFeedback = identificationResults.values().stream()
                .filter(result -> result.getUpdatedTime() != null)
                .collect(Collectors.toList());
        
        if (resultsWithFeedback.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<String, Double> accuracy = new HashMap<>();
        
        // 总体准确率
        long correctCount = resultsWithFeedback.stream()
                .mapToLong(result -> "VERIFIED".equals(result.getVerificationStatus()) ? 1 : 0)
                .sum();
        double overallAccuracy = (double) correctCount / resultsWithFeedback.size();
        accuracy.put("overall", overallAccuracy);
        
        // 按识别方法统计准确率
        Map<String, List<IdentificationResult>> byMethod = resultsWithFeedback.stream()
                .collect(Collectors.groupingBy(IdentificationResult::getIdentificationMethod));
        
        for (Map.Entry<String, List<IdentificationResult>> entry : byMethod.entrySet()) {
            List<IdentificationResult> methodResults = entry.getValue();
            long methodCorrectCount = methodResults.stream()
                    .mapToLong(result -> "VERIFIED".equals(result.getVerificationStatus()) ? 1 : 0)
                    .sum();
            double methodAccuracy = (double) methodCorrectCount / methodResults.size();
            accuracy.put(entry.getKey(), methodAccuracy);
        }
        
        return accuracy;
    }    
  
  @Override
    public List<Pest> getPossiblePestsByHostPlant(String hostPlant) {
        if (hostPlant == null || hostPlant.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return pestRepository.findByHostPlant(hostPlant);
    }
    
    @Override
    public List<Pest> getCommonPestsBySeason(String season) {
        // 模拟季节性病虫害数据
        List<Pest> allPests = pestRepository.findAll();
        return allPests.stream()
                .filter(pest -> isCommonInSeason(pest, season))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pest> getCommonPestsByRegion(String region) {
        return pestRepository.findByDistributionAreaContaining(region);
    }
    
    @Override
    public List<IdentificationResult> findSimilarIdentifications(String resultId) {
        IdentificationResult targetResult = identificationResults.get(resultId);
        if (targetResult == null) {
            return new ArrayList<>();
        }
        
        return identificationResults.values().stream()
                .filter(result -> !result.getId().equals(resultId))
                .filter(result -> isSimilarResult(targetResult, result))
                .sorted((r1, r2) -> Double.compare(r2.getConfidence(), r1.getConfidence()))
                .limit(5)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<IdentificationResult> batchIdentifyByImages(List<MultipartFile> imageFiles, String userId) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new BusinessException("图片文件列表不能为空");
        }
        
        if (imageFiles.size() > 10) {
            throw new BusinessException("批量识别最多支持10张图片");
        }
        
        List<IdentificationResult> results = new ArrayList<>();
        
        for (MultipartFile imageFile : imageFiles) {
            try {
                IdentificationResult result = identifyByImage(imageFile, userId);
                results.add(result);
            } catch (Exception e) {
                // 记录错误但继续处理其他图片
                IdentificationResult errorResult = new IdentificationResult();
                errorResult.setId(dataStorage.generateId());
                errorResult.setUserId(userId);
                errorResult.setIdentificationTime(LocalDateTime.now());
                errorResult.setIdentificationMethod("图片识别");
                errorResult.setNotes("识别失败: " + e.getMessage());
                errorResult.setConfidence(0.0);
                results.add(errorResult);
            }
        }
        
        return results;
    }
    
    @Override
    public List<String> getIdentificationSuggestions(String partialSymptom) {
        if (partialSymptom == null || partialSymptom.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 预定义的症状关键词
        List<String> allSymptoms = Arrays.asList(
            "叶片发黄", "叶片卷曲", "叶片有斑点", "叶片枯萎", "叶片被啃食",
            "树干有蛀孔", "树皮脱落", "枝条枯死", "根部腐烂", "果实变色",
            "分泌蜜露", "有虫粪", "有菌丝", "有异味", "生长缓慢"
        );
        
        String lowerPartial = partialSymptom.toLowerCase();
        return allSymptoms.stream()
                .filter(symptom -> symptom.toLowerCase().contains(lowerPartial))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean validateIdentificationResult(IdentificationResult result) {
        if (result == null) {
            return false;
        }
        
        // 验证必要字段
        if (result.getId() == null || result.getUserId() == null || 
            result.getPestId() == null || result.getIdentificationTime() == null) {
            return false;
        }
        
        // 验证置信度范围
        if (result.getConfidence() < 0.0 || result.getConfidence() > 1.0) {
            return false;
        }
        
        // 验证病虫害是否存在
        return pestRepository.existsById(result.getPestId());
    }
    
    @Override
    public String exportIdentificationHistory(String userId, String format) {
        List<IdentificationResult> history = getIdentificationHistory(userId);
        
        if ("json".equalsIgnoreCase(format)) {
            // 简化的JSON导出
            return history.toString();
        } else if ("csv".equalsIgnoreCase(format)) {
            // 简化的CSV导出
            StringBuilder csv = new StringBuilder();
            csv.append("识别时间,病虫害名称,置信度,识别方法\n");
            
            for (IdentificationResult result : history) {
                Optional<Pest> pestOpt = pestRepository.findById(result.getPestId());
                String pestName = pestOpt.map(Pest::getName).orElse("未知");
                
                csv.append(result.getIdentificationTime()).append(",")
                   .append(pestName).append(",")
                   .append(result.getConfidence()).append(",")
                   .append(result.getIdentificationMethod()).append("\n");
            }
            
            return csv.toString();
        }
        
        throw new BusinessException("不支持的导出格式: " + format);
    }
    
    @Override
    public List<Map<String, Object>> getPopularIdentifiedPests(int limit) {
        Map<String, Long> pestCounts = identificationResults.values().stream()
                .collect(Collectors.groupingBy(
                    IdentificationResult::getPestId,
                    Collectors.counting()
                ));
        
        return pestCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    Optional<Pest> pestOpt = pestRepository.findById(entry.getKey());
                    item.put("pestId", entry.getKey());
                    item.put("pestName", pestOpt.map(Pest::getName).orElse("未知"));
                    item.put("identificationCount", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> getIdentificationTrends(String period) {
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();
        
        DateTimeFormatter formatter;
        if ("daily".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if ("monthly".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy");
        }
        
        Map<String, Long> timeCounts = identificationResults.values().stream()
                .collect(Collectors.groupingBy(
                    result -> result.getIdentificationTime().format(formatter),
                    Collectors.counting()
                ));
        
        List<Map<String, Object>> trendData = timeCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("period", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        
        trends.put("identificationTrend", trendData);
        return trends;
    } 
   
    // ========== 私有辅助方法 ==========
    
    /**
     * 模拟图片识别算法
     */
    private IdentificationResult simulateImageIdentification(MultipartFile imageFile, String userId) {
        // 基于文件名或时间戳生成模拟识别结果
        String fileName = imageFile.getOriginalFilename();
        List<Pest> allPests = pestRepository.findAll();
        
        if (allPests.isEmpty()) {
            throw new BusinessException("系统中没有病虫害数据");
        }
        
        // 基于文件名哈希选择病虫害
        int index = Math.abs((fileName != null ? fileName.hashCode() : 0) + 
                            (int) System.currentTimeMillis()) % allPests.size();
        Pest selectedPest = allPests.get(index);
        
        // 生成置信度（0.6-0.95之间）
        double confidence = 0.6 + random.nextDouble() * 0.35;
        
        IdentificationResult result = createIdentificationResult(selectedPest, confidence, userId);
        result.setIdentificationMethod("图片识别");
        result.setImagePath(saveImageFile(imageFile));
        
        return result;
    }
    
    /**
     * 计算症状匹配分数
     */
    private double calculateSymptomMatchScore(List<String> inputSymptoms, Pest pest) {
        if (pest.getSymptoms() == null || pest.getSymptoms().isEmpty()) {
            return 0.0;
        }
        
        int matchCount = 0;
        for (String inputSymptom : inputSymptoms) {
            for (String pestSymptom : pest.getSymptoms()) {
                if (pestSymptom.contains(inputSymptom) || inputSymptom.contains(pestSymptom)) {
                    matchCount++;
                    break;
                }
            }
        }
        
        return (double) matchCount / inputSymptoms.size();
    }
    
    /**
     * 计算寄主植物匹配分数
     */
    private double calculateHostPlantMatchScore(String hostPlant, Pest pest) {
        if (pest.getHostPlants() == null || pest.getHostPlants().isEmpty()) {
            return 0.5; // 默认分数
        }
        
        for (String pestHost : pest.getHostPlants()) {
            if (pestHost.contains(hostPlant) || hostPlant.contains(pestHost)) {
                return 1.0;
            }
        }
        
        return 0.0;
    }
    
    /**
     * 计算季节匹配分数
     */
    private double calculateSeasonMatchScore(String season, Pest pest) {
        // 简化的季节匹配逻辑
        if (pest.getOccurrencePattern() != null && 
            pest.getOccurrencePattern().contains(season)) {
            return 1.0;
        }
        return 0.5; // 默认分数
    }
    
    /**
     * 计算地区匹配分数
     */
    private double calculateRegionMatchScore(String region, Pest pest) {
        if (pest.getDistributionArea() != null && 
            pest.getDistributionArea().contains(region)) {
            return 1.0;
        }
        return 0.5; // 默认分数
    }
    
    /**
     * 创建识别结果对象
     */
    private IdentificationResult createIdentificationResult(Pest pest, double confidence, String userId) {
        IdentificationResult result = new IdentificationResult();
        result.setId(dataStorage.generateId());
        result.setUserId(userId);
        result.setPestId(pest.getId());
        result.setConfidence(confidence);
        result.setIdentificationTime(LocalDateTime.now());
        result.setCreatedBy(userId);
        
        return result;
    }
    
    /**
     * 保存识别结果
     */
    private void saveIdentificationResult(IdentificationResult result, String userId) {
        identificationResults.put(result.getId(), result);
        userIdentificationHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(result.getId());
    }
    
    /**
     * 保存图片文件（模拟）
     */
    private String saveImageFile(MultipartFile imageFile) {
        // 模拟文件保存，返回文件路径
        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        return "/uploads/images/" + fileName;
    }
    
    /**
     * 判断是否为季节性常见病虫害
     */
    private boolean isCommonInSeason(Pest pest, String season) {
        // 简化的季节判断逻辑
        if (pest.getOccurrencePattern() == null) {
            return false;
        }
        
        String pattern = pest.getOccurrencePattern().toLowerCase();
        String lowerSeason = season.toLowerCase();
        
        return pattern.contains(lowerSeason) || 
               pattern.contains("全年") || 
               pattern.contains("常年");
    }
    
    /**
     * 判断识别结果是否相似
     */
    private boolean isSimilarResult(IdentificationResult target, IdentificationResult other) {
        // 相同病虫害
        if (target.getPestId().equals(other.getPestId())) {
            return true;
        }
        
        // 相同识别方法且置信度相近
        if (target.getIdentificationMethod().equals(other.getIdentificationMethod()) &&
            Math.abs(target.getConfidence() - other.getConfidence()) < 0.2) {
            return true;
        }
        
        // 症状相似
        if (target.getInputSymptoms() != null && other.getInputSymptoms() != null) {
            long commonSymptoms = target.getInputSymptoms().stream()
                    .filter(other.getInputSymptoms()::contains)
                    .count();
            return commonSymptoms >= 2;
        }
        
        return false;
    }
}