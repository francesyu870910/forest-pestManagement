package com.forestpest.data.factory;

import com.forestpest.entity.Pest;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 病虫害数据生成器
 */
@Component
public class PestDataFactory {
    
    @Autowired
    private DataStorage dataStorage;
    
    private final Random random = new Random();
    
    // 病虫害基础数据
    private final String[][] pestData = {
        // 食叶害虫
        {"松毛虫", "Dendrolimus punctatus", "食叶害虫", "松毛虫是松树的主要害虫，幼虫取食松针", "高风险"},
        {"舞毒蛾", "Lymantria dispar", "食叶害虫", "舞毒蛾幼虫食性杂，危害多种阔叶树", "中风险"},
        {"杨扇舟蛾", "Clostera anachoreta", "食叶害虫", "杨扇舟蛾主要危害杨树和柳树", "中风险"},
        
        // 蛀干害虫
        {"松墨天牛", "Monochamus alternatus", "蛀干害虫", "松墨天牛是松材线虫病的主要传播媒介", "极高风险"},
        {"光肩星天牛", "Anoplophora glabripennis", "蛀干害虫", "光肩星天牛危害多种阔叶树，钻蛀树干", "高风险"},
        {"桑天牛", "Apriona germari", "蛀干害虫", "桑天牛主要危害桑树、柳树等", "中风险"},
        
        // 刺吸害虫
        {"松大蚜", "Cinara pinea", "刺吸害虫", "松大蚜刺吸松树汁液，影响树木生长", "低风险"},
        {"杨树蚜虫", "Chaitophorus populeti", "刺吸害虫", "杨树蚜虫危害杨树嫩叶和嫩梢", "低风险"},
        {"柳蚜", "Tuberolachnus salignus", "刺吸害虫", "柳蚜主要危害柳树", "低风险"},
        
        // 真菌病害
        {"松枯梢病", "Diplodia pinea", "真菌病害", "松枯梢病导致松树枝梢枯死", "中风险"},
        {"杨树溃疡病", "Botryosphaeria dothidea", "真菌病害", "杨树溃疡病引起树皮溃疡和枝干枯死", "高风险"},
        {"栎树白粉病", "Microsphaera alphitoides", "真菌病害", "栎树白粉病影响叶片光合作用", "中风险"},
        
        // 细菌病害
        {"杨树细菌性溃疡病", "Xanthomonas populi", "细菌病害", "杨树细菌性溃疡病导致树皮溃疡", "中风险"},
        {"松树细菌性枯萎病", "Ralstonia solanacearum", "细菌病害", "松树细菌性枯萎病引起整株枯死", "高风险"},
        
        // 病毒病害
        {"桦树花叶病毒", "Birch leaf roll virus", "病毒病害", "桦树花叶病毒导致叶片变色卷曲", "低风险"}
    };
    
    /**
     * 生成病虫害数据
     */
    public void generatePests() {
        for (int i = 0; i < pestData.length; i++) {
            String[] data = pestData[i];
            
            Pest pest = new Pest();
            pest.setId(dataStorage.generateId());
            pest.setName(data[0]);
            pest.setScientificName(data[1]);
            pest.setCategory(data[2]);
            pest.setDescription(data[3]);
            pest.setRiskLevel(data[4]);
            
            // 生成症状列表
            pest.setSymptoms(generateSymptoms(data[2]));
            
            // 生成危害特征
            pest.setHarmCharacteristics(generateHarmCharacteristics(data[2]));
            
            // 生成发生规律
            pest.setOccurrencePattern(generateOccurrencePattern());
            
            // 生成适宜环境条件
            pest.setSuitableEnvironment(generateSuitableEnvironment());
            
            // 生成寄主植物
            pest.setHostPlants(generateHostPlants(data[2]));
            
            // 生成分布区域
            pest.setDistributionArea(generateDistributionArea());
            
            // 生成预防方法
            pest.setPreventionMethods(generatePreventionMethods(data[2]));
            
            // 生成图片列表（模拟）
            pest.setImages(Arrays.asList(
                "/images/pests/" + pest.getId() + "_1.jpg",
                "/images/pests/" + pest.getId() + "_2.jpg"
            ));
            
            pest.setCreatedBy("system");
            
            dataStorage.getPestStorage().save(pest);
        }
    }
    
    /**
     * 生成症状列表
     */
    private List<String> generateSymptoms(String category) {
        switch (category) {
            case "食叶害虫":
                return Arrays.asList("叶片被啃食", "叶片出现孔洞", "叶片边缘不规则", "大量落叶");
            case "蛀干害虫":
                return Arrays.asList("树干有蛀孔", "树皮下有虫道", "有木屑排出", "树势衰弱");
            case "刺吸害虫":
                return Arrays.asList("叶片发黄", "叶片卷曲", "分泌蜜露", "叶片萎蔫");
            case "真菌病害":
                return Arrays.asList("叶片有斑点", "枝条枯死", "有菌丝体", "孢子飞散");
            case "细菌病害":
                return Arrays.asList("组织溃烂", "有臭味", "流脓液", "快速扩散");
            case "病毒病害":
                return Arrays.asList("叶片花叶", "生长矮化", "叶片变形", "颜色异常");
            default:
                return Arrays.asList("症状不明显", "需要专业诊断");
        }
    }
    
    /**
     * 生成危害特征
     */
    private String generateHarmCharacteristics(String category) {
        switch (category) {
            case "食叶害虫":
                return "主要危害叶片，严重时可造成全株落叶，影响光合作用，导致树势衰弱，抗性下降。";
            case "蛀干害虫":
                return "钻蛀树干和枝条，破坏输导组织，影响水分和养分运输，严重时可导致整株死亡。";
            case "刺吸害虫":
                return "刺吸植物汁液，导致叶片失绿、卷曲，分泌蜜露引起煤污病，影响光合作用。";
            case "真菌病害":
                return "感染植物组织，产生病斑和孢子，通过风雨传播，可快速扩散造成大面积危害。";
            case "细菌病害":
                return "感染植物维管束，阻塞输导组织，导致萎蔫和死亡，传播速度快。";
            case "病毒病害":
                return "干扰植物正常生理代谢，导致生长异常，通过昆虫媒介或机械损伤传播。";
            default:
                return "危害特征需要进一步调查确定。";
        }
    }
    
    /**
     * 生成发生规律
     */
    private String generateOccurrencePattern() {
        String[] patterns = {
            "一年发生1-2代，以蛹或幼虫越冬，春季开始活动，夏季为危害高峰期。",
            "一年发生2-3代，成虫在4-5月开始羽化，6-8月为危害盛期。",
            "多年生害虫，无明显世代，全年均可发生，以春夏季危害较重。",
            "病害发生与气候条件密切相关，高温高湿条件下易爆发。"
        };
        return patterns[random.nextInt(patterns.length)];
    }
    
    /**
     * 生成适宜环境条件
     */
    private String generateSuitableEnvironment() {
        String[] environments = {
            "温度20-30℃，相对湿度60-80%，通风不良的环境下易发生。",
            "高温干燥环境下发生较重，温度25-35℃时繁殖最快。",
            "温暖湿润的气候条件，温度15-25℃，湿度70%以上时易爆发。",
            "春季温度回升，雨水充沛时发生严重。"
        };
        return environments[random.nextInt(environments.length)];
    }
    
    /**
     * 生成寄主植物
     */
    private List<String> generateHostPlants(String category) {
        switch (category) {
            case "食叶害虫":
            case "蛀干害虫":
                return Arrays.asList("马尾松", "杉木", "桉树", "杨树", "柳树");
            case "刺吸害虫":
                return Arrays.asList("杨树", "柳树", "桦树", "栎树");
            case "真菌病害":
            case "细菌病害":
            case "病毒病害":
                return Arrays.asList("松树", "杨树", "桦树", "栎树", "樟树");
            default:
                return Arrays.asList("多种林木");
        }
    }
    
    /**
     * 生成分布区域
     */
    private String generateDistributionArea() {
        String[] areas = {
            "华南地区广泛分布",
            "华中、华南地区常见",
            "全国各林区均有分布",
            "主要分布在长江流域及以南地区",
            "华北、华中地区多发"
        };
        return areas[random.nextInt(areas.length)];
    }
    
    /**
     * 生成预防方法
     */
    private String generatePreventionMethods(String category) {
        switch (category) {
            case "食叶害虫":
                return "加强林分管理，及时清除虫害木，生物防治结合化学防治。";
            case "蛀干害虫":
                return "定期检查，及时发现并清除受害木，设置诱捕器监测。";
            case "刺吸害虫":
                return "保护天敌，适时喷洒杀虫剂，加强水肥管理提高抗性。";
            case "真菌病害":
                return "改善通风条件，避免密植，及时清除病残体，喷洒杀菌剂。";
            case "细菌病害":
                return "加强检疫，避免机械损伤，及时处理病株，使用抗生素类药剂。";
            case "病毒病害":
                return "控制传播媒介，清除病株，选用抗病品种。";
            default:
                return "综合防治，预防为主。";
        }
    }
    
    /**
     * 获取随机病虫害ID
     */
    public String getRandomPestId() {
        List<Pest> pests = dataStorage.getPestStorage().findAll();
        if (pests.isEmpty()) {
            return null;
        }
        return pests.get(random.nextInt(pests.size())).getId();
    }
    
    /**
     * 根据类别获取随机病虫害ID
     */
    public String getRandomPestIdByCategory(String category) {
        List<Pest> pests = dataStorage.getPestStorage().findByCategory(category);
        if (pests.isEmpty()) {
            return null;
        }
        return pests.get(random.nextInt(pests.size())).getId();
    }
}