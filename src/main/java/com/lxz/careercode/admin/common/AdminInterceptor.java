package com.lxz.careercode.admin.common;

import cn.dev33.satoken.stp.StpUtil;
import com.lxz.careercode.admin.entity.admin.AdminUser;
import com.lxz.careercode.admin.mapper.admin.AdminUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台鉴权：令牌必须来自 admin: 前缀的 loginId，且对应账号存在、未禁用。
 * 每次请求都查一次库，账号被禁用或删除后立即失效，不用等令牌过期。
 */
@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "Admin-Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    /** 和登录时的前缀保持一致，不能用冒号（Sa-Token 内部当分隔符） */
    private static final String LOGIN_ID_PREFIX = "admin-";

    private final AdminUserMapper adminUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // CORS 预检不带令牌，直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            throw BizException.unauthorized("未登录");
        }

        Object loginId;
        try {
            loginId = StpUtil.getLoginIdByToken(token);
        } catch (Exception e) {
            throw BizException.unauthorized("登录状态已失效");
        }
        // Sa-Token 查不到令牌时返回 null，不抛异常
        if (loginId == null) {
            throw BizException.unauthorized("登录状态已失效");
        }

        // 1. 前缀校验：C 端用户令牌的 loginId 是纯数字，混进来直接拒
        String raw = loginId.toString();
        if (!raw.startsWith(LOGIN_ID_PREFIX)) {
            throw BizException.unauthorized("令牌类型不匹配");
        }

        Long adminId;
        try {
            adminId = Long.valueOf(raw.substring(LOGIN_ID_PREFIX.length()));
        } catch (NumberFormatException e) {
            throw BizException.unauthorized("登录状态异常");
        }

        // 2. 查账号状态
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null || admin.getDeletedAt() != null) {
            throw BizException.unauthorized("账号不存在");
        }
        if (admin.getStatus() == null || admin.getStatus() != AdminUser.STATUS_NORMAL) {
            throw BizException.forbidden("账号已被禁用");
        }

        AdminContext.set(adminId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 线程池复用，必须清理，否则下个请求会读到别人的身份
        AdminContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(TOKEN_HEADER);
        if (!StringUtils.hasText(header)) {
            return null;
        }
        return header.startsWith(TOKEN_PREFIX) ? header.substring(TOKEN_PREFIX.length()).trim() : header.trim();
    }
}
