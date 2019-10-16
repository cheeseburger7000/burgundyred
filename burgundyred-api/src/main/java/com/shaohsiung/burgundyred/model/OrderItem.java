package com.shaohsiung.burgundyred.model;

import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项数据模型
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {

    private String id;

    private String orderId;

    private String orderNo;

    private String productId;

    /** 商品数量 */
    private Integer quantity;

    /** 订单项总额 */
    private BigDecimal amount;
}
