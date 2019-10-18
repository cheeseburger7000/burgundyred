package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.CookieUtils;
import com.shaohsiung.burgundyred.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户服务
 */
@Controller
@Slf4j
@RequestMapping("/user")
@Validated
public class UserController {

    @Reference(version = "1.0.0")
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * TODO 获取用户注册页面
     * @return
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * 处理用户注册
     * @param userName
     * @param password
     * @param confirmPassword
     * @param email
     * @param mobile
     * @param model
     * @return
     */
    @PostMapping("/register")
    public String register(@RequestParam @NotBlank String userName,
                           @RequestParam @NotBlank String password,
                           @RequestParam @NotBlank String confirmPassword,
                           @RequestParam @Email String email,
                           @RequestParam @Pattern(regexp = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[35678]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|66\\d{2})\\d{6}$") String mobile,
                           Model model) {

        // 校验用户名
        String repeatName = authenticationService.confirmUserNameUnique(userName);
        if (repeatName != null) {
            model.addAttribute("message", "用户名已存在");
            return "message";
        }

        // 校验密码
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "两次密码不一致");
            return "message";
        }

        User user = User.builder().userName(userName)
                .password(password)
                .email(email)
                .mobile(mobile)
                .build();
        User register = authenticationService.register(user);

        String message = "注册成功！账户激活邮件已经发送到邮箱 "+ register.getEmail() + " , 请您在三天之内激活，否则需要重新注册！";
        model.addAttribute("message", message);
        return "message";
    }

    /**
     * TODO 获取用户登录页面
     * @return
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 处理用户登录
     * @param userName
     * @param password
     * @param response
     * @param model
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestParam @NotBlank String userName,
                        @RequestParam @NotBlank String password,
                        HttpServletResponse response,
                        Model model) {

        User user = authenticationService.login(userName, password);
        if (user == null) {
            model.addAttribute("message", "账号密码错误");
            return "message";
        }

        try {
            // 生成JWT
            String token = jwtUtils.createJWT(user.getId(), user.getUserName(), user.getAvatar());
            // 将token放在cookie中
            CookieUtils.set(response, CookieUtils.TOKEN, token, CookieUtils.expire);
        } catch (Exception e) {
            log.error("鉴权失败");
            model.addAttribute("message", "鉴权失败");
            return "message";
        }
        // 重定向到index
        return "redirect:/";
    }

    /**
     * 用户注销
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie token = CookieUtils.get(request, "token");
        token.setMaxAge(0);
        token.setPath("/");
        token.setHttpOnly(true);
        response.addCookie(token);
        log.info("用户注销登录");
        return "redirect:/";
    }

    /**
     * 账户激活
     * @param userId
     * @param token
     * @param model
     * @return
     */
    @GetMapping("/activate/{userId}/{token}")
    public String activate(@PathVariable("userId") String userId, @PathVariable("token") String token,
                           Model model) {
        String result = "账户激活失败！";
        BaseResponse activate = authenticationService.activate(userId, token);
        if (activate.getState().equals(ResultCode.SUCCESS.getCode())) {
            result = "账户激活成功！";
        }

        model.addAttribute("message", result);
        return "message";
    }
}
