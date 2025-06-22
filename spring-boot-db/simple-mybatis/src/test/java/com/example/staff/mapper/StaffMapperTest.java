package com.example.staff.mapper;

import com.example.staff.domain.Gender;
import com.example.staff.domain.Staff;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaffMapper测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StaffMapperTest {

    @Autowired
    private StaffMapper staffMapper;

    @Test
    void testSelectByPrimaryKey() {
        Staff staff = staffMapper.selectByPrimaryKey(1);
        assertNotNull(staff);
        assertEquals("EMP001", staff.getCode());
        assertEquals("张三", staff.getName());
    }

    @Test
    void testSelectByCode() {
        Staff staff = staffMapper.selectByCode("EMP002");
        assertNotNull(staff);
        assertEquals("李四", staff.getName());
        assertEquals(30, staff.getAge());
    }

    @Test
    void testSelectAll() {
        List<Staff> staffList = staffMapper.selectAll();
        assertNotNull(staffList);
        assertTrue(staffList.size() >= 5);
    }

    @Test
    void testSelectByNameLike() {
        List<Staff> staffList = staffMapper.selectByNameLike("张");
        assertNotNull(staffList);
        assertTrue(staffList.size() >= 1);
        assertTrue(staffList.stream().anyMatch(s -> s.getName().contains("张")));
    }

    @Test
    void testSelectByAgeRange() {
        List<Staff> staffList = staffMapper.selectByAgeRange(25, 30);
        assertNotNull(staffList);
        assertTrue(staffList.size() >= 2);
        assertTrue(staffList.stream().allMatch(s -> s.getAge() >= 25 && s.getAge() <= 30));
    }

    @Test
    void testSelectByGender() {
        List<Staff> maleStaff = staffMapper.selectByGender(Gender.MALE);
        assertNotNull(maleStaff);
        assertTrue(maleStaff.size() >= 2);
        assertTrue(maleStaff.stream().allMatch(s -> Gender.MALE.equals(s.getGender())));
    }

    @Test
    void testSelectByPage() {
        List<Staff> staffList = staffMapper.selectByPage(0, 3);
        assertNotNull(staffList);
        assertEquals(3, staffList.size());
    }

    @Test
    void testCountAll() {
        int count = staffMapper.countAll();
        assertTrue(count >= 5);
    }

    @Test
    void testCountByCondition() {
        Staff condition = new Staff();
        condition.setGender(Gender.MALE);
        int count = staffMapper.countByCondition(condition);
        assertTrue(count >= 2);
    }

    @Test
    void testInsert() {
        Staff staff = new Staff();
        staff.setCode("TEST001");
        staff.setName("测试员工");
        staff.setAge(25);
        staff.setGender(Gender.MALE);
        staff.setPhone("13900139001");
        staff.setEmail("test@example.com");
        staff.setCreateTime(new Date());
        staff.setModifyTime(new Date());

        int result = staffMapper.insert(staff);
        assertEquals(1, result);
        assertNotNull(staff.getId());

        // 验证插入成功
        Staff inserted = staffMapper.selectByPrimaryKey(staff.getId());
        assertNotNull(inserted);
        assertEquals("TEST001", inserted.getCode());
        assertEquals("测试员工", inserted.getName());
    }

    @Test
    void testInsertSelective() {
        Staff staff = new Staff();
        staff.setCode("TEST002");
        staff.setName("测试员工2");
        staff.setAge(30);
        // 不设置其他字段

        int result = staffMapper.insertSelective(staff);
        assertEquals(1, result);
        assertNotNull(staff.getId());

        // 验证插入成功
        Staff inserted = staffMapper.selectByPrimaryKey(staff.getId());
        assertNotNull(inserted);
        assertEquals("TEST002", inserted.getCode());
        assertEquals("测试员工2", inserted.getName());
        assertEquals(30, inserted.getAge());
    }

    @Test
    void testUpdateByPrimaryKeySelective() {
        // 先插入一条记录
        Staff staff = new Staff();
        staff.setCode("TEST003");
        staff.setName("测试员工3");
        staff.setAge(25);
        staff.setGender(Gender.WOMAN);
        staff.setCreateTime(new Date());
        staff.setModifyTime(new Date());
        staffMapper.insert(staff);

        // 更新部分字段
        Staff updateStaff = new Staff();
        updateStaff.setId(staff.getId());
        updateStaff.setName("更新后的姓名");
        updateStaff.setAge(26);

        int result = staffMapper.updateByPrimaryKeySelective(updateStaff);
        assertEquals(1, result);

        // 验证更新成功
        Staff updated = staffMapper.selectByPrimaryKey(staff.getId());
        assertNotNull(updated);
        assertEquals("更新后的姓名", updated.getName());
        assertEquals(26, updated.getAge());
        assertEquals(Gender.WOMAN, updated.getGender()); // 未更新的字段保持不变
    }

    @Test
    void testDeleteByPrimaryKey() {
        // 先插入一条记录
        Staff staff = new Staff();
        staff.setCode("TEST004");
        staff.setName("测试员工4");
        staff.setCreateTime(new Date());
        staff.setModifyTime(new Date());
        staffMapper.insert(staff);

        // 删除记录
        int result = staffMapper.deleteByPrimaryKey(staff.getId());
        assertEquals(1, result);

        // 验证删除成功
        Staff deleted = staffMapper.selectByPrimaryKey(staff.getId());
        assertNull(deleted);
    }

    @Test
    void testBatchInsert() {
        Staff staff1 = new Staff();
        staff1.setCode("BATCH001");
        staff1.setName("批量员工1");
        staff1.setAge(25);
        staff1.setCreateTime(new Date());
        staff1.setModifyTime(new Date());

        Staff staff2 = new Staff();
        staff2.setCode("BATCH002");
        staff2.setName("批量员工2");
        staff2.setAge(26);
        staff2.setCreateTime(new Date());
        staff2.setModifyTime(new Date());

        List<Staff> staffList = Arrays.asList(staff1, staff2);
        int result = staffMapper.batchInsert(staffList);
        assertEquals(2, result);

        // 验证插入成功
        Staff inserted1 = staffMapper.selectByCode("BATCH001");
        Staff inserted2 = staffMapper.selectByCode("BATCH002");
        assertNotNull(inserted1);
        assertNotNull(inserted2);
        assertEquals("批量员工1", inserted1.getName());
        assertEquals("批量员工2", inserted2.getName());
    }

    @Test
    void testBatchDelete() {
        // 先插入两条记录
        Staff staff1 = new Staff();
        staff1.setCode("DELETE001");
        staff1.setName("删除员工1");
        staff1.setCreateTime(new Date());
        staff1.setModifyTime(new Date());
        staffMapper.insert(staff1);

        Staff staff2 = new Staff();
        staff2.setCode("DELETE002");
        staff2.setName("删除员工2");
        staff2.setCreateTime(new Date());
        staff2.setModifyTime(new Date());
        staffMapper.insert(staff2);

        // 批量删除
        List<Integer> ids = Arrays.asList(staff1.getId(), staff2.getId());
        int result = staffMapper.batchDelete(ids);
        assertEquals(2, result);

        // 验证删除成功
        Staff deleted1 = staffMapper.selectByPrimaryKey(staff1.getId());
        Staff deleted2 = staffMapper.selectByPrimaryKey(staff2.getId());
        assertNull(deleted1);
        assertNull(deleted2);
    }
}
