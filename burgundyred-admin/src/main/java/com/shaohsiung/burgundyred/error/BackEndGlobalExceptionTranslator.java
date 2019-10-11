package com.shaohsiung.burgundyred.error;

import com.shaohsiung.burgundyred.api.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BackEndGlobalExceptionTranslator {
    /**
     * 处理后台异常
     * @param e
     * @return
     */
    @ExceptionHandler(BackEndException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse handleError(BackEndException e) {
        Integer code = e.getCode();
        String message = e.getMessage();
        log.error("【前台异常】code: {}, message: {}", code, message);

        return BaseResponse.builder().state(code)
                .message(message)
                .build();
    }
}
