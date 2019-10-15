package com.shaohsiung.burgundyred;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AdminApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(AdminApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
