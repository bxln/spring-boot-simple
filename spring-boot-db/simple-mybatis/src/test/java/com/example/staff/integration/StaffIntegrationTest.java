package com.example.staff.integration;

import com.example.staff.domain.Gender;
import com.example.staff.domain.Staff;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Staff集成测试类
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class StaffIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCompleteStaffWorkflow() throws Exception {
        // 1. 查询所有员工（初始数据）
        mockMvc.perform(get("/api/staff/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(5)); // 初始有5条测试数据

        // 2. 新增员工
        Staff newStaff = new Staff();
        newStaff.setCode("INTEGRATION001");
        newStaff.setName("集成测试员工");
        newStaff.setAge(28);
        newStaff.setGender(Gender.MALE);
        newStaff.setPhone("13900139999");
        newStaff.setEmail("integration@example.com");

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStaff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("新增成功"));

        // 3. 根据编码查询新增的员工
        mockMvc.perform(get("/api/staff/code/INTEGRATION001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("集成测试员工"))
                .andExpect(jsonPath("$.data.age").value(28));

        // 4. 模糊查询
        mockMvc.perform(get("/api/staff/search").param("name", "集成"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(1));

        // 5. 年龄范围查询
        mockMvc.perform(get("/api/staff/age-range")
                .param("minAge", "25")
                .param("maxAge", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 6. 分页查询
        mockMvc.perform(get("/api/staff/page")
                .param("pageNum", "1")
                .param("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(3));

        // 7. 统计总数
        mockMvc.perform(get("/api/staff/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(6)); // 原有5条 + 新增1条

        // 8. 查询所有员工（验证新增后的数量）
        mockMvc.perform(get("/api/staff/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(6));
    }

    @Test
    void testStaffValidation() throws Exception {
        // 测试新增员工时的验证

        // 1. 员工编码为空
        Staff invalidStaff1 = new Staff();
        invalidStaff1.setName("测试员工");
        invalidStaff1.setAge(25);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStaff1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新增失败: 员工编码不能为空"));

        // 2. 员工姓名为空
        Staff invalidStaff2 = new Staff();
        invalidStaff2.setCode("INVALID001");
        invalidStaff2.setAge(25);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStaff2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新增失败: 员工姓名不能为空"));

        // 3. 员工编码重复
        Staff duplicateStaff = new Staff();
        duplicateStaff.setCode("EMP001"); // 使用已存在的编码
        duplicateStaff.setName("重复编码员工");
        duplicateStaff.setAge(25);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateStaff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新增失败: 员工编码已存在: EMP001"));

        // 4. 年龄超出范围
        Staff invalidAgeStaff = new Staff();
        invalidAgeStaff.setCode("INVALID002");
        invalidAgeStaff.setName("年龄无效员工");
        invalidAgeStaff.setAge(200); // 超出范围

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAgeStaff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新增失败: 员工年龄必须在0-150之间"));
    }

    @Test
    void testStaffUpdateAndDelete() throws Exception {
        // 1. 先新增一个员工
        Staff newStaff = new Staff();
        newStaff.setCode("UPDATE_DELETE001");
        newStaff.setName("更新删除测试员工");
        newStaff.setAge(25);
        newStaff.setGender(Gender.WOMAN);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStaff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 2. 根据编码查询获取ID
        String response = mockMvc.perform(get("/api/staff/code/UPDATE_DELETE001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn().getResponse().getContentAsString();

        // 从响应中提取ID（这里简化处理，实际项目中可能需要更复杂的JSON解析）
        // 假设我们知道新增的员工ID是6（因为初始有5条数据）

        // 3. 更新员工信息
        Staff updateStaff = new Staff();
        updateStaff.setName("更新后的姓名");
        updateStaff.setAge(26);

        mockMvc.perform(put("/api/staff/6") // 假设ID是6
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateStaff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新成功"));

        // 4. 验证更新结果
        mockMvc.perform(get("/api/staff/code/UPDATE_DELETE001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("更新后的姓名"))
                .andExpect(jsonPath("$.data.age").value(26));

        // 5. 删除员工
        mockMvc.perform(delete("/api/staff/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("删除成功"));

        // 6. 验证删除结果
        mockMvc.perform(get("/api/staff/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("员工不存在"));
    }
}
