package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.dto.ProductItemDto;
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.model.User;
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
import java.util.List;

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
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }

        // 获取类目列表


        // 从redis缓存获取轮播图
        // TODO 卖家设置轮播图之后，同步缓存
        List<Banner> banners = (List<Banner>) redisTemplate.opsForValue().get(AppConstant.BANNER_CACHE_KEY);
        if (banners == null) {
            banners = sellerBannerService.indexBanner();
            redisTemplate.opsForValue().set(AppConstant.BANNER_CACHE_KEY, banners);
            log.info("【前台应用】缓存未命中，调用基础SVC并缓存轮播图数据");
        } else {
            log.info("【前台应用】缓存命中，从redis拉取轮播图数据");
        }

        // TODO 缓存，mq同步
        List<ProductItemDto> latestStyle = productService.latestStyle(4);
        List<ProductItemDto> recommendedStyle = productService.recommendedStyle(4);
        List<ProductItemDto> intimateStyle = productService.intimateStyle(4);
        List<ProductItemDto> scarceStyle = productService.scarceStyle(4);
        List<ProductItemDto> clearanceStyle = productService.clearanceStyle(4);

        model.addAttribute("banners", banners);
        model.addAttribute("latestStyle", latestStyle);
        model.addAttribute("recommendedStyle", recommendedStyle);
        model.addAttribute("intimateStyle", intimateStyle);
        model.addAttribute("scarceStyle", scarceStyle);
        model.addAttribute("clearanceStyle", clearanceStyle);

        return "index";
    }
}
