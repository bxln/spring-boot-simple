package com.example.primary.service;

import com.example.primary.domain.User;
import com.example.primary.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务类 - 主数据源
 */
@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    /**
     * 根据状态查询用户
     */
    public List<User> getUsersByStatus(String status) {
        return userMapper.selectByStatus(status);
    }

    /**
     * 根据年龄范围查询用户
     */
    public List<User> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        return userMapper.selectByAgeRange(minAge, maxAge);
    }

    /**
     * 分页查询用户
     */
    public List<User> getUsersByPage(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return userMapper.selectByPage(offset, pageSize);
    }

    /**
     * 统计用户总数
     */
    public int getTotalCount() {
        return userMapper.countAll();
    }

    /**
     * 根据状态统计用户数
     */
    public int getCountByStatus(String status) {
        return userMapper.countByStatus(status);
    }

    /**
     * 创建用户
     */
    public int createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已存在: " + user.getUsername());
        }
        
        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus("ACTIVE");
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        return userMapper.insert(user);
    }

    /**
     * 更新用户
     */
    public int updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByIdSelective(user);
    }

    /**
     * 删除用户
     */
    public int deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userMapper.deleteById(id);
    }

    /**
     * 批量创建用户
     */
    public int batchCreateUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("用户列表不能为空");
        }
        
        LocalDateTime now = LocalDateTime.now();
        for (User user : users) {
            if (user.getStatus() == null) {
                user.setStatus("ACTIVE");
            }
            user.setCreateTime(now);
            user.setUpdateTime(now);
        }
        
        return userMapper.batchInsert(users);
    }

    /**
     * 批量删除用户
     */
    public int batchDeleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }
        return userMapper.batchDelete(ids);
    }
}
