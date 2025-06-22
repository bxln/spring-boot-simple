package com.example.staff.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Gender枚举JSON序列化测试
 */
class GenderJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGenderSerialization() throws Exception {
        // 测试序列化
        String maleJson = objectMapper.writeValueAsString(Gender.MALE);
        assertEquals("\"男\"", maleJson);

        String femaleJson = objectMapper.writeValueAsString(Gender.WOMAN);
        assertEquals("\"女\"", femaleJson);

        String otherJson = objectMapper.writeValueAsString(Gender.OTHER);
        assertEquals("\"其他\"", otherJson);
    }

    @Test
    void testGenderDeserialization() throws Exception {
        // 测试反序列化
        Gender male = objectMapper.readValue("\"男\"", Gender.class);
        assertEquals(Gender.MALE, male);

        Gender female = objectMapper.readValue("\"女\"", Gender.class);
        assertEquals(Gender.WOMAN, female);

        Gender other = objectMapper.readValue("\"其他\"", Gender.class);
        assertEquals(Gender.OTHER, other);
    }

    @Test
    void testStaffWithGenderSerialization() throws Exception {
        // 测试Staff对象中Gender字段的序列化
        Staff staff = new Staff();
        staff.setId(1);
        staff.setCode("EMP001");
        staff.setName("张三");
        staff.setAge(25);
        staff.setGender(Gender.MALE);
        staff.setPhone("13800138001");
        staff.setEmail("zhangsan@example.com");

        String json = objectMapper.writeValueAsString(staff);
        assertTrue(json.contains("\"gender\":\"男\""));
    }

    @Test
    void testStaffWithGenderDeserialization() throws Exception {
        // 测试Staff对象中Gender字段的反序列化
        String json = "{\"id\":1,\"code\":\"EMP001\",\"name\":\"张三\",\"age\":25,\"gender\":\"男\",\"phone\":\"13800138001\",\"email\":\"zhangsan@example.com\"}";
        
        Staff staff = objectMapper.readValue(json, Staff.class);
        assertEquals(Gender.MALE, staff.getGender());
        assertEquals("男", staff.getGender().getCode());
    }

    @Test
    void testInvalidGenderDeserialization() throws Exception {
        // 测试无效性别值的反序列化
        String json = "\"无效性别\"";
        Gender gender = objectMapper.readValue(json, Gender.class);
        assertNull(gender);
    }
}
