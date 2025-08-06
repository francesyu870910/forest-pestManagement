package com.forestpest.data.factory;

import com.forestpest.entity.TreatmentPlan;
import com.forestpest.entity.TreatmentTask;
import com.forestpest.entity.TreatmentMethod;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * 防治方案数据生成器
 */
@Component
public class TreatmentDataFactory {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Autowired
    private PestDataFactory pestDataFactory;
    
    @Autowired
    private PesticideDataFactory pesticideDataFactory;
    
    @Autowired
    private UserDataFactory userDataFactory;
    
    private final Random random = new Random();
    
    private final List<String> priorities = Arrays.asList("低", "中", "高", "紧急");
    private final List<String> statuses = Arrays.asList("草稿", "待审批", "已批准", "执行中", "已完成");
    private final List<String> approvalStatuses = Arrays.asList("待审批", "已批准", "已拒绝");
    private final List<String> taskStatuses = Arrays.asList("待执行", "执行中", "已完成", "已取消");
    
    private final List<String> targetAreas = Arrays.asList(
        "东山林场第一林区", "东山林场第二林区", "西山林场松树区",
        "南山林场杨树区", "北山林场混交林区", "中心林场试验区"
    );
    
    private final List<String> equipment = Arrays.asList(
        "背负式喷雾器", "机动喷雾器", "无人机喷洒设备", "烟雾机",
        "注射器", "诱捕器", "防护服", "呼吸面罩"
    );
    
    /**
     * 生成防治方案数据
     */
    public void generateTreatmentPlans() {
        for (int i = 0; i < 12; i++) {
            String pestId = pestDataFactory.getRandomPestId();
            if (pestId == null) continue;
            
            TreatmentPlan plan = new TreatmentPlan();
            plan.setId(dataStorage.generateId());
            plan.setPestId(pestId);
            plan.setPlanName(generatePlanName(i + 1));
            plan.setDescription(generatePlanDescription());
            
            // 生成防治方法
            plan.setMethods(generateTreatmentMethods());
            
            // 生成所需药剂
            plan.setRequiredPesticides(pesticideDataFactory.getRandomPesticideIds(2 + random.nextInt(3)));
            
            // 生成所需设备
            plan.setRequiredEquipment(generateRequiredEquipment());
            
            // 生成时间安排
            plan.setTimeSchedule(generateTimeSchedule());
            
            // 设置其他属性
            plan.setTargetArea(targetAreas.get(random.nextInt(targetAreas.size())));
            plan.setPriority(priorities.get(random.nextInt(priorities.size())));
            plan.setStatus(statuses.get(random.nextInt(statuses.size())));
            plan.setApprovalStatus(approvalStatuses.get(random.nextInt(approvalStatuses.size())));
            
            // 生成预估成本和工期
            plan.setEstimatedCost(1000.0 + random.nextDouble() * 9000.0);
            plan.setEstimatedDuration(3 + random.nextInt(15)); // 3-17天
            
            // 设置审批人
            if ("已批准".equals(plan.getApprovalStatus())) {
                plan.setApprovedBy(userDataFactory.getRandomAdminUserId());
            }
            
            plan.setNotes(generatePlanNotes());
            plan.setCreatedBy(userDataFactory.getRandomUserId());
            
            dataStorage.getTreatmentStorage().savePlan(plan);
        }
    }
    
    /**
     * 生成防治任务数据
     */
    public void generateTreatmentTasks() {
        List<TreatmentPlan> plans = dataStorage.getTreatmentStorage().findAllPlans();
        
        for (TreatmentPlan plan : plans) {
            // 为每个方案生成1-3个任务
            int taskCount = 1 + random.nextInt(3);
            
            for (int i = 0; i < taskCount; i++) {
                TreatmentTask task = new TreatmentTask();
                task.setId(dataStorage.generateId());
                task.setPlanId(plan.getId());
                task.setTaskName(generateTaskName(plan.getPlanName(), i + 1));
                task.setDescription(generateTaskDescription());
                task.setAssignedTo(userDataFactory.getRandomNormalUserId());
                task.setStatus(taskStatuses.get(random.nextInt(taskStatuses.size())));
                
                // 生成时间
                LocalDateTime baseTime = LocalDateTime.now().plusDays(random.nextInt(30));
                task.setScheduledTime(baseTime);
                
                if ("已完成".equals(task.getStatus()) || "执行中".equals(task.getStatus())) {
                    task.setActualStartTime(baseTime.minusHours(random.nextInt(48)));
                    if ("已完成".equals(task.getStatus())) {
                        task.setActualEndTime(task.getActualStartTime().plusHours(2 + random.nextInt(6)));
                    }
                }
                
                // 设置目标区域
                task.setTargetArea(plan.getTargetArea());
                
                // 设置优先级
                task.setPriority(plan.getPriority());
                
                // 生成实际成本
                if ("已完成".equals(task.getStatus())) {
                    task.setActualCost(500.0 + random.nextDouble() * 2000.0);
                }
                
                // 生成天气信息
                task.setWeather(generateWeatherInfo());
                
                // 生成监督人
                task.setSupervisedBy(userDataFactory.getRandomAdminUserId());
                
                // 生成完成率
                if ("已完成".equals(task.getStatus())) {
                    task.setCompletionRate("100%");
                } else if ("执行中".equals(task.getStatus())) {
                    task.setCompletionRate((30 + random.nextInt(60)) + "%");
                } else {
                    task.setCompletionRate("0%");
                }
                
                // 生成执行记录
                task.setExecutionNotes(generateExecutionNotes(task.getStatus()));
                
                // 生成使用的药剂
                if (plan.getRequiredPesticides() != null && !plan.getRequiredPesticides().isEmpty()) {
                    task.setUsedPesticides(plan.getRequiredPesticides().subList(0, 
                        Math.min(2, plan.getRequiredPesticides().size())));
                }
                
                // 生成照片列表
                task.setPhotos(generateTaskPhotos(task.getId()));
                
                task.setCreatedBy(userDataFactory.getRandomUserId());
                
                dataStorage.getTreatmentStorage().saveTask(task);
            }
        }
    }
    
    /**
     * 生成方案名称
     */
    private String generatePlanName(int index) {
        String[] prefixes = {
            "松毛虫综合防治方案", "天牛防治专项方案", "病害防控应急方案",
            "春季病虫害预防方案", "夏季害虫防治方案", "秋季病害防控方案"
        };
        return prefixes[random.nextInt(prefixes.length)] + "-" + String.format("%03d", index);
    }
    
    /**
     * 生成方案描述
     */
    private String generatePlanDescription() {
        String[] descriptions = {
            "针对当前病虫害发生情况，制定综合防治方案，采用生物防治与化学防治相结合的策略。",
            "根据害虫生活史特点，在关键时期实施精准防治，最大化防治效果。",
            "结合森林生态环境特点，采用环境友好型防治措施，保护生态平衡。",
            "基于病害发生规律，实施预防为主、治疗为辅的防控策略。"
        };
        return descriptions[random.nextInt(descriptions.length)];
    }
    
    /**
     * 生成防治方法
     */
    private List<TreatmentMethod> generateTreatmentMethods() {
        List<TreatmentMethod> methods = new ArrayList<>();
        
        // 物理防治
        TreatmentMethod physicalMethod = new TreatmentMethod();
        physicalMethod.setMethodType("物理防治");
        physicalMethod.setMethodName("物理防治法");
        physicalMethod.setDescription("采用物理手段控制病虫害");
        physicalMethod.setOperationSteps(Arrays.asList(
            "设置诱捕器", "人工捕杀成虫", "清除病虫害枝条", "修剪感病部位"
        ));
        physicalMethod.setDosage("根据害虫密度确定诱捕器数量");
        physicalMethod.setPrecautions("操作时注意安全，及时处理捕获的害虫");
        methods.add(physicalMethod);
        
        // 生物防治
        TreatmentMethod biologicalMethod = new TreatmentMethod();
        biologicalMethod.setMethodType("生物防治");
        biologicalMethod.setMethodName("生物防治法");
        biologicalMethod.setDescription("利用天敌或生物制剂控制病虫害");
        biologicalMethod.setOperationSteps(Arrays.asList(
            "释放天敌昆虫", "喷施生物农药", "使用性信息素干扰", "保护自然天敌"
        ));
        biologicalMethod.setDosage("按产品说明书推荐剂量使用");
        biologicalMethod.setPrecautions("选择适宜的天气条件，避免对天敌造成伤害");
        methods.add(biologicalMethod);
        
        // 化学防治
        TreatmentMethod chemicalMethod = new TreatmentMethod();
        chemicalMethod.setMethodType("化学防治");
        chemicalMethod.setMethodName("化学防治法");
        chemicalMethod.setDescription("使用化学农药快速控制病虫害");
        chemicalMethod.setOperationSteps(Arrays.asList(
            "配制药液", "均匀喷雾", "重点部位处理", "安全间隔期管理"
        ));
        chemicalMethod.setDosage("严格按照农药标签用量使用");
        chemicalMethod.setPrecautions("穿戴防护用品，注意用药安全，遵守安全间隔期");
        methods.add(chemicalMethod);
        
        return methods;
    }
    
    /**
     * 生成所需设备
     */
    private List<String> generateRequiredEquipment() {
        List<String> required = new ArrayList<>();
        int count = 2 + random.nextInt(4); // 2-5个设备
        
        for (int i = 0; i < count; i++) {
            String item = equipment.get(random.nextInt(equipment.size()));
            if (!required.contains(item)) {
                required.add(item);
            }
        }
        
        return required;
    }
    
    /**
     * 生成时间安排
     */
    private String generateTimeSchedule() {
        String[] schedules = {
            "第1周：准备工作和设备检查；第2周：实施物理防治；第3周：生物防治；第4周：化学防治和效果评估",
            "前3天：现场调查和方案准备；第4-7天：集中防治；第8-10天：补充防治；第11-14天：效果监测",
            "分三个阶段实施：预防阶段（1-5天）、治疗阶段（6-12天）、巩固阶段（13-15天）",
            "根据害虫发育进度分期实施：卵期防治（3天）、幼虫期防治（7天）、成虫期防治（5天）"
        };
        return schedules[random.nextInt(schedules.length)];
    }
    
    /**
     * 生成方案备注
     */
    private String generatePlanNotes() {
        String[] notes = {
            "注意天气变化，雨天暂停喷药作业。",
            "加强安全防护，确保操作人员安全。",
            "密切监测防治效果，必要时调整方案。",
            "做好防治记录，为后续评估提供依据。"
        };
        return notes[random.nextInt(notes.length)];
    }
    
    /**
     * 生成任务名称
     */
    private String generateTaskName(String planName, int taskIndex) {
        return planName + "-任务" + taskIndex;
    }
    
    /**
     * 生成任务描述
     */
    private String generateTaskDescription() {
        String[] descriptions = {
            "按照防治方案要求，在指定区域实施病虫害防治作业。",
            "根据害虫发生情况，采用综合防治措施进行控制。",
            "在合适的天气条件下，实施药剂喷洒作业。",
            "配合生物防治措施，进行物理防治作业。"
        };
        return descriptions[random.nextInt(descriptions.length)];
    }
    
    /**
     * 生成天气信息
     */
    private String generateWeatherInfo() {
        String[] weathers = {
            "晴天，微风，适宜作业",
            "多云，无风，条件良好",
            "阴天，湿度适中",
            "小雨，暂停作业",
            "晴天，温度适宜"
        };
        return weathers[random.nextInt(weathers.length)];
    }
    
    /**
     * 生成任务照片
     */
    private List<String> generateTaskPhotos(String taskId) {
        List<String> photos = new ArrayList<>();
        int photoCount = random.nextInt(4); // 0-3张照片
        
        for (int i = 0; i < photoCount; i++) {
            photos.add("/images/tasks/" + taskId + "_" + (i + 1) + ".jpg");
        }
        
        return photos;
    }
    
    /**
     * 生成执行记录
     */
    private String generateExecutionNotes(String status) {
        switch (status) {
            case "已完成":
                return "防治任务已按计划完成，效果良好，害虫密度明显下降。";
            case "执行中":
                return "防治工作正在进行中，已完成第一阶段作业，效果初显。";
            case "待执行":
                return "任务已分配，等待合适天气条件开始执行。";
            case "已取消":
                return "由于天气原因或其他因素，任务已取消，待重新安排。";
            default:
                return "任务状态更新中。";
        }
    }
    
    /**
     * 获取随机防治方案ID
     */
    public String getRandomTreatmentPlanId() {
        List<TreatmentPlan> plans = dataStorage.getTreatmentStorage().findAllPlans();
        if (plans.isEmpty()) {
            return null;
        }
        return plans.get(random.nextInt(plans.size())).getId();
    }
    
    /**
     * 获取随机防治任务ID
     */
    public String getRandomTreatmentTaskId() {
        List<TreatmentTask> tasks = dataStorage.getTreatmentStorage().findAllTasks();
        if (tasks.isEmpty()) {
            return null;
        }
        return tasks.get(random.nextInt(tasks.size())).getId();
    }
}