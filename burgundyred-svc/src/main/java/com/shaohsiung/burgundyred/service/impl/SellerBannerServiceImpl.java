package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.mapper.BannerMapper;
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.service.SellerBannerService;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service(version = "1.0.0")
public class SellerBannerServiceImpl implements SellerBannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public BaseResponse bannerList(int pageNum, int pageSize) {
        int offset = pageNum * pageSize;
        List<Banner> bannerList = bannerMapper.bannerList(new RowBounds(offset, pageSize));
        log.info("【轮播图SVC】获取轮播图列表：{}", bannerList);

        return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .data(bannerList)
                .build();
    }

    @Override
    public BaseResponse addBanner(Banner banner) {
        banner.setActive(false);
        banner.setId(idWorker.nextId()+"");
        banner.setCreateTime(new Date());

        int save = bannerMapper.save(banner);
        if (save == 1) {
            log.info("【轮播图SVC】添加轮播图：{}", banner);
            return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                    .message(ResultCode.SUCCESS.getMessage())
                    .build();
        }
        throw new BackEndException(ErrorState.BANNER_CREATE_FAILED);
    }

    @Override
    public BaseResponse deleteBanner(String bannerId) {
        int delete = bannerMapper.deleteById(bannerId);
        if (delete == 1) {
            log.info("【轮播图SVC】删除轮播图：{}", bannerId);
            return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                    .message(ResultCode.SUCCESS.getMessage())
                    .build();
        }
        throw new BackEndException(ErrorState.BANNER_DELETE_FAILED);
    }

    @Override
    public BaseResponse activeBanner(String bannerId) {
        // 判断已激活的图片是否超过5张
        Integer activeCount = bannerMapper.calcActiveCount();
        if (activeCount >= AppConstant.MAX_BANNER_COUNT) {
            throw new BackEndException(ErrorState.BANNER_COUNT_REACHES_THE_UPPER_LIMIT);
        }

        int update = bannerMapper.active(bannerId);
        if (update == 1) {
            log.info("【轮播图SVC】激活轮播图：{}", bannerId);

            // 删除redis缓存 banner
            Boolean delete = redisTemplate.delete(AppConstant.BANNER_CACHE_KEY);
            if (delete) {
                log.info("【轮播图SVC】删除banner缓存成功！");
            }

            return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                    .message(ResultCode.SUCCESS.getMessage())
                    .build();
        }
        throw new BackEndException(ErrorState.BANNER_INACTIVATE_FAILED);
    }

    @Override
    public BaseResponse inactiveBanner(String bannerId) {
        int update = bannerMapper.inactive(bannerId);
        if (update == 1) {
            log.info("【轮播图SVC】取消轮播图：{}", bannerId);

            // 删除redis缓存 banner TODO 使用消息队列优化实现同步缓存
            Boolean delete = redisTemplate.delete(AppConstant.BANNER_CACHE_KEY);
            if (delete) {
                log.info("【轮播图SVC】删除banner缓存成功！");
            }

            return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                    .message(ResultCode.SUCCESS.getMessage())
                    .build();
        }
        throw new BackEndException(ErrorState.BANNER_CANCEL_FAILED);
    }

    /**
     * 获取主页轮播图
     *
     * @return
     */
    @Override
    public List<Banner> indexBanner() {
        Integer count = bannerMapper.calcActiveCount();
        if (count <= 0 || count > AppConstant.MAX_BANNER_COUNT) {
            log.error("轮播图获取出错");
            return Collections.emptyList();
        }

        List<Banner> result = bannerMapper.getIndexBanner(AppConstant.MAX_BANNER_COUNT);
        log.info("【轮播图SVC】获取主页轮播图：{}", result);
        return result;
    }

    /**
     * 获取轮播图数量
     *
     * @return
     */
    @Override
    public Integer bannerListTotalRecord() {
        return bannerMapper.bannerListTotalRecord();
    }
}
