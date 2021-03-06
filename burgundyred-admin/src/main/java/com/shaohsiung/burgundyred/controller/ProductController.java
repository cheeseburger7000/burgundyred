package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.model.Category;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.param.ProductParam;
import com.shaohsiung.burgundyred.service.CategoryService;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import com.shaohsiung.burgundyred.vo.CategoryVo;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品管理
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/product")
@CrossOrigin(allowCredentials="true", maxAge = 3600)
public class ProductController {

    @Reference(version = "1.0.0")
    public ProductService productService;

    @Reference(version = "1.0.0")
    public CategoryService categoryService;

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
    public BaseResponse productList(@PathVariable("page") Integer pageNum, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        List<Product> products = productService.sellerProductList(pageNum, AppConstant.PRODUCT_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = productService.sellerProductListTotalRecord();
//        Integer totalPage = (totalRecord + AppConstant.PRODUCT_PAGE_SIZE - 1) / AppConstant.PRODUCT_PAGE_SIZE;

        Map result = new HashMap();
//        result.put("admin", admin);
        result.put("products", products);
        result.put("pageSize", AppConstant.PRODUCT_PAGE_SIZE);
        result.put("totalRecord", totalRecord);
//        result.put("pageNum", pageNum);
//        result.put("totalPage", totalPage);

        return BaseResponseUtils.success(result);
    }

    /**
     * 上架商品
     * @param productId
     * @return
     */
    @PostMapping("/shelf/{productId}")
    public BaseResponse onShelves(@PathVariable("productId") String productId, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【后台应用】上架商品请求，商品id：{}", productId);
        return productService.onShelves(productId);
    }

    /**
     * 下架商品
     * @param productId
     * @return
     */
    @PostMapping("/remove/{productId}")
    public BaseResponse remove(@PathVariable("productId") String productId, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【后台应用】下架商品请求，商品id：{}", productId);
        return productService.remove(productId);
    }

    @GetMapping("/categories")
    public BaseResponse categories() {
        BaseResponse request = categoryService.categories();

        // List<Category> to List<CategoryVo>
        List<Category> categories= (List<Category>) request.getData();
        List<CategoryVo> result = categories.stream().map(category -> {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category, categoryVo);
            return categoryVo;
        }).collect(Collectors.toList());
        return BaseResponseUtils.success(result);
    }

    @GetMapping("/addStock/{productId}/{add}")
    public BaseResponse addProductStock(@PathVariable("productId") String productId,
                                        @PathVariable("add") @Min(1) Integer add,
                                        HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }
        return productService.addProductStock(productId, add);
    }
}
