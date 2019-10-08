package com.shaohsiung.burgundyred.enums;

import lombok.Getter;

/**
 * 商品状态
 */
@Getter
public enum OrderState {

    UNPAID(0, "未支付"),
    NOT_SHIPPED(1, "未发货"),
    CANCEL(2, "取消中"),
    SHIPPED(3, "已发货"),
    COMPLETED(4, "已完成"),
    CLOSED(5, "已关闭")
    ;

    private Integer code;
    private String message;

    OrderState(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
