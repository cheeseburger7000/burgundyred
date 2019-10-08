package com.shaohsiung.burgundyred.controller.request;

public class Response<T> extends BaseResponse {
    private T data;

    Response(Integer state, String message) {
        super(state, message);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
