package com.shaohsiung.burgundyred;

import com.shaohsiung.burgundyred.util.JwtUtils;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(WebApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }
}
