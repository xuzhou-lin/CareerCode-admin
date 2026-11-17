package com.lxz.careercode.admin.common;

import lombok.Getter;

/**
 * 业务异常。
 * <p>
 * 全局异常处理器会把它转成 {@link Result}，code 透传给前端。
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message) {
        this(400, message);
    }

    /** 未登录 / 令牌失效 */
    public static BizException unauthorized(String message) {
        return new BizException(401, message);
    }

    /** 登录了但不是管理员 */
    public static BizException forbidden(String message) {
        return new BizException(403, message);
    }
}
