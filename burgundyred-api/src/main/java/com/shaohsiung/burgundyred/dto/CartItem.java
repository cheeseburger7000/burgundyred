package com.shaohsiung.burgundyred.dto;

import com.shaohsiung.burgundyred.enums.ProductState;
import com.shaohsiung.burgundyred.model.Product;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class CartItem implements Serializable {

    private String productId;

    private String name;

    /** 产品单价*/
    private BigDecimal price;

    private Integer count;

    /** 购物项总价 */
    private BigDecimal amount;

}
