package com.shaohsiung.burgundyred.error;

import java.io.Serializable;

/**
 * 前台异常
 *
 * 面向用户
 */
public class FrontEndException extends RuntimeException implements Serializable {
    public FrontEndException(String message) {
        super(message);
    }
}
