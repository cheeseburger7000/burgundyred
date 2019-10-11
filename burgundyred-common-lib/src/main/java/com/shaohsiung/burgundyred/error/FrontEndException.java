package com.shaohsiung.burgundyred.error;

import lombok.Data;

import java.io.Serializable;

/**
 * 前台异常
 *
 * 面向用户
 */
public class FrontEndException extends RuntimeException implements Serializable {
    private Integer code;

    public FrontEndException(ErrorState errorState) {
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
