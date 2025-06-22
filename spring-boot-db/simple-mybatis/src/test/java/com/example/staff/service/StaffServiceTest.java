package com.example.staff.service;

import com.example.staff.domain.Gender;
import com.example.staff.domain.Staff;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaffService测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StaffServiceTest {

    @Autowired
    private StaffService staffService;

    @Test
    void testGetById() {
        Staff staff = staffService.getById(1);
        assertNotNull(staff);
        assertEquals("EMP001", staff.getCode());
        assertEquals("张三", staff.getName());
    }

    @Test
    void testGetByIdWithNull() {
        Staff staff = staffService.getById(null);
        assertNull(staff);
    }

    @Test
    void testGetByCode() {
        Staff staff = staffService.getByCode("EMP002");
        assertNotNull(staff);
        assertEquals("李四", staff.getName());
    }

    @Test
    void testGetByCodeWithNull() {
        Staff staff = staffService.getByCode(null);
        assertNull(staff);
    }

    @Test
    void testGetAllStaff() {
        List<Staff> staffList = staffService.getAllStaff();
        assertNotNull(staffList);
        assertTrue(staffList.size() >= 5);
    }

    @Test
    void testGetByNameLike() {
        List<Staff> staffList = staffService.getByNameLike("张");
        assertNotNull(staffList);
        assertTrue(staffList.size() >= 1);
        assertTrue(staffList.stream().anyMatch(s -> s.getName().contains("张")));
    }

    @Test
    void testGetByAgeRange() {
        List<Staff> staffList = staffService.getByAgeRange(25, 30);
        assertNotNull(staffList);
        assertTrue(staffList.size() >= 2);
        assertTrue(staffList.stream().allMatch(s -> s.getAge() >= 25 && s.getAge() <= 30));
    }

    @Test
    void testGetByGender() {
        List<Staff> maleStaff = staffService.getByGender(Gender.MALE);
        assertNotNull(maleStaff);
        assertTrue(maleStaff.size() >= 2);
        assertTrue(maleStaff.stream().allMatch(s -> Gender.MALE.equals(s.getGender())));
    }

    @Test
    void testGetByPage() {
        List<Staff> staffList = staffService.getByPage(1, 3);
        assertNotNull(staffList);
        assertEquals(3, staffList.size());
    }

    @Test
    void testGetTotalCount() {
        int count = staffService.getTotalCount();
        assertTrue(count >= 5);
    }

    @Test
    void testAddStaff() {
        Staff staff = new Staff();
        staff.setCode("SERVICE001");
        staff.setName("服务测试员工");
        staff.setAge(25);
        staff.setGender(Gender.MALE);
        staff.setPhone("13900139001");
        staff.setEmail("service@example.com");

        int result = staffService.addStaff(staff);
        assertEquals(1, result);
        assertNotNull(staff.getId());
        assertNotNull(staff.getCreateTime());
        assertNotNull(staff.getModifyTime());

        // 验证插入成功
        Staff inserted = staffService.getById(staff.getId());
        assertNotNull(inserted);
        assertEquals("SERVICE001", inserted.getCode());
    }

    @Test
    void testAddStaffWithNullData() {
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(null);
        });
    }

    @Test
    void testAddStaffWithEmptyCode() {
        Staff staff = new Staff();
        staff.setName("测试员工");
        
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(staff);
        });
    }

    @Test
    void testAddStaffWithDuplicateCode() {
        Staff staff = new Staff();
        staff.setCode("EMP001"); // 使用已存在的编码
        staff.setName("重复编码员工");

        assertThrows(IllegalArgumentException.class, () -> {
            staffService.addStaff(staff);
        });
    }

    @Test
    void testAddStaffSelective() {
        Staff staff = new Staff();
        staff.setCode("SERVICE002");
        staff.setName("选择性插入员工");
        staff.setAge(30);

        int result = staffService.addStaffSelective(staff);
        assertEquals(1, result);
        assertNotNull(staff.getId());

        // 验证插入成功
        Staff inserted = staffService.getById(staff.getId());
        assertNotNull(inserted);
        assertEquals("SERVICE002", inserted.getCode());
        assertEquals("选择性插入员工", inserted.getName());
    }

    @Test
    void testBatchAddStaff() {
        Staff staff1 = new Staff();
        staff1.setCode("BATCH_SERVICE001");
        staff1.setName("批量服务员工1");
        staff1.setAge(25);

        Staff staff2 = new Staff();
        staff2.setCode("BATCH_SERVICE002");
        staff2.setName("批量服务员工2");
        staff2.setAge(26);

        List<Staff> staffList = Arrays.asList(staff1, staff2);
        int result = staffService.batchAddStaff(staffList);
        assertEquals(2, result);

        // 验证插入成功
        Staff inserted1 = staffService.getByCode("BATCH_SERVICE001");
        Staff inserted2 = staffService.getByCode("BATCH_SERVICE002");
        assertNotNull(inserted1);
        assertNotNull(inserted2);
    }

    @Test
    void testUpdateStaffSelective() {
        // 先添加一个员工
        Staff staff = new Staff();
        staff.setCode("UPDATE001");
        staff.setName("更新测试员工");
        staff.setAge(25);
        staff.setGender(Gender.MALE);
        staffService.addStaff(staff);

        // 更新部分字段
        Staff updateStaff = new Staff();
        updateStaff.setId(staff.getId());
        updateStaff.setName("更新后的姓名");
        updateStaff.setAge(26);

        int result = staffService.updateStaffSelective(updateStaff);
        assertEquals(1, result);

        // 验证更新成功
        Staff updated = staffService.getById(staff.getId());
        assertNotNull(updated);
        assertEquals("更新后的姓名", updated.getName());
        assertEquals(26, updated.getAge());
        assertEquals(Gender.MALE, updated.getGender()); // 未更新的字段保持不变
    }

    @Test
    void testUpdateStaffWithNullId() {
        Staff staff = new Staff();
        staff.setName("测试");

        assertThrows(IllegalArgumentException.class, () -> {
            staffService.updateStaffSelective(staff);
        });
    }

    @Test
    void testDeleteById() {
        // 先添加一个员工
        Staff staff = new Staff();
        staff.setCode("DELETE_SERVICE001");
        staff.setName("删除测试员工");
        staffService.addStaff(staff);

        // 删除员工
        int result = staffService.deleteById(staff.getId());
        assertEquals(1, result);

        // 验证删除成功
        Staff deleted = staffService.getById(staff.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteByIdWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.deleteById(null);
        });
    }

    @Test
    void testBatchDelete() {
        // 先添加两个员工
        Staff staff1 = new Staff();
        staff1.setCode("BATCH_DELETE001");
        staff1.setName("批量删除员工1");
        staffService.addStaff(staff1);

        Staff staff2 = new Staff();
        staff2.setCode("BATCH_DELETE002");
        staff2.setName("批量删除员工2");
        staffService.addStaff(staff2);

        // 批量删除
        List<Integer> ids = Arrays.asList(staff1.getId(), staff2.getId());
        int result = staffService.batchDelete(ids);
        assertEquals(2, result);

        // 验证删除成功
        Staff deleted1 = staffService.getById(staff1.getId());
        Staff deleted2 = staffService.getById(staff2.getId());
        assertNull(deleted1);
        assertNull(deleted2);
    }

    @Test
    void testIsCodeExists() {
        boolean exists = staffService.isCodeExists("EMP001");
        assertTrue(exists);

        boolean notExists = staffService.isCodeExists("NOT_EXISTS");
        assertFalse(notExists);
    }

    @Test
    void testIsCodeExistsWithExcludeId() {
        // 获取一个现有员工
        Staff staff = staffService.getByCode("EMP001");
        assertNotNull(staff);

        // 测试排除自己的情况
        boolean exists = staffService.isCodeExists("EMP001", staff.getId());
        assertFalse(exists);

        // 测试不排除的情况
        boolean existsWithOtherId = staffService.isCodeExists("EMP001", 999);
        assertTrue(existsWithOtherId);
    }
}
