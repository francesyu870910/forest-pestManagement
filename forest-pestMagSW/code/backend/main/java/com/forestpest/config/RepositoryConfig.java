package com.forestpest.config;

import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import jakarta.annotation.PostConstruct;

/**
 * Repository配置类
 * 负责Repository层的初始化和配置
 */
@Configuration
@DependsOn("dataStorage")
public class RepositoryConfig {
    
    @Autowired
    private DataStorage dataStorage;
    
    /**
     * Repository层初始化
     */
    @PostConstruct
    public void initializeRepositories() {
        System.out.println("Repository层初始化完成");
        System.out.println("数据存储状态:");
        System.out.println("- 用户存储: " + (dataStorage.getUserStorage() != null ? "已就绪" : "未就绪"));
        System.out.println("- 病虫害存储: " + (dataStorage.getPestStorage() != null ? "已就绪" : "未就绪"));
        System.out.println("- 药剂存储: " + (dataStorage.getPesticideStorage() != null ? "已就绪" : "未就绪"));
        System.out.println("- 防治存储: " + (dataStorage.getTreatmentStorage() != null ? "已就绪" : "未就绪"));
        System.out.println("- 评估存储: " + (dataStorage.getEvaluationStorage() != null ? "已就绪" : "未就绪"));
        System.out.println("- 预测存储: " + (dataStorage.getPredictionStorage() != null ? "已就绪" : "未就绪"));
        System.out.println("- 森林资源存储: " + (dataStorage.getForestResourceStorage() != null ? "已就绪" : "未就绪"));
        System.out.println("- 知识库存储: " + (dataStorage.getKnowledgeStorage() != null ? "已就绪" : "未就绪"));
    }
}