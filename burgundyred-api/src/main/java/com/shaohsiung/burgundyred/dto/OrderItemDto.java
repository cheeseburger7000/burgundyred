package com.shaohsiung.burgundyred.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDto implements Serializable {
    private String productId;
    private String mainPicture;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
}
