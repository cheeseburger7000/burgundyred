package com.shaohsiung.burgundyred.enums;

import lombok.Getter;

@Getter
public enum UserState {

    INACTIVATED(0, "未激活"),
    NORMAL(1, "正常"),
    FREEZE(2, "冻结")
    ;

    private Integer code;
    private String message;

    UserState(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
