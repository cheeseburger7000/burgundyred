package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.model.Banner;

import java.util.List;

/**
 * 处理轮播图
 */
public interface BannerService {
    BaseResponse bannerList(int pageNum, int pageSize);
    BaseResponse addBanner(Banner banner);
    BaseResponse deleteBanner(String bannerId);
    BaseResponse activeBanner(String bannerId);
    BaseResponse inactiveBanner(String bannerId);
}
