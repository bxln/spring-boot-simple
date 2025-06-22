package com.example.staff.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 性别枚举
 */
public enum Gender {
    MALE("0", "男"),
    WOMAN("1", "女"),
    OTHER("2", "其他");

    private final String code;
    private final String description;

    Gender(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     */
    @JsonCreator
    public static Gender fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (Gender gender : Gender.values()) {
            if (gender.getCode().equals(code)) {
                return gender;
            }
        }
        return null;
    }

    /**
     * 根据描述获取枚举
     */
    public static Gender fromDescription(String description) {
        if (description == null) {
            return null;
        }
        for (Gender gender : Gender.values()) {
            if (gender.getDescription().equals(description)) {
                return gender;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
