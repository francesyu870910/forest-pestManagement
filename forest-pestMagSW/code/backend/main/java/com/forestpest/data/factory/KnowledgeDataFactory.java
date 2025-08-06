package com.forestpest.data.factory;

import com.forestpest.entity.KnowledgeBase;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * 知识库数据生成器
 */
@Component
public class KnowledgeDataFactory {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Autowired
    private UserDataFactory userDataFactory;
    
    @Autowired
    private PestDataFactory pestDataFactory;
    
    private final Random random = new Random();
    
    private final List<String> types = Arrays.asList(
        "病虫害识别", "防治技术", "药剂使用", "设备操作", "安全防护", "法规标准"
    );
    
    private final List<String> categories = Arrays.asList(
        "基础知识", "实用技术", "案例分析", "操作指南", "安全规范", "政策法规"
    );
    
    private final List<String> difficulties = Arrays.asList(
        "初级", "中级", "高级"
    );
    
    private final List<String> seasons = Arrays.asList(
        "春季", "夏季", "秋季", "冬季", "全年"
    );
    
    private final List<String> regions = Arrays.asList(
        "华南地区", "华中地区", "华北地区", "西南地区", "东北地区", "全国通用"
    );
    
    private final List<String> authors = Arrays.asList(
        "张教授", "李专家", "王研究员", "陈博士", "刘高工", "赵主任", "孙技师", "周工程师"
    );
    
    private final List<String> sources = Arrays.asList(
        "中国林业科学研究院", "北京林业大学", "南京林业大学", "中南林业科技大学",
        "国家林业和草原局", "各省林业厅", "林业技术推广站", "森林病虫害防治检疫站"
    );
    
    // 知识库内容模板
    private final String[][] knowledgeData = {
        // 病虫害识别类
        {"松毛虫识别与诊断技术", "病虫害识别", "基础知识", "详细介绍松毛虫的形态特征、生活史、危害症状及识别要点。"},
        {"天牛类害虫识别指南", "病虫害识别", "实用技术", "系统介绍各种天牛的识别特征、危害状况和分布规律。"},
        {"森林病害症状诊断手册", "病虫害识别", "操作指南", "提供常见森林病害的症状图谱和诊断流程。"},
        
        // 防治技术类
        {"生物防治技术应用", "防治技术", "实用技术", "介绍生物防治的原理、方法和实际应用案例。"},
        {"化学防治安全操作规程", "防治技术", "安全规范", "详细说明化学农药的安全使用方法和注意事项。"},
        {"综合防治策略制定", "防治技术", "案例分析", "通过实际案例分析综合防治策略的制定和实施。"},
        
        // 药剂使用类
        {"常用杀虫剂使用指南", "药剂使用", "操作指南", "介绍常用杀虫剂的特点、使用方法和注意事项。"},
        {"生物农药应用技术", "药剂使用", "实用技术", "详细介绍生物农药的种类、特点和使用技术。"},
        {"农药混配使用原则", "药剂使用", "基础知识", "说明农药混配的原理、方法和安全要求。"},
        
        // 设备操作类
        {"喷雾设备操作维护", "设备操作", "操作指南", "介绍各种喷雾设备的操作方法和维护保养。"},
        {"无人机植保技术", "设备操作", "实用技术", "详细介绍无人机在森林病虫害防治中的应用。"},
        {"诱捕器设置与管理", "设备操作", "基础知识", "说明各种诱捕器的设置方法和管理要点。"},
        
        // 安全防护类
        {"农药使用安全防护", "安全防护", "安全规范", "详细介绍农药使用过程中的安全防护措施。"},
        {"野外作业安全指南", "安全防护", "操作指南", "提供野外病虫害防治作业的安全指导。"},
        {"中毒急救处理方法", "安全防护", "基础知识", "介绍农药中毒的症状识别和急救处理方法。"},
        
        // 法规标准类
        {"森林病虫害防治条例", "法规标准", "政策法规", "详细解读国家森林病虫害防治相关法规。"},
        {"农药管理条例解读", "法规标准", "政策法规", "介绍农药登记、使用和管理的法律要求。"},
        {"森林检疫技术规程", "法规标准", "操作指南", "说明森林植物检疫的技术标准和操作流程。"}
    };
    
    /**
     * 生成知识库数据
     */
    public void generateKnowledgeBase() {
        // 生成预定义的知识条目
        for (int i = 0; i < knowledgeData.length; i++) {
            String[] data = knowledgeData[i];
            
            KnowledgeBase knowledge = new KnowledgeBase();
            knowledge.setId(dataStorage.generateId());
            knowledge.setTitle(data[0]);
            knowledge.setType(data[1]);
            knowledge.setCategory(data[2]);
            knowledge.setContent(generateDetailedContent(data[0], data[3]));
            knowledge.setSummary(data[3]);
            
            // 生成关键词
            knowledge.setKeywords(generateKeywords(data[1]));
            
            // 生成标签
            knowledge.setTags(generateTags(data[1], data[2]));
            
            // 设置作者和来源
            knowledge.setAuthor(authors.get(random.nextInt(authors.size())));
            knowledge.setSource(sources.get(random.nextInt(sources.size())));
            
            // 设置版本
            knowledge.setVersion("v" + (1 + random.nextInt(3)) + "." + random.nextInt(10));
            
            // 设置状态
            knowledge.setStatus("ACTIVE");
            knowledge.setApprovalStatus("已批准");
            
            // 生成使用统计
            knowledge.setViewCount(random.nextInt(1000));
            knowledge.setUseCount(random.nextInt(100));
            
            // 设置难度等级
            knowledge.setDifficulty(difficulties.get(random.nextInt(difficulties.size())));
            
            // 设置适用地区和季节
            knowledge.setApplicableRegion(regions.get(random.nextInt(regions.size())));
            knowledge.setApplicableSeason(seasons.get(random.nextInt(seasons.size())));
            
            // 生成相关病虫害
            if ("病虫害识别".equals(data[1]) || "防治技术".equals(data[1])) {
                knowledge.setRelatedPests(generateRelatedPests());
            }
            
            // 生成相关防治方法
            if ("防治技术".equals(data[1]) || "药剂使用".equals(data[1])) {
                knowledge.setRelatedTreatments(generateRelatedTreatments());
            }
            
            // 生成附件列表
            knowledge.setAttachments(generateAttachments(knowledge.getId()));
            
            // 设置审核人
            knowledge.setReviewedBy(userDataFactory.getRandomAdminUserId());
            
            knowledge.setCreatedBy("system");
            
            dataStorage.getKnowledgeStorage().save(knowledge);
        }
        
        // 生成额外的随机知识条目
        generateAdditionalKnowledge();
    }
    
    /**
     * 生成额外的知识条目
     */
    private void generateAdditionalKnowledge() {
        String[] additionalTitles = {
            "春季病虫害预防要点", "夏季高温期防治注意事项", "秋季病虫害综合治理",
            "冬季清园技术措施", "新型生物农药研究进展", "智能监测技术应用",
            "森林健康评估方法", "病虫害预警系统建设", "天敌保护与利用技术"
        };
        
        for (String title : additionalTitles) {
            KnowledgeBase knowledge = new KnowledgeBase();
            knowledge.setId(dataStorage.generateId());
            knowledge.setTitle(title);
            knowledge.setType(types.get(random.nextInt(types.size())));
            knowledge.setCategory(categories.get(random.nextInt(categories.size())));
            knowledge.setContent(generateDetailedContent(title, ""));
            knowledge.setSummary(generateSummary(title));
            
            knowledge.setKeywords(generateKeywords(knowledge.getType()));
            knowledge.setTags(generateTags(knowledge.getType(), knowledge.getCategory()));
            knowledge.setAuthor(authors.get(random.nextInt(authors.size())));
            knowledge.setSource(sources.get(random.nextInt(sources.size())));
            knowledge.setVersion("v1." + random.nextInt(5));
            knowledge.setStatus("ACTIVE");
            knowledge.setApprovalStatus("已批准");
            knowledge.setViewCount(random.nextInt(500));
            knowledge.setUseCount(random.nextInt(50));
            knowledge.setDifficulty(difficulties.get(random.nextInt(difficulties.size())));
            knowledge.setApplicableRegion(regions.get(random.nextInt(regions.size())));
            knowledge.setApplicableSeason(seasons.get(random.nextInt(seasons.size())));
            
            if (random.nextBoolean()) {
                knowledge.setRelatedPests(generateRelatedPests());
            }
            
            if (random.nextBoolean()) {
                knowledge.setRelatedTreatments(generateRelatedTreatments());
            }
            
            knowledge.setAttachments(generateAttachments(knowledge.getId()));
            knowledge.setReviewedBy(userDataFactory.getRandomAdminUserId());
            knowledge.setCreatedBy("system");
            
            dataStorage.getKnowledgeStorage().save(knowledge);
        }
    }
    
    /**
     * 生成详细内容
     */
    private String generateDetailedContent(String title, String summary) {
        StringBuilder content = new StringBuilder();
        content.append("# ").append(title).append("\n\n");
        
        if (!summary.isEmpty()) {
            content.append("## 概述\n");
            content.append(summary).append("\n\n");
        }
        
        content.append("## 主要内容\n\n");
        content.append("### 1. 基本原理\n");
        content.append("本技术基于科学的理论基础，结合实际应用经验，为森林病虫害防治提供有效指导。\n\n");
        
        content.append("### 2. 技术要点\n");
        content.append("- 准确识别病虫害种类和危害程度\n");
        content.append("- 选择合适的防治方法和时机\n");
        content.append("- 严格按照操作规程执行\n");
        content.append("- 注意安全防护和环境保护\n\n");
        
        content.append("### 3. 注意事项\n");
        content.append("在实施过程中应注意以下几点：\n");
        content.append("1. 根据实际情况调整技术参数\n");
        content.append("2. 密切观察防治效果\n");
        content.append("3. 做好记录和总结\n");
        content.append("4. 及时反馈问题和建议\n\n");
        
        content.append("### 4. 参考资料\n");
        content.append("相关技术标准、研究论文和实践案例可供进一步学习参考。\n");
        
        return content.toString();
    }
    
    /**
     * 生成摘要
     */
    private String generateSummary(String title) {
        return "本文档详细介绍了" + title + "的相关技术和方法，为森林病虫害防治工作提供专业指导。";
    }
    
    /**
     * 生成关键词
     */
    private List<String> generateKeywords(String type) {
        List<String> keywords = new ArrayList<>();
        
        switch (type) {
            case "病虫害识别":
                keywords.addAll(Arrays.asList("识别", "诊断", "症状", "形态特征", "生活史"));
                break;
            case "防治技术":
                keywords.addAll(Arrays.asList("防治", "综合治理", "生物防治", "化学防治", "物理防治"));
                break;
            case "药剂使用":
                keywords.addAll(Arrays.asList("农药", "杀虫剂", "杀菌剂", "用药安全", "抗药性"));
                break;
            case "设备操作":
                keywords.addAll(Arrays.asList("设备", "操作", "维护", "喷雾", "监测"));
                break;
            case "安全防护":
                keywords.addAll(Arrays.asList("安全", "防护", "中毒", "急救", "防护用品"));
                break;
            case "法规标准":
                keywords.addAll(Arrays.asList("法规", "标准", "检疫", "管理", "政策"));
                break;
        }
        
        // 添加通用关键词
        keywords.addAll(Arrays.asList("森林", "病虫害", "防治"));
        
        return keywords;
    }
    
    /**
     * 生成标签
     */
    private List<String> generateTags(String type, String category) {
        List<String> tags = new ArrayList<>();
        tags.add(type);
        tags.add(category);
        
        String[] commonTags = {"实用", "专业", "权威", "最新", "推荐"};
        tags.add(commonTags[random.nextInt(commonTags.length)]);
        
        return tags;
    }
    
    /**
     * 生成相关病虫害
     */
    private List<String> generateRelatedPests() {
        List<String> relatedPests = new ArrayList<>();
        int count = 1 + random.nextInt(3); // 1-3个相关病虫害
        
        for (int i = 0; i < count; i++) {
            String pestId = pestDataFactory.getRandomPestId();
            if (pestId != null && !relatedPests.contains(pestId)) {
                relatedPests.add(pestId);
            }
        }
        
        return relatedPests;
    }
    
    /**
     * 生成相关防治方法
     */
    private List<String> generateRelatedTreatments() {
        return Arrays.asList("生物防治", "化学防治", "物理防治", "综合防治");
    }
    
    /**
     * 生成附件列表
     */
    private List<String> generateAttachments(String knowledgeId) {
        List<String> attachments = new ArrayList<>();
        
        // 生成一些模拟的附件文件
        if (random.nextBoolean()) {
            attachments.add("/attachments/knowledge/" + knowledgeId + "_图谱.pdf");
        }
        
        if (random.nextBoolean()) {
            attachments.add("/attachments/knowledge/" + knowledgeId + "_操作视频.mp4");
        }
        
        if (random.nextBoolean()) {
            attachments.add("/attachments/knowledge/" + knowledgeId + "_案例分析.doc");
        }
        
        return attachments;
    }
    
    /**
     * 获取随机知识库条目ID
     */
    public String getRandomKnowledgeId() {
        List<KnowledgeBase> knowledgeList = dataStorage.getKnowledgeStorage().findAll();
        if (knowledgeList.isEmpty()) {
            return null;
        }
        return knowledgeList.get(random.nextInt(knowledgeList.size())).getId();
    }
    
    /**
     * 根据类型获取随机知识库条目ID
     */
    public String getRandomKnowledgeIdByType(String type) {
        List<KnowledgeBase> knowledgeList = dataStorage.getKnowledgeStorage().findByType(type);
        if (knowledgeList.isEmpty()) {
            return null;
        }
        return knowledgeList.get(random.nextInt(knowledgeList.size())).getId();
    }
}