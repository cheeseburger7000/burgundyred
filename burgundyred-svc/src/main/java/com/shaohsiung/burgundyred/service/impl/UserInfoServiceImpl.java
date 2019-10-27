package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.mapper.UserMapper;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.UserInfoService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service(version = "1.0.0")
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public BaseResponse getById(String userId) {
        User user = userMapper.getById(userId);
        user.setPassword("");
        return BaseResponseUtils.success(user);
    }
}
