package com.lxz.careercode.admin.common;

import lombok.Data;

/**
 * 统一返回体。
 * <p>
 * 结构刻意和 C 端保持一致（code / message / data / timestamp），
 * 前端 http.js 的解析逻辑不用为后台单独写一套。
 * HTTP 状态码统一 200，业务结果看 code。
 */
@Data
public class Result<T> {

    /** 200 成功；401 未登录；403 无权限；400 参数错误；500 服务异常 */
    private int code;
    private String message;
    private T data;
    private long timestamp;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
}
