package com.shaohsiung.burgundyred.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class Cart implements Serializable {

    private String userId;

    /** 产品id， 商品项 */
    private Map<String, CartItem> content;

    private BigDecimal total;

}
