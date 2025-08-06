package com.forestpest.repository.impl;

import com.forestpest.entity.User;
import com.forestpest.repository.UserRepository;
import com.forestpest.data.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户数据访问实现类
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    
    @Autowired
    private DataStorage dataStorage;
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(dataStorage.generateId());
        }
        user.setUpdatedTime(LocalDateTime.now());
        dataStorage.getUserStorage().save(user);
        return user;
    }
    
    @Override
    public Optional<User> findById(String id) {
        return dataStorage.getUserStorage().findById(id);
    }
    
    @Override
    public List<User> findAll() {
        return dataStorage.getUserStorage().findAll();
    }
    
    @Override
    public void deleteById(String id) {
        dataStorage.getUserStorage().deleteById(id);
    }
    
    @Override
    public void delete(User user) {
        if (user != null && user.getId() != null) {
            deleteById(user.getId());
        }
    }
    
    @Override
    public boolean existsById(String id) {
        return dataStorage.getUserStorage().findById(id).isPresent();
    }
    
    @Override
    public long count() {
        return dataStorage.getUserStorage().count();
    }
    
    @Override
    public List<User> saveAll(Iterable<User> users) {
        List<User> savedUsers = new java.util.ArrayList<>();
        for (User user : users) {
            savedUsers.add(save(user));
        }
        return savedUsers;
    }
    
    @Override
    public void deleteAll(Iterable<User> users) {
        for (User user : users) {
            delete(user);
        }
    }
    
    @Override
    public void deleteAll() {
        dataStorage.getUserStorage().clear();
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return dataStorage.getUserStorage().findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return dataStorage.getUserStorage().findByEmail(email);
    }
    
    @Override
    public List<User> findByRole(String role) {
        return dataStorage.getUserStorage().findByRole(role);
    }
    
    @Override
    public List<User> findByStatus(String status) {
        return findAll().stream()
                .filter(user -> status.equals(user.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findByDepartment(String department) {
        return findAll().stream()
                .filter(user -> department.equals(user.getDepartment()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return dataStorage.getUserStorage().existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return dataStorage.getUserStorage().existsByEmail(email);
    }
    
    @Override
    public List<User> findByRealNameContaining(String realName) {
        return findAll().stream()
                .filter(user -> user.getRealName() != null && user.getRealName().contains(realName))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findByRoleAndStatus(String role, String status) {
        return findAll().stream()
                .filter(user -> role.equals(user.getRole()) && status.equals(user.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findActiveUsers() {
        return findByStatus("ACTIVE");
    }
    
    @Override
    public List<User> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return findAll().stream()
                .filter(user -> user.getCreatedTime() != null &&
                               !user.getCreatedTime().isBefore(startTime) &&
                               !user.getCreatedTime().isAfter(endTime))
                .collect(Collectors.toList());
    }
}