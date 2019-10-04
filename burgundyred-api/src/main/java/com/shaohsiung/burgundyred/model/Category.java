package com.shaohsiung.burgundyred.model;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品类目数据模型
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    private String id;

    private String name;

    private String detail;

    private Boolean hot;

    private Date createTime;

    private Date updateTime;
}
