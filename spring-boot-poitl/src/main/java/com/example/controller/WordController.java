package com.example.controller;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.BorderStyle;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Word文档生成控制器
 * <p>
 * 该控制器提供Word文档生成功能，使用POI-TL模板引擎
 * 基于预定义的Word模板生成包含用户信息表格的文档
 *
 * @author 系统生成
 * @version 1.0
 */
@RestController
public class WordController {

    // 用于加载classpath下的Word模板文件
    @Resource
    private ResourceLoader resourceLoader;

    @GetMapping("/generate-word")
    public ResponseEntity<byte[]> generate() throws IOException {

        // 创建合并单元格的表头 - 必须也是5列
        RowRenderData mergedHeader = Rows.of("基本信息", "", "", "联系方式", "").textColor("FFFFFF")
                .bgColor("4472C4").center().create();

        // 创建表头行 - 5列
        RowRenderData header = Rows.of("姓名", "年龄", "性别", "手机号", "邮箱").center().create();

        // 创建数据行 - 5列
        RowRenderData row1 = Rows.of("张三", "28", "男", "13800138000", "zhangsan@example.com").center().create();
        RowRenderData row2 = Rows.of("李四", "30", "女", "13900139000", "lisi@example.com").center().create();

        TableRenderData table = Tables.of(mergedHeader, header, row1, row2).border(BorderStyle.DEFAULT).create();

        MergeCellRule rule = MergeCellRule.builder()
                .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(0, 2))
                .map(MergeCellRule.Grid.of(0, 3), MergeCellRule.Grid.of(0, 4)).build();
        table.setMergeRule(rule);


        // 创建表格
        Map<String, Object> data = new HashMap<>();
        data.put("table01", table);

        // 使用ResourceLoader加载模板
        InputStream templateStream = resourceLoader.getResource("classpath:templates/template.docx").getInputStream();
        XWPFTemplate template = XWPFTemplate.compile(templateStream).render(data);

        // 生成文档字节流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        template.write(outputStream);
        template.close();
        templateStream.close();

        // 构建响应
        HttpHeaders headersResponse = new HttpHeaders();
        headersResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headersResponse.setContentDispositionFormData("attachment", "user_info.docx");

        return ResponseEntity.ok()
                .headers(headersResponse)
                .body(outputStream.toByteArray());
    }
}