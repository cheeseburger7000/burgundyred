package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.CartService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    /**
     * 获取购物车页面
     * @param request
     * @param model
     * @return
     */
    @GetMapping
    public String getCart(HttpServletRequest request,
                          Model model) {
        // 登陆校验
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Cart cart = cartService.get(user.getId());

        model.addAttribute("cart", cart);

        return "cart";
    }

    /**
     * 删除购物车商品项
     * @param request
     * @param productId
     * @param model
     * @return
     */
    @GetMapping("/delete/{productId}")
    public String deleteCartItem(HttpServletRequest request,
                                 @PathVariable("productId") String productId,
                                 Model model) {
        // 登陆校验
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Cart cart = cartService.deleteCartItem(productId, user.getId());

        model.addAttribute("cart", cart);
        return "cart";
    }

    @GetMapping("/inc/{productId}")
    public String add(HttpServletRequest request,
                            @PathVariable("productId") String productId,
                      Model model) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Cart cart = cartService.add(productId, user.getId());

        model.addAttribute("cart", cart);

        return "cart";
    }

    @GetMapping("/inc2cart/{productId}")
    public String addToCart(HttpServletRequest request,
                            @PathVariable("productId") String productId) {

        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Cart cart = cartService.add(productId, user.getId());

        return "redirect:/product/" + productId;
    }

    @GetMapping("/decrease/{productId}")
    public String decrease(HttpServletRequest request,
                      @PathVariable("productId") String productId,
                      Model model) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Cart cart = cartService.decrease(productId, user.getId());

        model.addAttribute("cart", cart);

        return "cart";
    }
}
