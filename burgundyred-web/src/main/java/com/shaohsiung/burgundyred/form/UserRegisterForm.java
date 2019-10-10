package com.shaohsiung.burgundyred.form;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterForm {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    @Email
    private String email;

    @Pattern(regexp = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[35678]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|66\\d{2})\\d{6}$")
    private String mobile;
}
