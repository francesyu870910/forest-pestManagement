package com.forestpest.repository;

import java.util.List;
import java.util.Optional;

/**
 * 基础Repository接口
 * 定义通用的CRUD操作
 */
public interface BaseRepository<T, ID> {
    
    /**
     * 保存实体
     */
    T save(T entity);
    
    /**
     * 根据ID查找实体
     */
    Optional<T> findById(ID id);
    
    /**
     * 查找所有实体
     */
    List<T> findAll();
    
    /**
     * 根据ID删除实体
     */
    void deleteById(ID id);
    
    /**
     * 删除实体
     */
    void delete(T entity);
    
    /**
     * 检查实体是否存在
     */
    boolean existsById(ID id);
    
    /**
     * 统计实体数量
     */
    long count();
    
    /**
     * 批量保存
     */
    List<T> saveAll(Iterable<T> entities);
    
    /**
     * 批量删除
     */
    void deleteAll(Iterable<T> entities);
    
    /**
     * 清空所有数据
     */
    void deleteAll();
}