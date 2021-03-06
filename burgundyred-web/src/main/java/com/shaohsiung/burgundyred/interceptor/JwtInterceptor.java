package com.shaohsiung.burgundyred.interceptor;

import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.util.CookieUtils;
import com.shaohsiung.burgundyred.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    // 请求执行之前（controller方法之前）
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie中的jwt
        String jwt = CookieUtils.getToken(request);
        if (jwt != null) {
            Claims claims = null;
            try {
                log.info("jwt: {}", jwt);
                claims = jwtUtils.parseJWT(jwt);
            } catch(Exception ex) {
                log.warn("【JWT解析】用户会话过期，请重新登陆！");
            }
            // 将userid设置到请求头
            if (claims != null) {
                User user = User.builder().id(claims.getId())
                        .userName(claims.getSubject())
                        .avatar((String) claims.get("avatar"))
                        .build();
                request.setAttribute("user", user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        log.info("3 请求执行之后，视图渲染之前（controller方法之后）");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        log.info("3 请求执行之后，视图渲染之后（资源请求）");
    }
}
