package com.example.staff.mapper;

import com.example.staff.domain.Gender;
import com.example.staff.domain.Gender;
import com.example.staff.domain.Staff;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StaffMapper {
    // 基础CRUD操作
    int deleteByPrimaryKey(Integer id);

    int insert(Staff record);

    int insertSelective(Staff record);

    Staff selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Staff record);

    int updateByPrimaryKey(Staff record);

    // 扩展查询方法

    /**
     * 查询所有员工
     */
    List<Staff> selectAll();

    /**
     * 根据员工编码查询
     */
    Staff selectByCode(String code);

    /**
     * 根据姓名模糊查询
     */
    List<Staff> selectByNameLike(String name);

    /**
     * 根据年龄范围查询
     * <p>
     * 注意：在 XML 映射文件中，如果使用了动态 SQL，那么在 XML 文件中使用小于号（<）和大于号（>）时，需要进行转义，否则可能会导致 XML 解析错误。
     * 原始符号	转义符号
     * <	&lt;
     * <=	&lt;=
     * >	&gt;
     * >=	&gt;=
     */
    List<Staff> selectByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * 根据性别查询
     */
    List<Staff> selectByGender(Gender gender);

    /**
     * 分页查询
     */
    List<Staff> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计总数
     */
    int countAll();

    /**
     * 根据条件统计
     */
    int countByCondition(Staff condition);

    /**
     * 批量插入
     */
    int batchInsert(List<Staff> staffList);

    /**
     * 批量删除
     */
    int batchDelete(List<Integer> ids);
}