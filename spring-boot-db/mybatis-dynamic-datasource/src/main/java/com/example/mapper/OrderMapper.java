package com.example.mapper;

import com.example.annotation.DataSource;
import com.example.annotation.DataSourceType;
import com.example.domain.Order;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单Mapper接口 - 使用注解驱动
 * 演示动态数据源的读写分离
 */
@Repository
public interface OrderMapper {

    /**
     * 根据ID查询订单 - 从库
     */
    @DataSource(DataSourceType.SLAVE)
    @Select("SELECT * FROM orders WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    Order selectById(Long id);

    /**
     * 根据订单号查询订单 - 从库
     */
    @DataSource(DataSourceType.SLAVE)
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    Order selectByOrderNo(String orderNo);

    /**
     * 查询所有订单 - 从库（自动判断）
     */
    @Select("SELECT * FROM orders ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Order> selectAll();

    /**
     * 根据客户ID查询订单 - 从库（自动判断）
     */
    @Select("SELECT * FROM orders WHERE customer_id = #{customerId} ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Order> selectByCustomerId(Long customerId);

    /**
     * 根据状态查询订单 - 从库（自动判断）
     */
    @Select("SELECT * FROM orders WHERE status = #{status} ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Order> selectByStatus(String status);

    /**
     * 根据金额范围查询订单 - 从库（自动判断）
     */
    @Select("<script>" +
            "SELECT * FROM orders WHERE 1=1 " +
            "<if test='minAmount != null'> AND total_amount >= #{minAmount} </if>" +
            "<if test='maxAmount != null'> AND total_amount <= #{maxAmount} </if>" +
            "ORDER BY total_amount DESC" +
            "</script>")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Order> selectByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);

    /**
     * 分页查询订单 - 从库（自动判断）
     */
    @Select("SELECT * FROM orders ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Order> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计订单总数 - 从库（自动判断）
     */
    @Select("SELECT COUNT(*) FROM orders")
    int countAll();

    /**
     * 根据状态统计订单数 - 从库（自动判断）
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status}")
    int countByStatus(String status);

    /**
     * 根据客户ID统计订单数 - 从库（自动判断）
     */
    @Select("SELECT COUNT(*) FROM orders WHERE customer_id = #{customerId}")
    int countByCustomerId(Long customerId);

    /**
     * 插入订单 - 主库（自动判断）
     */
    @Insert("INSERT INTO orders (order_no, customer_id, customer_name, total_amount, status, payment_method, " +
            "payment_time, shipping_address, remark, create_time, update_time) " +
            "VALUES (#{orderNo}, #{customerId}, #{customerName}, #{totalAmount}, #{status}, #{paymentMethod}, " +
            "#{paymentTime}, #{shippingAddress}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    /**
     * 选择性插入订单 - 主库（自动判断）
     */
    @Insert("<script>" +
            "INSERT INTO orders " +
            "<trim prefix='(' suffix=')' suffixOverrides=','>" +
            "<if test='orderNo != null'>order_no,</if>" +
            "<if test='customerId != null'>customer_id,</if>" +
            "<if test='customerName != null'>customer_name,</if>" +
            "<if test='totalAmount != null'>total_amount,</if>" +
            "<if test='status != null'>status,</if>" +
            "<if test='paymentMethod != null'>payment_method,</if>" +
            "<if test='paymentTime != null'>payment_time,</if>" +
            "<if test='shippingAddress != null'>shipping_address,</if>" +
            "<if test='remark != null'>remark,</if>" +
            "<if test='createTime != null'>create_time,</if>" +
            "<if test='updateTime != null'>update_time,</if>" +
            "</trim>" +
            "<trim prefix='VALUES (' suffix=')' suffixOverrides=','>" +
            "<if test='orderNo != null'>#{orderNo},</if>" +
            "<if test='customerId != null'>#{customerId},</if>" +
            "<if test='customerName != null'>#{customerName},</if>" +
            "<if test='totalAmount != null'>#{totalAmount},</if>" +
            "<if test='status != null'>#{status},</if>" +
            "<if test='paymentMethod != null'>#{paymentMethod},</if>" +
            "<if test='paymentTime != null'>#{paymentTime},</if>" +
            "<if test='shippingAddress != null'>#{shippingAddress},</if>" +
            "<if test='remark != null'>#{remark},</if>" +
            "<if test='createTime != null'>#{createTime},</if>" +
            "<if test='updateTime != null'>#{updateTime},</if>" +
            "</trim>" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSelective(Order order);

    /**
     * 根据ID更新订单 - 主库（自动判断）
     */
    @Update("UPDATE orders SET order_no = #{orderNo}, customer_id = #{customerId}, customer_name = #{customerName}, " +
            "total_amount = #{totalAmount}, status = #{status}, payment_method = #{paymentMethod}, " +
            "payment_time = #{paymentTime}, shipping_address = #{shippingAddress}, remark = #{remark}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int updateById(Order order);

    /**
     * 选择性更新订单 - 主库（自动判断）
     */
    @Update("<script>" +
            "UPDATE orders " +
            "<set>" +
            "<if test='orderNo != null'>order_no = #{orderNo},</if>" +
            "<if test='customerId != null'>customer_id = #{customerId},</if>" +
            "<if test='customerName != null'>customer_name = #{customerName},</if>" +
            "<if test='totalAmount != null'>total_amount = #{totalAmount},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='paymentMethod != null'>payment_method = #{paymentMethod},</if>" +
            "<if test='paymentTime != null'>payment_time = #{paymentTime},</if>" +
            "<if test='shippingAddress != null'>shipping_address = #{shippingAddress},</if>" +
            "<if test='remark != null'>remark = #{remark},</if>" +
            "<if test='updateTime != null'>update_time = #{updateTime},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateByIdSelective(Order order);

    /**
     * 根据ID删除订单 - 主库（自动判断）
     */
    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 批量插入订单 - 主库
     */
    @DataSource(DataSourceType.MASTER)
    @Insert("<script>" +
            "INSERT INTO orders (order_no, customer_id, customer_name, total_amount, status, payment_method, " +
            "payment_time, shipping_address, remark, create_time, update_time) VALUES " +
            "<foreach collection='list' item='order' separator=','>" +
            "(#{order.orderNo}, #{order.customerId}, #{order.customerName}, #{order.totalAmount}, " +
            "#{order.status}, #{order.paymentMethod}, #{order.paymentTime}, #{order.shippingAddress}, " +
            "#{order.remark}, #{order.createTime}, #{order.updateTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(List<Order> orders);

    /**
     * 批量删除订单 - 主库
     */
    @DataSource(DataSourceType.MASTER)
    @Delete("<script>" +
            "DELETE FROM orders WHERE id IN " +
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDelete(List<Long> ids);
}
