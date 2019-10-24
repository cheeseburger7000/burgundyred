package com.shaohsiung.burgundyred.error;

import java.io.Serializable;

/**
 * 前台异常
 *
 * 面向用户
 */
public class FrontEndException extends RuntimeException implements Serializable {
    private String message;
    private Integer code;

    public FrontEndException(String message) {
        this.message = message;
        this.code = -1;
    }

    public FrontEndException(ErrorState errorState) {
        this.message = errorState.getMessage();
        this.code = errorState.getCode();
    }

    public FrontEndException(ErrorState errorState, String message) {
        if (message == null || message.equals("")) {
            this.message = errorState.getMessage();
        }
        this.message = message;
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
