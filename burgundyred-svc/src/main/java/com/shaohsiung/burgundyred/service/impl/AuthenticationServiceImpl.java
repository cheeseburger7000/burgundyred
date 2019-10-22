package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.converter.ObjectBytesConverter;
import com.shaohsiung.burgundyred.enums.UserState;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.mapper.AdminMapper;
import com.shaohsiung.burgundyred.mapper.UserMapper;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.AppUtils;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service(version = "1.0.0")
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

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

    /**
     * 判断用户名是否已经存在，若存在，则返回已存在的用户名
     *
     * 前台
     * @param userName
     * @return
     */
    @Override
    public String confirmUserNameUnique(String userName) {
        return userMapper.confirmUserNameUnique(userName);
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    @Override
    public BaseResponse userList(Integer pageNum, Integer pageSize) {
        int offset = pageNum * pageSize;
        List<User> userList = userMapper.userList(new RowBounds(offset, pageSize));
        userList.forEach(user -> {
            user.setPassword("");
        });
        log.info("【基础SVC-用户模块】获取用户列表：{}", userList);
        return BaseResponseUtils.success(userList);
    }

    /**
     * 获取用户列表总数量
     *
     * @return
     */
    @Override
    public Integer userListTotalRecord() {
        return userMapper.userListTotalRecord();
    }

    /**
     * 冻结用户
     *
     * @param userId
     * @return
     */
    @Override
    public BaseResponse freeze(String userId) {
        int update = userMapper.freeze(userId);
        if (update == 1) {
            log.info("【基础SVC-用户模块】冻结用户成功，userId：{}", userId);
            return BaseResponseUtils.success();
        }
        throw new BackEndException(ErrorState.FREEZE_USER_FAILED);
    }

    /**
     * 恢复用户正常状态
     *
     * @param userId
     * @return
     */
    @Override
    public BaseResponse normal(String userId) {
        int update = userMapper.normal(userId);
        if (update == 1) {
            log.info("【基础SVC-用户模块】恢复用户正常状态成功，userId：{}", userId);
            return BaseResponseUtils.success();
        }
        throw new BackEndException(ErrorState.NORMAL_USER_FAILED);
    }

    /**
     * 管理员登陆
     *
     * @param adminName
     * @param password
     * @return
     */
    @Override
    public Administrator adminLogin(String adminName, String password) {
        String encryptPassword = AppUtils.sha256Encrypt(password);
        Administrator administrator = adminMapper.findByAdminNameAndPassword(adminName, encryptPassword);
        if (administrator != null) {
            administrator.setPassword("");
        }
        return administrator;
    }


}
