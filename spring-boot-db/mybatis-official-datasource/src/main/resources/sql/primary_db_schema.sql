-- 主数据源数据库初始化脚本
-- 用户管理数据库

-- 创建数据库
CREATE DATABASE IF NOT EXISTS primary_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE primary_db;

-- 创建用户表
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status INT NOT NULL DEFAULT 1 COMMENT '用户状态：1-正常，0-禁用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据
INSERT INTO users (username, email, phone, status) VALUES
('admin', 'admin@example.com', '13800138000', 1),
('user1', 'user1@example.com', '13800138001', 1),
('user2', 'user2@example.com', '13800138002', 1),
('user3', 'user3@example.com', '13800138003', 0),
('test_user', 'test@example.com', '13800138004', 1);

-- 查询验证
SELECT '主数据源初始化完成' AS message;
SELECT COUNT(*) AS user_count FROM users;
SELECT * FROM users ORDER BY id;
