package com.forestpest.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 专家知识库实体类
 */
public class KnowledgeBase extends BaseEntity {
    
    @NotBlank(message = "知识标题不能为空")
    @Size(max = 200, message = "知识标题长度不能超过200个字符")
    private String title;
    
    @NotBlank(message = "知识类型不能为空")
    @Size(max = 50, message = "知识类型长度不能超过50个字符")
    private String type;
    
    @NotBlank(message = "知识分类不能为空")
    @Size(max = 100, message = "知识分类长度不能超过100个字符")
    private String category;
    
    @NotBlank(message = "知识内容不能为空")
    private String content;
    
    @Size(max = 500, message = "知识摘要长度不能超过500个字符")
    private String summary;
    
    private List<String> keywords;
    
    private List<String> tags;
    
    private String author;
    
    private String source;
    
    private String version;
    
    private String status;
    
    private Integer viewCount;
    
    private Integer useCount;
    
    private String difficulty;
    
    private String applicableRegion;
    
    private String applicableSeason;
    
    private List<String> relatedPests;
    
    private List<String> relatedTreatments;
    
    private List<String> attachments;
    
    private String reviewedBy;
    
    private String approvalStatus;

    public KnowledgeBase() {
        super();
        this.status = "ACTIVE";
        this.viewCount = 0;
        this.useCount = 0;
        this.approvalStatus = "PENDING";
    }

    public KnowledgeBase(String title, String type, String category, String content) {
        super();
        this.title = title;
        this.type = type;
        this.category = category;
        this.content = content;
        this.status = "ACTIVE";
        this.viewCount = 0;
        this.useCount = 0;
        this.approvalStatus = "PENDING";
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getApplicableRegion() {
        return applicableRegion;
    }

    public void setApplicableRegion(String applicableRegion) {
        this.applicableRegion = applicableRegion;
    }

    public String getApplicableSeason() {
        return applicableSeason;
    }

    public void setApplicableSeason(String applicableSeason) {
        this.applicableSeason = applicableSeason;
    }

    public List<String> getRelatedPests() {
        return relatedPests;
    }

    public void setRelatedPests(List<String> relatedPests) {
        this.relatedPests = relatedPests;
    }

    public List<String> getRelatedTreatments() {
        return relatedTreatments;
    }

    public void setRelatedTreatments(List<String> relatedTreatments) {
        this.relatedTreatments = relatedTreatments;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    @Override
    public String toString() {
        return "KnowledgeBase{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", summary='" + summary + '\'' +
                ", keywords=" + keywords +
                ", tags=" + tags +
                ", author='" + author + '\'' +
                ", source='" + source + '\'' +
                ", version='" + version + '\'' +
                ", status='" + status + '\'' +
                ", viewCount=" + viewCount +
                ", useCount=" + useCount +
                ", difficulty='" + difficulty + '\'' +
                ", applicableRegion='" + applicableRegion + '\'' +
                ", applicableSeason='" + applicableSeason + '\'' +
                ", relatedPests=" + relatedPests +
                ", relatedTreatments=" + relatedTreatments +
                ", reviewedBy='" + reviewedBy + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}