package com.shaohsiung.burgundyred.error;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class FrontEndGlobalExceptionTranslator {

    /**
     * 处理前台异常
     * @param e
     * @return
     */
    @ExceptionHandler(FrontEndException.class)
    public ModelAndView handleFrontEndException(FrontEndException e) {
        String exMessage = e.getMessage();
        log.warn("【前台异常】code: {}, message: {}", e.getCode(), exMessage);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", exMessage);
        modelAndView.setViewName("message");
        return modelAndView;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleConstraintViolationException(ConstraintViolationException e) {
        ModelAndView result = new ModelAndView();
        log.warn("【前台异常】Constraint Violation: {}", e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
        String message = String.format("%s:%s", path, violation.getMessage());

        result.addObject("message", message);
        result.setViewName("message");
        return result;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        ModelAndView result = new ModelAndView();
        log.warn("【前台异常】未知的错误: {}", e.getMessage());

        result.addObject("message", e.getMessage());
        result.setViewName("message");
        return result;
    }
}
