package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.CartService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * 购物车服务
 */
@Slf4j
@Validated
@Controller
@RequestMapping("/cart")
public class CartController {

    @Reference(version = "1.0.0")
    private CartService cartService;

    @PostMapping("/{productId}")
    @ResponseBody
    public BaseResponse add(HttpServletRequest request,
                            @PathVariable("productId") String productId) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Cart cart = cartService.add(productId, user.getId());
        return BaseResponseUtils.success(cart);
    }
}
