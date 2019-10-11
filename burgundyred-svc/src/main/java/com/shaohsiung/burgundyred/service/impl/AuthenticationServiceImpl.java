package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.converter.ObjectBytesConverter;
import com.shaohsiung.burgundyred.enums.UserState;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.mapper.UserMapper;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.AppUtils;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service(version = "1.0.0")
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
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
        user.setAvatar(AppConstant.DEFAULT_AVATAR_PATH);

        // 用户密码加盐加密
        String encryptPassword = AppUtils.sha256Encrypt(user.getPassword());
        user.setPassword(encryptPassword);

        int save = userMapper.save(user);
        if (save == 1) {

            // 向消息队列发送消息Spring Mail发送邮件
            byte[] bytes = new byte[0];
            try {
                bytes = ObjectBytesConverter.getBytesFromObject(user);
                rabbitTemplate.convertAndSend("email.exchange", "topic.email", bytes);
                log.info("【鉴权模块】用户注册：{}", user);
            } catch (Exception e) {
                throw new FrontEndException(ErrorState.REGISTER_EMAIL_SEND_FAILED);
            }
            return user;
        }
        throw new FrontEndException(ErrorState.REGISTER_FAILED);
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
            return null;
        }

        // 判断用户状态
        if (user.getState().equals(UserState.INACTIVATED)) {
            throw new FrontEndException(ErrorState.USER_INACTIVATE);
        }
        if (user.getState().equals(UserState.FREEZE)) {
            throw new FrontEndException(ErrorState.USER_FREEZE);
        }
        log.info("【鉴权模块】用户登录：{}", user);
        return user;
    }

    /**
     * 激活用户
     *
     * @param userId
     * @return
     */
    @Override
    public BaseResponse activate(String userId, String token) {
        String redisToken = (String) redisTemplate.opsForValue().get(AppConstant.USER_ACTIVATE_PREFIX + userId);
        if (redisToken != null && redisToken.equals(token)) {
            int update = userMapper.activate(userId);
            if (update == 1) {
                log.info("【鉴权模块】用户id：{} 激活成功", userId);
                redisTemplate.delete(AppConstant.USER_ACTIVATE_PREFIX + userId);
                return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                        .message(ResultCode.SUCCESS.getMessage())
                        .build();
            }
        }

        log.warn("【鉴权模块】用户id：{}  令牌错误，账户激活失败", userId);
        return BaseResponse.builder().state(ResultCode.FAILURE.getCode())
                .message(ResultCode.FAILURE.getMessage())
                .build();
    }

    @Override
    public String confirmUserNameUnique(String userName) {
        return userMapper.confirmUserNameUnique(userName);
    }
}
