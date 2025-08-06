package com.forestpest.data.storage;

import com.forestpest.entity.Pesticide;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 药剂数据存储
 */
@Component
public class PesticideStorage {
    
    private final Map<String, Pesticide> pesticides = new ConcurrentHashMap<>();
    private final Map<String, List<String>> categoryIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> safetyLevelIndex = new ConcurrentHashMap<>();
    
    public void save(Pesticide pesticide) {
        pesticides.put(pesticide.getId(), pesticide);
        
        // 更新分类索引
        if (pesticide.getCategory() != null) {
            categoryIndex.computeIfAbsent(pesticide.getCategory(), k -> new ArrayList<>()).add(pesticide.getId());
        }
        
        // 更新安全等级索引
        safetyLevelIndex.computeIfAbsent(pesticide.getSafetyLevel(), k -> new ArrayList<>()).add(pesticide.getId());
    }
    
    public Optional<Pesticide> findById(String id) {
        return Optional.ofNullable(pesticides.get(id));
    }
    
    public List<Pesticide> findAll() {
        return new ArrayList<>(pesticides.values());
    }
    
    public List<Pesticide> findByCategory(String category) {
        List<String> ids = categoryIndex.get(category);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(pesticides::get)
                .filter(pesticide -> pesticide != null)
                .collect(Collectors.toList());
    }
    
    public List<Pesticide> findBySafetyLevel(String safetyLevel) {
        List<String> ids = safetyLevelIndex.get(safetyLevel);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(pesticides::get)
                .filter(pesticide -> pesticide != null)
                .collect(Collectors.toList());
    }
    
    public List<Pesticide> findByNameContaining(String name) {
        return pesticides.values().stream()
                .filter(pesticide -> pesticide.getName().contains(name))
                .collect(Collectors.toList());
    }
    
    public List<Pesticide> findLowStock() {
        return pesticides.values().stream()
                .filter(pesticide -> pesticide.getMinStockLevel() != null && 
                                   pesticide.getStockQuantity() <= pesticide.getMinStockLevel())
                .collect(Collectors.toList());
    }
    
    public List<Pesticide> findExpiringSoon(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        return pesticides.values().stream()
                .filter(pesticide -> pesticide.getExpiryDate().isBefore(cutoffDate))
                .collect(Collectors.toList());
    }
    
    public List<Pesticide> findByStatus(String status) {
        return pesticides.values().stream()
                .filter(pesticide -> status.equals(pesticide.getStatus()))
                .collect(Collectors.toList());
    }
    
    public void deleteById(String id) {
        Pesticide pesticide = pesticides.remove(id);
        if (pesticide != null) {
            // 清理索引
            if (pesticide.getCategory() != null) {
                List<String> categoryIds = categoryIndex.get(pesticide.getCategory());
                if (categoryIds != null) {
                    categoryIds.remove(id);
                }
            }
            
            List<String> safetyIds = safetyLevelIndex.get(pesticide.getSafetyLevel());
            if (safetyIds != null) {
                safetyIds.remove(id);
            }
        }
    }
    
    public void clear() {
        pesticides.clear();
        categoryIndex.clear();
        safetyLevelIndex.clear();
    }
    
    public int count() {
        return pesticides.size();
    }
}