-- 从数据源数据库初始化脚本
-- 数据库: secondary_db

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS secondary_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE secondary_db;

-- 删除已存在的表
DROP TABLE IF EXISTS products;

-- 创建产品表
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '产品ID',
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '产品编码',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    description TEXT COMMENT '产品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock INT DEFAULT 0 COMMENT '库存数量',
    category VARCHAR(50) NOT NULL COMMENT '产品分类',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-上架，INACTIVE-下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

-- 创建索引
CREATE INDEX idx_products_code ON products(product_code);
CREATE INDEX idx_products_name ON products(product_name);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_create_time ON products(create_time);

-- 插入测试数据
INSERT INTO products (product_code, product_name, description, price, stock, category, status, create_time, update_time) VALUES
('P001', 'iPhone 15 Pro', '苹果最新旗舰手机，搭载A17 Pro芯片', 8999.00, 50, '手机', 'ACTIVE', NOW(), NOW()),
('P002', 'MacBook Pro 16', '苹果专业级笔记本电脑，M3 Max芯片', 25999.00, 20, '电脑', 'ACTIVE', NOW(), NOW()),
('P003', 'iPad Air', '轻薄便携的平板电脑，适合办公娱乐', 4399.00, 30, '平板', 'ACTIVE', NOW(), NOW()),
('P004', 'AirPods Pro', '主动降噪无线耳机，音质出色', 1899.00, 100, '耳机', 'ACTIVE', NOW(), NOW()),
('P005', 'Apple Watch Series 9', '智能手表，健康监测功能强大', 2999.00, 80, '手表', 'ACTIVE', NOW(), NOW()),
('P006', 'Samsung Galaxy S24', '三星旗舰手机，拍照功能强大', 7999.00, 40, '手机', 'ACTIVE', NOW(), NOW()),
('P007', 'Dell XPS 13', '戴尔超薄笔记本，商务办公首选', 8999.00, 25, '电脑', 'ACTIVE', NOW(), NOW()),
('P008', 'Sony WH-1000XM5', '索尼降噪耳机，音质顶级', 2299.00, 60, '耳机', 'INACTIVE', NOW(), NOW()),
('P009', 'Huawei MatePad Pro', '华为高端平板，办公娱乐两不误', 3999.00, 35, '平板', 'ACTIVE', NOW(), NOW()),
('P010', 'Xiaomi Watch S1', '小米智能手表，性价比极高', 1299.00, 120, '手表', 'ACTIVE', NOW(), NOW()),
('P011', 'Nintendo Switch', '任天堂游戏机，娱乐必备', 2099.00, 70, '游戏机', 'ACTIVE', NOW(), NOW()),
('P012', 'PlayStation 5', '索尼游戏主机，游戏体验极佳', 3999.00, 15, '游戏机', 'ACTIVE', NOW(), NOW());

-- 查看插入结果
SELECT COUNT(*) as total_products FROM products;
SELECT category, COUNT(*) as count FROM products GROUP BY category;
SELECT status, COUNT(*) as count FROM products GROUP BY status;
