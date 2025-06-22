-- 主数据源数据库初始化脚本
-- 数据库: primary_db

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS primary_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE primary_db;

-- 删除已存在的表
DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    age INT COMMENT '年龄',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃，INACTIVE-非活跃',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_age ON users(age);
CREATE INDEX idx_users_create_time ON users(create_time);

-- 插入测试数据
INSERT INTO users (username, email, phone, age, status, create_time, update_time) VALUES
('admin', 'admin@example.com', '13800000001', 30, 'ACTIVE', NOW(), NOW()),
('zhangsan', 'zhangsan@example.com', '13800000002', 25, 'ACTIVE', NOW(), NOW()),
('lisi', 'lisi@example.com', '13800000003', 28, 'ACTIVE', NOW(), NOW()),
('wangwu', 'wangwu@example.com', '13800000004', 32, 'INACTIVE', NOW(), NOW()),
('zhaoliu', 'zhaoliu@example.com', '13800000005', 27, 'ACTIVE', NOW(), NOW()),
('qianqi', 'qianqi@example.com', '13800000006', 29, 'ACTIVE', NOW(), NOW()),
('sunba', 'sunba@example.com', '13800000007', 26, 'INACTIVE', NOW(), NOW()),
('lijiu', 'lijiu@example.com', '13800000008', 31, 'ACTIVE', NOW(), NOW()),
('zhoushi', 'zhoushi@example.com', '13800000009', 24, 'ACTIVE', NOW(), NOW()),
('wushiyi', 'wushiyi@example.com', '13800000010', 33, 'ACTIVE', NOW(), NOW());

-- 查看插入结果
SELECT COUNT(*) as total_users FROM users;
SELECT status, COUNT(*) as count FROM users GROUP BY status;
