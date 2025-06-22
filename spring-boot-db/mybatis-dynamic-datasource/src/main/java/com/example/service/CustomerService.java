package com.example.service;

import com.example.annotation.DataSource;
import com.example.annotation.DataSourceType;
import com.example.domain.Customer;
import com.example.mapper.CustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户服务类
 * 演示动态数据源的使用
 */
@Service
@Transactional
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 根据ID查询客户 - 强制使用从库
     */
    @DataSource(DataSourceType.SLAVE)
    public Customer getCustomerById(Long id) {
        logger.info("查询客户信息，ID: {}", id);
        return customerMapper.selectById(id);
    }

    /**
     * 根据客户编码查询客户 - 强制使用从库
     */
    @DataSource(DataSourceType.SLAVE)
    public Customer getCustomerByCode(String customerCode) {
        logger.info("查询客户信息，编码: {}", customerCode);
        return customerMapper.selectByCustomerCode(customerCode);
    }

    /**
     * 查询所有客户 - 自动判断（使用从库）
     */
    public List<Customer> getAllCustomers() {
        logger.info("查询所有客户信息");
        return customerMapper.selectAll();
    }

    /**
     * 根据状态查询客户 - 自动判断（使用从库）
     */
    public List<Customer> getCustomersByStatus(String status) {
        logger.info("根据状态查询客户，状态: {}", status);
        return customerMapper.selectByStatus(status);
    }

    /**
     * 根据年龄范围查询客户 - 自动判断（使用从库）
     */
    public List<Customer> getCustomersByAgeRange(Integer minAge, Integer maxAge) {
        logger.info("根据年龄范围查询客户，年龄范围: {} - {}", minAge, maxAge);
        return customerMapper.selectByAgeRange(minAge, maxAge);
    }

    /**
     * 分页查询客户 - 自动判断（使用从库）
     */
    public List<Customer> getCustomersByPage(Integer pageNum, Integer pageSize) {
        logger.info("分页查询客户，页码: {}, 页大小: {}", pageNum, pageSize);
        int offset = (pageNum - 1) * pageSize;
        return customerMapper.selectByPage(offset, pageSize);
    }

    /**
     * 统计客户总数 - 自动判断（使用从库）
     */
    public int getTotalCount() {
        logger.info("统计客户总数");
        return customerMapper.countAll();
    }

    /**
     * 根据状态统计客户数 - 自动判断（使用从库）
     */
    public int getCountByStatus(String status) {
        logger.info("根据状态统计客户数，状态: {}", status);
        return customerMapper.countByStatus(status);
    }

    /**
     * 创建客户 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int createCustomer(Customer customer) {
        logger.info("创建客户: {}", customer.getCustomerName());
        
        if (customer == null) {
            throw new IllegalArgumentException("客户信息不能为空");
        }
        
        // 检查客户编码是否已存在
        Customer existingCustomer = customerMapper.selectByCustomerCode(customer.getCustomerCode());
        if (existingCustomer != null) {
            throw new IllegalArgumentException("客户编码已存在: " + customer.getCustomerCode());
        }
        
        // 设置默认值
        if (customer.getStatus() == null) {
            customer.setStatus("ACTIVE");
        }
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        
        return customerMapper.insert(customer);
    }

    /**
     * 更新客户 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int updateCustomer(Customer customer) {
        logger.info("更新客户: {}", customer.getId());
        
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        
        customer.setUpdateTime(LocalDateTime.now());
        return customerMapper.updateByIdSelective(customer);
    }

    /**
     * 删除客户 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int deleteCustomer(Long id) {
        logger.info("删除客户: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        return customerMapper.deleteById(id);
    }

    /**
     * 批量创建客户 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int batchCreateCustomers(List<Customer> customers) {
        logger.info("批量创建客户，数量: {}", customers.size());
        
        if (customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException("客户列表不能为空");
        }
        
        LocalDateTime now = LocalDateTime.now();
        for (Customer customer : customers) {
            if (customer.getStatus() == null) {
                customer.setStatus("ACTIVE");
            }
            customer.setCreateTime(now);
            customer.setUpdateTime(now);
        }
        
        return customerMapper.batchInsert(customers);
    }

    /**
     * 批量删除客户 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int batchDeleteCustomers(List<Long> ids) {
        logger.info("批量删除客户，数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("客户ID列表不能为空");
        }
        return customerMapper.batchDelete(ids);
    }

    /**
     * 激活客户 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int activateCustomer(Long id) {
        logger.info("激活客户: {}", id);
        
        Customer customer = new Customer();
        customer.setId(id);
        customer.setStatus("ACTIVE");
        customer.setUpdateTime(LocalDateTime.now());
        
        return customerMapper.updateByIdSelective(customer);
    }

    /**
     * 停用客户 - 强制使用主库
     */
    @DataSource(DataSourceType.MASTER)
    public int deactivateCustomer(Long id) {
        logger.info("停用客户: {}", id);
        
        Customer customer = new Customer();
        customer.setId(id);
        customer.setStatus("INACTIVE");
        customer.setUpdateTime(LocalDateTime.now());
        
        return customerMapper.updateByIdSelective(customer);
    }

    /**
     * 检查客户编码是否存在 - 自动判断（使用从库）
     */
    public boolean isCustomerCodeExists(String customerCode) {
        logger.info("检查客户编码是否存在: {}", customerCode);
        Customer customer = customerMapper.selectByCustomerCode(customerCode);
        return customer != null;
    }
}
