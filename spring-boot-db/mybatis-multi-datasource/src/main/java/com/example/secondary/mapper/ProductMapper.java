package com.example.secondary.mapper;

import com.example.secondary.domain.Product;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品Mapper接口 - 从数据源
 */
public interface ProductMapper {

    /**
     * 根据ID查询产品
     */
    Product selectById(Long id);

    /**
     * 根据产品编码查询产品
     */
    Product selectByProductCode(String productCode);

    /**
     * 查询所有产品
     */
    List<Product> selectAll();

    /**
     * 根据分类查询产品
     */
    List<Product> selectByCategory(String category);

    /**
     * 根据状态查询产品
     */
    List<Product> selectByStatus(String status);

    /**
     * 根据价格范围查询产品
     */
    List<Product> selectByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * 根据产品名称模糊查询
     */
    List<Product> selectByNameLike(String productName);

    /**
     * 分页查询产品
     */
    List<Product> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计产品总数
     */
    int countAll();

    /**
     * 根据分类统计产品数
     */
    int countByCategory(String category);

    /**
     * 根据状态统计产品数
     */
    int countByStatus(String status);

    /**
     * 插入产品
     */
    int insert(Product product);

    /**
     * 选择性插入产品
     */
    int insertSelective(Product product);

    /**
     * 根据ID更新产品
     */
    int updateById(Product product);

    /**
     * 选择性更新产品
     */
    int updateByIdSelective(Product product);

    /**
     * 根据ID删除产品
     */
    int deleteById(Long id);

    /**
     * 批量插入产品
     */
    int batchInsert(List<Product> products);

    /**
     * 批量删除产品
     */
    int batchDelete(List<Long> ids);

    /**
     * 更新库存
     */
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
}
