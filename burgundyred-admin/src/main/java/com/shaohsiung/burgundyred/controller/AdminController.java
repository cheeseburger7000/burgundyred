package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.param.AdminParam;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import com.shaohsiung.burgundyred.util.CookieUtils;
import com.shaohsiung.burgundyred.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admin")
@CrossOrigin(allowCredentials="true", maxAge = 3600)
public class AdminController {

    @Reference(version = "1.0.0")
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestBody AdminParam adminParam, HttpServletResponse response) {
        Administrator administrator = authenticationService.adminLogin(adminParam.getAdminName(), adminParam.getPassword());
        if (administrator == null) {
            return BaseResponseUtils.failure(ResultCode.FAILURE);
        }
        String token = "";
        try {
            // 生成JWT
            token = jwtUtils.createJWT(administrator.getId(), administrator.getAdminName(), administrator.getAvatar());
            // 将token放在cookie中
            CookieUtils.set(response, AppConstant.ADMIN_JWT_COOKIE_NAME, token, CookieUtils.expire);
        } catch (Exception e) {
            log.warn("【卖家应用】-【管理员服务】JWT生成失败！");
            throw new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }
        return BaseResponseUtils.success(ResultCode.SUCCESS, token);
    }

    @GetMapping("/name")
    public  BaseResponse getAdminName(HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        return BaseResponseUtils.success(ResultCode.SUCCESS, admin.getAdminName());
    }

    @PostMapping("/logout")
    public BaseResponse logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie token = CookieUtils.get(request, "admin_token");
        if (token != null) {
            token.setMaxAge(0);
            token.setPath("/");
            token.setHttpOnly(true);
            response.addCookie(token);
            log.info("管理员注销登录");
        }
        return BaseResponseUtils.success();
    }
}
