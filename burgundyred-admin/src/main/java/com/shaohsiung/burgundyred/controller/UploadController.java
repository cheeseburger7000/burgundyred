package com.shaohsiung.burgundyred.controller;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.constant.QiniuConstant;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/upload")
@CrossOrigin(allowCredentials="true", maxAge = 3600)
public class UploadController {

    @Autowired
    private QiniuConstant qiniuConstant;

    @PostMapping(consumes = "multipart/form-data")
    public BaseResponse uploadBannerPicture(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

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

}
