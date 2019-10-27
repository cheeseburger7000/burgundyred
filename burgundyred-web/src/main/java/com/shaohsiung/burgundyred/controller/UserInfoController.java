package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Shipping;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import com.shaohsiung.burgundyred.service.ShippingService;
import com.shaohsiung.burgundyred.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/info")
public class UserInfoController {

    @Reference(version = "1.0.0")
    private UserInfoService userInfoService;

    @Reference(version = "1.0.0")
    private ShippingService shippingService;

    @GetMapping
    public String infoPage(HttpServletRequest request, Model model) {
        // 登陆校验
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        // 调用用户服务获取用户基本信息
         User userResult = (User) userInfoService.getById(user.getId()).getData();
        // 调用物流服务获取用户所有物流信息
        List<Shipping> shippings = shippingService.shippingList(user.getId());

        model.addAttribute("user", userResult);
        model.addAttribute("shippings", shippings);

        return "info";
    }
}
