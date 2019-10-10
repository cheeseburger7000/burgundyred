package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.mapper.BannerMapper;
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.service.BannerService;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service(version = "1.0.0")
public class BannerServiceImpl implements BannerService {

    // TODO 最多主页轮播图数量
    private Integer MAX_BANNER_COUNT = 5;

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private IdWorker idWorker;

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
        throw new BackEndException("轮播图添加失败");
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
        throw new BackEndException("轮播图删除失败");
    }

    @Override
    public BaseResponse activeBanner(String bannerId) {
        // 判断已激活的图片是否超过5张
        Integer activeCount = bannerMapper.calcActiveCount();
        if (activeCount >= MAX_BANNER_COUNT) {
            throw new BackEndException("轮播图数量达到上限");
        }

        int update = bannerMapper.active(bannerId);
        if (update == 1) {
            log.info("【轮播图SVC】激活轮播图：{}", bannerId);
            return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                    .message(ResultCode.SUCCESS.getMessage())
                    .build();
        }
        throw new BackEndException("轮播图激活失败");
    }

    @Override
    public BaseResponse inactiveBanner(String bannerId) {
        int update = bannerMapper.inactive(bannerId);
        if (update == 1) {
            log.info("【轮播图SVC】取消轮播图：{}", bannerId);
            return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                    .message(ResultCode.SUCCESS.getMessage())
                    .build();
        }
        throw new BackEndException("轮播图取消失败");
    }
}
