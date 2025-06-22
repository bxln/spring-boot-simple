package com.example.aspect;

import com.example.annotation.DataSource;
import com.example.annotation.DataSourceType;
import com.example.config.DataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切换AOP切面
 * 
 * 功能：
 * 1. 拦截带有@DataSource注解的方法
 * 2. 根据注解配置或方法名自动切换数据源
 * 3. 方法执行完成后自动清理数据源上下文
 */
@Aspect
@Component
@Order(1) // 确保在事务切面之前执行
public class DataSourceAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);
    
    /**
     * 切点：拦截所有带有@DataSource注解的方法
     */
    @Pointcut("@annotation(com.example.annotation.DataSource)")
    public void dataSourcePointcut() {}
    
    /**
     * 切点：拦截所有Mapper接口的方法（自动判断数据源）
     */
    @Pointcut("execution(* com.example.mapper..*.*(..))")
    public void mapperPointcut() {}
    
    /**
     * 环绕通知：处理数据源切换
     */
    @Around("dataSourcePointcut() || mapperPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String className = point.getTarget().getClass().getSimpleName();
        
        logger.debug("执行方法: {}.{}", className, methodName);
        
        DataSourceType dataSourceType = determineDataSourceType(method, methodName);
        
        // 设置数据源
        DataSourceContextHolder.setDataSourceType(dataSourceType);
        
        try {
            logger.debug("开始执行方法: {}.{}, 使用数据源: {}", 
                        className, methodName, dataSourceType.getValue());
            
            // 执行目标方法
            Object result = point.proceed();
            
            logger.debug("方法执行完成: {}.{}", className, methodName);
            return result;
            
        } catch (Exception e) {
            logger.error("方法执行异常: {}.{}, 错误: {}", className, methodName, e.getMessage());
            throw e;
        } finally {
            // 清理数据源上下文
            DataSourceContextHolder.clearDataSourceType();
            logger.debug("清理数据源上下文: {}.{}", className, methodName);
        }
    }
    
    /**
     * 确定数据源类型
     */
    private DataSourceType determineDataSourceType(Method method, String methodName) {
        // 1. 首先检查方法级别的@DataSource注解
        DataSource dataSource = AnnotationUtils.findAnnotation(method, DataSource.class);
        
        // 2. 如果方法上没有，检查类级别的@DataSource注解
        if (dataSource == null) {
            dataSource = AnnotationUtils.findAnnotation(method.getDeclaringClass(), DataSource.class);
        }
        
        // 3. 如果有注解，根据注解配置决定数据源
        if (dataSource != null) {
            DataSourceType annotationType = dataSource.value();
            
            if (annotationType == DataSourceType.AUTO) {
                // 自动判断
                DataSourceType autoType = DataSourceType.getByMethodName(methodName);
                logger.debug("自动判断数据源类型: 方法名={}, 数据源={}", methodName, autoType.getValue());
                return autoType;
            } else {
                // 使用注解指定的数据源
                logger.debug("使用注解指定的数据源: {}", annotationType.getValue());
                return annotationType;
            }
        }
        
        // 4. 如果没有注解，根据方法名自动判断
        DataSourceType autoType = DataSourceType.getByMethodName(methodName);
        logger.debug("根据方法名自动判断数据源: 方法名={}, 数据源={}", methodName, autoType.getValue());
        return autoType;
    }
}
