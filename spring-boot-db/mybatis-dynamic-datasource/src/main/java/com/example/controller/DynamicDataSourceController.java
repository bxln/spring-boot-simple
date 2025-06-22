package com.example.controller;

import com.example.annotation.DataSource;
import com.example.annotation.DataSourceType;
import com.example.service.CustomerService;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源演示控制器
 * 展示如何在同一个接口中使用不同的数据源
 */
@RestController
@RequestMapping("/api/dynamic-datasource")
public class DynamicDataSourceController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    /**
     * 获取系统概览信息
     * 演示同时从主库和从库获取数据
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 从从库获取统计信息（读操作）
        Map<String, Object> readStats = new HashMap<>();
        readStats.put("totalCustomers", customerService.getTotalCount());
        readStats.put("activeCustomers", customerService.getCountByStatus("ACTIVE"));
        readStats.put("totalOrders", orderService.getTotalCount());
        readStats.put("pendingOrders", orderService.getCountByStatus("PENDING"));
        readStats.put("dataSource", "slave");
        
        overview.put("statistics", readStats);
        overview.put("message", "动态数据源概览信息获取成功");
        overview.put("note", "统计数据来自从库（读库）");
        
        return ResponseEntity.ok(overview);
    }

    /**
     * 强制使用主库查询
     * 演示强制指定数据源
     */
    @DataSource(DataSourceType.MASTER)
    @GetMapping("/master-query")
    public ResponseEntity<Map<String, Object>> getMasterQuery() {
        Map<String, Object> result = new HashMap<>();
        
        // 这些查询操作将强制使用主库
        int customerCount = customerService.getTotalCount();
        int orderCount = orderService.getTotalCount();
        
        result.put("customerCount", customerCount);
        result.put("orderCount", orderCount);
        result.put("dataSource", "master");
        result.put("message", "强制使用主库查询数据");
        result.put("note", "即使是查询操作，也使用了主库");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 强制使用从库查询
     * 演示强制指定数据源
     */
    @DataSource(DataSourceType.SLAVE)
    @GetMapping("/slave-query")
    public ResponseEntity<Map<String, Object>> getSlaveQuery() {
        Map<String, Object> result = new HashMap<>();
        
        // 这些查询操作将强制使用从库
        int customerCount = customerService.getTotalCount();
        int orderCount = orderService.getTotalCount();
        
        result.put("customerCount", customerCount);
        result.put("orderCount", orderCount);
        result.put("dataSource", "slave");
        result.put("message", "强制使用从库查询数据");
        result.put("note", "查询操作使用从库，提高读性能");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 自动判断数据源
     * 演示根据方法名自动选择数据源
     */
    @GetMapping("/auto-datasource")
    public ResponseEntity<Map<String, Object>> getAutoDataSource() {
        Map<String, Object> result = new HashMap<>();
        
        // 这些方法将根据方法名自动选择数据源
        // select、get、find等开头的方法使用从库
        int customerCount = customerService.getTotalCount(); // 自动使用从库
        int orderCount = orderService.getTotalCount(); // 自动使用从库
        
        result.put("customerCount", customerCount);
        result.put("orderCount", orderCount);
        result.put("dataSource", "auto -> slave");
        result.put("message", "根据方法名自动选择数据源");
        result.put("note", "getTotalCount方法自动使用从库");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 数据源健康检查
     * 检查主库和从库的连接状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkDataSourceHealth() {
        Map<String, Object> healthStatus = new HashMap<>();
        
        try {
            // 检查主库连接
            Map<String, Object> masterStatus = checkMasterHealth();
            healthStatus.put("masterDataSource", masterStatus);
        } catch (Exception e) {
            Map<String, Object> masterStatus = new HashMap<>();
            masterStatus.put("status", "DOWN");
            masterStatus.put("error", e.getMessage());
            masterStatus.put("message", "主库连接异常");
            healthStatus.put("masterDataSource", masterStatus);
        }
        
        try {
            // 检查从库连接
            Map<String, Object> slaveStatus = checkSlaveHealth();
            healthStatus.put("slaveDataSource", slaveStatus);
        } catch (Exception e) {
            Map<String, Object> slaveStatus = new HashMap<>();
            slaveStatus.put("status", "DOWN");
            slaveStatus.put("error", e.getMessage());
            slaveStatus.put("message", "从库连接异常");
            healthStatus.put("slaveDataSource", slaveStatus);
        }
        
        return ResponseEntity.ok(healthStatus);
    }

    /**
     * 检查主库健康状态
     */
    @DataSource(DataSourceType.MASTER)
    private Map<String, Object> checkMasterHealth() {
        Map<String, Object> status = new HashMap<>();
        
        // 执行简单查询测试主库连接
        int customerCount = customerService.getTotalCount();
        
        status.put("status", "UP");
        status.put("totalRecords", customerCount);
        status.put("message", "主库连接正常");
        status.put("dataSource", "master");
        
        return status;
    }

    /**
     * 检查从库健康状态
     */
    @DataSource(DataSourceType.SLAVE)
    private Map<String, Object> checkSlaveHealth() {
        Map<String, Object> status = new HashMap<>();
        
        // 执行简单查询测试从库连接
        int orderCount = orderService.getTotalCount();
        
        status.put("status", "UP");
        status.put("totalRecords", orderCount);
        status.put("message", "从库连接正常");
        status.put("dataSource", "slave");
        
        return status;
    }

    /**
     * 获取动态数据源配置信息
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getDataSourceConfig() {
        Map<String, Object> config = new HashMap<>();
        
        Map<String, Object> masterConfig = new HashMap<>();
        masterConfig.put("name", "主数据源 (Master)");
        masterConfig.put("description", "用于写操作：增删改");
        masterConfig.put("database", "master_db");
        masterConfig.put("usage", "insert, update, delete operations");
        masterConfig.put("features", new String[]{"写操作", "事务一致性", "实时数据"});
        
        Map<String, Object> slaveConfig = new HashMap<>();
        slaveConfig.put("name", "从数据源 (Slave)");
        slaveConfig.put("description", "用于读操作：查询统计");
        slaveConfig.put("database", "slave_db");
        slaveConfig.put("usage", "select, count, query operations");
        slaveConfig.put("features", new String[]{"读操作", "读写分离", "性能优化"});
        
        Map<String, Object> routingRules = new HashMap<>();
        routingRules.put("自动路由", "根据方法名自动选择数据源");
        routingRules.put("强制指定", "通过@DataSource注解强制指定");
        routingRules.put("读操作", "select、get、find、query、count等方法使用从库");
        routingRules.put("写操作", "insert、update、delete、save等方法使用主库");
        
        config.put("masterDataSource", masterConfig);
        config.put("slaveDataSource", slaveConfig);
        config.put("routingRules", routingRules);
        config.put("implementation", "基于AbstractRoutingDataSource + AOP切面");
        config.put("message", "动态数据源配置信息");
        
        return ResponseEntity.ok(config);
    }

    /**
     * 演示数据源切换性能
     */
    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceDemo() {
        Map<String, Object> performance = new HashMap<>();
        
        long startTime, endTime;
        
        // 测试从库查询性能
        startTime = System.currentTimeMillis();
        int slaveCustomerCount = customerService.getTotalCount(); // 自动使用从库
        endTime = System.currentTimeMillis();
        long slaveQueryTime = endTime - startTime;
        
        // 测试主库查询性能
        startTime = System.currentTimeMillis();
        int masterCustomerCount = getMasterCustomerCount(); // 强制使用主库
        endTime = System.currentTimeMillis();
        long masterQueryTime = endTime - startTime;
        
        Map<String, Object> slavePerf = new HashMap<>();
        slavePerf.put("dataSource", "slave");
        slavePerf.put("queryTime", slaveQueryTime + "ms");
        slavePerf.put("recordCount", slaveCustomerCount);
        slavePerf.put("description", "从库查询性能");
        
        Map<String, Object> masterPerf = new HashMap<>();
        masterPerf.put("dataSource", "master");
        masterPerf.put("queryTime", masterQueryTime + "ms");
        masterPerf.put("recordCount", masterCustomerCount);
        masterPerf.put("description", "主库查询性能");
        
        performance.put("slavePerformance", slavePerf);
        performance.put("masterPerformance", masterPerf);
        performance.put("note", "读写分离可以提高查询性能，减轻主库压力");
        
        return ResponseEntity.ok(performance);
    }

    /**
     * 强制使用主库查询客户数量
     */
    @DataSource(DataSourceType.MASTER)
    private int getMasterCustomerCount() {
        return customerService.getTotalCount();
    }
}
