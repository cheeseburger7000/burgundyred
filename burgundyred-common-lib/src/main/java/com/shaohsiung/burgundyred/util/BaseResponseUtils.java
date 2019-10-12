package com.shaohsiung.burgundyred.util;

import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;

public final class BaseResponseUtils {

    public static BaseResponse success() {
        return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .build();
    }

    public static BaseResponse success(Object data) {
        return BaseResponse.builder().state(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static BaseResponse failure() {
        return BaseResponse.builder().state(ResultCode.FAILURE.getCode())
                .message(ResultCode.FAILURE.getMessage())
                .build();
    }

    public static BaseResponse failure(Object data) {
        return BaseResponse.builder().state(ResultCode.FAILURE.getCode())
                .message(ResultCode.FAILURE.getMessage())
                .data(data)
                .build();
    }
}
