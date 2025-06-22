-- 从数据库初始化脚本 (读库)
-- 数据库: slave_db
-- 注意：在实际生产环境中，从库数据通常通过主从复制同步，这里为了演示目的手动创建相同结构和数据

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS slave_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE slave_db;

-- 删除已存在的表
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customers;

-- 创建客户表（与主库结构相同）
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID',
    customer_code VARCHAR(50) NOT NULL UNIQUE COMMENT '客户编码',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    age INT COMMENT '年龄',
    address VARCHAR(200) COMMENT '地址',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃，INACTIVE-非活跃',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表-从库';

-- 创建订单表（与主库结构相同）
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态：PENDING-待处理，PAID-已支付，SHIPPED-已发货，COMPLETED-已完成，CANCELLED-已取消',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_time DATETIME COMMENT '支付时间',
    shipping_address VARCHAR(200) COMMENT '收货地址',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表-从库';

-- 创建索引（优化查询性能）
-- 客户表索引
CREATE INDEX idx_customers_code ON customers(customer_code);
CREATE INDEX idx_customers_name ON customers(customer_name);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_age ON customers(age);
CREATE INDEX idx_customers_create_time ON customers(create_time);

-- 订单表索引
CREATE INDEX idx_orders_no ON orders(order_no);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_customer_name ON orders(customer_name);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_amount ON orders(total_amount);
CREATE INDEX idx_orders_create_time ON orders(create_time);
CREATE INDEX idx_orders_payment_time ON orders(payment_time);

-- 插入测试数据（与主库相同的数据，模拟主从同步）
-- 客户数据
INSERT INTO customers (customer_code, customer_name, email, phone, age, address, status, remark, create_time, update_time) VALUES
('C001', '张三', 'zhangsan@example.com', '13800000001', 28, '北京市朝阳区', 'ACTIVE', '重要客户', '2024-01-15 08:00:00', '2024-01-15 08:00:00'),
('C002', '李四', 'lisi@example.com', '13800000002', 32, '上海市浦东新区', 'ACTIVE', '普通客户', '2024-01-15 08:05:00', '2024-01-15 08:05:00'),
('C003', '王五', 'wangwu@example.com', '13800000003', 25, '广州市天河区', 'ACTIVE', '新客户', '2024-01-15 08:10:00', '2024-01-15 08:10:00'),
('C004', '赵六', 'zhaoliu@example.com', '13800000004', 35, '深圳市南山区', 'INACTIVE', '暂停合作', '2024-01-15 08:15:00', '2024-01-15 08:15:00'),
('C005', '钱七', 'qianqi@example.com', '13800000005', 29, '杭州市西湖区', 'ACTIVE', 'VIP客户', '2024-01-15 08:20:00', '2024-01-15 08:20:00'),
('C006', '孙八', 'sunba@example.com', '13800000006', 31, '成都市锦江区', 'ACTIVE', '普通客户', '2024-01-15 08:25:00', '2024-01-15 08:25:00'),
('C007', '周九', 'zhoujiu@example.com', '13800000007', 27, '武汉市武昌区', 'ACTIVE', '新客户', '2024-01-15 08:30:00', '2024-01-15 08:30:00'),
('C008', '吴十', 'wushi@example.com', '13800000008', 33, '西安市雁塔区', 'ACTIVE', '重要客户', '2024-01-15 08:35:00', '2024-01-15 08:35:00'),
('C009', '郑十一', 'zhengshiyi@example.com', '13800000009', 26, '南京市鼓楼区', 'INACTIVE', '流失客户', '2024-01-15 08:40:00', '2024-01-15 08:40:00'),
('C010', '王十二', 'wangshier@example.com', '13800000010', 30, '重庆市渝中区', 'ACTIVE', 'VIP客户', '2024-01-15 08:45:00', '2024-01-15 08:45:00');

-- 订单数据
INSERT INTO orders (order_no, customer_id, customer_name, total_amount, status, payment_method, payment_time, shipping_address, remark, create_time, update_time) VALUES
('ORD001', 1, '张三', 1299.99, 'COMPLETED', '支付宝', '2024-01-15 10:30:00', '北京市朝阳区xxx街道', '加急处理', '2024-01-15 09:00:00', '2024-01-15 18:00:00'),
('ORD002', 2, '李四', 2599.50, 'SHIPPED', '微信支付', '2024-01-16 14:20:00', '上海市浦东新区xxx路', '正常发货', '2024-01-16 11:00:00', '2024-01-16 16:00:00'),
('ORD003', 3, '王五', 899.00, 'PAID', '银行卡', '2024-01-17 16:45:00', '广州市天河区xxx大厦', '待发货', '2024-01-17 15:00:00', '2024-01-17 16:45:00'),
('ORD004', 1, '张三', 3299.99, 'PENDING', NULL, NULL, '北京市朝阳区xxx街道', '大额订单', '2024-01-18 08:30:00', '2024-01-18 08:30:00'),
('ORD005', 5, '钱七', 1899.99, 'COMPLETED', '支付宝', '2024-01-19 11:15:00', '杭州市西湖区xxx小区', 'VIP订单', '2024-01-19 09:45:00', '2024-01-19 17:30:00'),
('ORD006', 6, '孙八', 599.99, 'CANCELLED', NULL, NULL, '成都市锦江区xxx路', '客户取消', '2024-01-20 13:20:00', '2024-01-20 14:00:00'),
('ORD007', 7, '周九', 1599.00, 'SHIPPED', '微信支付', '2024-01-21 15:30:00', '武汉市武昌区xxx街', '正常订单', '2024-01-21 14:00:00', '2024-01-21 16:00:00'),
('ORD008', 8, '吴十', 2199.99, 'PAID', '银行卡', '2024-01-22 10:45:00', '西安市雁塔区xxx大道', '重要客户订单', '2024-01-22 09:15:00', '2024-01-22 10:45:00'),
('ORD009', 10, '王十二', 4999.99, 'COMPLETED', '支付宝', '2024-01-23 12:00:00', '重庆市渝中区xxx广场', 'VIP大额订单', '2024-01-23 10:30:00', '2024-01-23 15:00:00'),
('ORD010', 3, '王五', 799.50, 'PENDING', NULL, NULL, '广州市天河区xxx大厦', '普通订单', '2024-01-24 16:00:00', '2024-01-24 16:00:00');

-- 添加一些额外的历史数据（模拟从库有更多历史查询数据）
INSERT INTO customers (customer_code, customer_name, email, phone, age, address, status, remark, create_time, update_time) VALUES
('C011', '历史客户1', 'history1@example.com', '13800000011', 40, '天津市和平区', 'INACTIVE', '历史客户', '2023-12-01 10:00:00', '2023-12-01 10:00:00'),
('C012', '历史客户2', 'history2@example.com', '13800000012', 45, '苏州市姑苏区', 'INACTIVE', '历史客户', '2023-12-02 10:00:00', '2023-12-02 10:00:00');

INSERT INTO orders (order_no, customer_id, customer_name, total_amount, status, payment_method, payment_time, shipping_address, remark, create_time, update_time) VALUES
('ORD011', 11, '历史客户1', 999.99, 'COMPLETED', '支付宝', '2023-12-01 15:00:00', '天津市和平区xxx路', '历史订单', '2023-12-01 14:00:00', '2023-12-01 18:00:00'),
('ORD012', 12, '历史客户2', 1599.99, 'COMPLETED', '微信支付', '2023-12-02 16:00:00', '苏州市姑苏区xxx街', '历史订单', '2023-12-02 15:00:00', '2023-12-02 19:00:00');

-- 查看插入结果
SELECT '从库客户统计' as 统计类型, COUNT(*) as 总数 FROM customers
UNION ALL
SELECT '从库订单统计' as 统计类型, COUNT(*) as 总数 FROM orders;

SELECT '从库客户状态统计' as 统计类型, status as 状态, COUNT(*) as 数量 FROM customers GROUP BY status
UNION ALL
SELECT '从库订单状态统计' as 统计类型, status as 状态, COUNT(*) as 数量 FROM orders GROUP BY status;

-- 创建一些用于查询优化的视图
CREATE VIEW v_customer_summary AS
SELECT 
    status,
    COUNT(*) as customer_count,
    AVG(age) as avg_age,
    MIN(create_time) as first_customer_time,
    MAX(create_time) as last_customer_time
FROM customers 
GROUP BY status;

CREATE VIEW v_order_summary AS
SELECT 
    status,
    COUNT(*) as order_count,
    SUM(total_amount) as total_amount,
    AVG(total_amount) as avg_amount,
    MIN(create_time) as first_order_time,
    MAX(create_time) as last_order_time
FROM orders 
GROUP BY status;

-- 显示视图内容
SELECT '客户汇总视图' as 视图名称, status as 状态, customer_count as 客户数量, avg_age as 平均年龄 FROM v_customer_summary;
SELECT '订单汇总视图' as 视图名称, status as 状态, order_count as 订单数量, total_amount as 总金额 FROM v_order_summary;
