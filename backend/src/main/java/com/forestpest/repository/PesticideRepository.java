package com.forestpest.repository;

import com.forestpest.entity.Pesticide;

import java.time.LocalDate;
import java.util.List;

/**
 * 药剂数据访问接口
 */
public interface PesticideRepository extends BaseRepository<Pesticide, String> {
    
    /**
     * 根据名称模糊查询药剂
     */
    List<Pesticide> findByNameContaining(String name);
    
    /**
     * 根据类别查找药剂
     */
    List<Pesticide> findByCategory(String category);
    
    /**
     * 根据安全等级查找药剂
     */
    List<Pesticide> findBySafetyLevel(String safetyLevel);
    
    /**
     * 根据毒性等级查找药剂
     */
    List<Pesticide> findByToxicityLevel(String toxicityLevel);
    
    /**
     * 根据状态查找药剂
     */
    List<Pesticide> findByStatus(String status);
    
    /**
     * 根据供应商查找药剂
     */
    List<Pesticide> findBySupplier(String supplier);
    
    /**
     * 根据制造商查找药剂
     */
    List<Pesticide> findByManufacturer(String manufacturer);
    
    /**
     * 查找库存不足的药剂
     */
    List<Pesticide> findLowStockPesticides();
    
    /**
     * 查找即将过期的药剂
     */
    List<Pesticide> findExpiringSoon(int days);
    
    /**
     * 查找已过期的药剂
     */
    List<Pesticide> findExpiredPesticides();
    
    /**
     * 根据有效期范围查找药剂
     */
    List<Pesticide> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据库存数量范围查找药剂
     */
    List<Pesticide> findByStockQuantityBetween(Integer minStock, Integer maxStock);
    
    /**
     * 根据有效成分查找药剂
     */
    List<Pesticide> findByActiveIngredientContaining(String activeIngredient);
    
    /**
     * 获取所有类别
     */
    List<String> findAllCategories();
    
    /**
     * 获取所有安全等级
     */
    List<String> findAllSafetyLevels();
    
    /**
     * 获取所有毒性等级
     */
    List<String> findAllToxicityLevels();
    
    /**
     * 根据多个条件查询
     */
    List<Pesticide> findByCategoryAndSafetyLevel(String category, String safetyLevel);
    
    /**
     * 获取活跃状态的药剂
     */
    List<Pesticide> findActivePesticides();
    
    /**
     * 根据关键词搜索药剂
     */
    List<Pesticide> searchByKeyword(String keyword);
    
    /**
     * 统计各类别药剂数量
     */
    java.util.Map<String, Long> countByCategory();
    
    /**
     * 统计各安全等级药剂数量
     */
    java.util.Map<String, Long> countBySafetyLevel();
}