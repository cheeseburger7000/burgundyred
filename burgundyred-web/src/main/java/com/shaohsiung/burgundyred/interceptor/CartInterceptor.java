package com.shaohsiung.burgundyred.interceptor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CartInterceptor implements HandlerInterceptor {
    @Reference(version = "1.0.0")
    private CartService cartService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = (User) request.getAttribute("user");
        if (user != null) {
            // 获取购物车的商品数量
            Cart cart = cartService.get(user.getId());
            Integer cartCount = cart.getContent().size();
            request.setAttribute("cartCount", cartCount);
            log.info("【买家应用】-【购物车拦截器】获取当前用户：{}，购物车数量：{}", user.getUserName(), cartCount);
        }
    }
}
