package com.shaohsiung.burgundyred.api;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(0, "操作成功"),
    FAILURE(1, "操作失败"),


    // 支付提示
    ALIPAY_PAY_FAILED(4500, "支付宝预下单失败"),
    ALIPAY_PAY_STATE_UNKNOWN(4501, "系统异常，预下单状态未知"),
    ALIPAY_PAY_STATE_ERROR(4502, "不支持的交易状态，交易返回异常"),
    ALIPAY_PAY_SUCCESS(4503, "支付宝预下单成功"),
    ALIPAY_REPEATED_CALL(4504, "支付宝重复调用，状态错误"),
    ALIPAY_ORDER_NOT_EXIST(4504, "非商城的订单,回调忽略"),
    ;

    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
