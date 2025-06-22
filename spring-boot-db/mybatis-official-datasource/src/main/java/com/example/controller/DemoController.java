package com.example.controller;

import com.example.primary.entity.User;
import com.example.primary.mapper.UserMapper;
import com.example.secondary.entity.Order;
import com.example.secondary.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多数据源演示控制器
 * 展示如何在同一个应用中使用两个不同的数据源
 * 
 * @author example
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 系统概览信息
     * 展示两个数据源的基本统计信息
     */
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 主数据源统计
        Map<String, Object> primaryStats = new HashMap<>();
        primaryStats.put("totalUsers", userMapper.count());
        primaryStats.put("activeUsers", userMapper.countByStatus(1));
        primaryStats.put("disabledUsers", userMapper.countByStatus(0));
        
        // 第二数据源统计
        Map<String, Object> secondaryStats = new HashMap<>();
        secondaryStats.put("totalOrders", orderMapper.count());
        secondaryStats.put("pendingOrders", orderMapper.countByStatus(1));
        secondaryStats.put("completedOrders", orderMapper.countByStatus(4));
        
        result.put("primaryDataSource", primaryStats);
        result.put("secondaryDataSource", secondaryStats);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * 用户管理接口 - 主数据源
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userMapper.selectById(id);
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(1);
        
        int result = userMapper.insert(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result > 0);
        response.put("message", result > 0 ? "用户创建成功" : "用户创建失败");
        response.put("user", user);
        
        return response;
    }

    /**
     * 订单管理接口 - 第二数据源
     */
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderMapper.selectAll();
    }

    @GetMapping("/orders/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderMapper.selectById(id);
    }

    @GetMapping("/orders/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        return orderMapper.selectByUserId(userId);
    }

    @PostMapping("/orders")
    public Map<String, Object> createOrder(@RequestBody Order order) {
        // 生成订单号
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setStatus(1); // 待支付
        
        // 计算总金额
        if (order.getPrice() != null && order.getQuantity() != null) {
            order.setTotalAmount(order.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        }
        
        int result = orderMapper.insert(order);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result > 0);
        response.put("message", result > 0 ? "订单创建成功" : "订单创建失败");
        response.put("order", order);
        
        return response;
    }

    /**
     * 跨数据源查询演示
     * 查询用户及其订单信息
     */
    @GetMapping("/user-orders/{userId}")
    public Map<String, Object> getUserWithOrders(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 从主数据源查询用户信息
        User user = userMapper.selectById(userId);
        
        // 从第二数据源查询用户的订单信息
        List<Order> orders = orderMapper.selectByUserId(userId);
        long orderCount = orderMapper.countByUserId(userId);
        BigDecimal totalAmount = orderMapper.sumAmountByUserId(userId);
        
        result.put("user", user);
        result.put("orders", orders);
        result.put("orderCount", orderCount);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        return result;
    }

    /**
     * 数据源健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查主数据源
            long userCount = userMapper.count();
            result.put("primaryDataSource", "healthy");
            result.put("userCount", userCount);
        } catch (Exception e) {
            result.put("primaryDataSource", "error: " + e.getMessage());
        }
        
        try {
            // 检查第二数据源
            long orderCount = orderMapper.count();
            result.put("secondaryDataSource", "healthy");
            result.put("orderCount", orderCount);
        } catch (Exception e) {
            result.put("secondaryDataSource", "error: " + e.getMessage());
        }
        
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }
}
