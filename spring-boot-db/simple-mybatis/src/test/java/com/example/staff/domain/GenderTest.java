package com.example.staff.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Gender枚举测试类
 */
class GenderTest {

    @Test
    void testGenderValues() {
        // 测试枚举值
        assertEquals(3, Gender.values().length);
        assertEquals(Gender.MALE, Gender.valueOf("MALE"));
        assertEquals(Gender.WOMAN, Gender.valueOf("WOMAN"));
        assertEquals(Gender.OTHER, Gender.valueOf("OTHER"));
    }

    @Test
    void testGenderCodes() {
        // 测试代码值
        assertEquals("男", Gender.MALE.getCode());
        assertEquals("女", Gender.WOMAN.getCode());
        assertEquals("其他", Gender.OTHER.getCode());
    }

    @Test
    void testGenderDescriptions() {
        // 测试描述
        assertEquals("男性", Gender.MALE.getDescription());
        assertEquals("女性", Gender.WOMAN.getDescription());
        assertEquals("其他", Gender.OTHER.getDescription());
    }

    @Test
    void testFromCode() {
        // 测试根据代码获取枚举
        assertEquals(Gender.MALE, Gender.fromCode("男"));
        assertEquals(Gender.WOMAN, Gender.fromCode("女"));
        assertEquals(Gender.OTHER, Gender.fromCode("其他"));
        
        // 测试无效代码
        assertNull(Gender.fromCode("无效"));
        assertNull(Gender.fromCode(null));
        assertNull(Gender.fromCode(""));
    }

    @Test
    void testFromDescription() {
        // 测试根据描述获取枚举
        assertEquals(Gender.MALE, Gender.fromDescription("男性"));
        assertEquals(Gender.WOMAN, Gender.fromDescription("女性"));
        assertEquals(Gender.OTHER, Gender.fromDescription("其他"));
        
        // 测试无效描述
        assertNull(Gender.fromDescription("无效"));
        assertNull(Gender.fromDescription(null));
        assertNull(Gender.fromDescription(""));
    }

    @Test
    void testToString() {
        // 测试toString方法
        assertEquals("男", Gender.MALE.toString());
        assertEquals("女", Gender.WOMAN.toString());
        assertEquals("其他", Gender.OTHER.toString());
    }
}
