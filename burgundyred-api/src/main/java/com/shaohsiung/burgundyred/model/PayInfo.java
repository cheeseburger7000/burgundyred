package com.shaohsiung.burgundyred.model;

import com.shaohsiung.burgundyred.enums.PayPlatformEnum;
import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单支付信息数据模型
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayInfo implements Serializable {

    private String id;

    private String userId;

    private String orderNo;

    /** 支付平台类型 */
    private PayPlatformEnum payPlatform;

    /** 平台支付流水号 */
    private String platformNumber;

    /** 平台支付状态 */
    private String platformStatus;

    private Date createTime;

    private Date updateTime;
}
