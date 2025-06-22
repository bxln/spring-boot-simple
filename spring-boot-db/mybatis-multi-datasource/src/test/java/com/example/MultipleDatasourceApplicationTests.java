package com.example;

import com.example.primary.domain.User;
import com.example.primary.service.UserService;
import com.example.secondary.domain.Product;
import com.example.secondary.service.ProductService;
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
 * 多数据源应用测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {
    "classpath:sql/primary-test-schema.sql",
    "classpath:sql/secondary-test-schema.sql"
})
class MultipleDatasourceApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Test
    void contextLoads() {
        // 测试Spring上下文是否正常加载
        assertNotNull(userService);
        assertNotNull(productService);
    }

    @Test
    @Transactional(transactionManager = "primaryTransactionManager")
    void testPrimaryDataSource() {
        // 测试主数据源 - 用户相关操作
        
        // 测试查询所有用户
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertTrue(users.size() >= 5);
        
        // 测试根据用户名查询
        User user = userService.getUserByUsername("testuser1");
        assertNotNull(user);
        assertEquals("testuser1", user.getUsername());
        assertEquals("test1@example.com", user.getEmail());
        
        // 测试统计功能
        int totalCount = userService.getTotalCount();
        assertTrue(totalCount >= 5);
        
        int activeCount = userService.getCountByStatus("ACTIVE");
        assertTrue(activeCount >= 4);
    }

    @Test
    @Transactional(transactionManager = "secondaryTransactionManager")
    void testSecondaryDataSource() {
        // 测试从数据源 - 产品相关操作
        
        // 测试查询所有产品
        List<Product> products = productService.getAllProducts();
        assertNotNull(products);
        assertTrue(products.size() >= 5);
        
        // 测试根据产品编码查询
        Product product = productService.getProductByCode("TEST001");
        assertNotNull(product);
        assertEquals("TEST001", product.getProductCode());
        assertEquals("测试产品1", product.getProductName());
        
        // 测试统计功能
        int totalCount = productService.getTotalCount();
        assertTrue(totalCount >= 5);
        
        int activeCount = productService.getCountByStatus("ACTIVE");
        assertTrue(activeCount >= 4);
    }

    @Test
    void testMultiDataSourceOperations() {
        // 测试多数据源同时操作
        
        // 从主数据源获取用户数据
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        
        // 从从数据源获取产品数据
        List<Product> products = productService.getAllProducts();
        assertNotNull(products);
        
        // 验证两个数据源都能正常工作
        assertTrue(users.size() > 0);
        assertTrue(products.size() > 0);
        
        // 测试统计信息
        int userCount = userService.getTotalCount();
        int productCount = productService.getTotalCount();
        
        assertTrue(userCount > 0);
        assertTrue(productCount > 0);
    }

    @Test
    @Transactional(transactionManager = "primaryTransactionManager")
    void testUserCRUDOperations() {
        // 测试用户CRUD操作
        
        // 创建新用户
        User newUser = new User("newuser", "newuser@example.com", "13900000000", 26);
        int result = userService.createUser(newUser);
        assertEquals(1, result);
        assertNotNull(newUser.getId());
        
        // 查询新创建的用户
        User createdUser = userService.getUserById(newUser.getId());
        assertNotNull(createdUser);
        assertEquals("newuser", createdUser.getUsername());
        
        // 更新用户
        createdUser.setAge(27);
        result = userService.updateUser(createdUser);
        assertEquals(1, result);
        
        // 验证更新
        User updatedUser = userService.getUserById(createdUser.getId());
        assertEquals(27, updatedUser.getAge());
        
        // 删除用户
        result = userService.deleteUser(createdUser.getId());
        assertEquals(1, result);
        
        // 验证删除
        User deletedUser = userService.getUserById(createdUser.getId());
        assertNull(deletedUser);
    }

    @Test
    @Transactional(transactionManager = "secondaryTransactionManager")
    void testProductCRUDOperations() {
        // 测试产品CRUD操作
        
        // 创建新产品
        Product newProduct = new Product("NEW001", "新产品", "这是一个新产品", 
                                       new BigDecimal("599.99"), 100, "新分类");
        int result = productService.createProduct(newProduct);
        assertEquals(1, result);
        assertNotNull(newProduct.getId());
        
        // 查询新创建的产品
        Product createdProduct = productService.getProductById(newProduct.getId());
        assertNotNull(createdProduct);
        assertEquals("NEW001", createdProduct.getProductCode());
        
        // 更新产品
        createdProduct.setPrice(new BigDecimal("699.99"));
        result = productService.updateProduct(createdProduct);
        assertEquals(1, result);
        
        // 验证更新
        Product updatedProduct = productService.getProductById(createdProduct.getId());
        assertEquals(0, new BigDecimal("699.99").compareTo(updatedProduct.getPrice()));
        
        // 更新库存
        result = productService.updateStock(createdProduct.getId(), 50);
        assertEquals(1, result);
        
        // 验证库存更新
        Product stockUpdatedProduct = productService.getProductById(createdProduct.getId());
        assertEquals(50, stockUpdatedProduct.getStock());
        
        // 删除产品
        result = productService.deleteProduct(createdProduct.getId());
        assertEquals(1, result);
        
        // 验证删除
        Product deletedProduct = productService.getProductById(createdProduct.getId());
        assertNull(deletedProduct);
    }
}
