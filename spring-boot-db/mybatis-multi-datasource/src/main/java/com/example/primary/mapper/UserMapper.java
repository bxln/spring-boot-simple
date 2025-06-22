package com.example.primary.mapper;

import com.example.primary.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口 - 主数据源
 */
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    User selectById(Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 根据状态查询用户
     */
    List<User> selectByStatus(String status);

    /**
     * 根据年龄范围查询用户
     */
    List<User> selectByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * 分页查询用户
     */
    List<User> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计用户总数
     */
    int countAll();

    /**
     * 根据状态统计用户数
     */
    int countByStatus(String status);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 选择性插入用户
     */
    int insertSelective(User user);

    /**
     * 根据ID更新用户
     */
    int updateById(User user);

    /**
     * 选择性更新用户
     */
    int updateByIdSelective(User user);

    /**
     * 根据ID删除用户
     */
    int deleteById(Long id);

    /**
     * 批量插入用户
     */
    int batchInsert(List<User> users);

    /**
     * 批量删除用户
     */
    int batchDelete(List<Long> ids);
}
