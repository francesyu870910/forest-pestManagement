package com.forestpest.data.factory;

import com.forestpest.entity.ForestResource;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * 森林资源数据生成器
 */
@Component
public class ForestResourceDataFactory {
    
    @Autowired
    private DataStorage dataStorage;
    
    private final Random random = new Random();
    
    private final List<String> treeSpecies = Arrays.asList(
        "马尾松", "杉木", "桉树", "樟树", "栎树", "桦树", "柳树", "杨树"
    );
    
    private final List<String> healthStatuses = Arrays.asList(
        "优良", "良好", "一般", "较差", "差"
    );
    
    private final List<String> managementLevels = Arrays.asList(
        "精细管理", "常规管理", "粗放管理"
    );
    
    private final List<String> standConditions = Arrays.asList(
        "郁闭", "半郁闭", "稀疏", "过密", "适中"
    );
    
    private final List<String> topographies = Arrays.asList(
        "平地", "缓坡", "陡坡", "山脊", "山谷", "台地"
    );
    
    private final List<String> soilTypes = Arrays.asList(
        "红壤", "黄壤", "棕壤", "黑土", "沙土", "粘土"
    );
    
    private final List<String> climates = Arrays.asList(
        "亚热带季风气候", "温带大陆性气候", "温带季风气候", "热带季风气候"
    );
    
    private final List<String> aspects = Arrays.asList(
        "阳坡", "阴坡", "半阳坡", "半阴坡", "平地"
    );
    
    private final List<String> accessibilities = Arrays.asList(
        "便利", "一般", "困难", "很困难"
    );
    
    /**
     * 生成森林资源数据
     */
    public void generateForestResources() {
        // 生成林场（顶级）
        List<String> forestFarmIds = generateForestFarms();
        
        // 为每个林场生成林区
        for (String farmId : forestFarmIds) {
            List<String> forestAreaIds = generateForestAreas(farmId);
            
            // 为每个林区生成小班
            for (String areaId : forestAreaIds) {
                generateForestPlots(areaId);
            }
        }
    }
    
    /**
     * 生成林场数据
     */
    private List<String> generateForestFarms() {
        List<String> farmIds = new ArrayList<>();
        String[] farmNames = {"东山林场", "西山林场", "南山林场"};
        
        for (int i = 0; i < farmNames.length; i++) {
            ForestResource farm = new ForestResource();
            farm.setId(dataStorage.generateId());
            farm.setAreaName(farmNames[i]);
            farm.setAreaCode("LF" + String.format("%02d", i + 1));
            farm.setAreaType("林场");
            farm.setArea(8000.0 + random.nextDouble() * 4000.0); // 8000-12000公顷
            farm.setParentAreaId(null); // 顶级区域
            
            // 设置林场级别的属性
            farm.setTreeSpecies(generateTreeSpeciesList());
            farm.setDominantSpecies(farm.getTreeSpecies().get(0));
            farm.setForestAge(15 + random.nextInt(20)); // 15-34年
            farm.setStandCondition(standConditions.get(random.nextInt(standConditions.size())));
            farm.setTopography(topographies.get(random.nextInt(topographies.size())));
            farm.setSoilType(soilTypes.get(random.nextInt(soilTypes.size())));
            farm.setClimate(climates.get(random.nextInt(climates.size())));
            farm.setElevation(generateElevation());
            farm.setSlope(generateSlope());
            farm.setAspect(aspects.get(random.nextInt(aspects.size())));
            farm.setHealthStatus(healthStatuses.get(random.nextInt(healthStatuses.size())));
            farm.setManagementLevel(managementLevels.get(random.nextInt(managementLevels.size())));
            farm.setAccessibility(accessibilities.get(random.nextInt(accessibilities.size())));
            farm.setCoordinates(generateCoordinates());
            farm.setBoundary(generateBoundary());
            farm.setNotes(generateNotes("林场"));
            farm.setCreatedBy("system");
            
            dataStorage.getForestResourceStorage().save(farm);
            farmIds.add(farm.getId());
        }
        
        return farmIds;
    }
    
    /**
     * 生成林区数据
     */
    private List<String> generateForestAreas(String parentFarmId) {
        List<String> areaIds = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) { // 每个林场4个林区
            ForestResource area = new ForestResource();
            area.setId(dataStorage.generateId());
            area.setAreaName(generateAreaName(parentFarmId, i + 1, "林区"));
            area.setAreaCode(generateAreaCode(parentFarmId, i + 1, "LQ"));
            area.setAreaType("林区");
            area.setArea(1500.0 + random.nextDouble() * 1000.0); // 1500-2500公顷
            area.setParentAreaId(parentFarmId);
            
            // 继承部分父级属性并设置特有属性
            ForestResource parentFarm = dataStorage.getForestResourceStorage().findById(parentFarmId).orElse(null);
            if (parentFarm != null) {
                area.setClimate(parentFarm.getClimate());
                area.setSoilType(parentFarm.getSoilType());
            }
            
            area.setTreeSpecies(generateTreeSpeciesList());
            area.setDominantSpecies(area.getTreeSpecies().get(0));
            area.setForestAge(12 + random.nextInt(25)); // 12-36年
            area.setStandCondition(standConditions.get(random.nextInt(standConditions.size())));
            area.setTopography(topographies.get(random.nextInt(topographies.size())));
            area.setElevation(generateElevation());
            area.setSlope(generateSlope());
            area.setAspect(aspects.get(random.nextInt(aspects.size())));
            area.setHealthStatus(healthStatuses.get(random.nextInt(healthStatuses.size())));
            area.setManagementLevel(managementLevels.get(random.nextInt(managementLevels.size())));
            area.setAccessibility(accessibilities.get(random.nextInt(accessibilities.size())));
            area.setCoordinates(generateCoordinates());
            area.setBoundary(generateBoundary());
            area.setNotes(generateNotes("林区"));
            area.setCreatedBy("system");
            
            dataStorage.getForestResourceStorage().save(area);
            areaIds.add(area.getId());
        }
        
        return areaIds;
    }
    
    /**
     * 生成小班数据
     */
    private void generateForestPlots(String parentAreaId) {
        for (int i = 0; i < 6; i++) { // 每个林区6个小班
            ForestResource plot = new ForestResource();
            plot.setId(dataStorage.generateId());
            plot.setAreaName(generateAreaName(parentAreaId, i + 1, "小班"));
            plot.setAreaCode(generateAreaCode(parentAreaId, i + 1, "XB"));
            plot.setAreaType("小班");
            plot.setArea(200.0 + random.nextDouble() * 300.0); // 200-500公顷
            plot.setParentAreaId(parentAreaId);
            
            // 继承部分父级属性
            ForestResource parentArea = dataStorage.getForestResourceStorage().findById(parentAreaId).orElse(null);
            if (parentArea != null) {
                plot.setClimate(parentArea.getClimate());
                plot.setSoilType(parentArea.getSoilType());
            }
            
            // 小班通常是单一树种或主要树种
            List<String> plotSpecies = new ArrayList<>();
            plotSpecies.add(treeSpecies.get(random.nextInt(treeSpecies.size())));
            if (random.nextBoolean()) {
                plotSpecies.add(treeSpecies.get(random.nextInt(treeSpecies.size())));
            }
            plot.setTreeSpecies(plotSpecies);
            plot.setDominantSpecies(plotSpecies.get(0));
            
            plot.setForestAge(8 + random.nextInt(30)); // 8-37年
            plot.setStandCondition(standConditions.get(random.nextInt(standConditions.size())));
            plot.setTopography(topographies.get(random.nextInt(topographies.size())));
            plot.setElevation(generateElevation());
            plot.setSlope(generateSlope());
            plot.setAspect(aspects.get(random.nextInt(aspects.size())));
            plot.setHealthStatus(healthStatuses.get(random.nextInt(healthStatuses.size())));
            plot.setManagementLevel(managementLevels.get(random.nextInt(managementLevels.size())));
            plot.setAccessibility(accessibilities.get(random.nextInt(accessibilities.size())));
            plot.setCoordinates(generateCoordinates());
            plot.setBoundary(generateBoundary());
            plot.setNotes(generateNotes("小班"));
            plot.setCreatedBy("system");
            
            dataStorage.getForestResourceStorage().save(plot);
        }
    }
    
    /**
     * 生成区域名称
     */
    private String generateAreaName(String parentId, int index, String type) {
        ForestResource parent = dataStorage.getForestResourceStorage().findById(parentId).orElse(null);
        if (parent != null) {
            return parent.getAreaName() + "第" + index + type;
        }
        return type + index;
    }
    
    /**
     * 生成区域代码
     */
    private String generateAreaCode(String parentId, int index, String prefix) {
        ForestResource parent = dataStorage.getForestResourceStorage().findById(parentId).orElse(null);
        if (parent != null) {
            return parent.getAreaCode() + "-" + prefix + String.format("%02d", index);
        }
        return prefix + String.format("%02d", index);
    }
    
    /**
     * 生成树种列表
     */
    private List<String> generateTreeSpeciesList() {
        List<String> species = new ArrayList<>();
        int count = 1 + random.nextInt(3); // 1-3个树种
        
        for (int i = 0; i < count; i++) {
            String specie = treeSpecies.get(random.nextInt(treeSpecies.size()));
            if (!species.contains(specie)) {
                species.add(specie);
            }
        }
        
        return species;
    }
    
    /**
     * 生成海拔
     */
    private String generateElevation() {
        int elevation = 100 + random.nextInt(1400); // 100-1500米
        return elevation + "米";
    }
    
    /**
     * 生成坡度
     */
    private String generateSlope() {
        int slope = random.nextInt(46); // 0-45度
        String level;
        if (slope <= 5) {
            level = "平缓";
        } else if (slope <= 15) {
            level = "缓坡";
        } else if (slope <= 25) {
            level = "斜坡";
        } else if (slope <= 35) {
            level = "陡坡";
        } else {
            level = "急坡";
        }
        return slope + "度(" + level + ")";
    }
    
    /**
     * 生成坐标
     */
    private String generateCoordinates() {
        double lat = 20.0 + random.nextDouble() * 30.0; // 20-50度纬度
        double lng = 100.0 + random.nextDouble() * 40.0; // 100-140度经度
        return String.format("%.6f,%.6f", lat, lng);
    }
    
    /**
     * 生成边界描述
     */
    private String generateBoundary() {
        String[] boundaries = {
            "东至山脊线，南至河流，西至公路，北至村界",
            "以自然地形为界，四周有明显标识",
            "东西长约2公里，南北宽约1.5公里",
            "边界清晰，有界桩标识"
        };
        return boundaries[random.nextInt(boundaries.length)];
    }
    
    /**
     * 生成备注
     */
    private String generateNotes(String type) {
        switch (type) {
            case "林场":
                return "重点保护区域，加强病虫害监测和防治工作。";
            case "林区":
                return "定期巡查，注意森林健康状况变化。";
            case "小班":
                return "具体作业单位，详细记录各项管理措施。";
            default:
                return "需要持续关注和管理。";
        }
    }
    
    /**
     * 获取随机森林资源ID
     */
    public String getRandomForestResourceId() {
        List<ForestResource> resources = dataStorage.getForestResourceStorage().findAll();
        if (resources.isEmpty()) {
            return null;
        }
        return resources.get(random.nextInt(resources.size())).getId();
    }
    
    /**
     * 根据区域类型获取随机森林资源ID
     */
    public String getRandomForestResourceIdByType(String areaType) {
        List<ForestResource> resources = dataStorage.getForestResourceStorage().findByAreaType(areaType);
        if (resources.isEmpty()) {
            return null;
        }
        return resources.get(random.nextInt(resources.size())).getId();
    }
}