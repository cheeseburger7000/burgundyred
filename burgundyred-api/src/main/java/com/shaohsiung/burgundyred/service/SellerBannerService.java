package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.model.Banner;

import java.util.List;

/**
 * 处理轮播图
 */
public interface SellerBannerService {
    // 卖家
    BaseResponse bannerList(int pageNum, int pageSize);
    BaseResponse addBanner(Banner banner);
    BaseResponse deleteBanner(String bannerId);
    BaseResponse activeBanner(String bannerId);
    BaseResponse inactiveBanner(String bannerId);

    // 买家

    /**
     * 获取主页轮播图
     * @return
     */
    List<Banner> indexBanner();

    /**
     * 获取轮播图数量
     * @return
     */
    Integer bannerListTotalRecord();
}
