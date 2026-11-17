package com.lxz.careercode.admin.service.impl;

import com.lxz.careercode.admin.common.AdminContext;
import com.lxz.careercode.admin.common.BizException;
import com.lxz.careercode.admin.common.ClientIpResolver;
import com.lxz.careercode.admin.dto.AdminCreateRequest;
import com.lxz.careercode.admin.dto.AdminItemResponse;
import com.lxz.careercode.admin.dto.AdminPasswordRequest;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.entity.admin.AdminOperationLog;
import com.lxz.careercode.admin.entity.admin.AdminUser;
import com.lxz.careercode.admin.mapper.admin.AdminOperationLogMapper;
import com.lxz.careercode.admin.mapper.admin.AdminUserMapper;
import com.lxz.careercode.admin.service.AdminAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAccountServiceImpl implements AdminAccountService {

    private final AdminUserMapper adminUserMapper;
    private final AdminOperationLogMapper operationLogMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<AdminItemResponse> list(int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;

        long total = adminUserMapper.countAll();
        List<AdminItemResponse> items = adminUserMapper.selectPage(offset, safeSize)
                .stream()
                .map(this::toItem)
                .toList();
        return new PageResult<>(total, items);
    }

    @Override
    public void create(AdminCreateRequest request) {
        String username = request.username().trim();

        // 1. 登录名唯一，软删的也算占用，避免历史日志里两个同名的人
        if (adminUserMapper.selectByUsername(username) != null) {
            throw new BizException("登录名已被占用");
        }

        // 2. 落库。现在只有超管一种角色，role 固定写 1
        AdminUser admin = new AdminUser();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setNickname(StringUtils.hasText(request.nickname()) ? request.nickname().trim() : username);
        admin.setRole(AdminUser.ROLE_SUPER);
        admin.setStatus(AdminUser.STATUS_NORMAL);
        adminUserMapper.insert(admin);

        writeLog("admin_create", admin.getId(), username, "新增管理员");
        log.info("新增管理员 username={} 操作人={}", username, AdminContext.getId());
    }

    @Override
    public void resetPassword(Long adminId, AdminPasswordRequest request) {
        AdminUser target = requireAdmin(adminId);
        adminUserMapper.updatePassword(adminId, passwordEncoder.encode(request.password()));

        writeLog("admin_reset_pwd", adminId, target.getUsername(), "重置密码");
        log.info("重置管理员密码 adminId={} 操作人={}", adminId, AdminContext.getId());
    }

    @Override
    public void updateStatus(Long adminId, Integer status) {
        if (status == null || (status != AdminUser.STATUS_NORMAL && status != AdminUser.STATUS_DISABLED)) {
            throw new BizException("状态只能是 0 或 1");
        }

        // 1. 不能禁用自己，否则当场把自己锁在外面
        if (adminId.equals(AdminContext.requiredId())) {
            throw new BizException("不能禁用自己");
        }

        AdminUser target = requireAdmin(adminId);
        if (target.getStatus() != null && target.getStatus().equals(status)) {
            throw new BizException(status == AdminUser.STATUS_DISABLED ? "该账号已是禁用状态" : "该账号当前正常");
        }

        // 2. 禁用前留个底：至少还有一个能登录的管理员
        if (status == AdminUser.STATUS_DISABLED && adminUserMapper.countEnabled() <= 1) {
            throw new BizException("至少要保留一个可用管理员");
        }

        int updated = adminUserMapper.updateStatus(adminId, status);
        if (updated == 0) {
            throw new BizException("状态更新失败，账号可能已被删除");
        }

        writeLog(status == AdminUser.STATUS_DISABLED ? "admin_disable" : "admin_enable",
                adminId, target.getUsername(), status == AdminUser.STATUS_DISABLED ? "禁用账号" : "启用账号");
        log.info("管理员状态变更 adminId={} status={} 操作人={}", adminId, status, AdminContext.getId());
    }

    private AdminUser requireAdmin(Long adminId) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null || admin.getDeletedAt() != null) {
            throw new BizException("管理员不存在");
        }
        return admin;
    }

    private void writeLog(String action, Long targetId, String username, String detail) {
        Long adminId = AdminContext.getId();
        if (adminId == null) {
            return;
        }
        try {
            AdminOperationLog entry = new AdminOperationLog();
            entry.setAdminUserId(adminId);
            entry.setModule("admin");
            entry.setAction(action);
            entry.setTargetId(targetId);
            entry.setTargetTitle(username);
            entry.setDetail(detail);
            entry.setIp(ClientIpResolver.currentIp());
            operationLogMapper.insert(entry);
        } catch (Exception e) {
            log.warn("写操作日志失败 adminId={}", targetId, e);
        }
    }

    private AdminItemResponse toItem(AdminUser admin) {
        return new AdminItemResponse(
                admin.getId(),
                admin.getUsername(),
                admin.getNickname(),
                admin.getRole(),
                admin.getStatus(),
                admin.getLastLoginAt(),
                admin.getLastLoginIp(),
                admin.getCreatedAt());
    }
}
