package com.shaohsiung.burgundyred.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductItemDto implements Serializable {
    private String id;
    private String name;
    private String detail;
    private BigDecimal price;
    private String mainPicture;
}
