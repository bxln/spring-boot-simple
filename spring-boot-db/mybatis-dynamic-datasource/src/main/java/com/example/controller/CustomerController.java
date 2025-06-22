package com.example.controller;

import com.example.domain.Customer;
import com.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户控制器
 * 演示动态数据源的读写分离
 */
@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 根据ID查询客户
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据客户编码查询客户
     */
    @GetMapping("/code/{customerCode}")
    public ResponseEntity<Customer> getCustomerByCode(@PathVariable String customerCode) {
        Customer customer = customerService.getCustomerByCode(customerCode);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 查询所有客户
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据状态查询客户
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Customer>> getCustomersByStatus(@PathVariable String status) {
        List<Customer> customers = customerService.getCustomersByStatus(status);
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据年龄范围查询客户
     */
    @GetMapping("/age-range")
    public ResponseEntity<List<Customer>> getCustomersByAgeRange(
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {
        List<Customer> customers = customerService.getCustomersByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(customers);
    }

    /**
     * 分页查询客户
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getCustomersByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Customer> customers = customerService.getCustomersByPage(pageNum, pageSize);
        int totalCount = customerService.getTotalCount();
        
        Map<String, Object> result = new HashMap<>();
        result.put("customers", customers);
        result.put("totalCount", totalCount);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
        
        return ResponseEntity.ok(result);
    }

    /**
     * 统计客户总数
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalCount() {
        int count = customerService.getTotalCount();
        return ResponseEntity.ok(count);
    }

    /**
     * 根据状态统计客户数
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Integer> getCountByStatus(@PathVariable String status) {
        int count = customerService.getCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    /**
     * 创建客户
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            int result = customerService.createCustomer(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "客户创建成功" : "客户创建失败");
            response.put("customer", customer);
            response.put("dataSource", "master"); // 标识使用的数据源
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCustomer(@PathVariable Long id, 
                                                             @Valid @RequestBody Customer customer) {
        try {
            customer.setId(id);
            int result = customerService.updateCustomer(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "客户更新成功" : "客户更新失败");
            response.put("dataSource", "master"); // 标识使用的数据源
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable Long id) {
        try {
            int result = customerService.deleteCustomer(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "客户删除成功" : "客户删除失败");
            response.put("dataSource", "master"); // 标识使用的数据源
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 激活客户
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Map<String, Object>> activateCustomer(@PathVariable Long id) {
        try {
            int result = customerService.activateCustomer(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "客户激活成功" : "客户激活失败");
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
     * 停用客户
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateCustomer(@PathVariable Long id) {
        try {
            int result = customerService.deactivateCustomer(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "客户停用成功" : "客户停用失败");
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
     * 批量创建客户
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchCreateCustomers(@Valid @RequestBody List<Customer> customers) {
        try {
            int result = customerService.batchCreateCustomers(customers);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", "成功创建 " + result + " 个客户");
            response.put("count", result);
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
     * 批量删除客户
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteCustomers(@RequestBody List<Long> ids) {
        try {
            int result = customerService.batchDeleteCustomers(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", "成功删除 " + result + " 个客户");
            response.put("count", result);
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
     * 检查客户编码是否存在
     */
    @GetMapping("/exists/{customerCode}")
    public ResponseEntity<Map<String, Object>> checkCustomerCodeExists(@PathVariable String customerCode) {
        boolean exists = customerService.isCustomerCodeExists(customerCode);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("customerCode", customerCode);
        response.put("dataSource", "slave"); // 查询操作使用从库
        return ResponseEntity.ok(response);
    }
}
