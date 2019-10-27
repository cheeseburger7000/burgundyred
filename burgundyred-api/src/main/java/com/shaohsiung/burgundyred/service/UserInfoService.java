package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.api.BaseResponse;

/**
 * 个人中心服务
 */
public interface UserInfoService {
    BaseResponse getById(String userId);
}
