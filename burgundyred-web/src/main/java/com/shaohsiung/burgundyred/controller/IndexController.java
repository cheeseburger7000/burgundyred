package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.dto.CategoryListItemDto;
import com.shaohsiung.burgundyred.dto.ProductItemDto;
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.CartService;
import com.shaohsiung.burgundyred.service.CategoryService;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.service.SellerBannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 商城门户
 */
@Slf4j
@Controller
@RequestMapping("/")
public class IndexController {

    @Reference(version = "1.0.0")
    private CategoryService categoryService;

    @Reference(version = "1.0.0")
    private SellerBannerService sellerBannerService;

    @Reference(version = "1.0.0")
    private ProductService productService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping
    public String index(HttpServletRequest request, Model model) {
        // 判断用户是否登录
        User user = (User) request.getAttribute("user");
        if (user != null) {
            log.info("【前台应用】门户服务-当前用户：{}", user.getUserName());
            model.addAttribute("user", user);
        }

        // 获取商品类目列表
        List<CategoryListItemDto> categoryList = categoryService.indexCategoryList();

        // 获取轮播图数据
        List<Banner> banners = (List<Banner>) redisTemplate.opsForValue().get(AppConstant.BANNER_CACHE_KEY);
        if (banners == null) {
            banners = sellerBannerService.indexBanner();
            redisTemplate.opsForValue().set(AppConstant.BANNER_CACHE_KEY, banners, 1, TimeUnit.DAYS);
            log.info("【前台应用】门户服务-缓存未命中，调用基础SVC并缓存轮播图数据");
        } else {
            log.info("【前台应用】门户服务-缓存命中，从redis拉取轮播图数据");
        }

        // 获取商品展示数据
        List<ProductItemDto> latestStyle = productService.latestStyle(4);
        List<ProductItemDto> recommendedStyle = productService.recommendedStyle(4);
        List<ProductItemDto> intimateStyle = productService.intimateStyle(4);
        List<ProductItemDto> scarceStyle = productService.scarceStyle(4);
        List<ProductItemDto> clearanceStyle = productService.clearanceStyle(4);

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("banners", banners);
        model.addAttribute("latestStyle", latestStyle);
        model.addAttribute("recommendedStyle", recommendedStyle);
        model.addAttribute("intimateStyle", intimateStyle);
        model.addAttribute("scarceStyle", scarceStyle);
        model.addAttribute("clearanceStyle", clearanceStyle);

        return "index";
    }
}
