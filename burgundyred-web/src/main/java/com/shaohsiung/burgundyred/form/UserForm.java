package com.shaohsiung.burgundyred.form;

import lombok.Data;

@Data
public class UserForm {
    private String userName;

    private String password;

    private String confirmPassword;

    private String email;

    private String mobile;
}
