package com.lxz.careercode.admin.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 从请求里取真实客户端 IP。直连时取 remoteAddr，过代理时取转发头。
 */
public final class ClientIpResolver {

    private static final String UNKNOWN = "unknown";

    private ClientIpResolver() {
    }

    public static String resolve(HttpServletRequest request) {
        String ip = firstValid(request, "X-Forwarded-For");
        if (ip == null) {
            ip = firstValid(request, "X-Real-IP");
        }
        return ip == null ? request.getRemoteAddr() : ip;
    }

    /** 取当前请求的 IP，Service 层写操作日志时用；非 Web 线程返回 null */
    public static String currentIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return resolve(servletAttributes.getRequest());
        }
        return null;
    }

    private static String firstValid(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        if (!StringUtils.hasText(value) || UNKNOWN.equalsIgnoreCase(value)) {
            return null;
        }
        // X-Forwarded-For 可能是 "客户端, 代理1, 代理2"，取第一个
        int comma = value.indexOf(',');
        return comma > 0 ? value.substring(0, comma).trim() : value.trim();
    }
}
