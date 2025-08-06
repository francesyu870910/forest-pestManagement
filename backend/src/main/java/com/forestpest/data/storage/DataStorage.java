package com.forestpest.data.storage;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据存储管理器
 * 提供线程安全的内存数据存储和管理功能
 */
@Component
public class DataStorage {
    
    // ID生成器
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    // 各类型数据存储
    private final UserStorage userStorage = new UserStorage();
    private final PestStorage pestStorage = new PestStorage();
    private final PesticideStorage pesticideStorage = new PesticideStorage();
    private final TreatmentStorage treatmentStorage = new TreatmentStorage();
    private final EvaluationStorage evaluationStorage = new EvaluationStorage();
    private final PredictionStorage predictionStorage = new PredictionStorage();
    private final ForestResourceStorage forestResourceStorage = new ForestResourceStorage();
    private final KnowledgeStorage knowledgeStorage = new KnowledgeStorage();
    
    /**
     * 生成唯一ID
     */
    public String generateId() {
        return String.valueOf(idGenerator.getAndIncrement());
    }
    
    /**
     * 重置所有数据
     */
    public void resetAllData() {
        userStorage.clear();
        pestStorage.clear();
        pesticideStorage.clear();
        treatmentStorage.clear();
        evaluationStorage.clear();
        predictionStorage.clear();
        forestResourceStorage.clear();
        knowledgeStorage.clear();
        idGenerator.set(1);
    }
    
    // Getters for storage instances
    public UserStorage getUserStorage() {
        return userStorage;
    }
    
    public PestStorage getPestStorage() {
        return pestStorage;
    }
    
    public PesticideStorage getPesticideStorage() {
        return pesticideStorage;
    }
    
    public TreatmentStorage getTreatmentStorage() {
        return treatmentStorage;
    }
    
    public EvaluationStorage getEvaluationStorage() {
        return evaluationStorage;
    }
    
    public PredictionStorage getPredictionStorage() {
        return predictionStorage;
    }
    
    public ForestResourceStorage getForestResourceStorage() {
        return forestResourceStorage;
    }
    
    public KnowledgeStorage getKnowledgeStorage() {
        return knowledgeStorage;
    }
}