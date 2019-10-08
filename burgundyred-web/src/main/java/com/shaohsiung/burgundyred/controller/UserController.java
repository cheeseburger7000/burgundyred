package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.form.UserForm;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Reference(version = "1.0.0")
    private AuthenticationService authenticationService;

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
    public String login(@RequestParam("userName") String userName, @RequestParam("password") String password, Model model) {
        String token = authenticationService.login(userName, password);
        if (token == null) {

        }

        // 将token放在cookie中
        //model.addAttribute("login", login);
        return "index";
    }

    /** 用户注销 */
}
