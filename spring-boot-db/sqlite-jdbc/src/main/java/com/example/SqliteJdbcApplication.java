package com.example;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Controller
@SpringBootApplication
public class SqliteJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqliteJdbcApplication.class, args);
    }

    @ResponseBody
    @RequestMapping(value = "/sqlite")
    public Object sqlite() {

        // 加载SQLite JDBC驱动
        // Class.forName("org.sqlite.JDBC");
        String dbName = "sample.db";
        File db_temp = new File(getClass().getClassLoader().getResource("").getFile(), "db_temp");
        File db_path = new File(db_temp, System.currentTimeMillis() + "");
        String path = db_path.getAbsolutePath() + File.separator + dbName;
        db_path.mkdirs();
        // 连接到SQLite数据库（如果数据库不存在，会自动创建）
        String url = "jdbc:sqlite:" + path;

        try (Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();
            // 启用WAL模式
            statement.execute("PRAGMA journal_mode=WAL;");
            // 优化SQLite配置
            statement.execute("PRAGMA cache_size = 10000;");
            statement.execute("PRAGMA synchronous = OFF;");

            // 假设dataList是你的数据列表
            List<Map<Integer, Object>> dataList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                Map<Integer, Object> map = new HashMap<>();
                map.put(1, "Hello World");
                map.put(2, 18);
                map.put(3, 2024.06);
                // map.put(4, DateUtil.parse("2100-12-31 23:59:59", DatePattern.NORM_DATETIME_PATTERN));
                map.put(4, DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
                dataList.add(map);
            }

            // 创建表
            createTable(connection);
            // 插入数据
            insertData(connection, dataList);

            // 查询数据
            return queryData(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "age INTEGER NOT NULL," +
                "amount REAL(12,2) NOT NULL," +
                "ts TEXT NOT NULL" +
                ");";

        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
        System.out.println("表创建成功！");
    }

    private static void insertData(Connection connection, List<Map<Integer, Object>> dataList) throws SQLException {
        String sql = "INSERT INTO users (name, age, amount,ts) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false); // 关闭自动提交

            int count = 0;
            for (Map<Integer, Object> data : dataList) {
                for (Map.Entry<Integer, Object> entry : data.entrySet()) {
                    Integer key = entry.getKey();
                    Object value = entry.getValue();

                    if (key == 1) {
                        stmt.setString(1, String.valueOf(value));
                    }
                    if (key == 2) {
                        stmt.setInt(2, Integer.valueOf(value.toString()));
                    }
                    if (key == 3) {
                        stmt.setBigDecimal(3, new BigDecimal(value.toString()));
                    }
                    if (key == 4) {
                        stmt.setString(4, value.toString());
                    }
                }
                stmt.addBatch();
                count++;

                if (count % 1000 == 0) { // 每1000条记录提交一次
                    stmt.executeBatch();
                    connection.commit();
                }
            }

            if (count % 1000 != 0) { // 提交剩余的记录
                stmt.executeBatch();
                connection.commit();
            }
        } catch (SQLException e) {
            connection.rollback(); // 发生异常时回滚事务
            throw e;
        }
    }


    private static ArrayNode queryData(Connection connection) throws SQLException {
        String sql = "SELECT id, name, age, amount FROM users;";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        // 1.创建 ObjectMapper 对象
        ObjectMapper mapper = new ObjectMapper();

        // 创建一个空的 JSON 数组
        ArrayNode jsonArray = mapper.createArrayNode();

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            BigDecimal amount = rs.getBigDecimal("amount");

            System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age + ", Amount: " + amount);

            // 2.创建一个空的 JSON 对象
            ObjectNode jsonObject = mapper.createObjectNode();
            // 3.向 JSON 对象中添加元素
            jsonObject.put("id", id);
            jsonObject.put("Name", name);
            jsonObject.put("Age", age);
            jsonObject.put("Amount", amount);
            jsonArray.add(jsonObject);
        }

        rs.close();
        statement.close();

        return jsonArray;
    }

}
