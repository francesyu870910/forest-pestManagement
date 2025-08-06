package com.forestpest.data.storage;

import com.forestpest.entity.KnowledgeBase;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 知识库数据存储
 */
@Component
public class KnowledgeStorage {
    
    private final Map<String, KnowledgeBase> knowledgeBase = new ConcurrentHashMap<>();
    private final Map<String, List<String>> typeIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> categoryIndex = new ConcurrentHashMap<>();
    private final Map<String, List<String>> authorIndex = new ConcurrentHashMap<>();
    
    public void save(KnowledgeBase knowledge) {
        knowledgeBase.put(knowledge.getId(), knowledge);
        
        // 更新类型索引
        typeIndex.computeIfAbsent(knowledge.getType(), k -> new ArrayList<>()).add(knowledge.getId());
        
        // 更新分类索引
        categoryIndex.computeIfAbsent(knowledge.getCategory(), k -> new ArrayList<>()).add(knowledge.getId());
        
        // 更新作者索引
        if (knowledge.getAuthor() != null) {
            authorIndex.computeIfAbsent(knowledge.getAuthor(), k -> new ArrayList<>()).add(knowledge.getId());
        }
    }
    
    public Optional<KnowledgeBase> findById(String id) {
        return Optional.ofNullable(knowledgeBase.get(id));
    }
    
    public List<KnowledgeBase> findAll() {
        return new ArrayList<>(knowledgeBase.values());
    }
    
    public List<KnowledgeBase> findByType(String type) {
        List<String> ids = typeIndex.get(type);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(knowledgeBase::get)
                .filter(knowledge -> knowledge != null)
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByCategory(String category) {
        List<String> ids = categoryIndex.get(category);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(knowledgeBase::get)
                .filter(knowledge -> knowledge != null)
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByAuthor(String author) {
        List<String> ids = authorIndex.get(author);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(knowledgeBase::get)
                .filter(knowledge -> knowledge != null)
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByTitleContaining(String title) {
        return knowledgeBase.values().stream()
                .filter(knowledge -> knowledge.getTitle().contains(title))
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByKeyword(String keyword) {
        return knowledgeBase.values().stream()
                .filter(knowledge -> knowledge.getKeywords() != null &&
                                   knowledge.getKeywords().contains(keyword))
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByTag(String tag) {
        return knowledgeBase.values().stream()
                .filter(knowledge -> knowledge.getTags() != null &&
                                   knowledge.getTags().contains(tag))
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByStatus(String status) {
        return knowledgeBase.values().stream()
                .filter(knowledge -> status.equals(knowledge.getStatus()))
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByDifficulty(String difficulty) {
        return knowledgeBase.values().stream()
                .filter(knowledge -> difficulty.equals(knowledge.getDifficulty()))
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByApplicableRegion(String region) {
        return knowledgeBase.values().stream()
                .filter(knowledge -> region.equals(knowledge.getApplicableRegion()))
                .collect(Collectors.toList());
    }
    
    public List<KnowledgeBase> findByRelatedPest(String pestId) {
        return knowledgeBase.values().stream()
                .filter(knowledge -> knowledge.getRelatedPests() != null &&
                                   knowledge.getRelatedPests().contains(pestId))
                .collect(Collectors.toList());
    }
    
    public void deleteById(String id) {
        KnowledgeBase knowledge = knowledgeBase.remove(id);
        if (knowledge != null) {
            // 清理索引
            List<String> typeIds = typeIndex.get(knowledge.getType());
            if (typeIds != null) {
                typeIds.remove(id);
            }
            
            List<String> categoryIds = categoryIndex.get(knowledge.getCategory());
            if (categoryIds != null) {
                categoryIds.remove(id);
            }
            
            if (knowledge.getAuthor() != null) {
                List<String> authorIds = authorIndex.get(knowledge.getAuthor());
                if (authorIds != null) {
                    authorIds.remove(id);
                }
            }
        }
    }
    
    public void clear() {
        knowledgeBase.clear();
        typeIndex.clear();
        categoryIndex.clear();
        authorIndex.clear();
    }
    
    public int count() {
        return knowledgeBase.size();
    }
}