package com.shaohsiung.burgundyred;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.shaohsiung.burgundyred.util.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableDubbo
@SpringBootApplication
@MapperScan("com.shaohsiung.burgundyred.mapper")
// TODO @EnableTransactionManagement
public class SvcApplication {
    public static void main(String[] args) {
        SpringApplication.run(SvcApplication.class, args);
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(1, 1, 1);
    }
}
