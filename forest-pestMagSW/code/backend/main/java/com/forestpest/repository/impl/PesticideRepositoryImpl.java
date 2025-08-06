package com.forestpest.repository.impl;

import com.forestpest.entity.Pesticide;
import com.forestpest.repository.PesticideRepository;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 药剂数据访问实现类
 */
@Repository
public class PesticideRepositoryImpl implements PesticideRepository {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Override
    public Pesticide save(Pesticide pesticide) {
        if (pesticide.getId() == null) {
            pesticide.setId(dataStorage.generateId());
        }
        pesticide.setUpdatedTime(LocalDateTime.now());
        dataStorage.getPesticideStorage().save(pesticide);
        return pesticide;
    }
    
    @Override
    public Optional<Pesticide> findById(String id) {
        return dataStorage.getPesticideStorage().findById(id);
    }
    
    @Override
    public List<Pesticide> findAll() {
        return dataStorage.getPesticideStorage().findAll();
    }
    
    @Override
    public void deleteById(String id) {
        dataStorage.getPesticideStorage().deleteById(id);
    }
    
    @Override
    public void delete(Pesticide pesticide) {
        if (pesticide != null && pesticide.getId() != null) {
            deleteById(pesticide.getId());
        }
    }
    
    @Override
    public boolean existsById(String id) {
        return dataStorage.getPesticideStorage().findById(id).isPresent();
    }
    
    @Override
    public long count() {
        return dataStorage.getPesticideStorage().count();
    }
    
    @Override
    public List<Pesticide> saveAll(Iterable<Pesticide> pesticides) {
        List<Pesticide> savedPesticides = new java.util.ArrayList<>();
        for (Pesticide pesticide : pesticides) {
            savedPesticides.add(save(pesticide));
        }
        return savedPesticides;
    }
    
    @Override
    public void deleteAll(Iterable<Pesticide> pesticides) {
        for (Pesticide pesticide : pesticides) {
            delete(pesticide);
        }
    }
    
    @Override
    public void deleteAll() {
        dataStorage.getPesticideStorage().clear();
    }
    
    @Override
    public List<Pesticide> findByNameContaining(String name) {
        return dataStorage.getPesticideStorage().findByNameContaining(name);
    }
    
    @Override
    public List<Pesticide> findByCategory(String category) {
        return dataStorage.getPesticideStorage().findByCategory(category);
    }
    
    @Override
    public List<Pesticide> findBySafetyLevel(String safetyLevel) {
        return dataStorage.getPesticideStorage().findBySafetyLevel(safetyLevel);
    }
    
    @Override
    public List<Pesticide> findByToxicityLevel(String toxicityLevel) {
        return findAll().stream()
                .filter(pesticide -> toxicityLevel.equals(pesticide.getToxicityLevel()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> findByStatus(String status) {
        return dataStorage.getPesticideStorage().findByStatus(status);
    }
    
    @Override
    public List<Pesticide> findBySupplier(String supplier) {
        return findAll().stream()
                .filter(pesticide -> supplier.equals(pesticide.getSupplier()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> findByManufacturer(String manufacturer) {
        return findAll().stream()
                .filter(pesticide -> manufacturer.equals(pesticide.getManufacturer()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> findLowStockPesticides() {
        return dataStorage.getPesticideStorage().findLowStock();
    }
    
    @Override
    public List<Pesticide> findExpiringSoon(int days) {
        return dataStorage.getPesticideStorage().findExpiringSoon(days);
    }
    
    @Override
    public List<Pesticide> findExpiredPesticides() {
        LocalDate today = LocalDate.now();
        return findAll().stream()
                .filter(pesticide -> pesticide.getExpiryDate().isBefore(today))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(pesticide -> !pesticide.getExpiryDate().isBefore(startDate) &&
                                   !pesticide.getExpiryDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> findByStockQuantityBetween(Integer minStock, Integer maxStock) {
        return findAll().stream()
                .filter(pesticide -> pesticide.getStockQuantity() >= minStock &&
                                   pesticide.getStockQuantity() <= maxStock)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> findByActiveIngredientContaining(String activeIngredient) {
        return findAll().stream()
                .filter(pesticide -> pesticide.getActiveIngredient() != null &&
                                   pesticide.getActiveIngredient().toLowerCase().contains(activeIngredient.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllCategories() {
        return findAll().stream()
                .map(Pesticide::getCategory)
                .filter(category -> category != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllSafetyLevels() {
        return findAll().stream()
                .map(Pesticide::getSafetyLevel)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllToxicityLevels() {
        return findAll().stream()
                .map(Pesticide::getToxicityLevel)
                .filter(level -> level != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> findByCategoryAndSafetyLevel(String category, String safetyLevel) {
        return findAll().stream()
                .filter(pesticide -> category.equals(pesticide.getCategory()) &&
                                   safetyLevel.equals(pesticide.getSafetyLevel()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pesticide> findActivePesticides() {
        return findByStatus("ACTIVE");
    }
    
    @Override
    public List<Pesticide> searchByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return findAll().stream()
                .filter(pesticide ->
                    (pesticide.getName() != null && pesticide.getName().toLowerCase().contains(lowerKeyword)) ||
                    (pesticide.getActiveIngredient() != null && pesticide.getActiveIngredient().toLowerCase().contains(lowerKeyword)) ||
                    (pesticide.getCategory() != null && pesticide.getCategory().toLowerCase().contains(lowerKeyword)) ||
                    (pesticide.getManufacturer() != null && pesticide.getManufacturer().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Long> countByCategory() {
        return findAll().stream()
                .filter(pesticide -> pesticide.getCategory() != null)
                .collect(Collectors.groupingBy(Pesticide::getCategory, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countBySafetyLevel() {
        return findAll().stream()
                .collect(Collectors.groupingBy(Pesticide::getSafetyLevel, Collectors.counting()));
    }
}