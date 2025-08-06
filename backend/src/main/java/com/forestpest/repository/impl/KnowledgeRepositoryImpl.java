package com.forestpest.repository.impl;

import com.forestpest.entity.KnowledgeBase;
import com.forestpest.repository.KnowledgeRepository;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * 知识库数据访问实现类
 */
@Repository
public class KnowledgeRepositoryImpl implements KnowledgeRepository {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Override
    public KnowledgeBase save(KnowledgeBase knowledge) {
        if (knowledge.getId() == null) {
            knowledge.setId(dataStorage.generateId());
        }
        knowledge.setUpdatedTime(LocalDateTime.now());
        dataStorage.getKnowledgeStorage().save(knowledge);
        return knowledge;
    }
    
    @Override
    public Optional<KnowledgeBase> findById(String id) {
        return dataStorage.getKnowledgeStorage().findById(id);
    }
    
    @Override
    public List<KnowledgeBase> findAll() {
        return dataStorage.getKnowledgeStorage().findAll();
    }
    
    @Override
    public void deleteById(String id) {
        dataStorage.getKnowledgeStorage().deleteById(id);
    }
    
    @Override
    public void delete(KnowledgeBase knowledge) {
        if (knowledge != null && knowledge.getId() != null) {
            deleteById(knowledge.getId());
        }
    }
    
    @Override
    public boolean existsById(String id) {
        return dataStorage.getKnowledgeStorage().findById(id).isPresent();
    }
    
    @Override
    public long count() {
        return dataStorage.getKnowledgeStorage().count();
    }
    
    @Override
    public List<KnowledgeBase> saveAll(Iterable<KnowledgeBase> knowledgeList) {
        List<KnowledgeBase> savedKnowledge = new java.util.ArrayList<>();
        for (KnowledgeBase knowledge : knowledgeList) {
            savedKnowledge.add(save(knowledge));
        }
        return savedKnowledge;
    }
    
    @Override
    public void deleteAll(Iterable<KnowledgeBase> knowledgeList) {
        for (KnowledgeBase knowledge : knowledgeList) {
            delete(knowledge);
        }
    }
    
    @Override
    public void deleteAll() {
        dataStorage.getKnowledgeStorage().clear();
    }
    
    @Override
    public List<KnowledgeBase> findByType(String type) {
        return dataStorage.getKnowledgeStorage().findByType(type);
    }
    
    @Override
    public List<KnowledgeBase> findByCategory(String category) {
        return dataStorage.getKnowledgeStorage().findByCategory(category);
    }
    
    @Override
    public List<KnowledgeBase> findByAuthor(String author) {
        return dataStorage.getKnowledgeStorage().findByAuthor(author);
    }
    
    @Override
    public List<KnowledgeBase> findByTitleContaining(String title) {
        return dataStorage.getKnowledgeStorage().findByTitleContaining(title);
    }
    
    @Override
    public List<KnowledgeBase> findByContentContaining(String content) {
        return findAll().stream()
                .filter(knowledge -> knowledge.getContent() != null &&
                                   knowledge.getContent().toLowerCase().contains(content.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findByKeyword(String keyword) {
        return dataStorage.getKnowledgeStorage().findByKeyword(keyword);
    }
    
    @Override
    public List<KnowledgeBase> findByTag(String tag) {
        return dataStorage.getKnowledgeStorage().findByTag(tag);
    }
    
    @Override
    public List<KnowledgeBase> findByStatus(String status) {
        return dataStorage.getKnowledgeStorage().findByStatus(status);
    }
    
    @Override
    public List<KnowledgeBase> findByApprovalStatus(String approvalStatus) {
        return findAll().stream()
                .filter(knowledge -> approvalStatus.equals(knowledge.getApprovalStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findByDifficulty(String difficulty) {
        return dataStorage.getKnowledgeStorage().findByDifficulty(difficulty);
    }
    
    @Override
    public List<KnowledgeBase> findByApplicableRegion(String region) {
        return dataStorage.getKnowledgeStorage().findByApplicableRegion(region);
    }
    
    @Override
    public List<KnowledgeBase> findByApplicableSeason(String season) {
        return findAll().stream()
                .filter(knowledge -> season.equals(knowledge.getApplicableSeason()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findByRelatedPest(String pestId) {
        return dataStorage.getKnowledgeStorage().findByRelatedPest(pestId);
    }
    
    @Override
    public List<KnowledgeBase> findByRelatedTreatment(String treatment) {
        return findAll().stream()
                .filter(knowledge -> knowledge.getRelatedTreatments() != null &&
                                   knowledge.getRelatedTreatments().contains(treatment))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findBySource(String source) {
        return findAll().stream()
                .filter(knowledge -> source.equals(knowledge.getSource()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findByReviewedBy(String reviewedBy) {
        return findAll().stream()
                .filter(knowledge -> reviewedBy.equals(knowledge.getReviewedBy()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllTypes() {
        return findAll().stream()
                .map(KnowledgeBase::getType)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllCategories() {
        return findAll().stream()
                .map(KnowledgeBase::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllDifficulties() {
        return findAll().stream()
                .map(KnowledgeBase::getDifficulty)
                .filter(difficulty -> difficulty != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllApplicableRegions() {
        return findAll().stream()
                .map(KnowledgeBase::getApplicableRegion)
                .filter(region -> region != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findAllApplicableSeasons() {
        return findAll().stream()
                .map(KnowledgeBase::getApplicableSeason)
                .filter(season -> season != null)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> searchByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return findAll().stream()
                .filter(knowledge ->
                    (knowledge.getTitle() != null && knowledge.getTitle().toLowerCase().contains(lowerKeyword)) ||
                    (knowledge.getContent() != null && knowledge.getContent().toLowerCase().contains(lowerKeyword)) ||
                    (knowledge.getSummary() != null && knowledge.getSummary().toLowerCase().contains(lowerKeyword)) ||
                    (knowledge.getKeywords() != null && knowledge.getKeywords().stream()
                            .anyMatch(k -> k.toLowerCase().contains(lowerKeyword))) ||
                    (knowledge.getTags() != null && knowledge.getTags().stream()
                            .anyMatch(t -> t.toLowerCase().contains(lowerKeyword)))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findByTypeAndCategory(String type, String category) {
        return findAll().stream()
                .filter(knowledge -> type.equals(knowledge.getType()) &&
                                   category.equals(knowledge.getCategory()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findActiveKnowledge() {
        return findByStatus("ACTIVE");
    }
    
    @Override
    public List<KnowledgeBase> findApprovedKnowledge() {
        return findByApprovalStatus("已批准");
    }
    
    @Override
    public List<KnowledgeBase> findPendingApprovalKnowledge() {
        return findByApprovalStatus("待审批");
    }
    
    @Override
    public List<KnowledgeBase> findPopularKnowledge(int limit) {
        return findAll().stream()
                .filter(knowledge -> knowledge.getViewCount() != null)
                .sorted(Comparator.comparing(KnowledgeBase::getViewCount, Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeBase> findRecentKnowledge(int limit) {
        return findAll().stream()
                .sorted(Comparator.comparing(KnowledgeBase::getCreatedTime, 
                                           Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Long> countByType() {
        return findAll().stream()
                .collect(Collectors.groupingBy(KnowledgeBase::getType, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countByCategory() {
        return findAll().stream()
                .collect(Collectors.groupingBy(KnowledgeBase::getCategory, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countByDifficulty() {
        return findAll().stream()
                .filter(knowledge -> knowledge.getDifficulty() != null)
                .collect(Collectors.groupingBy(KnowledgeBase::getDifficulty, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countByApprovalStatus() {
        return findAll().stream()
                .filter(knowledge -> knowledge.getApprovalStatus() != null)
                .collect(Collectors.groupingBy(KnowledgeBase::getApprovalStatus, Collectors.counting()));
    }
    
    @Override
    public void incrementViewCount(String id) {
        Optional<KnowledgeBase> knowledgeOpt = findById(id);
        if (knowledgeOpt.isPresent()) {
            KnowledgeBase knowledge = knowledgeOpt.get();
            int currentCount = knowledge.getViewCount() != null ? knowledge.getViewCount() : 0;
            knowledge.setViewCount(currentCount + 1);
            save(knowledge);
        }
    }
    
    @Override
    public void incrementUseCount(String id) {
        Optional<KnowledgeBase> knowledgeOpt = findById(id);
        if (knowledgeOpt.isPresent()) {
            KnowledgeBase knowledge = knowledgeOpt.get();
            int currentCount = knowledge.getUseCount() != null ? knowledge.getUseCount() : 0;
            knowledge.setUseCount(currentCount + 1);
            save(knowledge);
        }
    }
}