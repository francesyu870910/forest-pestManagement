package com.forestpest.data.factory;

import com.forestpest.entity.User;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 用户数据生成器
 */
@Component
public class UserDataFactory {
    
    @Autowired
    private DataStorage dataStorage;
    
    private final Random random = new Random();
    
    private final List<String> departments = Arrays.asList(
        "森林保护科", "病虫害防治科", "森林资源管理科", "技术推广科", "综合管理科"
    );
    
    private final List<String> adminNames = Arrays.asList(
        "张管理员", "李系统管理员"
    );
    
    private final List<String> userNames = Arrays.asList(
        "王森林", "刘护林", "陈防治", "杨技术", "赵专家", "孙调查", "周监测", "吴分析"
    );
    
    /**
     * 生成用户数据
     */
    public void generateUsers() {
        // 生成管理员用户
        generateAdminUsers();
        
        // 生成普通用户
        generateNormalUsers();
    }
    
    /**
     * 生成管理员用户
     */
    private void generateAdminUsers() {
        for (int i = 0; i < 2; i++) {
            User admin = new User();
            admin.setId(dataStorage.generateId());
            admin.setUsername("admin" + (i + 1));
            admin.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jskrjkq7Ry0K"); // 加密后的 "123456"
            admin.setEmail("admin" + (i + 1) + "@forestpest.com");
            admin.setRealName(adminNames.get(i));
            admin.setRole("ADMIN");
            admin.setStatus("ACTIVE");
            admin.setPhone("138" + String.format("%08d", random.nextInt(100000000)));
            admin.setDepartment(departments.get(random.nextInt(departments.size())));
            admin.setCreatedBy("system");
            admin.setLastLoginTime(LocalDateTime.now().minusDays(random.nextInt(7)));
            
            dataStorage.getUserStorage().save(admin);
        }
    }
    
    /**
     * 生成普通用户
     */
    private void generateNormalUsers() {
        for (int i = 0; i < 8; i++) {
            User user = new User();
            user.setId(dataStorage.generateId());
            user.setUsername("user" + (i + 1));
            user.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jskrjkq7Ry0K"); // 加密后的 "123456"
            user.setEmail("user" + (i + 1) + "@forestpest.com");
            user.setRealName(userNames.get(i));
            user.setRole("USER");
            user.setStatus("ACTIVE");
            user.setPhone("139" + String.format("%08d", random.nextInt(100000000)));
            user.setDepartment(departments.get(random.nextInt(departments.size())));
            user.setCreatedBy("admin1");
            user.setLastLoginTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            dataStorage.getUserStorage().save(user);
        }
    }
    
    /**
     * 获取随机用户ID
     */
    public String getRandomUserId() {
        List<User> users = dataStorage.getUserStorage().findAll();
        if (users.isEmpty()) {
            return null;
        }
        return users.get(random.nextInt(users.size())).getId();
    }
    
    /**
     * 获取随机管理员用户ID
     */
    public String getRandomAdminUserId() {
        List<User> admins = dataStorage.getUserStorage().findByRole("ADMIN");
        if (admins.isEmpty()) {
            return null;
        }
        return admins.get(random.nextInt(admins.size())).getId();
    }
    
    /**
     * 获取随机普通用户ID
     */
    public String getRandomNormalUserId() {
        List<User> users = dataStorage.getUserStorage().findByRole("USER");
        if (users.isEmpty()) {
            return null;
        }
        return users.get(random.nextInt(users.size())).getId();
    }
}