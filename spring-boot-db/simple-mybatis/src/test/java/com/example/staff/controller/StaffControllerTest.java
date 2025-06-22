package com.example.staff.controller;

import com.example.staff.domain.Gender;
import com.example.staff.domain.Staff;
import com.example.staff.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * StaffController测试类
 */
@WebMvcTest(StaffController.class)
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 使用 @SuppressWarnings 忽略弃用警告
    @SuppressWarnings("all")
    @MockBean
    private StaffService staffService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetById() throws Exception {
        Staff staff = createTestStaff();
        when(staffService.getById(1)).thenReturn(staff);

        mockMvc.perform(get("/api/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("EMP001"))
                .andExpect(jsonPath("$.data.name").value("张三"))
                .andExpect(jsonPath("$.data.gender").value("男"))
                .andExpect(jsonPath("$.message").value("查询成功"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(staffService.getById(999)).thenReturn(null);

        mockMvc.perform(get("/api/staff/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("员工不存在"));
    }

    @Test
    void testGetByCode() throws Exception {
        Staff staff = createTestStaff();
        when(staffService.getByCode("EMP001")).thenReturn(staff);

        mockMvc.perform(get("/api/staff/code/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("EMP001"))
                .andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    void testGetAllStaff() throws Exception {
        List<Staff> staffList = Arrays.asList(createTestStaff(), createTestStaff2());
        when(staffService.getAllStaff()).thenReturn(staffList);

        mockMvc.perform(get("/api/staff/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.message").value("查询成功"));
    }

    @Test
    void testSearchByName() throws Exception {
        List<Staff> staffList = Arrays.asList(createTestStaff());
        when(staffService.getByNameLike("张")).thenReturn(staffList);

        mockMvc.perform(get("/api/staff/search").param("name", "张"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void testGetByAgeRange() throws Exception {
        List<Staff> staffList = Arrays.asList(createTestStaff());
        when(staffService.getByAgeRange(25, 30)).thenReturn(staffList);

        mockMvc.perform(get("/api/staff/age-range")
                .param("minAge", "25")
                .param("maxAge", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetByPage() throws Exception {
        List<Staff> staffList = Arrays.asList(createTestStaff(), createTestStaff2());
        when(staffService.getByPage(1, 10)).thenReturn(staffList);
        when(staffService.getTotalCount()).thenReturn(2);

        mockMvc.perform(get("/api/staff/page")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testAddStaff() throws Exception {
        Staff staff = createTestStaff();
        when(staffService.addStaff(any(Staff.class))).thenReturn(1);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("新增成功"));
    }

    @Test
    void testAddStaffFailed() throws Exception {
        Staff staff = createTestStaff();
        when(staffService.addStaff(any(Staff.class))).thenReturn(0);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新增失败"));
    }

    @Test
    void testAddStaffWithException() throws Exception {
        Staff staff = createTestStaff();
        when(staffService.addStaff(any(Staff.class)))
                .thenThrow(new IllegalArgumentException("员工编码已存在"));

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新增失败: 员工编码已存在"));
    }

    @Test
    void testBatchAddStaff() throws Exception {
        List<Staff> staffList = Arrays.asList(createTestStaff(), createTestStaff2());
        when(staffService.batchAddStaff(anyList())).thenReturn(2);

        mockMvc.perform(post("/api/staff/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staffList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.message").value("批量新增成功，共新增2条记录"));
    }

    @Test
    void testUpdateStaff() throws Exception {
        Staff staff = createTestStaff();
        when(staffService.updateStaffSelective(any(Staff.class))).thenReturn(1);

        mockMvc.perform(put("/api/staff/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testUpdateStaffNotFound() throws Exception {
        Staff staff = createTestStaff();
        when(staffService.updateStaffSelective(any(Staff.class))).thenReturn(0);

        mockMvc.perform(put("/api/staff/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("更新失败，员工不存在"));
    }

    @Test
    void testDeleteStaff() throws Exception {
        when(staffService.deleteById(1)).thenReturn(1);

        mockMvc.perform(delete("/api/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testDeleteStaffNotFound() throws Exception {
        when(staffService.deleteById(999)).thenReturn(0);

        mockMvc.perform(delete("/api/staff/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败，员工不存在"));
    }

    @Test
    void testBatchDeleteStaff() throws Exception {
        List<Integer> ids = Arrays.asList(1, 2);
        when(staffService.batchDelete(anyList())).thenReturn(2);

        mockMvc.perform(delete("/api/staff/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.message").value("批量删除成功，共删除2条记录"));
    }

    @Test
    void testGetByGender() throws Exception {
        List<Staff> maleStaff = Arrays.asList(createTestStaff());
        when(staffService.getByGender(Gender.MALE)).thenReturn(maleStaff);

        mockMvc.perform(get("/api/staff/gender/男"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].gender").value("男"))
                .andExpect(jsonPath("$.message").value("查询成功"));
    }

    @Test
    void testGetByGenderInvalid() throws Exception {
        mockMvc.perform(get("/api/staff/gender/无效性别"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无效的性别参数: 无效性别"));
    }

    @Test
    void testGetAllGenders() throws Exception {
        mockMvc.perform(get("/api/staff/genders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.MALE").value("男"))
                .andExpect(jsonPath("$.data.FEMALE").value("女"))
                .andExpect(jsonPath("$.data.OTHER").value("其他"))
                .andExpect(jsonPath("$.message").value("获取成功"));
    }

    @Test
    void testGetTotalCount() throws Exception {
        when(staffService.getTotalCount()).thenReturn(10);

        mockMvc.perform(get("/api/staff/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(10))
                .andExpect(jsonPath("$.message").value("统计成功"));
    }

    private Staff createTestStaff() {
        Staff staff = new Staff();
        staff.setId(1);
        staff.setCode("EMP001");
        staff.setName("张三");
        staff.setAge(25);
        staff.setGender(Gender.MALE);
        staff.setPhone("13800138001");
        staff.setEmail("zhangsan@example.com");
        staff.setCreateTime(new Date());
        staff.setModifyTime(new Date());
        return staff;
    }

    private Staff createTestStaff2() {
        Staff staff = new Staff();
        staff.setId(2);
        staff.setCode("EMP002");
        staff.setName("李四");
        staff.setAge(30);
        staff.setGender(Gender.WOMAN);
        staff.setPhone("13800138002");
        staff.setEmail("lisi@example.com");
        staff.setCreateTime(new Date());
        staff.setModifyTime(new Date());
        return staff;
    }
}
