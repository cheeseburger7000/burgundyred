package com.shaohsiung.burgundyred.service.impl;

import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.service.SellerBannerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SellerBannerServiceImplTest {

    @Autowired
    private SellerBannerService sellerBannerService;

    @Test
    @Transactional
    public void bannerList() {
        BaseResponse response = sellerBannerService.bannerList(0, 4);
        Assert.assertNotNull(response.getData());
    }

    @Test
    @Transactional
    public void addBanner() {
        Banner banner = Banner.builder().name("勃艮第红")
                .path("http://xxx.jpg")
                .build();
        BaseResponse baseResponse = sellerBannerService.addBanner(banner);
        Assert.assertEquals(ResultCode.SUCCESS.getCode(), baseResponse.getState());
    }

    @Test
    public void addDefaultBanner() {
        Banner banner1 = Banner.builder().name("默认轮播图1")
                .path("http://localhost:8080/images/banner/default_banner_1.jpg")
                .build();
        Banner banner2 = Banner.builder().name("默认轮播图2")
                .path("http://localhost:8080/images/banner/default_banner_2.jpg")
                .build();
        Banner banner3 = Banner.builder().name("默认轮播图3")
                .path("http://localhost:8080/images/banner/default_banner_3.jpg")
                .build();
        sellerBannerService.addBanner(banner1);
        sellerBannerService.addBanner(banner2);
        sellerBannerService.addBanner(banner3);
    }

    @Test
    @Transactional
    public void deleteBanner() {
        BaseResponse baseResponse = sellerBannerService.deleteBanner("1");
        Assert.assertEquals(ResultCode.SUCCESS.getCode(), baseResponse.getState());
    }

    @Test
    @Transactional
    public void activeBanner() {
        BaseResponse baseResponse = sellerBannerService.activeBanner("2");
        Assert.assertEquals(ResultCode.SUCCESS.getCode(), baseResponse.getState());
    }

    @Test
    @Transactional
    public void inactiveBanner() {
        BaseResponse baseResponse = sellerBannerService.inactiveBanner("2");
        Assert.assertEquals(ResultCode.SUCCESS.getCode(), baseResponse.getState());
    }

    @Test
    public void indexBanner() {
        List<Banner> banners = sellerBannerService.indexBanner();
        Assert.assertNotEquals(0, banners.size());
    }
}
