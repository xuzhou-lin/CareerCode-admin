package com.lxz.careercode.admin.common;

/**
 * 当前管理员上下文。
 * <p>
 * 必须在线程结束时 clear，否则线程池复用会串号。
 */
public final class AdminContext {

    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();

    private AdminContext() {
    }

    public static void set(Long adminId) {
        HOLDER.set(adminId);
    }

    public static Long getId() {
        return HOLDER.get();
    }

    /**
     * 取当前管理员 ID，取不到说明拦截器没生效，属于严重问题。
     */
    public static Long requiredId() {
        Long id = HOLDER.get();
        if (id == null) {
            throw BizException.unauthorized("未登录或登录状态已失效");
        }
        return id;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
