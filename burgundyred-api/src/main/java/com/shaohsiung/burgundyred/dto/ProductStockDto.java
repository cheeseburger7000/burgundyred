package com.shaohsiung.burgundyred.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockDto implements Serializable {
    private String productId;
    private Integer quanity;
}
