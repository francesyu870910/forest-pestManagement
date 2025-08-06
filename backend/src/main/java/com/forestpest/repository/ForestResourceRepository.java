package com.forestpest.repository;

import com.forestpest.entity.ForestResource;

import java.util.List;

/**
 * 森林资源数据访问接口
 */
public interface ForestResourceRepository extends BaseRepository<ForestResource, String> {
    
    /**
     * 根据区域类型查找森林资源
     */
    List<ForestResource> findByAreaType(String areaType);
    
    /**
     * 根据父区域ID查找子区域
     */
    List<ForestResource> findByParentAreaId(String parentAreaId);
    
    /**
     * 根据健康状况查找森林资源
     */
    List<ForestResource> findByHealthStatus(String healthStatus);
    
    /**
     * 根据管理水平查找森林资源
     */
    List<ForestResource> findByManagementLevel(String managementLevel);
    
    /**
     * 根据区域名称模糊查询
     */
    List<ForestResource> findByAreaNameContaining(String areaName);
    
    /**
     * 根据区域代码查找森林资源
     */
    List<ForestResource> findByAreaCode(String areaCode);
    
    /**
     * 根据主要树种查找森林资源
     */
    List<ForestResource> findByDominantSpecies(String dominantSpecies);
    
    /**
     * 根据树种查找森林资源
     */
    List<ForestResource> findByTreeSpecies(String treeSpecies);
    
    /**
     * 根据林龄范围查找森林资源
     */
    List<ForestResource> findByForestAgeBetween(Integer minAge, Integer maxAge);
    
    /**
     * 根据面积范围查找森林资源
     */
    List<ForestResource> findByAreaBetween(Double minArea, Double maxArea);
    
    /**
     * 获取根区域（顶级区域）
     */
    List<ForestResource> findRootAreas();
    
    /**
     * 获取叶子区域（没有子区域的区域）
     */
    List<ForestResource> findLeafAreas();
    
    /**
     * 根据地形查找森林资源
     */
    List<ForestResource> findByTopography(String topography);
    
    /**
     * 根据土壤类型查找森林资源
     */
    List<ForestResource> findBySoilType(String soilType);
    
    /**
     * 根据气候类型查找森林资源
     */
    List<ForestResource> findByClimate(String climate);
    
    /**
     * 根据坡向查找森林资源
     */
    List<ForestResource> findByAspect(String aspect);
    
    /**
     * 根据可达性查找森林资源
     */
    List<ForestResource> findByAccessibility(String accessibility);
    
    /**
     * 获取所有区域类型
     */
    List<String> findAllAreaTypes();
    
    /**
     * 获取所有健康状况
     */
    List<String> findAllHealthStatuses();
    
    /**
     * 获取所有管理水平
     */
    List<String> findAllManagementLevels();
    
    /**
     * 获取所有主要树种
     */
    List<String> findAllDominantSpecies();
    
    /**
     * 根据关键词搜索森林资源
     */
    List<ForestResource> searchByKeyword(String keyword);
    
    /**
     * 根据多个条件查询
     */
    List<ForestResource> findByAreaTypeAndHealthStatus(String areaType, String healthStatus);
    
    /**
     * 获取需要关注的森林资源（健康状况较差）
     */
    List<ForestResource> findResourcesNeedingAttention();
    
    /**
     * 统计各区域类型数量
     */
    java.util.Map<String, Long> countByAreaType();
    
    /**
     * 统计各健康状况数量
     */
    java.util.Map<String, Long> countByHealthStatus();
    
    /**
     * 统计各管理水平数量
     */
    java.util.Map<String, Long> countByManagementLevel();
    
    /**
     * 计算总面积
     */
    Double calculateTotalArea();
    
    /**
     * 根据区域类型计算面积
     */
    java.util.Map<String, Double> calculateAreaByType();
}