package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.service.BannerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内容管理
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference(version = "1.0.0")
    private BannerService bannerService;


}
