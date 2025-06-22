package com.example.staff.handler;

import com.example.staff.domain.Gender;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * GenderTypeHandler测试类
 */
class GenderTypeHandlerTest {

    private GenderTypeHandler handler;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private CallableStatement callableStatement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GenderTypeHandler();
    }

    @Test
    void testSetNonNullParameter() throws SQLException {
        // 测试设置参数
        handler.setNonNullParameter(preparedStatement, 1, Gender.MALE, JdbcType.VARCHAR);
        verify(preparedStatement).setString(1, "男");

        handler.setNonNullParameter(preparedStatement, 2, Gender.WOMAN, JdbcType.VARCHAR);
        verify(preparedStatement).setString(2, "女");

        handler.setNonNullParameter(preparedStatement, 3, Gender.OTHER, JdbcType.VARCHAR);
        verify(preparedStatement).setString(3, "其他");
    }

    @Test
    void testGetNullableResultByColumnName() throws SQLException {
        // 测试根据列名获取结果
        when(resultSet.getString("gender")).thenReturn("男");
        assertEquals(Gender.MALE, handler.getNullableResult(resultSet, "gender"));

        when(resultSet.getString("gender")).thenReturn("女");
        assertEquals(Gender.WOMAN, handler.getNullableResult(resultSet, "gender"));

        when(resultSet.getString("gender")).thenReturn("其他");
        assertEquals(Gender.OTHER, handler.getNullableResult(resultSet, "gender"));

        when(resultSet.getString("gender")).thenReturn(null);
        assertNull(handler.getNullableResult(resultSet, "gender"));

        when(resultSet.getString("gender")).thenReturn("无效值");
        assertNull(handler.getNullableResult(resultSet, "gender"));
    }

    @Test
    void testGetNullableResultByColumnIndex() throws SQLException {
        // 测试根据列索引获取结果
        when(resultSet.getString(1)).thenReturn("男");
        assertEquals(Gender.MALE, handler.getNullableResult(resultSet, 1));

        when(resultSet.getString(1)).thenReturn("女");
        assertEquals(Gender.WOMAN, handler.getNullableResult(resultSet, 1));

        when(resultSet.getString(1)).thenReturn("其他");
        assertEquals(Gender.OTHER, handler.getNullableResult(resultSet, 1));

        when(resultSet.getString(1)).thenReturn(null);
        assertNull(handler.getNullableResult(resultSet, 1));
    }

    @Test
    void testGetNullableResultFromCallableStatement() throws SQLException {
        // 测试从CallableStatement获取结果
        when(callableStatement.getString(1)).thenReturn("男");
        assertEquals(Gender.MALE, handler.getNullableResult(callableStatement, 1));

        when(callableStatement.getString(1)).thenReturn("女");
        assertEquals(Gender.WOMAN, handler.getNullableResult(callableStatement, 1));

        when(callableStatement.getString(1)).thenReturn("其他");
        assertEquals(Gender.OTHER, handler.getNullableResult(callableStatement, 1));

        when(callableStatement.getString(1)).thenReturn(null);
        assertNull(handler.getNullableResult(callableStatement, 1));
    }
}
