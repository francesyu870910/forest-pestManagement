package com.forestpest.data.storage;

import com.forestpest.entity.Pest;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 病虫害数据存储
 */
@Component
public class PestStorage {
    
    private final Map<String, Pest> pests = new ConcurrentHashMap<>();
    private final Map<String, List<String>> categoryIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> riskLevelIndex = new ConcurrentHashMap<>();
    
    public void save(Pest pest) {
        pests.put(pest.getId(), pest);
        
        // 更新分类索引
        categoryIndex.computeIfAbsent(pest.getCategory(), k -> new ArrayList<>()).add(pest.getId());
        
        // 更新风险等级索引
        if (pest.getRiskLevel() != null) {
            riskLevelIndex.computeIfAbsent(pest.getRiskLevel(), k -> new ArrayList<>()).add(pest.getId());
        }
    }
    
    public Optional<Pest> findById(String id) {
        return Optional.ofNullable(pests.get(id));
    }
    
    public List<Pest> findAll() {
        return new ArrayList<>(pests.values());
    }
    
    public List<Pest> findByCategory(String category) {
        List<String> ids = categoryIndex.get(category);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(pests::get)
                .filter(pest -> pest != null)
                .collect(Collectors.toList());
    }
    
    public List<Pest> findByRiskLevel(String riskLevel) {
        List<String> ids = riskLevelIndex.get(riskLevel);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(pests::get)
                .filter(pest -> pest != null)
                .collect(Collectors.toList());
    }
    
    public List<Pest> findByNameContaining(String name) {
        return pests.values().stream()
                .filter(pest -> pest.getName().contains(name))
                .collect(Collectors.toList());
    }
    
    public List<Pest> findBySymptom(String symptom) {
        return pests.values().stream()
                .filter(pest -> pest.getSymptoms() != null && 
                               pest.getSymptoms().stream().anyMatch(s -> s.contains(symptom)))
                .collect(Collectors.toList());
    }
    
    public void deleteById(String id) {
        Pest pest = pests.remove(id);
        if (pest != null) {
            // 清理索引
            List<String> categoryIds = categoryIndex.get(pest.getCategory());
            if (categoryIds != null) {
                categoryIds.remove(id);
            }
            
            if (pest.getRiskLevel() != null) {
                List<String> riskIds = riskLevelIndex.get(pest.getRiskLevel());
                if (riskIds != null) {
                    riskIds.remove(id);
                }
            }
        }
    }
    
    public void clear() {
        pests.clear();
        categoryIndex.clear();
        riskLevelIndex.clear();
    }
    
    public int count() {
        return pests.size();
    }
}