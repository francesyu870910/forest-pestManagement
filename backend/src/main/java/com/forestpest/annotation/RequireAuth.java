package com.forestpest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要认证注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
    
    /**
     * 是否需要认证，默认为true
     */
    boolean value() default true;
    
    /**
     * 需要的角色
     */
    String[] roles() default {};
    
    /**
     * 需要的权限
     */
    String[] permissions() default {};
}