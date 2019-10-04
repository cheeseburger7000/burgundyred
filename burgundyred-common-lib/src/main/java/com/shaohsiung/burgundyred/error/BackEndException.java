package com.shaohsiung.burgundyred.error;

import java.io.Serializable;

/**
 * 后台异常
 *
 * 面向管理员
 */
public class BackEndException extends RuntimeException implements Serializable {
    public BackEndException(String message) {
        super(message);
    }
}
