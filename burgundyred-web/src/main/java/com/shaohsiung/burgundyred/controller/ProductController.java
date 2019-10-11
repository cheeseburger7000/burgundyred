package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.document.ProductDocument;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 产品服务
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    @Reference(version = "1.0.0")
    private ProductService productService;

    @Reference(version = "1.0.0")
    private SearchService searchService;

    /** 查看商品详情 */
    @GetMapping("/{id}")
    public String productDetail(@PathVariable("id") String productId, Model model) {
        Product product = productService.getProductById(productId);
        // TODO 评论服务
        model.addAttribute("product", product);
        return "detail";
    }

    /** 搜索商品*/
    @GetMapping("/search/{keyword}/{page}")
    public String search(@PathVariable("keyword") String keyword, @PathVariable("page") Integer page, Model model) {
        List<ProductDocument> productDocuments = searchService.search(keyword, page, AppConstant.PRODUCT_PAGE_SIZE);
        model.addAttribute("products", productDocuments);
        return "list";
    }

    /** 按类目获取商品列表*/
    @GetMapping("/list/{id}/{page}")
    public String listByCategoryId(@PathVariable("id") String categoryId, @PathVariable("page") Integer page, Model model) {
        List<Product> productList = productService.getProductListByCategoryId(categoryId, page, AppConstant.PRODUCT_PAGE_SIZE);
        model.addAttribute("products", productList);
        return "list";
    }
}
