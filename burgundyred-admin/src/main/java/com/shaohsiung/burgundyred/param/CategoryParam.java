package com.shaohsiung.burgundyred.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryParam {
    @NotBlank
    private String name;
    @NotBlank
    private String detail;
}
