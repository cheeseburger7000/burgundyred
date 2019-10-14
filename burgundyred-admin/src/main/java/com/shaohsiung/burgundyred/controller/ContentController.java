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
import com.shaohsiung.burgundyred.model.Banner;
import com.shaohsiung.burgundyred.param.BannerParam;
import com.shaohsiung.burgundyred.service.SellerBannerService;
import com.shaohsiung.burgundyred.util.AppUtils;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 内容管理
 */
@Slf4j
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference(version = "1.0.0")
    private SellerBannerService sellerBannerService;

    @Autowired
    private QiniuConstant qiniuConstant;

    @PostMapping("/banner/upload")
    public BaseResponse uploadBannerPicture(@RequestParam("picture") MultipartFile multipartFile) {
        FileInputStream inputStream = null;
        String path = "";
        try {
            inputStream = (FileInputStream) multipartFile.getInputStream();
            path = uploadQiniuImg(inputStream, UUID.randomUUID().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("【后台应用】上传轮播图请求，七牛云图片路径：{}", path);
        return BaseResponseUtils.success(path);
    }
    /**
     *  七牛云上传图片
     * @param inputStream 输入流
     * @param fileName 图片服务器上文件名称
     * @return URL 文件资源路径
     */
    private String uploadQiniuImg(FileInputStream inputStream, String fileName) throws QiniuException {
        Configuration cfg = new Configuration(Zone.zone2());
        UploadManager uploadManager = new UploadManager(cfg);

        Auth auth = Auth.create(qiniuConstant.getAccessKey(), qiniuConstant.getSecretKey());
        String upToken = auth.uploadToken(qiniuConstant.getBucket());

        Response response = uploadManager.put(inputStream, fileName, upToken, null, null);
        // 解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        String result = "http://" + qiniuConstant.getHost() + "/" + putRet.key;

        return result;
    }

    /**
     * 添加轮播图
     * @param bannerParam
     * @return
     */
    @PostMapping("/banner")
    public BaseResponse addBanner(@Valid @RequestBody BannerParam bannerParam) {
        log.info("【后台应用】添加轮播图请求，参数：{}", bannerParam);
        Banner banner = new Banner();
        BeanUtils.copyProperties(bannerParam, banner);
        BaseResponse result = sellerBannerService.addBanner(banner);
        return result;
    }

    @PostMapping("/banner/active/{bannerId}")
    public BaseResponse activeBanner(@PathVariable("bannerId") String bannerId) {
        log.info("【后台应用】激活轮播图请求，轮播图id：{}", bannerId);
        BaseResponse result = sellerBannerService.activeBanner(bannerId);
        return result;
    }

    @PostMapping("/banner/inactive/{bannerId}")
    public BaseResponse inactiveBanner(@PathVariable("bannerId") String bannerId) {
        log.info("【后台应用】取消激活轮播图请求，轮播图id：{}", bannerId);
        BaseResponse result = sellerBannerService.inactiveBanner(bannerId);
        return result;
    }

    @GetMapping("/banner/{pageNum}")
    public BaseResponse bannerList(@PathVariable("pageNum") Integer pageNum) {
        BaseResponse bannerList = sellerBannerService.bannerList(pageNum, AppConstant.BANNER_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = sellerBannerService.bannerListTotalRecord();
        Integer totalPage = (totalRecord + AppConstant.BANNER_PAGE_SIZE - 1) / AppConstant.BANNER_PAGE_SIZE;

        Map result = new HashMap();
        result.put("bannerList", bannerList.getData());
        result.put("page", pageNum);
        result.put("totalPage", totalPage);
        return BaseResponseUtils.success(result);
    }
}
