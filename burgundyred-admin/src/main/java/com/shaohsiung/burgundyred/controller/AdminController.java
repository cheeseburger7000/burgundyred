package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.param.AdminParam;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import com.shaohsiung.burgundyred.util.CookieUtils;
import com.shaohsiung.burgundyred.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Reference(version = "1.0.0")
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public BaseResponse login(@Valid @RequestBody AdminParam adminParam, HttpServletResponse response) {
        Administrator administrator = authenticationService.adminLogin(adminParam.getAdminName(), adminParam.getPassword());
        try {
            // 生成JWT
            String token = jwtUtils.createJWT(administrator.getId(), administrator.getAdminName(), administrator.getAvatar());
            // 将token放在cookie中
            CookieUtils.set(response, CookieUtils.TOKEN, token, CookieUtils.expire);
        } catch (Exception e) {
            log.error("鉴权失败");
        }
        return BaseResponseUtils.success(administrator);
    }
}
