package com.example.secondary.service;

import com.example.secondary.domain.Product;
import com.example.secondary.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 产品服务类 - 从数据源
 */
@Service
@Transactional(transactionManager = "secondaryTransactionManager")
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 根据ID查询产品
     */
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }

    /**
     * 根据产品编码查询产品
     */
    public Product getProductByCode(String productCode) {
        return productMapper.selectByProductCode(productCode);
    }

    /**
     * 查询所有产品
     */
    public List<Product> getAllProducts() {
        return productMapper.selectAll();
    }

    /**
     * 根据分类查询产品
     */
    public List<Product> getProductsByCategory(String category) {
        return productMapper.selectByCategory(category);
    }

    /**
     * 根据状态查询产品
     */
    public List<Product> getProductsByStatus(String status) {
        return productMapper.selectByStatus(status);
    }

    /**
     * 根据价格范围查询产品
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productMapper.selectByPriceRange(minPrice, maxPrice);
    }

    /**
     * 根据产品名称模糊查询
     */
    public List<Product> getProductsByNameLike(String productName) {
        return productMapper.selectByNameLike(productName);
    }

    /**
     * 分页查询产品
     */
    public List<Product> getProductsByPage(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return productMapper.selectByPage(offset, pageSize);
    }

    /**
     * 统计产品总数
     */
    public int getTotalCount() {
        return productMapper.countAll();
    }

    /**
     * 根据分类统计产品数
     */
    public int getCountByCategory(String category) {
        return productMapper.countByCategory(category);
    }

    /**
     * 根据状态统计产品数
     */
    public int getCountByStatus(String status) {
        return productMapper.countByStatus(status);
    }

    /**
     * 创建产品
     */
    public int createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("产品信息不能为空");
        }
        
        // 检查产品编码是否已存在
        Product existingProduct = productMapper.selectByProductCode(product.getProductCode());
        if (existingProduct != null) {
            throw new IllegalArgumentException("产品编码已存在: " + product.getProductCode());
        }
        
        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus("ACTIVE");
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        
        return productMapper.insert(product);
    }

    /**
     * 更新产品
     */
    public int updateProduct(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("产品ID不能为空");
        }
        
        product.setUpdateTime(LocalDateTime.now());
        return productMapper.updateByIdSelective(product);
    }

    /**
     * 删除产品
     */
    public int deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("产品ID不能为空");
        }
        return productMapper.deleteById(id);
    }

    /**
     * 更新库存
     */
    public int updateStock(Long id, Integer stock) {
        if (id == null) {
            throw new IllegalArgumentException("产品ID不能为空");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("库存数量不能为空且不能小于0");
        }
        return productMapper.updateStock(id, stock);
    }

    /**
     * 批量创建产品
     */
    public int batchCreateProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("产品列表不能为空");
        }
        
        LocalDateTime now = LocalDateTime.now();
        for (Product product : products) {
            if (product.getStatus() == null) {
                product.setStatus("ACTIVE");
            }
            if (product.getStock() == null) {
                product.setStock(0);
            }
            product.setCreateTime(now);
            product.setUpdateTime(now);
        }
        
        return productMapper.batchInsert(products);
    }

    /**
     * 批量删除产品
     */
    public int batchDeleteProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("产品ID列表不能为空");
        }
        return productMapper.batchDelete(ids);
    }
}
