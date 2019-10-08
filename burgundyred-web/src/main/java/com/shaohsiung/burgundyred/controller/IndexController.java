package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Category;
import com.shaohsiung.burgundyred.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商城门户
 */
@RestController
@RequestMapping("/")
public class IndexController {

    @Reference(version = "1.0.0")
    private CategoryService categoryService;

    @GetMapping
    public String index() {
        // 获取类目列表
        // 获取轮播图 redis缓存
        // 根据类目获取商品 F5
        return null;
    }
}
