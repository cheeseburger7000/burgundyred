package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.model.Category;
import com.shaohsiung.burgundyred.service.CategoryService;
import com.shaohsiung.burgundyred.service.SellerBannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private RedisTemplate redisTemplate;

    private String BANNER_CACHE_KEY = "banner_cache_key";

    @GetMapping
    public String index(Model model) {
        // 获取类目列表

        // 从redis缓存获取轮播图
        // TODO 卖家设置轮播图之后，同步缓存
        List<Banner> banners = (List<Banner>) redisTemplate.opsForValue().get(BANNER_CACHE_KEY);
        if (banners == null) {
            banners = sellerBannerService.indexBanner();
            redisTemplate.opsForValue().set(BANNER_CACHE_KEY, banners);
            log.info("缓存未命中，调用基础SVC并缓存轮播图数据");
        } else {
            log.info("缓存命中，从redis拉取轮播图数据");
        }

        // 根据类目获取商品 F5

        model.addAttribute("banners", banners);
        return "index";
    }
}
