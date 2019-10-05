package com.shaohsiung.burgundyred.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;

/**
 * 封装JJWT的JWT工具类，SALT 和 TTL 由调用该工具类的微服务模块负责传递
 */
@Getter
@Setter
//@ConfigurationProperties("jwt.config")
public class JwtUtils {

    /** 服务端私钥 **/
    private String key = "burgundyred";

    /** TODO JWT 过期时间 **/
    private long ttl = 30000;

    /**
     * 生成JWT
     * @param id 用户ID
     * @param name 用户名称
     * @param avatar
     * @return JWT
     */
    public String createJWT(String id, String name, String avatar) {
        long nowMillis = System.currentTimeMillis();  // new Date().getTime();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder().setId(id)
                .setSubject(name)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key).claim("avatar", avatar);

        // 存在过期时间
        if (ttl > 0) {
            builder.setExpiration(new Date(nowMillis + ttl));
        }

        return builder.compact();
    }

    /**
     * 解析JWT
     *
     * 参考：
     * log.info("用户认证时间：{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(claims.getIssuedAt()));
     * log.info("JWT过期时间：{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(claims.getExpiration()));
     * return User.builder().id(claims.getId()).userName(claims.getSubject()).roles((String) claims.get("roles")).build();
     *
     * @param token
     * @return
     */
    public Claims parseJWT(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

}