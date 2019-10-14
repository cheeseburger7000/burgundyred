package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(version = "1.0.0")
    private AuthenticationService authenticationService;

    /**
     * 获取所有用户列表
     * @return
     */
    @GetMapping("/{pageNum}")
    public BaseResponse userList(@PathVariable("pageNum") Integer pageNum) {
        BaseResponse userList = authenticationService.userList(pageNum, AppConstant.USER_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = authenticationService.userListTotalRecord();
        Integer totalPage = (totalRecord + AppConstant.USER_PAGE_SIZE - 1) / AppConstant.USER_PAGE_SIZE;

        Map result = new HashMap();
        result.put("userList", userList.getData());
        result.put("page", pageNum);
        result.put("totalPage", totalPage);
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
