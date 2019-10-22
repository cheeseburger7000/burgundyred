package com.shaohsiung.burgundyred.interceptor;

import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.util.CookieUtils;
import com.shaohsiung.burgundyred.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = getAdminToken(request);
        if (jwt != null) {
            Claims claims = null;
            try {
                log.info("【卖家应用-JWT解析器】admin's JWT: {}", jwt);
                claims = jwtUtils.parseJWT(jwt);
            } catch(Exception ex) {
                log.warn("【卖家应用-JWT解析器】用户会话过期，请重新登陆！");
            }

            // 将admin放到request域中
            if (claims != null) {
                Administrator admin = Administrator.builder().id(claims.getId())
                        .adminName(claims.getSubject())
                        .avatar((String) claims.get("avatar"))
                        .build();
                request.setAttribute("admin", admin);
            }
        }
        return true;
    }

    /**
     * 获取Cookie中的JWT
     * @param request
     * @return
     */
    public static String getAdminToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return null;
        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> AppConstant.ADMIN_JWT_COOKIE_NAME.equals(cookie.getName()))
                .findAny().orElse(null);
        if (tokenCookie == null) return null;
        return tokenCookie.getValue();
    }
}
