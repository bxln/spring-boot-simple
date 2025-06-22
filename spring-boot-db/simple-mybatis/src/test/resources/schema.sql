-- 创建员工表
DROP TABLE IF EXISTS staff;

CREATE TABLE staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '员工编码',
    name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    age INT COMMENT '年龄',
    gender VARCHAR(10) COMMENT '性别',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    modify_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
);

-- 创建索引
CREATE INDEX idx_staff_code ON staff(code);
CREATE INDEX idx_staff_name ON staff(name);
CREATE INDEX idx_staff_age ON staff(age);
CREATE INDEX idx_staff_gender ON staff(gender);
