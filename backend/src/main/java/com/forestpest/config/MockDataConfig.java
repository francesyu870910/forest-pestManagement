package com.forestpest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 模拟数据配置类
 * 从配置文件中读取模拟数据的生成参数
 */
@Configuration
@ConfigurationProperties(prefix = "mock-data")
public class MockDataConfig {
    
    private Users users = new Users();
    private Pests pests = new Pests();
    private Pesticides pesticides = new Pesticides();
    private TreatmentPlans treatmentPlans = new TreatmentPlans();
    private ForestResources forestResources = new ForestResources();
    private KnowledgeBase knowledgeBase = new KnowledgeBase();
    
    // Getters and Setters
    public Users getUsers() {
        return users;
    }
    
    public void setUsers(Users users) {
        this.users = users;
    }
    
    public Pests getPests() {
        return pests;
    }
    
    public void setPests(Pests pests) {
        this.pests = pests;
    }
    
    public Pesticides getPesticides() {
        return pesticides;
    }
    
    public void setPesticides(Pesticides pesticides) {
        this.pesticides = pesticides;
    }
    
    public TreatmentPlans getTreatmentPlans() {
        return treatmentPlans;
    }
    
    public void setTreatmentPlans(TreatmentPlans treatmentPlans) {
        this.treatmentPlans = treatmentPlans;
    }
    
    public ForestResources getForestResources() {
        return forestResources;
    }
    
    public void setForestResources(ForestResources forestResources) {
        this.forestResources = forestResources;
    }
    
    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }
    
    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
    
    // 内部配置类
    public static class Users {
        private int adminCount = 2;
        private int userCount = 8;
        private List<String> departments;
        
        public int getAdminCount() {
            return adminCount;
        }
        
        public void setAdminCount(int adminCount) {
            this.adminCount = adminCount;
        }
        
        public int getUserCount() {
            return userCount;
        }
        
        public void setUserCount(int userCount) {
            this.userCount = userCount;
        }
        
        public List<String> getDepartments() {
            return departments;
        }
        
        public void setDepartments(List<String> departments) {
            this.departments = departments;
        }
    }
    
    public static class Pests {
        private int count = 15;
        private List<String> categories;
        private List<String> riskLevels;
        
        public int getCount() {
            return count;
        }
        
        public void setCount(int count) {
            this.count = count;
        }
        
        public List<String> getCategories() {
            return categories;
        }
        
        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
        
        public List<String> getRiskLevels() {
            return riskLevels;
        }
        
        public void setRiskLevels(List<String> riskLevels) {
            this.riskLevels = riskLevels;
        }
    }
    
    public static class Pesticides {
        private int count = 20;
        private List<String> categories;
        private List<String> safetyLevels;
        private List<String> toxicityLevels;
        private List<String> units;
        
        public int getCount() {
            return count;
        }
        
        public void setCount(int count) {
            this.count = count;
        }
        
        public List<String> getCategories() {
            return categories;
        }
        
        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
        
        public List<String> getSafetyLevels() {
            return safetyLevels;
        }
        
        public void setSafetyLevels(List<String> safetyLevels) {
            this.safetyLevels = safetyLevels;
        }
        
        public List<String> getToxicityLevels() {
            return toxicityLevels;
        }
        
        public void setToxicityLevels(List<String> toxicityLevels) {
            this.toxicityLevels = toxicityLevels;
        }
        
        public List<String> getUnits() {
            return units;
        }
        
        public void setUnits(List<String> units) {
            this.units = units;
        }
    }
    
    public static class TreatmentPlans {
        private int count = 12;
        private List<String> priorities;
        private List<String> statuses;
        private List<String> approvalStatuses;
        
        public int getCount() {
            return count;
        }
        
        public void setCount(int count) {
            this.count = count;
        }
        
        public List<String> getPriorities() {
            return priorities;
        }
        
        public void setPriorities(List<String> priorities) {
            this.priorities = priorities;
        }
        
        public List<String> getStatuses() {
            return statuses;
        }
        
        public void setStatuses(List<String> statuses) {
            this.statuses = statuses;
        }
        
        public List<String> getApprovalStatuses() {
            return approvalStatuses;
        }
        
        public void setApprovalStatuses(List<String> approvalStatuses) {
            this.approvalStatuses = approvalStatuses;
        }
    }
    
    public static class ForestResources {
        private int forestFarms = 3;
        private int forestAreasPerFarm = 4;
        private int forestPlotsPerArea = 6;
        private List<String> treeSpecies;
        private List<String> areaTypes;
        private List<String> healthStatuses;
        private List<String> managementLevels;
        
        public int getForestFarms() {
            return forestFarms;
        }
        
        public void setForestFarms(int forestFarms) {
            this.forestFarms = forestFarms;
        }
        
        public int getForestAreasPerFarm() {
            return forestAreasPerFarm;
        }
        
        public void setForestAreasPerFarm(int forestAreasPerFarm) {
            this.forestAreasPerFarm = forestAreasPerFarm;
        }
        
        public int getForestPlotsPerArea() {
            return forestPlotsPerArea;
        }
        
        public void setForestPlotsPerArea(int forestPlotsPerArea) {
            this.forestPlotsPerArea = forestPlotsPerArea;
        }
        
        public List<String> getTreeSpecies() {
            return treeSpecies;
        }
        
        public void setTreeSpecies(List<String> treeSpecies) {
            this.treeSpecies = treeSpecies;
        }
        
        public List<String> getAreaTypes() {
            return areaTypes;
        }
        
        public void setAreaTypes(List<String> areaTypes) {
            this.areaTypes = areaTypes;
        }
        
        public List<String> getHealthStatuses() {
            return healthStatuses;
        }
        
        public void setHealthStatuses(List<String> healthStatuses) {
            this.healthStatuses = healthStatuses;
        }
        
        public List<String> getManagementLevels() {
            return managementLevels;
        }
        
        public void setManagementLevels(List<String> managementLevels) {
            this.managementLevels = managementLevels;
        }
    }
    
    public static class KnowledgeBase {
        private int count = 25;
        private List<String> types;
        private List<String> categories;
        private List<String> difficulties;
        private List<String> seasons;
        private List<String> regions;
        
        public int getCount() {
            return count;
        }
        
        public void setCount(int count) {
            this.count = count;
        }
        
        public List<String> getTypes() {
            return types;
        }
        
        public void setTypes(List<String> types) {
            this.types = types;
        }
        
        public List<String> getCategories() {
            return categories;
        }
        
        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
        
        public List<String> getDifficulties() {
            return difficulties;
        }
        
        public void setDifficulties(List<String> difficulties) {
            this.difficulties = difficulties;
        }
        
        public List<String> getSeasons() {
            return seasons;
        }
        
        public void setSeasons(List<String> seasons) {
            this.seasons = seasons;
        }
        
        public List<String> getRegions() {
            return regions;
        }
        
        public void setRegions(List<String> regions) {
            this.regions = regions;
        }
    }
}