package com.shaohsiung.burgundyred.enums;

import lombok.Getter;

@Getter
public enum ProductState {

    ON_THE_SHELF(0, "上架中"),
    HAS_BEEN_REMOVED(1, "已下架")
    ;

    private Integer code;
    private String message;

    ProductState(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
