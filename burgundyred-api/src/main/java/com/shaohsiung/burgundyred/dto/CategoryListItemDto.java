package com.shaohsiung.burgundyred.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryListItemDto implements Serializable {
    private String id;
    private String name;
}
