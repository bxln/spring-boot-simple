package com.example.secondary.mapper;

import com.example.secondary.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单Mapper接口
 * 对应第二数据源，所有SQL在XML文件中定义
 * 
 * @author example
 */
@Mapper
public interface OrderMapper {

    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID
     * @return 订单信息
     */
    Order selectById(@Param("id") Long id);

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询订单列表
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据状态查询订单列表
     * 
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> selectByStatus(@Param("status") Integer status);

    /**
     * 查询所有订单
     * 
     * @return 订单列表
     */
    List<Order> selectAll();

    /**
     * 插入订单
     * 
     * @param order 订单信息
     * @return 影响行数
     */
    int insert(Order order);

    /**
     * 更新订单信息
     * 
     * @param order 订单信息
     * @return 影响行数
     */
    int update(Order order);

    /**
     * 更新订单状态
     * 
     * @param id 订单ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据ID删除订单
     * 
     * @param id 订单ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 统计订单总数
     * 
     * @return 订单总数
     */
    long count();

    /**
     * 根据状态统计订单数
     * 
     * @param status 订单状态
     * @return 订单数量
     */
    long countByStatus(@Param("status") Integer status);

    /**
     * 根据用户ID统计订单数
     * 
     * @param userId 用户ID
     * @return 订单数量
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 计算用户订单总金额
     * 
     * @param userId 用户ID
     * @return 总金额
     */
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);
}
