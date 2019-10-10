package com.shaohsiung.burgundyred.service.impl;

import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.service.BannerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BannerServiceImplTest {

    @Autowired
    private BannerService bannerService;

    @Test
    @Transactional
    public void bannerList() {
        BaseResponse response = bannerService.bannerList(0, 4);
        Assert.assertNotNull(response.getData());
    }

    @Test
    @Transactional
    public void addBanner() {
        Banner banner = Banner.builder().name("勃艮第红")
                .path("http://xxx.jpg")
                .build();
        BaseResponse baseResponse = bannerService.addBanner(banner);
        Assert.assertEquals(ResultCode.SUCCESS.getCode(), baseResponse.getState());
    }

    @Test
    @Transactional
    public void deleteBanner() {
        BaseResponse baseResponse = bannerService.deleteBanner("1");
        Assert.assertEquals(ResultCode.SUCCESS.getCode(), baseResponse.getState());
    }

    @Test
    @Transactional
    public void activeBanner() {
        BaseResponse baseResponse = bannerService.deleteBanner("2");
        Assert.assertEquals(ResultCode.SUCCESS.getCode(), baseResponse.getState());
    }

    @Test
    @Transactional
    public void inactiveBanner() {
        BaseResponse baseResponse = bannerService.inactiveBanner("2");
        Assert.assertEquals(ResultCode.SUCCESS.getCode(), baseResponse.getState());
    }
}
