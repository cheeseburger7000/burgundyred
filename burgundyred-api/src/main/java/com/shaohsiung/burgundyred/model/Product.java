package com.shaohsiung.burgundyred.model;

import com.shaohsiung.burgundyred.enums.ProductState;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品数据模型
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private String id;

    private String name;

    private String introduction;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private ProductState state;

    private String mainPicture;

    /** 使用英文逗号分割。 */
    private String subPicture;

    private String categoryId;

    private Date createTime;

    private Date updateTime;
}
