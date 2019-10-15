package com.shaohsiung.burgundyred.error;

import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 后台异常捕获器
 */
@Slf4j
@RestControllerAdvice
public class BackEndGlobalExceptionTranslator {
    /**
     * 处理后台异常
     * @param e
     * @return
     */
    @ExceptionHandler(BackEndException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse handleError(BackEndException e) {
        Integer code = e.getCode();
        String message = e.getMessage();
        log.error("【后台异常】code: {}, message: {}", code, message);

        return BaseResponse.builder().state(code)
                .message(message)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("【后台异常】Constraint Violation: {}", e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
        String message = String.format("%s:%s", path, violation.getMessage());
        return BaseResponse.builder().state(ResultCode.FAILURE.getCode())
                .message(message)
                .build();
    }
}
