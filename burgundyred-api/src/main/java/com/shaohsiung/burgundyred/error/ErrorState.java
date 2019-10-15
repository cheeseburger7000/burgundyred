package com.shaohsiung.burgundyred.error;

import lombok.Getter;

import java.io.Serializable;

/**
 * 4xxx 前台ex
 * 5xxx 后台ex
 * 备注：第二位表示模块类型
 */
@Getter
public enum ErrorState {

    // 前台
    REGISTER_EMAIL_SEND_FAILED(4000, "注册邮件消息发送失败"),
    REGISTER_FAILED(4001, "注册失败"),
    USER_INACTIVATE(4002, "该用户未激活"),
    USER_FREEZE(4003, "该用户已被冻结"),

    PRODUCT_HAS_BEEN_REMOVED(4104, "该商品已下架"),
    PRODUCT_STOCK_LACKING(4105, "商品库存不足"),
    PRODUCT_NOT_EXIST(4106, "商品不存在"),
    PRODUCT_STOCK_UPDATE_FAILED(4107, "商品库存更新失败"),
    PRODUCT_STOCK_LACK(4108, "商品库存不足"),

    ITEM_DOES_NOT_EXIST_IN_THE_CART(4206, "购物车中不存在该商品"),
    CART_IS_EMPTY(4207, "购物车空空如也"),

    ORDER_NOT_EXIST(4308, "订单不存在"),
    ORDER_CAN_NOT_CLOSE(4309, "该订单无法关闭"),
    ORDER_CANCEL_FAILED(4310, "订单取消失败"),
    ORDER_STATE_TRANSFORM_ERROR(4311, "订单状态机转化错误"),

    SHIPPING_CREATE_FAILED(4412, "添加物流信息失败"),
    SHIPPING_UPDATE_FAILED(4413, "修改物流信息失败"),
    SHIPPING_DELETE_FAILED(4414, "删除物流信息失败"),


    // 后台
    CATEGORY_CREATE_FAILED(5007, "商品类目创建失败"),

    PRODUCT_CREATE_FAILED(5108, "产品创建失败"),
    ACQUIRE_PRODUCT_STOCK_FAILED(5109, "获取商品库存错误"),

    BANNER_CREATE_FAILED(5210, "轮播图添加失败"),
    BANNER_DELETE_FAILED(5211, "轮播图删除失败"),
    BANNER_COUNT_REACHES_THE_UPPER_LIMIT(5211, "轮播图数量达到上限"),
    BANNER_INACTIVATE_FAILED(5212, "轮播图激活失败"),
    BANNER_CANCEL_FAILED(5213, "轮播图取消失败"),

    CATEGORY_SET_HOT_FAILED(5214, "商品类目设置热门失败"),
    CATEGORY_SET_UNHOT_FAILED(5215, "商品类目取消热门失败"),

    // 用户模块
    FREEZE_USER_FAILED(5301, "用户冻结失败"),
    NORMAL_USER_FAILED(5302, "恢复用户正常状态失败"),

    // 商品模块
    SET_PRODUCT_ON_THE_SHELF_FAILED(5401, "商品上架失败"),
    PRODUCT_REMOVE_FAILED(5401, "商品下架失败"),
    ;

    private Integer code;
    private String message;

    ErrorState(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
