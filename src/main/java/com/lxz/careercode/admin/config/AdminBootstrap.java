package com.lxz.careercode.admin.config;

import com.lxz.careercode.admin.entity.admin.AdminUser;
import com.lxz.careercode.admin.mapper.admin.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 部署兜底：启动时如果一条管理员记录都没有，自动建一个，免得新环境进不去。
 * <p>
 * 只在表为空时动手，已有账号的环境完全不受影响。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_NICKNAME = "超级管理员";

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    /** 关掉后启动不再自动建号，适合生产环境自己管账号 */
    @Value("${admin.init-enabled:true}")
    private boolean initEnabled;

    /** 默认密码，生产环境建议用环境变量 ADMIN_INIT_PASSWORD 覆盖 */
    @Value("${admin.init-password:Admin@123456}")
    private String initPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!initEnabled) {
            return;
        }
        long existing = adminUserMapper.countAll();
        if (existing > 0) {
            return;
        }

        AdminUser admin = new AdminUser();
        admin.setUsername(DEFAULT_USERNAME);
        admin.setPasswordHash(passwordEncoder.encode(initPassword));
        admin.setNickname(DEFAULT_NICKNAME);
        admin.setRole(AdminUser.ROLE_SUPER);
        admin.setStatus(AdminUser.STATUS_NORMAL);
        adminUserMapper.insert(admin);

        log.warn("账号表为空，已自动创建管理员 {} / {}，请登录后立即修改密码", DEFAULT_USERNAME, initPassword);
    }
}
