package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.constant.QiniuConstant;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.model.Category;
import com.shaohsiung.burgundyred.param.BannerParam;
import com.shaohsiung.burgundyred.param.CategoryParam;
import com.shaohsiung.burgundyred.service.CategoryService;
import com.shaohsiung.burgundyred.service.SellerBannerService;
import com.shaohsiung.burgundyred.util.AppUtils;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 内容管理
 */
@Slf4j
@RestController
@RequestMapping("/content")
@CrossOrigin(allowCredentials="true", maxAge = 3600)
public class ContentController {

    @Reference(version = "1.0.0")
    private SellerBannerService sellerBannerService;

    @Reference(version = "1.0.0")
    private CategoryService categoryService;

    /**
     * 添加轮播图
     * @param bannerParam
     * @return
     */
    @PostMapping("/banner")
    public BaseResponse addBanner(@Valid @RequestBody BannerParam bannerParam, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【后台应用】添加轮播图请求，参数：{}", bannerParam);
        Banner banner = new Banner();
        BeanUtils.copyProperties(bannerParam, banner);
        BaseResponse result = sellerBannerService.addBanner(banner);
        return result;
    }

    @PostMapping("/banner/active/{bannerId}")
    public BaseResponse activeBanner(@PathVariable("bannerId") String bannerId, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【后台应用】激活轮播图请求，轮播图id：{}", bannerId);
        BaseResponse result = sellerBannerService.activeBanner(bannerId);
        return result;
    }

    @PostMapping("/banner/inactive/{bannerId}")
    public BaseResponse inactiveBanner(@PathVariable("bannerId") String bannerId, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【后台应用】取消激活轮播图请求，轮播图id：{}", bannerId);
        BaseResponse result = sellerBannerService.inactiveBanner(bannerId);
        return result;
    }

    @GetMapping("/banner/{pageNum}")
    public BaseResponse bannerList(@PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        BaseResponse bannerList = sellerBannerService.bannerList(pageNum, AppConstant.BANNER_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = sellerBannerService.bannerListTotalRecord();

        Map result = new HashMap();
        result.put("bannerList", bannerList.getData());
        result.put("pageSize", AppConstant.BANNER_PAGE_SIZE);
        result.put("totalRecord", totalRecord);
        return BaseResponseUtils.success(result);
    }

    @GetMapping("/category/{pageNum}")
    public BaseResponse categoryList(@PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        List<Category> categories = categoryService.categoryList(pageNum, AppConstant.CATEGORY_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = categoryService.categoryListTotalRecord();

        Map result = new HashMap();
        result.put("categories", categories);
        result.put("pageSize", AppConstant.CATEGORY_PAGE_SIZE);
        result.put("totalRecord", totalRecord);
        return BaseResponseUtils.success(result);
    }

    @PostMapping("/category")
    public BaseResponse addCategory(@Valid @RequestBody CategoryParam categoryParam, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        Category category = new Category();
        BeanUtils.copyProperties(categoryParam, category);
        Category result = categoryService.addCategory(category);
        return BaseResponseUtils.success(result);
    }

    @PostMapping("/category/hot/{categoryId}")
    public BaseResponse setHot(@PathVariable("categoryId") String categoryId,
                               HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【后台应用】商品类目设置为热门请求，商品类目id：{}", categoryId);
        return categoryService.setHot(categoryId);
    }

    @PostMapping("/category/unhot/{categoryId}")
    public BaseResponse setUnhot(@PathVariable("categoryId") String categoryId,
                                 HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【后台应用】商品类目设置为热门请求，商品类目id：{}", categoryId);
        return categoryService.setUnhot(categoryId);
    }
}
