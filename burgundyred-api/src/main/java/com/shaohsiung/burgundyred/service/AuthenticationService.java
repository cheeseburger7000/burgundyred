package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.model.Administrator;
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
    User login(String userName, String password);

    /**
     * 激活用户
     * @param userId
     * @param token
     * @return
     */
    BaseResponse activate(String userId, String token);

    /**
     * 判断用户名是否已经存在，若存在，则返回已存在的用户名
     *
     * 前台
     * @param userName
     * @return
     */
    String confirmUserNameUnique(String userName);

    /**
     * 获取用户列表
     * @return
     */
    BaseResponse userList(Integer pageNum, Integer pageSize);

    /**
     * 获取用户列表总数量
     * @return
     */
    Integer userListTotalRecord();

    /**
     * 冻结用户
     * @param userId
     * @return
     */
    BaseResponse freeze(String userId);

    /**
     * 恢复用户正常状态
     * @param userId
     * @return
     */
    BaseResponse normal(String userId);


    /**
     * 管理员登陆
     * @param adminName
     * @param password
     * @return
     */
    Administrator adminLogin(String adminName, String password);
}
