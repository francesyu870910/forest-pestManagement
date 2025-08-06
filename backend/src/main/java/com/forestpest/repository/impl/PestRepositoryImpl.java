package com.forestpest.repository.impl;

import com.forestpest.entity.Pest;
import com.forestpest.repository.PestRepository;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 病虫害数据访问实现类
 */
@Repository
public class PestRepositoryImpl implements PestRepository {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Override
    public Pest save(Pest pest) {
        if (pest.getId() == null) {
            pest.setId(dataStorage.generateId());
        }
        pest.setUpdatedTime(LocalDateTime.now());
        dataStorage.getPestStorage().save(pest);
        return pest;
    }
    
    @Override
    public Optional<Pest> findById(String id) {
        return dataStorage.getPestStorage().findById(id);
    }
    
    @Override
    public List<Pest> findAll() {
        return dataStorage.getPestStorage().findAll();
    }
    
    @Override
    public void deleteById(String id) {
        dataStorage.getPestStorage().deleteById(id);
    }
    
    @Override
    public void delete(Pest pest) {
        if (pest != null && pest.getId() != null) {
            deleteById(pest.getId());
        }
    }
    
    @Override
    public boolean existsById(String id) {
        return dataStorage.getPestStorage().findById(id).isPresent();
    }
    
    @Override
    public long count() {
        return dataStorage.getPestStorage().count();
    }
    
    @Override
    public List<Pest> saveAll(Iterable<Pest> pests) {
        List<Pest> savedPests = new java.util.ArrayList<>();
        for (Pest pest : pests) {
            savedPests.add(save(pest));
        }
        return savedPests;
    }
    
    @Override
    public void deleteAll(Iterable<Pest> pests) {
        for (Pest pest : pests) {
            delete(pest);
        }
    }
    
    @Override
    public void deleteAll() {
        dataStorage.getPestStorage().clear();
    }
    
    @Override
    public List<Pest> findByNameContaining(String name) {
        return dataStorage.getPestStorage().findByNameContaining(name);
    }
    
    @Override
    public List<Pest> findByCategory(String category) {
        return dataStorage.getPestStorage().findByCategory(category);
    }
    
    @Override
    public List<Pest> findByRiskLevel(String riskLevel) {
        return dataStorage.getPestStorage().findByRiskLevel(riskLevel);
    }
    
    @Override
    public List<Pest> findByScientificNameContaining(String scientificName) {
        return findAll().stream()
                .filter(pest -> pest.getScientificName() != null && 
                               pest.getScientificName().toLowerCase().contains(scientificName.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pest> findBySymptom(String symptom) {
        return dataStorage.getPestStorage().findBySymptom(symptom);
    }
    
    @Override
    public List<Pest> findByHostPlant(String hostPlant) {
        return findAll().stream()
                .filter(pest -> pest.getHostPlants() != null && 
                               pest.getHostPlants().contains(hostPlant))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pest> findByDistributionAreaContaining(String distributionArea) {
        return findAll().stream()
                .filter(pest -> pest.getDistributionArea() != null && 
                               pest.getDistributionArea().contains(distributionArea))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllCategories() {
        return findAll().stream()
                .map(Pest::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllRiskLevels() {
        return findAll().stream()
                .map(Pest::getRiskLevel)
                .filter(level -> level != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pest> findByCategoryAndRiskLevel(String category, String riskLevel) {
        return findAll().stream()
                .filter(pest -> category.equals(pest.getCategory()) && 
                               riskLevel.equals(pest.getRiskLevel()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pest> findHighRiskPests() {
        return findAll().stream()
                .filter(pest -> "高风险".equals(pest.getRiskLevel()) || 
                               "极高风险".equals(pest.getRiskLevel()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pest> searchByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return findAll().stream()
                .filter(pest -> 
                    (pest.getName() != null && pest.getName().toLowerCase().contains(lowerKeyword)) ||
                    (pest.getScientificName() != null && pest.getScientificName().toLowerCase().contains(lowerKeyword)) ||
                    (pest.getDescription() != null && pest.getDescription().toLowerCase().contains(lowerKeyword)) ||
                    (pest.getCategory() != null && pest.getCategory().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
}