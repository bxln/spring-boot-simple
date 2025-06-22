package com.example.annotation;

/**
 * 数据源类型枚举
 */
public enum DataSourceType {
    
    /**
     * 主数据源（写库）
     * 用于：增删改操作
     */
    MASTER("master"),
    
    /**
     * 从数据源（读库）
     * 用于：查询操作
     */
    SLAVE("slave"),
    
    /**
     * 自动判断
     * 根据方法名自动选择数据源：
     * - select、find、get、query、count等开头的方法使用从库
     * - insert、update、delete、save、remove等开头的方法使用主库
     */
    AUTO("auto");
    
    private final String value;
    
    DataSourceType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 根据方法名自动判断数据源类型
     */
    public static DataSourceType getByMethodName(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return MASTER;
        }
        
        String lowerMethodName = methodName.toLowerCase();
        
        // 读操作使用从库
        if (lowerMethodName.startsWith("select") ||
            lowerMethodName.startsWith("find") ||
            lowerMethodName.startsWith("get") ||
            lowerMethodName.startsWith("query") ||
            lowerMethodName.startsWith("count") ||
            lowerMethodName.startsWith("list") ||
            lowerMethodName.startsWith("search") ||
            lowerMethodName.startsWith("exists")) {
            return SLAVE;
        }
        
        // 写操作使用主库
        return MASTER;
    }
}
