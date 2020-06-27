package com.gggsl.config;

import com.gggsl.common.dto.Result;
import com.gggsl.common.exception.BusinessException;
import com.gggsl.common.exception.ExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    Result<String> accessDeniedExceptionHandler(HttpServletRequest req, Exception e) {
        Result<String> result = new Result<>();
        result.setCode(ExceptionType.SYSTEM_EXCEPTION.getCode());
        result.setMessage(ExceptionType.SYSTEM_EXCEPTION.getMessage());
        LOGGER.error("AccessDenied:{}\r\n{}", result,e);
        return result;
    }
    /**业务异常处理
     * @param req
     * @param e 业务异常
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus
    Result<String> businessExceptionHandler(HttpServletRequest req, BusinessException e) {
        Result<String> result = new Result<>();
        result.setCode(e.getCode());
        result.setMessage(e.getMessage());
        LOGGER.error("业务异常:{} \r\n{}", result,e);

        return result;
    }
}
