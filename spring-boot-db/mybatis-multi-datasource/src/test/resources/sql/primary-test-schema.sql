-- 主数据源测试数据库初始化脚本

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    age INT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- 插入测试数据
INSERT INTO users (username, email, phone, age, status) VALUES
('testuser1', 'test1@example.com', '13800000001', 25, 'ACTIVE'),
('testuser2', 'test2@example.com', '13800000002', 30, 'ACTIVE'),
('testuser3', 'test3@example.com', '13800000003', 28, 'INACTIVE'),
('testuser4', 'test4@example.com', '13800000004', 32, 'ACTIVE'),
('testuser5', 'test5@example.com', '13800000005', 27, 'ACTIVE');
