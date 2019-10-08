package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.enums.UserState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.mapper.UserMapper;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.AppUtils;
import com.shaohsiung.burgundyred.util.IdWorker;
import com.shaohsiung.burgundyred.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
@Service(version = "1.0.0")
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IdWorker idWorker;
    /**
     * 用户注册
     * <p>
     * 前台
     *
     * @param user
     * @return
     */
    @Override
    public User register(User user) {
        // 初始化默认值
        user.setId(idWorker.nextId()+"");
        user.setCreateTime(new Date());
        user.setState(UserState.INACTIVATED);

        // 设置默认头像
        user.setAvatar("http://todo.jpg");

        // 用户密码加盐加密
        String encryptPassword = AppUtils.sha256Encrypt(user.getPassword());
        user.setPassword(encryptPassword);

        int save = userMapper.save(user);
        if (save == 1) {

            // TODO Spring Mail发送邮件

            log.info("【鉴权模块】用户注册：{}", user);
            return user;
        }
        throw new FrontEndException("注册失败");
    }

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @return json web token 包括id，用户名称，头像
     */
    @Override
    public User login(String userName, String password) {
        String encryptPassword = AppUtils.sha256Encrypt(password);
        User user = userMapper.findByUserNameAndPassword(userName, encryptPassword);
        if (user == null) {
            throw new FrontEndException("账号或密码错误");
        }

        // 判断用户状态
        if (user.getState().equals(UserState.INACTIVATED)) {
            throw new FrontEndException("该用户未激活");
        }
        if (user.getState().equals(UserState.FREEZE)) {
            throw new FrontEndException("该用户已被冻结");
        }
        log.info("【鉴权模块】用户登录：{}", user);
        return user;

        // 生成JWT
//        try {
//            String result = jwtUtils.createJWT(user.getId(), user.getUserName(), user.getAvatar());
//            return result;
//        } catch (Exception e) {
//            throw new FrontEndException("鉴权失败");
//        }
    }

    /**
     * TODO 激活用户
     *
     * @param userId
     * @return
     */
    @Override
    public User activate(String userId) {
        return null;
    }
}
