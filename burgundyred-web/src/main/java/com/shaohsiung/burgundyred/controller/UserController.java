package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.form.UserForm;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.util.CookieUtils;
import com.shaohsiung.burgundyred.util.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户服务
 */
@Controller
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
    public String register(UserForm userForm, Model model) {
        User user = new User();
        BeanUtils.copyProperties(userForm, user);
        User register = authenticationService.register(user);

        model.addAttribute("register", register);
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

            // TODO 将JWT存储到redis


            // 将token放在cookie中
            CookieUtils.set(response, CookieUtils.TOKEN, token, CookieUtils.expire);

        } catch (Exception e) {
            throw new FrontEndException("鉴权失败");
        }

        model.addAttribute("user", user);
        return "index";
    }

    /** 用户注销 */
    /** 处理用户登录 */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, Model model) {
        Cookie token = CookieUtils.get(request, "token");
        token.setMaxAge(0);

        // TODO redis移除token

        model.addAttribute("user", null);
        return "index";
    }
}
