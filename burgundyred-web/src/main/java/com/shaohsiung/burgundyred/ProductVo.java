package com.shaohsiung.burgundyred;

import com.shaohsiung.burgundyred.enums.ProductState;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductVo implements Serializable {

    private String id;

    private String name;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private ProductState state;

    private String mainPicture;

    private String subPicture;

    private String categoryId;

    private String categoryName;
}
