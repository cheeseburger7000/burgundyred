package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.vo.ProductVo;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.document.ProductDocument;
import com.shaohsiung.burgundyred.model.Category;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.CategoryService;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 产品服务
 */
@Slf4j
@Controller
@RequestMapping("/product")
public class ProductController {
    @Reference(version = "1.0.0")
    private ProductService productService;

    @Reference(version = "1.0.0")
    private SearchService searchService;

    @Reference(version = "1.0.0")
    private CategoryService categoryService;

    /**
     * 查看商品详情
     * @param request
     * @param productId
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String productDetail(HttpServletRequest request,
                                @PathVariable("id") String productId,
                                Model model) {
        // 判断用户是否登录
        User user = (User) request.getAttribute("user");
        if (user != null) {
            log.info("【前台应用】商品服务-当前用户：{}", user.getUserName());
            model.addAttribute("user", user);
        }

        Product product = productService.getProductById(productId);

        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(product, productVo);

        Category category = categoryService.getById(product.getCategoryId());
        if (category == null) {
            // TODO 处理暂未划分类目 0
            productVo.setCategoryName("该商品暂未划分类目");
            productVo.setCategoryId("0");
        }
        productVo.setCategoryName(category.getName());

        model.addAttribute("product", productVo);
        return "detail";
    }

    /**
     * 搜索商品
     * @param keyword
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/search/{keyword}/{page}")
    public String search(HttpServletRequest request,
                         @PathVariable("keyword") String keyword,
                         @PathVariable("page") Integer page,
                         Model model) {
        // 判断用户是否登录
        User user = (User) request.getAttribute("user");
        if (user != null) {
            log.info("【前台应用】商品服务-当前用户：{}", user.getUserName());
            model.addAttribute("user", user);
        }

        List<ProductDocument> productDocuments = searchService.search(keyword, page, AppConstant.PRODUCT_PAGE_SIZE);
        model.addAttribute("products", productDocuments);
        return "list";
    }

    /**
     * 按类目获取商品列表
     * @param request
     * @param categoryId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/list/{id}/{page}")
    public String listByCategoryId(HttpServletRequest request,
                                   @PathVariable("id") String categoryId,
                                   @PathVariable("page") Integer page,
                                   Model model) {
        // 判断用户是否登录
        User user = (User) request.getAttribute("user");
        if (user != null) {
            log.info("【前台应用】商品服务-当前用户：{}", user.getUserName());
            model.addAttribute("user", user);
        }

        List<Product> productList = productService.getProductListByCategoryId(categoryId, page, AppConstant.PRODUCT_PAGE_SIZE);
        model.addAttribute("products", productList);
        return "list";
    }
}
