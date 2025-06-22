-- 从数据源测试数据库初始化脚本

-- 创建产品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_products_code ON products(product_code);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(product_name);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);

-- 插入测试数据
INSERT INTO products (product_code, product_name, description, price, stock, category, status) VALUES
('TEST001', '测试产品1', '这是一个测试产品', 99.99, 10, '测试分类', 'ACTIVE'),
('TEST002', '测试产品2', '这是另一个测试产品', 199.99, 20, '测试分类', 'ACTIVE'),
('TEST003', '测试产品3', '第三个测试产品', 299.99, 15, '其他分类', 'INACTIVE'),
('TEST004', '测试产品4', '第四个测试产品', 399.99, 5, '测试分类', 'ACTIVE'),
('TEST005', '测试产品5', '第五个测试产品', 499.99, 8, '其他分类', 'ACTIVE');
