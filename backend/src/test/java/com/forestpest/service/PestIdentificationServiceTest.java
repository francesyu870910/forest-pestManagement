package com.forestpest.service;

import com.forestpest.entity.IdentificationResult;
import com.forestpest.entity.Pest;
import com.forestpest.exception.BusinessException;
import com.forestpest.repository.PestRepository;
import com.forestpest.service.impl.PestIdentificationServiceImpl;
import com.forestpest.data.storage.DataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 病虫害识别服务测试类
 */
@ExtendWith(MockitoExtension.class)
class PestIdentificationServiceTest {
    
    @Mock
    private PestRepository pestRepository;
    
    @Mock
    private DataStorage dataStorage;
    
    @InjectMocks
    private PestIdentificationServiceImpl pestIdentificationService;
    
    private Pest testPest;
    private MultipartFile testImageFile;
    private String testUserId;
    
    @BeforeEach
    void setUp() {
        testUserId = "test-user-123";
        
        // 创建测试病虫害数据
        testPest = new Pest();
        testPest.setId("pest-001");
        testPest.setName("松毛虫");
        testPest.setScientificName("Dendrolimus punctatus");
        testPest.setType("害虫");
        testPest.setSymptoms(Arrays.asList("叶片被啃食", "有虫粪", "叶片枯萎"));
        testPest.setHostPlants(Arrays.asList("松树", "云杉"));
        testPest.setOccurrencePattern("春季、夏季");
        testPest.setDistributionArea("华北、东北");
        
        // 创建测试图片文件
        testImageFile = new MockMultipartFile(
            "test-image", 
            "test.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );
        
        // Mock DataStorage
        when(dataStorage.generateId()).thenReturn("result-" + System.currentTimeMillis());
    }
    
    @Test
    void testIdentifyByImage_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        
        // Act
        IdentificationResult result = pestIdentificationService.identifyByImage(testImageFile, testUserId);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(testUserId, result.getUserId());
        assertEquals("图片识别", result.getIdentificationMethod());
        assertTrue(result.getConfidence() >= 0.6 && result.getConfidence() <= 0.95);
        assertNotNull(result.getIdentificationTime());
        assertNotNull(result.getImagePath());
    }
    
    @Test
    void testIdentifyByImage_NullFile() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.identifyByImage(null, testUserId));
        assertEquals("图片文件不能为空", exception.getMessage());
    }
    
    @Test
    void testIdentifyByImage_EmptyUserId() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.identifyByImage(testImageFile, ""));
        assertEquals("用户ID不能为空", exception.getMessage());
    }
    
    @Test
    void testIdentifyByImage_InvalidFileType() {
        // Arrange
        MultipartFile invalidFile = new MockMultipartFile(
            "test-file", 
            "test.txt", 
            "text/plain", 
            "test content".getBytes()
        );
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.identifyByImage(invalidFile, testUserId));
        assertEquals("文件格式不正确，请上传图片文件", exception.getMessage());
    }
    
    @Test
    void testIdentifyByImage_FileTooLarge() {
        // Arrange
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MultipartFile largeFile = new MockMultipartFile(
            "large-image", 
            "large.jpg", 
            "image/jpeg", 
            largeContent
        );
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.identifyByImage(largeFile, testUserId));
        assertEquals("图片文件大小不能超过10MB", exception.getMessage());
    }
    
    @Test
    void testIdentifyBySymptoms_Success() {
        // Arrange
        List<String> symptoms = Arrays.asList("叶片被啃食", "有虫粪");
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        
        // Act
        List<IdentificationResult> results = pestIdentificationService.identifyBySymptoms(symptoms, testUserId);
        
        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        IdentificationResult result = results.get(0);
        assertEquals(testUserId, result.getUserId());
        assertEquals("症状匹配", result.getIdentificationMethod());
        assertEquals(symptoms, result.getInputSymptoms());
        assertTrue(result.getConfidence() > 0.3);
    }
    
    @Test
    void testIdentifyBySymptoms_EmptySymptoms() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.identifyBySymptoms(new ArrayList<>(), testUserId));
        assertEquals("症状描述不能为空", exception.getMessage());
    }
    
    @Test
    void testIdentifyBySymptoms_NullSymptoms() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.identifyBySymptoms(null, testUserId));
        assertEquals("症状描述不能为空", exception.getMessage());
    }
    
    @Test
    void testIdentifyByMultipleConditions_Success() {
        // Arrange
        List<String> symptoms = Arrays.asList("叶片被啃食");
        String hostPlant = "松树";
        String season = "春季";
        String region = "华北";
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        
        // Act
        List<IdentificationResult> results = pestIdentificationService.identifyByMultipleConditions(
            symptoms, hostPlant, season, region, testUserId);
        
        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        IdentificationResult result = results.get(0);
        assertEquals(testUserId, result.getUserId());
        assertEquals("综合条件匹配", result.getIdentificationMethod());
        assertEquals(symptoms, result.getInputSymptoms());
        assertEquals(hostPlant, result.getHostPlant());
        assertEquals(season, result.getSeason());
        assertEquals(region, result.getRegion());
    }
    
    @Test
    void testGetIdentificationHistory_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        
        // 先创建一些识别记录
        pestIdentificationService.identifyByImage(testImageFile, testUserId);
        pestIdentificationService.identifyBySymptoms(Arrays.asList("叶片被啃食"), testUserId);
        
        // Act
        List<IdentificationResult> history = pestIdentificationService.getIdentificationHistory(testUserId);
        
        // Assert
        assertNotNull(history);
        assertEquals(2, history.size());
        // 验证按时间倒序排列
        assertTrue(history.get(0).getIdentificationTime().isAfter(history.get(1).getIdentificationTime()) ||
                  history.get(0).getIdentificationTime().equals(history.get(1).getIdentificationTime()));
    }
    
    @Test
    void testGetIdentificationHistory_WithPagination() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        
        // 创建多个识别记录
        for (int i = 0; i < 5; i++) {
            pestIdentificationService.identifyByImage(testImageFile, testUserId);
        }
        
        // Act
        List<IdentificationResult> page1 = pestIdentificationService.getIdentificationHistory(testUserId, 0, 2);
        List<IdentificationResult> page2 = pestIdentificationService.getIdentificationHistory(testUserId, 1, 2);
        
        // Assert
        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
        assertNotEquals(page1.get(0).getId(), page2.get(0).getId());
    }
    
    @Test
    void testDeleteIdentificationResult_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        IdentificationResult result = pestIdentificationService.identifyByImage(testImageFile, testUserId);
        
        // Act
        boolean deleted = pestIdentificationService.deleteIdentificationResult(result.getId(), testUserId);
        
        // Assert
        assertTrue(deleted);
        assertNull(pestIdentificationService.getIdentificationResult(result.getId()));
    }
    
    @Test
    void testDeleteIdentificationResult_WrongUser() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        IdentificationResult result = pestIdentificationService.identifyByImage(testImageFile, testUserId);
        
        // Act
        boolean deleted = pestIdentificationService.deleteIdentificationResult(result.getId(), "wrong-user");
        
        // Assert
        assertFalse(deleted);
        assertNotNull(pestIdentificationService.getIdentificationResult(result.getId()));
    }
    
    @Test
    void testGetIdentificationStatistics_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        
        // 创建不同类型的识别记录
        pestIdentificationService.identifyByImage(testImageFile, testUserId);
        pestIdentificationService.identifyBySymptoms(Arrays.asList("叶片被啃食"), testUserId);
        
        // Act
        Map<String, Object> statistics = pestIdentificationService.getIdentificationStatistics(testUserId);
        
        // Assert
        assertNotNull(statistics);
        assertEquals(2, statistics.get("totalIdentifications"));
        assertNotNull(statistics.get("byMethod"));
        assertNotNull(statistics.get("byMonth"));
        assertNotNull(statistics.get("averageConfidence"));
    }
    
    @Test
    void testUpdateUserFeedback_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        IdentificationResult result = pestIdentificationService.identifyByImage(testImageFile, testUserId);
        
        // Act
        IdentificationResult updatedResult = pestIdentificationService.updateUserFeedback(
            result.getId(), testUserId, true, "识别正确");
        
        // Assert
        assertNotNull(updatedResult);
        assertEquals("识别正确", updatedResult.getUserFeedback());
        assertTrue(updatedResult.isFeedbackCorrect());
        assertNotNull(updatedResult.getFeedbackTime());
    }
    
    @Test
    void testUpdateUserFeedback_WrongUser() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        IdentificationResult result = pestIdentificationService.identifyByImage(testImageFile, testUserId);
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.updateUserFeedback(result.getId(), "wrong-user", true, "test"));
        assertEquals("识别结果不存在或无权限", exception.getMessage());
    }
    
    @Test
    void testGetAccuracyStatistics_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        IdentificationResult result1 = pestIdentificationService.identifyByImage(testImageFile, testUserId);
        IdentificationResult result2 = pestIdentificationService.identifyBySymptoms(Arrays.asList("叶片被啃食"), testUserId);
        
        // 添加用户反馈
        pestIdentificationService.updateUserFeedback(result1.getId(), testUserId, true, "正确");
        pestIdentificationService.updateUserFeedback(result2.getId(), testUserId, false, "错误");
        
        // Act
        Map<String, Double> accuracy = pestIdentificationService.getAccuracyStatistics();
        
        // Assert
        assertNotNull(accuracy);
        assertEquals(0.5, accuracy.get("overall"), 0.01);
        assertTrue(accuracy.containsKey("图片识别"));
        assertTrue(accuracy.containsKey("症状匹配"));
    }
    
    @Test
    void testGetPossiblePestsByHostPlant_Success() {
        // Arrange
        when(pestRepository.findByHostPlant("松树")).thenReturn(Arrays.asList(testPest));
        
        // Act
        List<Pest> pests = pestIdentificationService.getPossiblePestsByHostPlant("松树");
        
        // Assert
        assertNotNull(pests);
        assertEquals(1, pests.size());
        assertEquals(testPest.getId(), pests.get(0).getId());
    }
    
    @Test
    void testGetCommonPestsBySeason_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        
        // Act
        List<Pest> pests = pestIdentificationService.getCommonPestsBySeason("春季");
        
        // Assert
        assertNotNull(pests);
        assertEquals(1, pests.size());
        assertEquals(testPest.getId(), pests.get(0).getId());
    }
    
    @Test
    void testGetCommonPestsByRegion_Success() {
        // Arrange
        when(pestRepository.findByDistributionAreaContaining("华北")).thenReturn(Arrays.asList(testPest));
        
        // Act
        List<Pest> pests = pestIdentificationService.getCommonPestsByRegion("华北");
        
        // Assert
        assertNotNull(pests);
        assertEquals(1, pests.size());
        assertEquals(testPest.getId(), pests.get(0).getId());
    }
    
    @Test
    void testBatchIdentifyByImages_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        List<MultipartFile> imageFiles = Arrays.asList(testImageFile, testImageFile);
        
        // Act
        List<IdentificationResult> results = pestIdentificationService.batchIdentifyByImages(imageFiles, testUserId);
        
        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        for (IdentificationResult result : results) {
            assertEquals(testUserId, result.getUserId());
            assertEquals("图片识别", result.getIdentificationMethod());
        }
    }
    
    @Test
    void testBatchIdentifyByImages_TooManyFiles() {
        // Arrange
        List<MultipartFile> tooManyFiles = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            tooManyFiles.add(testImageFile);
        }
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.batchIdentifyByImages(tooManyFiles, testUserId));
        assertEquals("批量识别最多支持10张图片", exception.getMessage());
    }
    
    @Test
    void testGetIdentificationSuggestions_Success() {
        // Act
        List<String> suggestions = pestIdentificationService.getIdentificationSuggestions("叶片");
        
        // Assert
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("叶片")));
    }
    
    @Test
    void testValidateIdentificationResult_Success() {
        // Arrange
        when(pestRepository.existsById(testPest.getId())).thenReturn(true);
        
        IdentificationResult result = new IdentificationResult();
        result.setId("test-id");
        result.setUserId(testUserId);
        result.setPestId(testPest.getId());
        result.setIdentificationTime(LocalDateTime.now());
        result.setConfidence(0.8);
        
        // Act
        boolean isValid = pestIdentificationService.validateIdentificationResult(result);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void testValidateIdentificationResult_InvalidConfidence() {
        // Arrange
        IdentificationResult result = new IdentificationResult();
        result.setId("test-id");
        result.setUserId(testUserId);
        result.setPestId(testPest.getId());
        result.setIdentificationTime(LocalDateTime.now());
        result.setConfidence(1.5); // 无效的置信度
        
        // Act
        boolean isValid = pestIdentificationService.validateIdentificationResult(result);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testExportIdentificationHistory_JSON() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        pestIdentificationService.identifyByImage(testImageFile, testUserId);
        
        // Act
        String exportData = pestIdentificationService.exportIdentificationHistory(testUserId, "json");
        
        // Assert
        assertNotNull(exportData);
        assertFalse(exportData.isEmpty());
    }
    
    @Test
    void testExportIdentificationHistory_CSV() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        when(pestRepository.findById(anyString())).thenReturn(Optional.of(testPest));
        pestIdentificationService.identifyByImage(testImageFile, testUserId);
        
        // Act
        String exportData = pestIdentificationService.exportIdentificationHistory(testUserId, "csv");
        
        // Assert
        assertNotNull(exportData);
        assertTrue(exportData.contains("识别时间,病虫害名称,置信度,识别方法"));
    }
    
    @Test
    void testExportIdentificationHistory_UnsupportedFormat() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pestIdentificationService.exportIdentificationHistory(testUserId, "xml"));
        assertEquals("不支持的导出格式: xml", exception.getMessage());
    }
    
    @Test
    void testGetPopularIdentifiedPests_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        when(pestRepository.findById(testPest.getId())).thenReturn(Optional.of(testPest));
        
        // 创建多个识别记录
        pestIdentificationService.identifyByImage(testImageFile, testUserId);
        pestIdentificationService.identifyByImage(testImageFile, "user2");
        
        // Act
        List<Map<String, Object>> popular = pestIdentificationService.getPopularIdentifiedPests(5);
        
        // Assert
        assertNotNull(popular);
        assertFalse(popular.isEmpty());
        Map<String, Object> firstItem = popular.get(0);
        assertEquals(testPest.getId(), firstItem.get("pestId"));
        assertEquals(testPest.getName(), firstItem.get("pestName"));
        assertEquals(2L, firstItem.get("identificationCount"));
    }
    
    @Test
    void testGetIdentificationTrends_Success() {
        // Arrange
        when(pestRepository.findAll()).thenReturn(Arrays.asList(testPest));
        pestIdentificationService.identifyByImage(testImageFile, testUserId);
        
        // Act
        Map<String, List<Map<String, Object>>> trends = pestIdentificationService.getIdentificationTrends("monthly");
        
        // Assert
        assertNotNull(trends);
        assertTrue(trends.containsKey("identificationTrend"));
        List<Map<String, Object>> trendData = trends.get("identificationTrend");
        assertNotNull(trendData);
        assertFalse(trendData.isEmpty());
    }
}