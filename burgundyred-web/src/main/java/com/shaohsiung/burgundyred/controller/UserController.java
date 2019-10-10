package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.form.UserForm;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.CookieUtils;
import com.shaohsiung.burgundyred.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 用户服务
 */
@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Reference(version = "1.0.0")
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtils jwtUtils;

    /** 获取用户注册页面 */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /** 处理用户注册 */
    @PostMapping("/register")
    @ResponseBody
    public String register(@Valid @RequestBody UserForm userForm, Model model) {
        if (!userForm.getPassword().equals(userForm.getConfirmPassword())) {
            // TODO
            throw new FrontEndException("两次密码不一致");
        }

        User user = new User();
        BeanUtils.copyProperties(userForm, user);
        User register = authenticationService.register(user);

        String message = "注册成功！账户激活邮件已经发送到邮箱 "+ register.getEmail() + " , 请您在三天直接激活，否则需要重新注册！";
        model.addAttribute("message", message);

        log.info("{}", message);
        return "message";
    }

    /** 获取用户登录页面 */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /** 处理用户登录 */
    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName, @RequestParam("password") String password,
                        Model model, HttpServletResponse response) {
        User user = authenticationService.login(userName, password);
        if (user == null) {
            throw new FrontEndException("账号密码错误");
        }

        try {
            // 生成JWT
            String token = jwtUtils.createJWT(user.getId(), user.getUserName(), user.getAvatar());
            // 将token放在cookie中
            CookieUtils.set(response, CookieUtils.TOKEN, token, CookieUtils.expire);
        } catch (Exception e) {
            throw new FrontEndException("鉴权失败");
        }

        model.addAttribute("user", user);
        return "index";
    }

    /** 用户注销 */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie token = CookieUtils.get(request, "token");
        token.setMaxAge(0);
        response.addCookie(token);
        return "index";
    }

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
