package com.example.staff.service;

import com.example.staff.domain.Gender;
import com.example.staff.domain.Staff;

import java.util.List;

/**
 * 员工服务接口
 */
public interface StaffService {

    /**
     * 根据ID查询员工
     */
    Staff getById(Integer id);

    /**
     * 根据员工编码查询
     */
    Staff getByCode(String code);

    /**
     * 查询所有员工
     */
    List<Staff> getAllStaff();

    /**
     * 根据姓名模糊查询
     */
    List<Staff> getByNameLike(String name);

    /**
     * 根据年龄范围查询
     */
    List<Staff> getByAgeRange(Integer minAge, Integer maxAge);

    /**
     * 根据性别查询
     */
    List<Staff> getByGender(Gender gender);

    /**
     * 分页查询
     */
    List<Staff> getByPage(Integer pageNum, Integer pageSize);

    /**
     * 统计总数
     */
    int getTotalCount();

    /**
     * 根据条件统计
     */
    int getCountByCondition(Staff condition);

    /**
     * 新增员工
     */
    int addStaff(Staff staff);

    /**
     * 选择性新增员工
     */
    int addStaffSelective(Staff staff);

    /**
     * 批量新增员工
     */
    int batchAddStaff(List<Staff> staffList);

    /**
     * 更新员工信息
     */
    int updateStaff(Staff staff);

    /**
     * 选择性更新员工信息
     */
    int updateStaffSelective(Staff staff);

    /**
     * 根据ID删除员工
     */
    int deleteById(Integer id);

    /**
     * 批量删除员工
     */
    int batchDelete(List<Integer> ids);

    /**
     * 检查员工编码是否存在
     */
    boolean isCodeExists(String code);

    /**
     * 检查员工编码是否存在（排除指定ID）
     */
    boolean isCodeExists(String code, Integer excludeId);
}
