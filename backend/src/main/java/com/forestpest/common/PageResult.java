package com.forestpest.common;

import java.util.List;

/**
 * 分页结果类
 */
public class PageResult<T> {
    
    private List<T> list;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    
    public PageResult() {
    }
    
    public PageResult(List<T> list, Long total, Integer page, Integer size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
    }
    
    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> list, Long total, Integer page, Integer size) {
        return new PageResult<>(list, total, page, size);
    }
    
    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty(Integer page, Integer size) {
        return new PageResult<>(List.of(), 0L, page, size);
    }
    
    // Getter and Setter methods
    public List<T> getList() {
        return list;
    }
    
    public void setList(List<T> list) {
        this.list = list;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    @Override
    public String toString() {
        return "PageResult{" +
                "list=" + list +
                ", total=" + total +
                ", page=" + page +
                ", size=" + size +
                ", totalPages=" + totalPages +
                '}';
    }
}