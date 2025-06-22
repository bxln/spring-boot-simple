package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * H2数据库配置测试
 */
@SpringBootTest
@ActiveProfiles("test")
class H2ConfigTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testH2DatabaseConnection() throws Exception {
        // 测试数据库连接
        assertNotNull(dataSource);
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
            
            // 获取数据库元数据
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("数据库产品名称: " + metaData.getDatabaseProductName());
            System.out.println("数据库版本: " + metaData.getDatabaseProductVersion());
            System.out.println("驱动名称: " + metaData.getDriverName());
            System.out.println("连接URL: " + metaData.getURL());
            
            // 验证是H2数据库
            assertTrue(metaData.getDatabaseProductName().contains("H2"));
        }
    }

    @Test
    void testStaffTableExists() throws Exception {
        // 测试staff表是否存在
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            try (ResultSet tables = metaData.getTables(null, null, "STAFF", new String[]{"TABLE"})) {
                assertTrue(tables.next(), "staff表应该存在");
                System.out.println("找到表: " + tables.getString("TABLE_NAME"));
            }
        }
    }

    @Test
    void testStaffTableStructure() throws Exception {
        // 测试staff表结构
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            try (ResultSet columns = metaData.getColumns(null, null, "STAFF", null)) {
                boolean hasIdColumn = false;
                boolean hasCodeColumn = false;
                boolean hasNameColumn = false;
                boolean hasGenderColumn = false;
                
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    System.out.println("列名: " + columnName + ", 类型: " + columns.getString("TYPE_NAME"));
                    
                    switch (columnName.toUpperCase()) {
                        case "ID":
                            hasIdColumn = true;
                            break;
                        case "CODE":
                            hasCodeColumn = true;
                            break;
                        case "NAME":
                            hasNameColumn = true;
                            break;
                        case "GENDER":
                            hasGenderColumn = true;
                            break;
                    }
                }
                
                assertTrue(hasIdColumn, "应该有ID列");
                assertTrue(hasCodeColumn, "应该有CODE列");
                assertTrue(hasNameColumn, "应该有NAME列");
                assertTrue(hasGenderColumn, "应该有GENDER列");
            }
        }
    }

    @Test
    void testInitialData() throws Exception {
        // 测试初始数据是否插入成功
        try (Connection connection = dataSource.getConnection()) {
            try (var statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) as count FROM staff")) {
                    assertTrue(rs.next());
                    int count = rs.getInt("count");
                    System.out.println("staff表中的记录数: " + count);
                    assertTrue(count >= 5, "应该至少有5条测试数据");
                }
            }
        }
    }

    @Test
    void testChineseDataEncoding() throws Exception {
        // 测试中文数据编码
        try (Connection connection = dataSource.getConnection()) {
            try (var statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("SELECT name FROM staff WHERE code = 'EMP001'")) {
                    assertTrue(rs.next());
                    String name = rs.getString("name");
                    System.out.println("员工姓名: " + name);
                    assertEquals("张三", name, "中文姓名应该正确显示");
                }
            }
        }
    }
}
