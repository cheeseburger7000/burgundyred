package com.shaohsiung.burgundyred.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {
    private Integer state;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
}
