package com.shaohsiung.burgundyred.model;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 快递数据模型
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipping implements Serializable {

    private String id;

    private String userId;

    /** 收件人名称 */
    private String receiverName;

    /** 收件人手机号码 */
    private String receiverMobile;

    /** 收件人省份 */
    private String receiverProvince;

    /** 收件人手机城市 */
    private String receiverCity;

    /** 收件人手机区，县 */
    private String receiverDistrict;

    /** 收件人详细地址 */
    private String receiverAddress;

    /** 收件人邮编 */
    private String receiverZip;

    private Date createTime;

    private Date updateTime;
}
