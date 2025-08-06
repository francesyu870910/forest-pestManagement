package com.forestpest.data.storage;

import com.forestpest.entity.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 用户数据存储
 */
@Component
public class UserStorage {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToId = new ConcurrentHashMap<>();
    private final Map<String, String> emailToId = new ConcurrentHashMap<>();
    
    public void save(User user) {
        users.put(user.getId(), user);
        usernameToId.put(user.getUsername(), user.getId());
        emailToId.put(user.getEmail(), user.getId());
    }
    
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
    
    public Optional<User> findByUsername(String username) {
        String id = usernameToId.get(username);
        return id != null ? Optional.ofNullable(users.get(id)) : Optional.empty();
    }
    
    public Optional<User> findByEmail(String email) {
        String id = emailToId.get(email);
        return id != null ? Optional.ofNullable(users.get(id)) : Optional.empty();
    }
    
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    public List<User> findByRole(String role) {
        return users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public boolean existsByUsername(String username) {
        return usernameToId.containsKey(username);
    }
    
    public boolean existsByEmail(String email) {
        return emailToId.containsKey(email);
    }
    
    public void deleteById(String id) {
        User user = users.remove(id);
        if (user != null) {
            usernameToId.remove(user.getUsername());
            emailToId.remove(user.getEmail());
        }
    }
    
    public void clear() {
        users.clear();
        usernameToId.clear();
        emailToId.clear();
    }
    
    public int count() {
        return users.size();
    }
}