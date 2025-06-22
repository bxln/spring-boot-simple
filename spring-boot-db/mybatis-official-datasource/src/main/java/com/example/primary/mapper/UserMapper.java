package com.example.primary.mapper;

import com.example.primary.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 对应主数据源，所有SQL在XML文件中定义
 * 
 * @author example
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<User> selectAll();

    /**
     * 根据状态查询用户
     * 
     * @param status 用户状态
     * @return 用户列表
     */
    List<User> selectByStatus(@Param("status") Integer status);

    /**
     * 插入用户
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    int update(User user);

    /**
     * 根据ID删除用户
     * 
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 统计用户总数
     * 
     * @return 用户总数
     */
    long count();

    /**
     * 根据状态统计用户数
     * 
     * @param status 用户状态
     * @return 用户数量
     */
    long countByStatus(@Param("status") Integer status);
}
