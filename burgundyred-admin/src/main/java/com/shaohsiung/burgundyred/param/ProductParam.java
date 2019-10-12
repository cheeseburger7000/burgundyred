package com.shaohsiung.burgundyred.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductParam {
    @NotBlank
    private String name;
    @NotBlank
    private String detail;
    @NotBlank
    private BigDecimal price;
    @NotNull
    private Integer stock;
    @NotBlank
    private String mainPicture;
    @NotBlank
    private String subPicture;
    @NotNull
    private String categoryId;
}
