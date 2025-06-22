package com.example.staff.service.impl;

import com.example.staff.domain.Gender;
import com.example.staff.domain.Staff;
import com.example.staff.mapper.StaffMapper;
import com.example.staff.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 员工服务实现类
 */
@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffMapper staffMapper;

    @Override
    @Transactional(readOnly = true)
    public Staff getById(Integer id) {
        if (id == null) {
            return null;
        }
        return staffMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Staff getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return staffMapper.selectByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getAllStaff() {
        return staffMapper.selectAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getByNameLike(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllStaff();
        }
        return staffMapper.selectByNameLike(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getByAgeRange(Integer minAge, Integer maxAge) {
        return staffMapper.selectByAgeRange(minAge, maxAge);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getByGender(Gender gender) {
        if (gender == null) {
            return getAllStaff();
        }
        return staffMapper.selectByGender(gender);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        return staffMapper.selectByPage(offset, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalCount() {
        return staffMapper.countAll();
    }

    @Override
    @Transactional(readOnly = true)
    public int getCountByCondition(Staff condition) {
        if (condition == null) {
            return getTotalCount();
        }
        return staffMapper.countByCondition(condition);
    }

    @Override
    public int addStaff(Staff staff) {
        if (staff == null) {
            throw new IllegalArgumentException("员工信息不能为空");
        }
        validateStaff(staff);
        
        // 检查员工编码是否已存在
        if (isCodeExists(staff.getCode())) {
            throw new IllegalArgumentException("员工编码已存在: " + staff.getCode());
        }
        
        // 设置创建时间和修改时间
        Date now = new Date();
        staff.setCreateTime(now);
        staff.setModifyTime(now);
        
        return staffMapper.insert(staff);
    }

    @Override
    public int addStaffSelective(Staff staff) {
        if (staff == null) {
            throw new IllegalArgumentException("员工信息不能为空");
        }
        
        // 检查员工编码是否已存在
        if (staff.getCode() != null && isCodeExists(staff.getCode())) {
            throw new IllegalArgumentException("员工编码已存在: " + staff.getCode());
        }
        
        // 设置创建时间和修改时间
        Date now = new Date();
        if (staff.getCreateTime() == null) {
            staff.setCreateTime(now);
        }
        if (staff.getModifyTime() == null) {
            staff.setModifyTime(now);
        }
        
        return staffMapper.insertSelective(staff);
    }

    @Override
    public int batchAddStaff(List<Staff> staffList) {
        if (staffList == null || staffList.isEmpty()) {
            return 0;
        }
        
        Date now = new Date();
        for (Staff staff : staffList) {
            validateStaff(staff);
            
            // 检查员工编码是否已存在
            if (isCodeExists(staff.getCode())) {
                throw new IllegalArgumentException("员工编码已存在: " + staff.getCode());
            }
            
            // 设置创建时间和修改时间
            staff.setCreateTime(now);
            staff.setModifyTime(now);
        }
        
        return staffMapper.batchInsert(staffList);
    }

    @Override
    public int updateStaff(Staff staff) {
        if (staff == null || staff.getId() == null) {
            throw new IllegalArgumentException("员工ID不能为空");
        }
        
        validateStaff(staff);
        
        // 检查员工编码是否已存在（排除当前员工）
        if (isCodeExists(staff.getCode(), staff.getId())) {
            throw new IllegalArgumentException("员工编码已存在: " + staff.getCode());
        }
        
        // 设置修改时间
        staff.setModifyTime(new Date());
        
        return staffMapper.updateByPrimaryKey(staff);
    }

    @Override
    public int updateStaffSelective(Staff staff) {
        if (staff == null || staff.getId() == null) {
            throw new IllegalArgumentException("员工ID不能为空");
        }
        
        // 检查员工编码是否已存在（排除当前员工）
        if (staff.getCode() != null && isCodeExists(staff.getCode(), staff.getId())) {
            throw new IllegalArgumentException("员工编码已存在: " + staff.getCode());
        }
        
        // 设置修改时间
        staff.setModifyTime(new Date());
        
        return staffMapper.updateByPrimaryKeySelective(staff);
    }

    @Override
    public int deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("员工ID不能为空");
        }
        return staffMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return staffMapper.batchDelete(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCodeExists(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        return staffMapper.selectByCode(code) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCodeExists(String code, Integer excludeId) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        Staff existingStaff = staffMapper.selectByCode(code);
        return existingStaff != null && !existingStaff.getId().equals(excludeId);
    }

    /**
     * 验证员工信息
     */
    private void validateStaff(Staff staff) {
        if (staff.getCode() == null || staff.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("员工编码不能为空");
        }
        if (staff.getName() == null || staff.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("员工姓名不能为空");
        }
        if (staff.getAge() != null && (staff.getAge() < 0 || staff.getAge() > 150)) {
            throw new IllegalArgumentException("员工年龄必须在0-150之间");
        }
    }
}
