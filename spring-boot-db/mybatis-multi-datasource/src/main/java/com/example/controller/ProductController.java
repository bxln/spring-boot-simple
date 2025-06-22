package com.example.controller;

import com.example.secondary.domain.Product;
import com.example.secondary.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品控制器 - 从数据源
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 根据ID查询产品
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据产品编码查询产品
     */
    @GetMapping("/code/{productCode}")
    public ResponseEntity<Product> getProductByCode(@PathVariable String productCode) {
        Product product = productService.getProductByCode(productCode);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 查询所有产品
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 根据分类查询产品
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据状态查询产品
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Product>> getProductsByStatus(@PathVariable String status) {
        List<Product> products = productService.getProductsByStatus(status);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据价格范围查询产品
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据产品名称模糊查询
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> getProductsByNameLike(@RequestParam String productName) {
        List<Product> products = productService.getProductsByNameLike(productName);
        return ResponseEntity.ok(products);
    }

    /**
     * 分页查询产品
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getProductsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Product> products = productService.getProductsByPage(pageNum, pageSize);
        int totalCount = productService.getTotalCount();
        
        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("totalCount", totalCount);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
        
        return ResponseEntity.ok(result);
    }

    /**
     * 统计产品总数
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalCount() {
        int count = productService.getTotalCount();
        return ResponseEntity.ok(count);
    }

    /**
     * 根据分类统计产品数
     */
    @GetMapping("/count/category/{category}")
    public ResponseEntity<Integer> getCountByCategory(@PathVariable String category) {
        int count = productService.getCountByCategory(category);
        return ResponseEntity.ok(count);
    }

    /**
     * 根据状态统计产品数
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Integer> getCountByStatus(@PathVariable String status) {
        int count = productService.getCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    /**
     * 创建产品
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        try {
            int result = productService.createProduct(product);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "产品创建成功" : "产品创建失败");
            response.put("product", product);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新产品
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            product.setId(id);
            int result = productService.updateProduct(product);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "产品更新成功" : "产品更新失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        try {
            int result = productService.deleteProduct(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "产品删除成功" : "产品删除失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新库存
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        try {
            int result = productService.updateStock(id, stock);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", result > 0 ? "库存更新成功" : "库存更新失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量创建产品
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchCreateProducts(@RequestBody List<Product> products) {
        try {
            int result = productService.batchCreateProducts(products);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", "成功创建 " + result + " 个产品");
            response.put("count", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量删除产品
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteProducts(@RequestBody List<Long> ids) {
        try {
            int result = productService.batchDeleteProducts(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result > 0);
            response.put("message", "成功删除 " + result + " 个产品");
            response.put("count", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
