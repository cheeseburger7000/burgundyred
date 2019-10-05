package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.model.User;

/**
 * 鉴权模块
 */
public interface AuthenticationService {

    /**
     * 用户注册
     *
     * 前台
     * @param user
     * @return
     */
    User register(User user);

    /**
     * 用户登录
     * @param userName
     * @param password
     * @return json web token
     */
    String login(String userName, String password);

    /**
     * 激活用户
     * @param userId
     * @return
     */
    User activate(String userId);
}
