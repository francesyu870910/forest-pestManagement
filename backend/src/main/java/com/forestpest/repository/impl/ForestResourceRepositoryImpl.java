package com.forestpest.repository.impl;

import com.forestpest.entity.ForestResource;
import com.forestpest.repository.ForestResourceRepository;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 森林资源数据访问实现类
 */
@Repository
public class ForestResourceRepositoryImpl implements ForestResourceRepository {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Override
    public ForestResource save(ForestResource resource) {
        if (resource.getId() == null) {
            resource.setId(dataStorage.generateId());
        }
        resource.setUpdatedTime(LocalDateTime.now());
        dataStorage.getForestResourceStorage().save(resource);
        return resource;
    }
    
    @Override
    public Optional<ForestResource> findById(String id) {
        return dataStorage.getForestResourceStorage().findById(id);
    }
    
    @Override
    public List<ForestResource> findAll() {
        return dataStorage.getForestResourceStorage().findAll();
    }
    
    @Override
    public void deleteById(String id) {
        dataStorage.getForestResourceStorage().deleteById(id);
    }
    
    @Override
    public void delete(ForestResource resource) {
        if (resource != null && resource.getId() != null) {
            deleteById(resource.getId());
        }
    }
    
    @Override
    public boolean existsById(String id) {
        return dataStorage.getForestResourceStorage().findById(id).isPresent();
    }
    
    @Override
    public long count() {
        return dataStorage.getForestResourceStorage().count();
    }
    
    @Override
    public List<ForestResource> saveAll(Iterable<ForestResource> resources) {
        List<ForestResource> savedResources = new java.util.ArrayList<>();
        for (ForestResource resource : resources) {
            savedResources.add(save(resource));
        }
        return savedResources;
    }
    
    @Override
    public void deleteAll(Iterable<ForestResource> resources) {
        for (ForestResource resource : resources) {
            delete(resource);
        }
    }
    
    @Override
    public void deleteAll() {
        dataStorage.getForestResourceStorage().clear();
    }
    
    @Override
    public List<ForestResource> findByAreaType(String areaType) {
        return dataStorage.getForestResourceStorage().findByAreaType(areaType);
    }
    
    @Override
    public List<ForestResource> findByParentAreaId(String parentAreaId) {
        return dataStorage.getForestResourceStorage().findByParentAreaId(parentAreaId);
    }
    
    @Override
    public List<ForestResource> findByHealthStatus(String healthStatus) {
        return dataStorage.getForestResourceStorage().findByHealthStatus(healthStatus);
    }
    
    @Override
    public List<ForestResource> findByManagementLevel(String managementLevel) {
        return findAll().stream()
                .filter(resource -> managementLevel.equals(resource.getManagementLevel()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByAreaNameContaining(String areaName) {
        return dataStorage.getForestResourceStorage().findByAreaNameContaining(areaName);
    }
    
    @Override
    public List<ForestResource> findByAreaCode(String areaCode) {
        return findAll().stream()
                .filter(resource -> areaCode.equals(resource.getAreaCode()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByDominantSpecies(String dominantSpecies) {
        return dataStorage.getForestResourceStorage().findByDominantSpecies(dominantSpecies);
    }
    
    @Override
    public List<ForestResource> findByTreeSpecies(String treeSpecies) {
        return findAll().stream()
                .filter(resource -> resource.getTreeSpecies() != null &&
                                  resource.getTreeSpecies().contains(treeSpecies))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByForestAgeBetween(Integer minAge, Integer maxAge) {
        return findAll().stream()
                .filter(resource -> resource.getForestAge() != null &&
                                  resource.getForestAge() >= minAge &&
                                  resource.getForestAge() <= maxAge)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByAreaBetween(Double minArea, Double maxArea) {
        return findAll().stream()
                .filter(resource -> resource.getArea() != null &&
                                  resource.getArea() >= minArea &&
                                  resource.getArea() <= maxArea)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findRootAreas() {
        return dataStorage.getForestResourceStorage().findRootAreas();
    }
    
    @Override
    public List<ForestResource> findLeafAreas() {
        List<ForestResource> allResources = findAll();
        List<String> parentIds = allResources.stream()
                .map(ForestResource::getParentAreaId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        return allResources.stream()
                .filter(resource -> !parentIds.contains(resource.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByTopography(String topography) {
        return findAll().stream()
                .filter(resource -> topography.equals(resource.getTopography()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findBySoilType(String soilType) {
        return findAll().stream()
                .filter(resource -> soilType.equals(resource.getSoilType()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByClimate(String climate) {
        return findAll().stream()
                .filter(resource -> climate.equals(resource.getClimate()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByAspect(String aspect) {
        return findAll().stream()
                .filter(resource -> aspect.equals(resource.getAspect()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByAccessibility(String accessibility) {
        return findAll().stream()
                .filter(resource -> accessibility.equals(resource.getAccessibility()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllAreaTypes() {
        return findAll().stream()
                .map(ForestResource::getAreaType)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllHealthStatuses() {
        return findAll().stream()
                .map(ForestResource::getHealthStatus)
                .filter(status -> status != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllManagementLevels() {
        return findAll().stream()
                .map(ForestResource::getManagementLevel)
                .filter(level -> level != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllDominantSpecies() {
        return findAll().stream()
                .map(ForestResource::getDominantSpecies)
                .filter(species -> species != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> searchByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return findAll().stream()
                .filter(resource ->
                    (resource.getAreaName() != null && resource.getAreaName().toLowerCase().contains(lowerKeyword)) ||
                    (resource.getAreaCode() != null && resource.getAreaCode().toLowerCase().contains(lowerKeyword)) ||
                    (resource.getDominantSpecies() != null && resource.getDominantSpecies().toLowerCase().contains(lowerKeyword)) ||
                    (resource.getNotes() != null && resource.getNotes().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findByAreaTypeAndHealthStatus(String areaType, String healthStatus) {
        return findAll().stream()
                .filter(resource -> areaType.equals(resource.getAreaType()) &&
                                  healthStatus.equals(resource.getHealthStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ForestResource> findResourcesNeedingAttention() {
        return findAll().stream()
                .filter(resource -> "较差".equals(resource.getHealthStatus()) ||
                                  "差".equals(resource.getHealthStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Long> countByAreaType() {
        return findAll().stream()
                .collect(Collectors.groupingBy(ForestResource::getAreaType, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countByHealthStatus() {
        return findAll().stream()
                .filter(resource -> resource.getHealthStatus() != null)
                .collect(Collectors.groupingBy(ForestResource::getHealthStatus, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countByManagementLevel() {
        return findAll().stream()
                .filter(resource -> resource.getManagementLevel() != null)
                .collect(Collectors.groupingBy(ForestResource::getManagementLevel, Collectors.counting()));
    }
    
    @Override
    public Double calculateTotalArea() {
        return findAll().stream()
                .filter(resource -> resource.getArea() != null)
                .mapToDouble(ForestResource::getArea)
                .sum();
    }
    
    @Override
    public Map<String, Double> calculateAreaByType() {
        return findAll().stream()
                .filter(resource -> resource.getArea() != null)
                .collect(Collectors.groupingBy(
                    ForestResource::getAreaType,
                    Collectors.summingDouble(ForestResource::getArea)
                ));
    }
}