package com.lxz.careercode.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lxz.careercode.admin.common.BizException;
import com.lxz.careercode.admin.dto.AdminInfoResponse;
import com.lxz.careercode.admin.dto.AdminInitRequest;
import com.lxz.careercode.admin.dto.AdminLoginRequest;
import com.lxz.careercode.admin.dto.AdminLoginResponse;
import com.lxz.careercode.admin.entity.admin.AdminLoginLog;
import com.lxz.careercode.admin.entity.admin.AdminUser;
import com.lxz.careercode.admin.mapper.admin.AdminLoginLogMapper;
import com.lxz.careercode.admin.mapper.admin.AdminUserMapper;
import com.lxz.careercode.admin.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    /** 登录态前缀：和 C 端的用户 id 区分开，防止两边 id 撞号。
     *  不能用冒号，Sa-Token 内部拿它当分隔符，会直接报错。 */
    private static final String LOGIN_ID_PREFIX = "admin-";

    private final AdminUserMapper adminUserMapper;
    private final AdminLoginLogMapper adminLoginLogMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminLoginResponse login(AdminLoginRequest request, String ip, String userAgent) {
        // 1. 账号存在性
        AdminUser admin = adminUserMapper.selectByUsername(request.username());
        if (admin == null) {
            writeLoginLog(null, request.username(), ip, userAgent, false, "账号不存在");
            throw new BizException(401, "账号或密码错误");
        }

        // 2. 密码校验：提示和"账号不存在"保持一致，避免被枚举出哪些账号有效
        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            writeLoginLog(admin.getId(), request.username(), ip, userAgent, false, "密码错误");
            throw new BizException(401, "账号或密码错误");
        }

        // 3. 状态：密码对了才明确告知被禁用
        if (admin.getStatus() == null || admin.getStatus() != AdminUser.STATUS_NORMAL) {
            writeLoginLog(admin.getId(), request.username(), ip, userAgent, false, "账号已禁用");
            throw BizException.forbidden("账号已被禁用");
        }

        // 4. 签发令牌，落 Redis database 2
        StpUtil.login(LOGIN_ID_PREFIX + admin.getId());
        String token = StpUtil.getTokenValue();

        writeLoginLog(admin.getId(), request.username(), ip, userAgent, true, null);
        adminUserMapper.updateLastLogin(admin.getId(), ip);

        return new AdminLoginResponse(token, StpUtil.getTokenTimeout(),
                admin.getId(), admin.getNickname(), admin.getRole());
    }

    @Override
    public AdminInfoResponse me(Long adminId) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null || admin.getDeletedAt() != null) {
            throw BizException.unauthorized("账号不存在");
        }
        return new AdminInfoResponse(admin.getId(), admin.getUsername(), admin.getNickname(), admin.getRole());
    }

    @Override
    @Transactional
    public void init(AdminInitRequest request) {
        // 只在一条管理员都没有时可用，防止被当成后门接口
        if (adminUserMapper.countAll() > 0) {
            throw new BizException("已经有管理员了，不能再初始化");
        }

        AdminUser admin = new AdminUser();
        admin.setUsername(request.username().trim());
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setNickname(StringUtils.hasText(request.nickname()) ? request.nickname().trim() : "超级管理员");
        admin.setRole(AdminUser.ROLE_SUPER);
        admin.setStatus(AdminUser.STATUS_NORMAL);
        adminUserMapper.insert(admin);

        log.info("已初始化管理员 username={}", request.username());
    }

    private void writeLoginLog(Long adminId, String username, String ip, String userAgent,
                               boolean success, String failReason) {
        try {
            AdminLoginLog entry = new AdminLoginLog();
            entry.setAdminUserId(adminId);
            entry.setUsername(username);
            entry.setIp(ip);
            entry.setUserAgent(userAgent == null ? null : userAgent.substring(0, Math.min(userAgent.length(), 255)));
            entry.setSuccess(success ? 1 : 0);
            entry.setFailReason(failReason);
            adminLoginLogMapper.insert(entry);
        } catch (Exception e) {
            // 日志写失败不能影响登录本身
            log.warn("写登录日志失败 username={}", username, e);
        }
    }
}
