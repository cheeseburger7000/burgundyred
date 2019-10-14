package com.shaohsiung.burgundyred.error;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台异常
 *
 * 面向管理员
 */
public class BackEndException extends RuntimeException implements Serializable {
    private String message;
    private Integer code;

    public BackEndException(String message) {
        this.message = message;
        this.code = -1;
    }

    public BackEndException(ErrorState errorState) {
        this.message = errorState.getMessage();
        this.code = errorState.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
