package com.example.service;

import com.example.annotation.DataSource;
import com.example.annotation.DataSourceType;
import com.example.domain.Order;
import com.example.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务类
 * 演示动态数据源的使用
 */
@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 根据ID查询订单 - 强制使用从库
     */
    @DataSource(DataSourceType.SLAVE)
    public Order getOrderById(Long id) {
        logger.info("查询订单信息，ID: {}", id);
        return orderMapper.selectById(id);
    }

    /**
     * 根据订单号查询订单 - 强制使用从库
     */
    @DataSource(DataSourceType.SLAVE)
    public Order getOrderByOrderNo(String orderNo) {
        logger.info("查询订单信息，订单号: {}", orderNo);
        return orderMapper.selectByOrderNo(orderNo);
    }

    /**
     * 查询所有订单 - 自动判断（使用从库）
     */
    public List<Order> getAllOrders() {
        logger.info("查询所有订单信息");
        return orderMapper.selectAll();
    }

    /**
     * 根据客户ID查询订单 - 自动判断（使用从库）
     */
    public List<Order> getOrdersByCustomerId(Long customerId) {
        logger.info("根据客户ID查询订单，客户ID: {}", customerId);
        return orderMapper.selectByCustomerId(customerId);
    }

    /**
     * 根据状态查询订单 - 自动判断（使用从库）
     */
    public List<Order> getOrdersByStatus(String status) {
        logger.info("根据状态查询订单，状态: {}", status);
        return orderMapper.selectByStatus(status);
    }

    /**
     * 分页查询订单 - 自动判断（使用从库）
     */
    public List<Order> getOrdersByPage(Integer pageNum, Integer pageSize) {
        logger.info("分页查询订单，页码: {}, 页大小: {}", pageNum, pageSize);
        int offset = (pageNum - 1) * pageSize;
        return orderMapper.selectByPage(offset, pageSize);
    }

    /**
     * 统计订单总数 - 自动判断（使用从库）
     */
    public int getTotalCount() {
        logger.info("统计订单总数");
        return orderMapper.countAll();
    }

    /**
     * 根据状态统计订单数 - 自动判断（使用从库）
     */
    public int getCountByStatus(String status) {
        logger.info("根据状态统计订单数，状态: {}", status);
        return orderMapper.countByStatus(status);
    }

    /**
     * 创建订单 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int createOrder(Order order) {
        logger.info("创建订单: {}", order.getOrderNo());
        
        if (order == null) {
            throw new IllegalArgumentException("订单信息不能为空");
        }
        
        // 检查订单号是否已存在
        Order existingOrder = orderMapper.selectByOrderNo(order.getOrderNo());
        if (existingOrder != null) {
            throw new IllegalArgumentException("订单号已存在: " + order.getOrderNo());
        }
        
        // 设置默认值
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.insert(order);
    }

    /**
     * 更新订单 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int updateOrder(Order order) {
        logger.info("更新订单: {}", order.getId());
        
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateByIdSelective(order);
    }

    /**
     * 删除订单 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int deleteOrder(Long id) {
        logger.info("删除订单: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        return orderMapper.deleteById(id);
    }

    /**
     * 支付订单 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int payOrder(Long id, String paymentMethod) {
        logger.info("支付订单: {}, 支付方式: {}", id, paymentMethod);
        
        Order order = new Order();
        order.setId(id);
        order.setStatus("PAID");
        order.setPaymentMethod(paymentMethod);
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateByIdSelective(order);
    }

    /**
     * 检查订单号是否存在 - 自动判断（使用从库）
     */
    public boolean isOrderNoExists(String orderNo) {
        logger.info("检查订单号是否存在: {}", orderNo);
        Order order = orderMapper.selectByOrderNo(orderNo);
        return order != null;
    }
}
