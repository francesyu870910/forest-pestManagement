package com.forestpest.data.factory;

import com.forestpest.entity.Pesticide;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 药剂数据生成器
 */
@Component
public class PesticideDataFactory {
    
    @Autowired
    private DataStorage dataStorage;
    
    private final Random random = new Random();
    
    // 药剂基础数据 [名称, 有效成分, 规格, 类别, 安全等级, 毒性等级, 单位]
    private final String[][] pesticideData = {
        // 杀虫剂
        {"敌敌畏", "敌敌畏80%", "1000ml", "杀虫剂", "中等毒", "中等毒", "毫升"},
        {"氯氰菊酯", "氯氰菊酯10%", "500ml", "杀虫剂", "低毒", "低毒", "毫升"},
        {"吡虫啉", "吡虫啉20%", "250ml", "杀虫剂", "低毒", "微毒", "毫升"},
        {"阿维菌素", "阿维菌素1.8%", "100ml", "杀虫剂", "低毒", "微毒", "毫升"},
        {"马拉硫磷", "马拉硫磷50%", "1000ml", "杀虫剂", "中等毒", "中等毒", "毫升"},
        {"辛硫磷", "辛硫磷50%", "500ml", "杀虫剂", "低毒", "低毒", "毫升"},
        
        // 杀菌剂
        {"多菌灵", "多菌灵50%", "1000g", "杀菌剂", "低毒", "微毒", "克"},
        {"百菌清", "百菌清75%", "500g", "杀菌剂", "低毒", "低毒", "克"},
        {"甲基托布津", "甲基托布津70%", "250g", "杀菌剂", "低毒", "微毒", "克"},
        {"代森锰锌", "代森锰锌80%", "1000g", "杀菌剂", "低毒", "低毒", "克"},
        {"三唑酮", "三唑酮25%", "100g", "杀菌剂", "低毒", "微毒", "克"},
        {"铜制剂", "氢氧化铜77%", "500g", "杀菌剂", "低毒", "微毒", "克"},
        
        // 除草剂
        {"草甘膦", "草甘膦41%", "1000ml", "除草剂", "低毒", "微毒", "毫升"},
        {"2,4-D", "2,4-D丁酯72%", "500ml", "除草剂", "中等毒", "低毒", "毫升"},
        {"百草枯", "百草枯20%", "200ml", "除草剂", "高毒", "中等毒", "毫升"},
        
        // 生物农药
        {"苏云金杆菌", "苏云金杆菌8000IU", "100g", "生物农药", "低毒", "微毒", "克"},
        {"白僵菌", "白僵菌孢子10亿/g", "500g", "生物农药", "低毒", "微毒", "克"},
        {"性信息素", "松毛虫性信息素", "10个", "生物农药", "低毒", "微毒", "个"},
        
        // 植物生长调节剂
        {"萘乙酸", "萘乙酸98%", "50g", "植物生长调节剂", "低毒", "微毒", "克"},
        {"赤霉素", "赤霉素90%", "10g", "植物生长调节剂", "低毒", "微毒", "克"}
    };
    
    private final List<String> suppliers = Arrays.asList(
        "中农化工有限公司", "拜耳作物科学", "先正达农化", "巴斯夫农化", 
        "杜邦农化", "陶氏益农", "华农生物科技", "绿康农药公司"
    );
    
    private final List<String> manufacturers = Arrays.asList(
        "江苏农药厂", "山东化工集团", "河北农化公司", "湖南生物制药",
        "广东农药制造", "浙江化学工业", "四川农化科技", "安徽生物农药"
    );
    
    /**
     * 生成药剂数据
     */
    public void generatePesticides() {
        for (int i = 0; i < pesticideData.length; i++) {
            String[] data = pesticideData[i];
            
            Pesticide pesticide = new Pesticide();
            pesticide.setId(dataStorage.generateId());
            pesticide.setName(data[0]);
            pesticide.setActiveIngredient(data[1]);
            pesticide.setSpecification(data[2]);
            pesticide.setCategory(data[3]);
            pesticide.setSafetyLevel(data[4]);
            pesticide.setToxicityLevel(data[5]);
            pesticide.setUnit(data[6]);
            
            // 生成库存数量
            pesticide.setStockQuantity(generateStockQuantity());
            
            // 生成有效期
            pesticide.setExpiryDate(generateExpiryDate());
            
            // 生成供应商
            pesticide.setSupplier(suppliers.get(random.nextInt(suppliers.size())));
            
            // 生成制造商
            pesticide.setManufacturer(manufacturers.get(random.nextInt(manufacturers.size())));
            
            // 生成单价
            pesticide.setUnitPrice(generateUnitPrice(data[3]));
            
            // 生成最低库存水平
            pesticide.setMinStockLevel(generateMinStockLevel());
            
            // 生成存储条件
            pesticide.setStorageConditions(generateStorageConditions());
            
            // 生成使用说明
            pesticide.setUsageInstructions(generateUsageInstructions(data[3]));
            
            // 生成注册号
            pesticide.setRegistrationNumber(generateRegistrationNumber());
            
            pesticide.setStatus("ACTIVE");
            pesticide.setCreatedBy("system");
            
            dataStorage.getPesticideStorage().save(pesticide);
        }
    }
    
    /**
     * 生成库存数量
     */
    private Integer generateStockQuantity() {
        // 生成0-1000之间的随机库存
        return random.nextInt(1001);
    }
    
    /**
     * 生成有效期
     */
    private LocalDate generateExpiryDate() {
        // 生成未来6个月到3年之间的有效期
        int daysToAdd = 180 + random.nextInt(900); // 180天到1080天
        return LocalDate.now().plusDays(daysToAdd);
    }
    
    /**
     * 生成单价
     */
    private BigDecimal generateUnitPrice(String category) {
        double basePrice;
        switch (category) {
            case "杀虫剂":
                basePrice = 20 + random.nextDouble() * 80; // 20-100元
                break;
            case "杀菌剂":
                basePrice = 15 + random.nextDouble() * 60; // 15-75元
                break;
            case "除草剂":
                basePrice = 10 + random.nextDouble() * 40; // 10-50元
                break;
            case "生物农药":
                basePrice = 30 + random.nextDouble() * 120; // 30-150元
                break;
            case "植物生长调节剂":
                basePrice = 50 + random.nextDouble() * 200; // 50-250元
                break;
            default:
                basePrice = 20 + random.nextDouble() * 80;
        }
        return BigDecimal.valueOf(Math.round(basePrice * 100.0) / 100.0);
    }
    
    /**
     * 生成最低库存水平
     */
    private Integer generateMinStockLevel() {
        return 10 + random.nextInt(41); // 10-50之间
    }
    
    /**
     * 生成存储条件
     */
    private String generateStorageConditions() {
        String[] conditions = {
            "阴凉干燥处保存，避免阳光直射，温度不超过30℃",
            "密封保存，远离火源和热源，避免与食品混放",
            "低温干燥环境，相对湿度不超过75%，通风良好",
            "避光保存，温度5-25℃，防止受潮结块"
        };
        return conditions[random.nextInt(conditions.length)];
    }
    
    /**
     * 生成使用说明
     */
    private String generateUsageInstructions(String category) {
        switch (category) {
            case "杀虫剂":
                return "稀释1000-2000倍喷雾，害虫发生初期使用效果最佳，避免在花期使用。";
            case "杀菌剂":
                return "稀释500-1000倍喷雾，发病前或发病初期使用，间隔7-10天喷施一次。";
            case "除草剂":
                return "按标签用量兑水喷雾，在杂草幼苗期使用，避免药液飘移到作物上。";
            case "生物农药":
                return "按推荐浓度使用，最好在傍晚或阴天施用，可与其他生物农药混用。";
            case "植物生长调节剂":
                return "严格按照推荐浓度使用，过量使用可能产生药害，注意施用时期。";
            default:
                return "按照产品标签说明使用，注意安全防护。";
        }
    }
    
    /**
     * 生成注册号
     */
    private String generateRegistrationNumber() {
        String prefix = "PD";
        int year = 2020 + random.nextInt(4); // 2020-2023
        int number = 1000 + random.nextInt(9000); // 1000-9999
        return prefix + year + "-" + number;
    }
    
    /**
     * 获取随机药剂ID
     */
    public String getRandomPesticideId() {
        List<Pesticide> pesticides = dataStorage.getPesticideStorage().findAll();
        if (pesticides.isEmpty()) {
            return null;
        }
        return pesticides.get(random.nextInt(pesticides.size())).getId();
    }
    
    /**
     * 根据类别获取随机药剂ID
     */
    public String getRandomPesticideIdByCategory(String category) {
        List<Pesticide> pesticides = dataStorage.getPesticideStorage().findByCategory(category);
        if (pesticides.isEmpty()) {
            return null;
        }
        return pesticides.get(random.nextInt(pesticides.size())).getId();
    }
    
    /**
     * 获取多个随机药剂ID
     */
    public List<String> getRandomPesticideIds(int count) {
        List<Pesticide> pesticides = dataStorage.getPesticideStorage().findAll();
        if (pesticides.isEmpty()) {
            return Arrays.asList();
        }
        
        return pesticides.stream()
                .limit(Math.min(count, pesticides.size()))
                .map(Pesticide::getId)
                .collect(java.util.stream.Collectors.toList());
    }
}