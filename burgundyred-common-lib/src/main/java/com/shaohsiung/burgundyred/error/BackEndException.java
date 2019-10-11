package com.shaohsiung.burgundyred.error;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台异常
 *
 * 面向管理员
 */
public class BackEndException extends RuntimeException implements Serializable {
    private Integer code;

    public BackEndException(ErrorState errorState) {
        super(errorState.getMessage());
        this.code = errorState.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
