package com.shaohsiung.burgundyred.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminParam {
    @NotBlank
    private String adminName;
    @NotBlank
    private String password;
}
