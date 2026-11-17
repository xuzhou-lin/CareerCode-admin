package com.lxz.careercode.admin.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：把异常统一转成 {@link Result}，前端只看 code。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<Void> onBiz(BizException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    /** @Valid 校验失败 */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> onInvalid(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数不合法" : fieldError.getDefaultMessage();
        return Result.error(400, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> onOther(Exception e) {
        log.error("后台接口异常", e);
        return Result.error(500, "服务异常：" + e.getMessage());
    }
}
