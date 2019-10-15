package com.shaohsiung.burgundyred;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.util.IdWorker;
import com.shaohsiung.burgundyred.util.JwtUtils;
import io.lettuce.core.ReadFrom;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@EnableDubbo
@SpringBootApplication
@MapperScan("com.shaohsiung.burgundyred.mapper")
public class SvcApplication {
    // TODO 关闭内置tomcat
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(SvcApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE) // 关闭内嵌Tomcat
                .run(args);
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(1, 1, 1);
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public RedisTemplate<String, Cart> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Cart> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
    @Bean
    public LettuceClientConfigurationBuilderCustomizer customizer() {
        return builder -> builder.readFrom(ReadFrom.MASTER_PREFERRED);
    }
}
