-- 主数据库测试初始化脚本

-- 创建客户表
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    age INT,
    address VARCHAR(200),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_time TIMESTAMP,
    shipping_address VARCHAR(200),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_customers_code ON customers(customer_code);
CREATE INDEX IF NOT EXISTS idx_customers_status ON customers(status);
CREATE INDEX IF NOT EXISTS idx_orders_no ON orders(order_no);
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);

-- 插入测试数据
INSERT INTO customers (customer_code, customer_name, email, phone, age, address, status, remark) VALUES
('TEST_C001', '测试客户1', 'test1@example.com', '13800000001', 25, '测试地址1', 'ACTIVE', '测试客户'),
('TEST_C002', '测试客户2', 'test2@example.com', '13800000002', 30, '测试地址2', 'ACTIVE', '测试客户'),
('TEST_C003', '测试客户3', 'test3@example.com', '13800000003', 28, '测试地址3', 'INACTIVE', '测试客户'),
('TEST_C004', '测试客户4', 'test4@example.com', '13800000004', 32, '测试地址4', 'ACTIVE', '测试客户'),
('TEST_C005', '测试客户5', 'test5@example.com', '13800000005', 27, '测试地址5', 'ACTIVE', '测试客户');

INSERT INTO orders (order_no, customer_id, customer_name, total_amount, status, payment_method, shipping_address, remark) VALUES
('TEST_ORD001', 1, '测试客户1', 999.99, 'COMPLETED', '支付宝', '测试地址1', '测试订单'),
('TEST_ORD002', 2, '测试客户2', 1599.50, 'SHIPPED', '微信支付', '测试地址2', '测试订单'),
('TEST_ORD003', 3, '测试客户3', 799.00, 'PAID', '银行卡', '测试地址3', '测试订单'),
('TEST_ORD004', 4, '测试客户4', 2299.99, 'PENDING', NULL, '测试地址4', '测试订单'),
('TEST_ORD005', 5, '测试客户5', 1299.99, 'CANCELLED', NULL, '测试地址5', '测试订单');
