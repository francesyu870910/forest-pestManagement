package com.forestpest.data.storage;

import com.forestpest.entity.ForestResource;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 森林资源数据存储
 */
@Component
public class ForestResourceStorage {
    
    private final Map<String, ForestResource> forestResources = new ConcurrentHashMap<>();
    private final Map<String, List<String>> areaTypeIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> parentAreaIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> healthStatusIndex = new ConcurrentHashMap<>();
    
    public void save(ForestResource resource) {
        forestResources.put(resource.getId(), resource);
        
        // 更新区域类型索引
        areaTypeIndex.computeIfAbsent(resource.getAreaType(), k -> new ArrayList<>()).add(resource.getId());
        
        // 更新父区域索引
        if (resource.getParentAreaId() != null) {
            parentAreaIndex.computeIfAbsent(resource.getParentAreaId(), k -> new ArrayList<>()).add(resource.getId());
        }
        
        // 更新健康状况索引
        if (resource.getHealthStatus() != null) {
            healthStatusIndex.computeIfAbsent(resource.getHealthStatus(), k -> new ArrayList<>()).add(resource.getId());
        }
    }
    
    public Optional<ForestResource> findById(String id) {
        return Optional.ofNullable(forestResources.get(id));
    }
    
    public List<ForestResource> findAll() {
        return new ArrayList<>(forestResources.values());
    }
    
    public List<ForestResource> findByAreaType(String areaType) {
        List<String> ids = areaTypeIndex.get(areaType);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(forestResources::get)
                .filter(resource -> resource != null)
                .collect(Collectors.toList());
    }
    
    public List<ForestResource> findByParentAreaId(String parentAreaId) {
        List<String> ids = parentAreaIndex.get(parentAreaId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(forestResources::get)
                .filter(resource -> resource != null)
                .collect(Collectors.toList());
    }
    
    public List<ForestResource> findByHealthStatus(String healthStatus) {
        List<String> ids = healthStatusIndex.get(healthStatus);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(forestResources::get)
                .filter(resource -> resource != null)
                .collect(Collectors.toList());
    }
    
    public List<ForestResource> findByAreaNameContaining(String areaName) {
        return forestResources.values().stream()
                .filter(resource -> resource.getAreaName().contains(areaName))
                .collect(Collectors.toList());
    }
    
    public List<ForestResource> findByDominantSpecies(String dominantSpecies) {
        return forestResources.values().stream()
                .filter(resource -> dominantSpecies.equals(resource.getDominantSpecies()))
                .collect(Collectors.toList());
    }
    
    public List<ForestResource> findRootAreas() {
        return forestResources.values().stream()
                .filter(resource -> resource.getParentAreaId() == null)
                .collect(Collectors.toList());
    }
    
    public void deleteById(String id) {
        ForestResource resource = forestResources.remove(id);
        if (resource != null) {
            // 清理索引
            List<String> typeIds = areaTypeIndex.get(resource.getAreaType());
            if (typeIds != null) {
                typeIds.remove(id);
            }
            
            if (resource.getParentAreaId() != null) {
                List<String> parentIds = parentAreaIndex.get(resource.getParentAreaId());
                if (parentIds != null) {
                    parentIds.remove(id);
                }
            }
            
            if (resource.getHealthStatus() != null) {
                List<String> healthIds = healthStatusIndex.get(resource.getHealthStatus());
                if (healthIds != null) {
                    healthIds.remove(id);
                }
            }
        }
    }
    
    public void clear() {
        forestResources.clear();
        areaTypeIndex.clear();
        parentAreaIndex.clear();
        healthStatusIndex.clear();
    }
    
    public int count() {
        return forestResources.size();
    }
}