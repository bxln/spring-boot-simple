package com.example.controller;

import com.example.domain.Order;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 演示动态数据源的读写分离
 */
@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 根据ID查询订单
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/orderNo/{orderNo}")
    public ResponseEntity<Order> getOrderByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 查询所有订单
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据客户ID查询订单
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据状态查询订单
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * 分页查询订单
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getOrdersByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Order> orders = orderService.getOrdersByPage(pageNum, pageSize);
        int totalCount = orderService.getTotalCount();
        
        Map<String, Object> result = new HashMap<>();
        result.put("orders", orders);
        result.put("totalCount", totalCount);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
        result.put("dataSource", "slave"); // 查询操作使用从库
        
        return ResponseEntity.ok(result);
    }

    /**
     * 统计订单总数
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getTotalCount() {
        int count = orderService.getTotalCount();
        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", count);
        response.put("dataSource", "slave");
        return ResponseEntity.ok(response);
    }

    /**
     * 根据状态统计订单数
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Object>> getCountByStatus(@PathVariable String status) {
        int count = orderService.getCountByStatus(status);
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("status", status);
        response.put("dataSource", "slave");
        return ResponseEntity.ok(response);
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody Order order) {
        try {
            int result = orderService.createOrder(order);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "订单创建成功" : "订单创建失败");
            response.put("order", order);
            response.put("dataSource", "master"); // 写操作使用主库
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(@PathVariable Long id, 
                                                          @Valid @RequestBody Order order) {
        try {
            order.setId(id);
            int result = orderService.updateOrder(order);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "订单更新成功" : "订单更新失败");
            response.put("dataSource", "master");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long id) {
        try {
            int result = orderService.deleteOrder(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "订单删除成功" : "订单删除失败");
            response.put("dataSource", "master");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 支付订单
     */
    @PutMapping("/{id}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(@PathVariable Long id, 
                                                       @RequestParam String paymentMethod) {
        try {
            int result = orderService.payOrder(id, paymentMethod);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "订单支付成功" : "订单支付失败");
            response.put("orderId", id);
            response.put("paymentMethod", paymentMethod);
            response.put("dataSource", "master");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 检查订单号是否存在
     */
    @GetMapping("/exists/{orderNo}")
    public ResponseEntity<Map<String, Object>> checkOrderNoExists(@PathVariable String orderNo) {
        boolean exists = orderService.isOrderNoExists(orderNo);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("orderNo", orderNo);
        response.put("dataSource", "slave"); // 查询操作使用从库
        return ResponseEntity.ok(response);
    }
}
