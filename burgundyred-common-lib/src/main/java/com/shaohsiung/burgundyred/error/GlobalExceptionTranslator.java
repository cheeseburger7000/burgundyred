package com.shaohsiung.burgundyred.error;

import com.shaohsiung.burgundyred.api.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionTranslator {

    @ExceptionHandler(FrontEndException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse handleError(FrontEndException e) {
        String exMessage = e.getMessage();
        log.warn("【FrontEndException】{}", exMessage);

        return BaseResponse
                .builder()
                .state(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exMessage)
                .build();
    }
}
