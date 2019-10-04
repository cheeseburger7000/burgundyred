package com.shaohsiung.burgundyred.enums;

import lombok.Getter;

/**
 * 商品状态
 */
@Getter
public enum OrderState {

    NOT_SHIPPED(0, "未发货"),
    CANCEL(1, "取消中"),
    SHIPPED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CLOSED(4, "已关闭")
    ;

    private Integer code;
    private String message;

    OrderState(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
