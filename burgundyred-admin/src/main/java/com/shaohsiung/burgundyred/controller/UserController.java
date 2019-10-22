package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理
 */
@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(allowCredentials="true", maxAge = 3600)
public class UserController {

    @Reference(version = "1.0.0")
    private AuthenticationService authenticationService;

    /**
     * 获取所有用户列表
     * @return
     */
    @GetMapping("/{pageNum}")
    public BaseResponse userList(@PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【产品管理】当前管理员：{}", admin);

        BaseResponse userList = authenticationService.userList(pageNum, AppConstant.USER_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = authenticationService.userListTotalRecord();

        Map result = new HashMap();
        result.put("userList", userList.getData());
        result.put("pageSize", AppConstant.USER_PAGE_SIZE);
        result.put("totalRecord", totalRecord);
        return BaseResponseUtils.success(result);
    }

    @PostMapping("/freeze/{userId}")
    public BaseResponse freezeUser(@PathVariable("userId") String userId) {
        log.info("【后台应用】冻结用户请求，userId：{}", userId);
        return authenticationService.freeze(userId);
    }

    @PostMapping("/normal/{userId}")
    public BaseResponse normalUser(@PathVariable("userId") String userId) {
        log.info("【后台应用】恢复用户状态请求，userId：{}", userId);
        return authenticationService.normal(userId);
    }
}
