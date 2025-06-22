-- 第二数据源数据库初始化脚本
-- 订单管理数据库

-- 创建数据库
CREATE DATABASE IF NOT EXISTS secondary_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE secondary_db;

-- 创建订单表
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID（关联主数据源）',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status INT NOT NULL DEFAULT 1 COMMENT '订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 插入测试数据
INSERT INTO orders (order_no, user_id, product_name, quantity, price, total_amount, status) VALUES
('ORD202406220001', 1, 'MacBook Pro 14寸', 1, 15999.00, 15999.00, 2),
('ORD202406220002', 1, 'iPhone 15 Pro', 1, 7999.00, 7999.00, 4),
('ORD202406220003', 2, 'iPad Air', 1, 4399.00, 4399.00, 3),
('ORD202406220004', 2, 'AirPods Pro', 2, 1899.00, 3798.00, 2),
('ORD202406220005', 3, 'Apple Watch', 1, 2999.00, 2999.00, 1),
('ORD202406220006', 5, 'Magic Keyboard', 1, 1099.00, 1099.00, 4),
('ORD202406220007', 5, 'Magic Mouse', 1, 699.00, 699.00, 2);

-- 查询验证
SELECT '第二数据源初始化完成' AS message;
SELECT COUNT(*) AS order_count FROM orders;
SELECT * FROM orders ORDER BY id;
