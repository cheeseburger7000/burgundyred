package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.dto.ProductItemDto;
import com.shaohsiung.burgundyred.model.Product;

import java.util.List;

/**
 * 商品模块
 */
public interface ProductService {

    /**
     * 根据类目id获取商品列表
     *
     * 前台
     * @param categoryId
     * @return
     */
    List<Product> getProductListByCategoryId(String categoryId, int pageNum, int pageSize);

    /**
     * 根据商品id获取商品详情
     *
     * 前台
     * @param productId
     * @return
     */
    Product getProductById(String productId);

    /**
     * 添加商品
     *
     * 后台
     * @param product
     * @return
     */
    Product addProduct(Product product);

    /**
     * 获取商品库存
     *
     * @param productId
     * @return
     */
    Integer getStockByProductId(String productId);

    /**
     * 减少商品库存
     *
     * 前台订单服务
     * @param productId
     * @param quanity
     * @return
     */
    Product incStock(String productId, int quanity);


    // 门户 limit 4
    //1F Latest Style
    List<ProductItemDto> latestStyle(int limit);
    //2F Recommended Style
    List<ProductItemDto> recommendedStyle(int limit);
    //3F Intimate Style
    List<ProductItemDto> intimateStyle(int limit);
    //4F Scarce Style
    List<ProductItemDto> scarceStyle(int limit);
    //5F Clearance Sale
    List<ProductItemDto> clearanceStyle(int limit);

    /**
     * 卖家商品列表
     *
     * @param pageNum
     *
     * @return
     */
    List<Product> sellerProductList(int pageNum, int pageSize);

    Integer sellerProductListTotalRecord();
}
