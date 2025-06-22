package com.example.staff.controller;

import com.example.staff.domain.Gender;
import com.example.staff.domain.Staff;
import com.example.staff.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工控制器
 */
@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    /**
     * 根据ID查询员工
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Staff staff = staffService.getById(id);
            if (staff != null) {
                result.put("success", true);
                result.put("data", staff);
                result.put("message", "查询成功");
            } else {
                result.put("success", false);
                result.put("message", "员工不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 根据员工编码查询
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Map<String, Object>> getByCode(@PathVariable String code) {
        Map<String, Object> result = new HashMap<>();
        try {
            Staff staff = staffService.getByCode(code);
            if (staff != null) {
                result.put("success", true);
                result.put("data", staff);
                result.put("message", "查询成功");
            } else {
                result.put("success", false);
                result.put("message", "员工不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 查询所有员工
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStaff() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Staff> staffList = staffService.getAllStaff();
            result.put("success", true);
            result.put("data", staffList);
            result.put("total", staffList.size());
            result.put("message", "查询成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 根据姓名模糊查询
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchByName(@RequestParam(required = false) String name) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Staff> staffList = staffService.getByNameLike(name);
            result.put("success", true);
            result.put("data", staffList);
            result.put("total", staffList.size());
            result.put("message", "查询成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 根据年龄范围查询
     */
    @GetMapping("/age-range")
    public ResponseEntity<Map<String, Object>> getByAgeRange(
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Staff> staffList = staffService.getByAgeRange(minAge, maxAge);
            result.put("success", true);
            result.put("data", staffList);
            result.put("total", staffList.size());
            result.put("message", "查询成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Staff> staffList = staffService.getByPage(pageNum, pageSize);
            int totalCount = staffService.getTotalCount();
            
            result.put("success", true);
            result.put("data", staffList);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("total", totalCount);
            result.put("totalPages", (totalCount + pageSize - 1) / pageSize);
            result.put("message", "查询成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增员工
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addStaff(@RequestBody Staff staff) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = staffService.addStaff(staff);
            if (count > 0) {
                result.put("success", true);
                result.put("data", staff);
                result.put("message", "新增成功");
            } else {
                result.put("success", false);
                result.put("message", "新增失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "新增失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 批量新增员工
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchAddStaff(@RequestBody List<Staff> staffList) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = staffService.batchAddStaff(staffList);
            result.put("success", true);
            result.put("count", count);
            result.put("message", "批量新增成功，共新增" + count + "条记录");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量新增失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 更新员工信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStaff(@PathVariable Integer id, @RequestBody Staff staff) {
        Map<String, Object> result = new HashMap<>();
        try {
            staff.setId(id);
            int count = staffService.updateStaffSelective(staff);
            if (count > 0) {
                result.put("success", true);
                result.put("message", "更新成功");
            } else {
                result.put("success", false);
                result.put("message", "更新失败，员工不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStaff(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = staffService.deleteById(id);
            if (count > 0) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败，员工不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 批量删除员工
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteStaff(@RequestBody List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = staffService.batchDelete(ids);
            result.put("success", true);
            result.put("count", count);
            result.put("message", "批量删除成功，共删除" + count + "条记录");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 根据性别查询员工
     */
    @GetMapping("/gender/{gender}")
    public ResponseEntity<Map<String, Object>> getByGender(@PathVariable String gender) {
        Map<String, Object> result = new HashMap<>();
        try {
            Gender genderEnum = Gender.fromCode(gender);
            if (genderEnum == null) {
                result.put("success", false);
                result.put("message", "无效的性别参数: " + gender);
                return ResponseEntity.ok(result);
            }

            List<Staff> staffList = staffService.getByGender(genderEnum);
            result.put("success", true);
            result.put("data", staffList);
            result.put("total", staffList.size());
            result.put("message", "查询成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有性别枚举值
     */
    @GetMapping("/genders")
    public ResponseEntity<Map<String, Object>> getAllGenders() {
        Map<String, Object> result = new HashMap<>();
        try {
            Gender[] genders = Gender.values();
            Map<String, String> genderMap = new HashMap<>();
            for (Gender gender : genders) {
                genderMap.put(gender.name(), gender.getCode());
            }
            result.put("success", true);
            result.put("data", genderMap);
            result.put("message", "获取成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 统计员工总数
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getTotalCount() {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = staffService.getTotalCount();
            result.put("success", true);
            result.put("count", count);
            result.put("message", "统计成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "统计失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
