package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.param.ProductParam;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品管理
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Reference(version = "1.0.0")
    public ProductService productService;

    /**
     * 添加商品
     * @param productParam
     * @return
     */
    @PostMapping
    public BaseResponse addProduct(@Valid @RequestBody ProductParam productParam) {
        Product product = new Product();
        BeanUtils.copyProperties(productParam, product);
        Product result = productService.addProduct(product);
        return BaseResponseUtils.success(result);
    }

    /**
     * 获取商品列表
     * @param pageNum
     * @return
     */
    @GetMapping("/list/{page}")
    public BaseResponse productList(@PathVariable("page") Integer pageNum) {
        List<Product> products = productService.sellerProductList(pageNum, AppConstant.PRODUCT_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = productService.sellerProductListTotalRecord();
        Integer totalPage = (totalRecord + AppConstant.PRODUCT_PAGE_SIZE - 1) / AppConstant.PRODUCT_PAGE_SIZE;

        Map result = new HashMap();
        result.put("products", products);
        result.put("page", pageNum);
        result.put("totalPage", totalPage);
        return BaseResponseUtils.success(result);
    }

    /**
     * 上架商品
     * @param productId
     * @return
     */
    @PostMapping("/shelf/{productId}")
    public BaseResponse onShelves(@PathVariable("productId") String productId) {
        log.info("【后台应用】上架商品请求，商品id：{}", productId);
        return productService.onShelves(productId);
    }

    /**
     * 下架商品
     * @param productId
     * @return
     */
    @PostMapping("/remove/{productId}")
    public BaseResponse remove(@PathVariable("productId") String productId) {
        log.info("【后台应用】下架商品请求，商品id：{}", productId);
        return productService.remove(productId);
    }
}
