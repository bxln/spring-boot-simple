package com.example.controller;

import com.example.primary.domain.User;
import com.example.primary.service.UserService;
import com.example.secondary.domain.Product;
import com.example.secondary.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多数据源演示控制器
 * 展示如何在同一个接口中使用多个数据源
 */
@RestController
@RequestMapping("/api/multi-datasource")
public class MultiDataSourceController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    /**
     * 获取系统概览信息
     * 同时从主数据源和从数据源获取数据
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 从主数据源获取用户统计信息
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalUsers", userService.getTotalCount());
        userStats.put("activeUsers", userService.getCountByStatus("ACTIVE"));
        userStats.put("inactiveUsers", userService.getCountByStatus("INACTIVE"));
        
        // 从从数据源获取产品统计信息
        Map<String, Object> productStats = new HashMap<>();
        productStats.put("totalProducts", productService.getTotalCount());
        productStats.put("activeProducts", productService.getCountByStatus("ACTIVE"));
        productStats.put("inactiveProducts", productService.getCountByStatus("INACTIVE"));
        
        overview.put("userStatistics", userStats);
        overview.put("productStatistics", productStats);
        overview.put("message", "多数据源统计信息获取成功");
        
        return ResponseEntity.ok(overview);
    }

    /**
     * 获取最新数据
     * 从两个数据源分别获取最新的用户和产品信息
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestData() {
        Map<String, Object> latestData = new HashMap<>();
        
        // 从主数据源获取最新用户（前5个）
        List<User> latestUsers = userService.getUsersByPage(1, 5);
        
        // 从从数据源获取最新产品（前5个）
        List<Product> latestProducts = productService.getProductsByPage(1, 5);
        
        latestData.put("latestUsers", latestUsers);
        latestData.put("latestProducts", latestProducts);
        latestData.put("message", "最新数据获取成功");
        
        return ResponseEntity.ok(latestData);
    }

    /**
     * 数据源健康检查
     * 检查两个数据源的连接状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkDataSourceHealth() {
        Map<String, Object> healthStatus = new HashMap<>();
        
        try {
            // 检查主数据源
            int userCount = userService.getTotalCount();
            Map<String, Object> primaryStatus = new HashMap<>();
            primaryStatus.put("status", "UP");
            primaryStatus.put("totalRecords", userCount);
            primaryStatus.put("message", "主数据源连接正常");
            healthStatus.put("primaryDataSource", primaryStatus);
        } catch (Exception e) {
            Map<String, Object> primaryStatus = new HashMap<>();
            primaryStatus.put("status", "DOWN");
            primaryStatus.put("error", e.getMessage());
            primaryStatus.put("message", "主数据源连接异常");
            healthStatus.put("primaryDataSource", primaryStatus);
        }
        
        try {
            // 检查从数据源
            int productCount = productService.getTotalCount();
            Map<String, Object> secondaryStatus = new HashMap<>();
            secondaryStatus.put("status", "UP");
            secondaryStatus.put("totalRecords", productCount);
            secondaryStatus.put("message", "从数据源连接正常");
            healthStatus.put("secondaryDataSource", secondaryStatus);
        } catch (Exception e) {
            Map<String, Object> secondaryStatus = new HashMap<>();
            secondaryStatus.put("status", "DOWN");
            secondaryStatus.put("error", e.getMessage());
            secondaryStatus.put("message", "从数据源连接异常");
            healthStatus.put("secondaryDataSource", secondaryStatus);
        }
        
        return ResponseEntity.ok(healthStatus);
    }

    /**
     * 获取数据源配置信息
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getDataSourceConfig() {
        Map<String, Object> config = new HashMap<>();
        
        Map<String, Object> primaryConfig = new HashMap<>();
        primaryConfig.put("name", "主数据源 (Primary)");
        primaryConfig.put("description", "用于存储用户相关数据");
        primaryConfig.put("database", "primary_db");
        primaryConfig.put("entities", "User");
        primaryConfig.put("mapperPackage", "com.example.primary.mapper");
        primaryConfig.put("xmlLocation", "classpath:mapper/primary/*.xml");
        
        Map<String, Object> secondaryConfig = new HashMap<>();
        secondaryConfig.put("name", "从数据源 (Secondary)");
        secondaryConfig.put("description", "用于存储产品相关数据");
        secondaryConfig.put("database", "secondary_db");
        secondaryConfig.put("entities", "Product");
        secondaryConfig.put("mapperPackage", "com.example.secondary.mapper");
        secondaryConfig.put("xmlLocation", "classpath:mapper/secondary/*.xml");
        
        config.put("primaryDataSource", primaryConfig);
        config.put("secondaryDataSource", secondaryConfig);
        config.put("message", "多数据源配置信息");
        
        return ResponseEntity.ok(config);
    }
}
