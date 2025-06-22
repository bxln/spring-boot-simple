package com.example.mapper;

import com.example.annotation.DataSource;
import com.example.annotation.DataSourceType;
import com.example.domain.Customer;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户Mapper接口 - 使用注解驱动
 * 演示动态数据源的读写分离
 */
@Repository
public interface CustomerMapper {

    /**
     * 根据ID查询客户 - 从库
     */
    @DataSource(DataSourceType.SLAVE)
    @Select("SELECT * FROM customers WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "customerCode", column = "customer_code"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "age", column = "age"),
        @Result(property = "address", column = "address"),
        @Result(property = "status", column = "status"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    Customer selectById(Long id);

    /**
     * 根据客户编码查询客户 - 从库
     */
    @DataSource(DataSourceType.SLAVE)
    @Select("SELECT * FROM customers WHERE customer_code = #{customerCode}")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "customerCode", column = "customer_code"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "age", column = "age"),
        @Result(property = "address", column = "address"),
        @Result(property = "status", column = "status"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    Customer selectByCustomerCode(String customerCode);

    /**
     * 查询所有客户 - 从库（自动判断）
     */
    @Select("SELECT * FROM customers ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "customerCode", column = "customer_code"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "age", column = "age"),
        @Result(property = "address", column = "address"),
        @Result(property = "status", column = "status"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Customer> selectAll();

    /**
     * 根据状态查询客户 - 从库（自动判断）
     */
    @Select("SELECT * FROM customers WHERE status = #{status} ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "customerCode", column = "customer_code"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "age", column = "age"),
        @Result(property = "address", column = "address"),
        @Result(property = "status", column = "status"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Customer> selectByStatus(String status);

    /**
     * 根据年龄范围查询客户 - 从库（自动判断）
     */
    @Select("<script>" +
            "SELECT * FROM customers WHERE 1=1 " +
            "<if test='minAge != null'> AND age >= #{minAge} </if>" +
            "<if test='maxAge != null'> AND age <= #{maxAge} </if>" +
            "ORDER BY age ASC" +
            "</script>")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "customerCode", column = "customer_code"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "age", column = "age"),
        @Result(property = "address", column = "address"),
        @Result(property = "status", column = "status"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Customer> selectByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * 分页查询客户 - 从库（自动判断）
     */
    @Select("SELECT * FROM customers ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "customerCode", column = "customer_code"),
        @Result(property = "customerName", column = "customer_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "age", column = "age"),
        @Result(property = "address", column = "address"),
        @Result(property = "status", column = "status"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Customer> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计客户总数 - 从库（自动判断）
     */
    @Select("SELECT COUNT(*) FROM customers")
    int countAll();

    /**
     * 根据状态统计客户数 - 从库（自动判断）
     */
    @Select("SELECT COUNT(*) FROM customers WHERE status = #{status}")
    int countByStatus(String status);

    /**
     * 插入客户 - 主库（自动判断）
     */
    @Insert("INSERT INTO customers (customer_code, customer_name, email, phone, age, address, status, remark, create_time, update_time) " +
            "VALUES (#{customerCode}, #{customerName}, #{email}, #{phone}, #{age}, #{address}, #{status}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);

    /**
     * 选择性插入客户 - 主库（自动判断）
     */
    @Insert("<script>" +
            "INSERT INTO customers " +
            "<trim prefix='(' suffix=')' suffixOverrides=','>" +
            "<if test='customerCode != null'>customer_code,</if>" +
            "<if test='customerName != null'>customer_name,</if>" +
            "<if test='email != null'>email,</if>" +
            "<if test='phone != null'>phone,</if>" +
            "<if test='age != null'>age,</if>" +
            "<if test='address != null'>address,</if>" +
            "<if test='status != null'>status,</if>" +
            "<if test='remark != null'>remark,</if>" +
            "<if test='createTime != null'>create_time,</if>" +
            "<if test='updateTime != null'>update_time,</if>" +
            "</trim>" +
            "<trim prefix='VALUES (' suffix=')' suffixOverrides=','>" +
            "<if test='customerCode != null'>#{customerCode},</if>" +
            "<if test='customerName != null'>#{customerName},</if>" +
            "<if test='email != null'>#{email},</if>" +
            "<if test='phone != null'>#{phone},</if>" +
            "<if test='age != null'>#{age},</if>" +
            "<if test='address != null'>#{address},</if>" +
            "<if test='status != null'>#{status},</if>" +
            "<if test='remark != null'>#{remark},</if>" +
            "<if test='createTime != null'>#{createTime},</if>" +
            "<if test='updateTime != null'>#{updateTime},</if>" +
            "</trim>" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSelective(Customer customer);

    /**
     * 根据ID更新客户 - 主库（自动判断）
     */
    @Update("UPDATE customers SET customer_code = #{customerCode}, customer_name = #{customerName}, " +
            "email = #{email}, phone = #{phone}, age = #{age}, address = #{address}, " +
            "status = #{status}, remark = #{remark}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int updateById(Customer customer);

    /**
     * 选择性更新客户 - 主库（自动判断）
     */
    @Update("<script>" +
            "UPDATE customers " +
            "<set>" +
            "<if test='customerCode != null'>customer_code = #{customerCode},</if>" +
            "<if test='customerName != null'>customer_name = #{customerName},</if>" +
            "<if test='email != null'>email = #{email},</if>" +
            "<if test='phone != null'>phone = #{phone},</if>" +
            "<if test='age != null'>age = #{age},</if>" +
            "<if test='address != null'>address = #{address},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='remark != null'>remark = #{remark},</if>" +
            "<if test='updateTime != null'>update_time = #{updateTime},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateByIdSelective(Customer customer);

    /**
     * 根据ID删除客户 - 主库（自动判断）
     */
    @Delete("DELETE FROM customers WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 批量插入客户 - 主库
     */
    @DataSource(DataSourceType.MASTER)
    @Insert("<script>" +
            "INSERT INTO customers (customer_code, customer_name, email, phone, age, address, status, remark, create_time, update_time) VALUES " +
            "<foreach collection='list' item='customer' separator=','>" +
            "(#{customer.customerCode}, #{customer.customerName}, #{customer.email}, #{customer.phone}, " +
            "#{customer.age}, #{customer.address}, #{customer.status}, #{customer.remark}, " +
            "#{customer.createTime}, #{customer.updateTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(List<Customer> customers);

    /**
     * 批量删除客户 - 主库
     */
    @DataSource(DataSourceType.MASTER)
    @Delete("<script>" +
            "DELETE FROM customers WHERE id IN " +
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDelete(List<Long> ids);
}
