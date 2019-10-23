package com.shaohsiung.burgundyred.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shaohsiung.burgundyred.enums.OrderState;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单数据模型
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private String id;

    /** 订单号，添加唯一索引。 */
    private String orderNo;

    private BigDecimal total;

    private OrderState state;

    private String userId;

    private String shippingId;

    private Date createTime;

    private Date updateTime;
}
