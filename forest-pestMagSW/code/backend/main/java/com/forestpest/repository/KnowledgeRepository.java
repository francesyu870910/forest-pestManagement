package com.forestpest.repository;

import com.forestpest.entity.KnowledgeBase;

import java.util.List;

/**
 * 知识库数据访问接口
 */
public interface KnowledgeRepository extends BaseRepository<KnowledgeBase, String> {
    
    /**
     * 根据类型查找知识库条目
     */
    List<KnowledgeBase> findByType(String type);
    
    /**
     * 根据分类查找知识库条目
     */
    List<KnowledgeBase> findByCategory(String category);
    
    /**
     * 根据作者查找知识库条目
     */
    List<KnowledgeBase> findByAuthor(String author);
    
    /**
     * 根据标题模糊查询
     */
    List<KnowledgeBase> findByTitleContaining(String title);
    
    /**
     * 根据内容模糊查询
     */
    List<KnowledgeBase> findByContentContaining(String content);
    
    /**
     * 根据关键词查找知识库条目
     */
    List<KnowledgeBase> findByKeyword(String keyword);
    
    /**
     * 根据标签查找知识库条目
     */
    List<KnowledgeBase> findByTag(String tag);
    
    /**
     * 根据状态查找知识库条目
     */
    List<KnowledgeBase> findByStatus(String status);
    
    /**
     * 根据审批状态查找知识库条目
     */
    List<KnowledgeBase> findByApprovalStatus(String approvalStatus);
    
    /**
     * 根据难度等级查找知识库条目
     */
    List<KnowledgeBase> findByDifficulty(String difficulty);
    
    /**
     * 根据适用地区查找知识库条目
     */
    List<KnowledgeBase> findByApplicableRegion(String region);
    
    /**
     * 根据适用季节查找知识库条目
     */
    List<KnowledgeBase> findByApplicableSeason(String season);
    
    /**
     * 根据相关病虫害查找知识库条目
     */
    List<KnowledgeBase> findByRelatedPest(String pestId);
    
    /**
     * 根据相关防治方法查找知识库条目
     */
    List<KnowledgeBase> findByRelatedTreatment(String treatment);
    
    /**
     * 根据来源查找知识库条目
     */
    List<KnowledgeBase> findBySource(String source);
    
    /**
     * 根据审核人查找知识库条目
     */
    List<KnowledgeBase> findByReviewedBy(String reviewedBy);
    
    /**
     * 获取所有类型
     */
    List<String> findAllTypes();
    
    /**
     * 获取所有分类
     */
    List<String> findAllCategories();
    
    /**
     * 获取所有难度等级
     */
    List<String> findAllDifficulties();
    
    /**
     * 获取所有适用地区
     */
    List<String> findAllApplicableRegions();
    
    /**
     * 获取所有适用季节
     */
    List<String> findAllApplicableSeasons();
    
    /**
     * 根据关键词搜索知识库条目
     */
    List<KnowledgeBase> searchByKeyword(String keyword);
    
    /**
     * 根据多个条件查询
     */
    List<KnowledgeBase> findByTypeAndCategory(String type, String category);
    
    /**
     * 获取活跃状态的知识库条目
     */
    List<KnowledgeBase> findActiveKnowledge();
    
    /**
     * 获取已批准的知识库条目
     */
    List<KnowledgeBase> findApprovedKnowledge();
    
    /**
     * 获取待审批的知识库条目
     */
    List<KnowledgeBase> findPendingApprovalKnowledge();
    
    /**
     * 获取热门知识库条目（按查看次数排序）
     */
    List<KnowledgeBase> findPopularKnowledge(int limit);
    
    /**
     * 获取最近添加的知识库条目
     */
    List<KnowledgeBase> findRecentKnowledge(int limit);
    
    /**
     * 统计各类型知识库条目数量
     */
    java.util.Map<String, Long> countByType();
    
    /**
     * 统计各分类知识库条目数量
     */
    java.util.Map<String, Long> countByCategory();
    
    /**
     * 统计各难度等级知识库条目数量
     */
    java.util.Map<String, Long> countByDifficulty();
    
    /**
     * 统计各审批状态知识库条目数量
     */
    java.util.Map<String, Long> countByApprovalStatus();
    
    /**
     * 增加查看次数
     */
    void incrementViewCount(String id);
    
    /**
     * 增加使用次数
     */
    void incrementUseCount(String id);
}