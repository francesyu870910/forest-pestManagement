package com.forestpest.repository;

import com.forestpest.entity.Pest;

import java.util.List;

/**
 * 病虫害数据访问接口
 */
public interface PestRepository extends BaseRepository<Pest, String> {
    
    /**
     * 根据名称查找病虫害
     */
    List<Pest> findByNameContaining(String name);
    
    /**
     * 根据类别查找病虫害
     */
    List<Pest> findByCategory(String category);
    
    /**
     * 根据风险等级查找病虫害
     */
    List<Pest> findByRiskLevel(String riskLevel);
    
    /**
     * 根据学名查找病虫害
     */
    List<Pest> findByScientificNameContaining(String scientificName);
    
    /**
     * 根据症状查找病虫害
     */
    List<Pest> findBySymptom(String symptom);
    
    /**
     * 根据寄主植物查找病虫害
     */
    List<Pest> findByHostPlant(String hostPlant);
    
    /**
     * 根据分布区域查找病虫害
     */
    List<Pest> findByDistributionAreaContaining(String distributionArea);
    
    /**
     * 获取所有类别
     */
    List<String> findAllCategories();
    
    /**
     * 获取所有风险等级
     */
    List<String> findAllRiskLevels();
    
    /**
     * 根据多个条件查询
     */
    List<Pest> findByCategoryAndRiskLevel(String category, String riskLevel);
    
    /**
     * 获取高风险病虫害
     */
    List<Pest> findHighRiskPests();
    
    /**
     * 根据关键词搜索病虫害
     */
    List<Pest> searchByKeyword(String keyword);
}