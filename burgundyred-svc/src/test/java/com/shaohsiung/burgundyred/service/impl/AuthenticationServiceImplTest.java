package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AuthenticationServiceImplTest {

    @Reference(version = "1.0.0")
    private AuthenticationService authenticationService;

    @Test
    public void register() {
        User shaohsiung = User.builder().email("583809255@qq.com")
                .userName("shaohsiung")
                .password("123")
                .mobile("13055228247")
                .build();
        User register = authenticationService.register(shaohsiung);
    }

    @Test
    public void login() {
        String token = authenticationService.login("shaohsiung", "123");
    }

    @Test
    public void activate() {
    }
}
