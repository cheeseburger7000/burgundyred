package com.shaohsiung.burgundyred.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
@ControllerAdvice
public class GlobalExceptionTranslator {

    @ExceptionHandler(FrontEndException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleError(FrontEndException e) {
        String exMessage = e.getMessage();
        log.error("【前台异常】code: {}, message: {}", e.getCode(), exMessage);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", exMessage);
        modelAndView.setViewName("message");
        return modelAndView;
    }
}
