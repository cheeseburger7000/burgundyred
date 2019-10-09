package com.shaohsiung.burgundyred.dto;

import com.shaohsiung.burgundyred.enums.OrderState;
import com.shaohsiung.burgundyred.model.OrderItem;
import com.shaohsiung.burgundyred.model.Shipping;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDetailDto implements Serializable {
    /** 订单号，添加唯一索引。 */
    private String orderNo;

    private BigDecimal total;

    private OrderState state;

    private Date createTime;

    private Date updateTime;

    List<OrderItem> orderItemList;

    Shipping shipping;
}
