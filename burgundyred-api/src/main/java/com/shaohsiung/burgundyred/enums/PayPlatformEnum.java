package com.shaohsiung.burgundyred.enums;

import lombok.Getter;

@Getter
public enum PayPlatformEnum{
    WECHAT(0, "微信"),
    ALIPAY(1, "支付宝"),
    ;

    PayPlatformEnum(int code,String value){
        this.code = code;
        this.value = value;
    }
    private String value;
    private int code;
}
