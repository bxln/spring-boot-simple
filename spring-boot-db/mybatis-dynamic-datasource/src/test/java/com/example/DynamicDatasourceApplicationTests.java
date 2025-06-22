package com.example;

import com.example.annotation.DataSource;
import com.example.annotation.DataSourceType;
import com.example.config.DataSourceContextHolder;
import com.example.domain.Customer;
import com.example.domain.Order;
import com.example.service.CustomerService;
import com.example.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 动态数据源应用测试类
 * 测试动态数据源的切换功能
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {
    "classpath:sql/master-test-schema.sql",
    "classpath:sql/slave-test-schema.sql"
})
class DynamicDatasourceApplicationTests {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Test
    void contextLoads() {
        // 测试Spring上下文是否正常加载
        assertNotNull(customerService);
        assertNotNull(orderService);
    }

    @Test
    void testDataSourceContextHolder() {
        // 测试数据源上下文持有者
        
        // 设置主库
        DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
        assertEquals(DataSourceType.MASTER, DataSourceContextHolder.getDataSourceType());
        assertEquals("master", DataSourceContextHolder.getCurrentDataSourceKey());
        
        // 设置从库
        DataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE);
        assertEquals(DataSourceType.SLAVE, DataSourceContextHolder.getDataSourceType());
        assertEquals("slave", DataSourceContextHolder.getCurrentDataSourceKey());
        
        // 清除数据源
        DataSourceContextHolder.clearDataSourceType();
        assertEquals(DataSourceType.MASTER, DataSourceContextHolder.getDataSourceType()); // 默认主库
    }

    @Test
    void testDataSourceTypeAutoDetection() {
        // 测试根据方法名自动判断数据源类型
        
        // 查询方法应该使用从库
        assertEquals(DataSourceType.SLAVE, DataSourceType.getByMethodName("selectAll"));
        assertEquals(DataSourceType.SLAVE, DataSourceType.getByMethodName("findById"));
        assertEquals(DataSourceType.SLAVE, DataSourceType.getByMethodName("getCustomer"));
        assertEquals(DataSourceType.SLAVE, DataSourceType.getByMethodName("queryOrders"));
        assertEquals(DataSourceType.SLAVE, DataSourceType.getByMethodName("countAll"));
        
        // 写操作方法应该使用主库
        assertEquals(DataSourceType.MASTER, DataSourceType.getByMethodName("insert"));
        assertEquals(DataSourceType.MASTER, DataSourceType.getByMethodName("update"));
        assertEquals(DataSourceType.MASTER, DataSourceType.getByMethodName("delete"));
        assertEquals(DataSourceType.MASTER, DataSourceType.getByMethodName("save"));
        assertEquals(DataSourceType.MASTER, DataSourceType.getByMethodName("create"));
    }

    @Test
    @Transactional
    void testCustomerOperations() {
        // 测试客户相关操作
        
        // 查询操作（使用从库）
        List<Customer> customers = customerService.getAllCustomers();
        assertNotNull(customers);
        assertTrue(customers.size() >= 5);
        
        Customer customer = customerService.getCustomerById(1L);
        assertNotNull(customer);
        assertEquals("TEST_C001", customer.getCustomerCode());
        
        // 写操作（使用主库）
        Customer newCustomer = new Customer("NEW_C001", "新客户", "new@example.com", "13900000000", 30);
        int result = customerService.createCustomer(newCustomer);
        assertEquals(1, result);
        assertNotNull(newCustomer.getId());
    }

    @Test
    @Transactional
    void testOrderOperations() {
        // 测试订单相关操作
        
        // 查询操作（使用从库）
        List<Order> orders = orderService.getAllOrders();
        assertNotNull(orders);
        assertTrue(orders.size() >= 5);
        
        Order order = orderService.getOrderById(1L);
        assertNotNull(order);
        assertEquals("TEST_ORD001", order.getOrderNo());
        
        // 写操作（使用主库）
        Order newOrder = new Order("NEW_ORD001", 1L, "测试客户1", new BigDecimal("1999.99"));
        int result = orderService.createOrder(newOrder);
        assertEquals(1, result);
        assertNotNull(newOrder.getId());
    }

    @DataSource(DataSourceType.SLAVE)
    @Test
    void testSlaveDataSource() {
        // 强制使用从库
        int count = customerService.getTotalCount();
        assertTrue(count >= 0);
    }

    @DataSource(DataSourceType.MASTER)
    @Test
    void testMasterDataSource() {
        // 强制使用主库
        int count = customerService.getTotalCount();
        assertTrue(count >= 0);
    }

    @Test
    @Transactional
    void testMultiDataSourceOperations() {
        // 测试多数据源同时操作
        
        // 从从库读取数据
        List<Customer> customers = customerService.getAllCustomers();
        List<Order> orders = orderService.getAllOrders();
        
        assertNotNull(customers);
        assertNotNull(orders);
        assertTrue(customers.size() > 0);
        assertTrue(orders.size() > 0);
        
        // 向主库写入数据
        Customer newCustomer = new Customer("MULTI_C001", "多数据源客户", "multi@example.com", "13900000001", 25);
        int customerResult = customerService.createCustomer(newCustomer);
        assertEquals(1, customerResult);
        
        Order newOrder = new Order("MULTI_ORD001", newCustomer.getId(), newCustomer.getCustomerName(), new BigDecimal("999.99"));
        int orderResult = orderService.createOrder(newOrder);
        assertEquals(1, orderResult);
        
        // 验证数据已写入
        Customer createdCustomer = customerService.getCustomerById(newCustomer.getId());
        Order createdOrder = orderService.getOrderById(newOrder.getId());
        
        assertNotNull(createdCustomer);
        assertNotNull(createdOrder);
        assertEquals("MULTI_C001", createdCustomer.getCustomerCode());
        assertEquals("MULTI_ORD001", createdOrder.getOrderNo());
    }
}
