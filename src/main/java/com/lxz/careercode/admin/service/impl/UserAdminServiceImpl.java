package com.lxz.careercode.admin.service.impl;

import com.lxz.careercode.admin.common.AdminContext;
import com.lxz.careercode.admin.common.BizException;
import com.lxz.careercode.admin.common.ClientIpResolver;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.UserItemResponse;
import com.lxz.careercode.admin.dto.UserStatusRequest;
import com.lxz.careercode.admin.entity.admin.AdminOperationLog;
import com.lxz.careercode.admin.entity.biz.User;
import com.lxz.careercode.admin.mapper.admin.AdminOperationLogMapper;
import com.lxz.careercode.admin.mapper.biz.UserMapper;
import com.lxz.careercode.admin.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserMapper userMapper;
    private final AdminOperationLogMapper operationLogMapper;

    @Override
    public PageResult<UserItemResponse> list(String keyword, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        String trimmed = StringUtils.hasText(keyword) ? keyword.trim() : null;

        long total = userMapper.countForAdmin(trimmed);
        List<UserItemResponse> items = userMapper.selectPageForAdmin(trimmed, offset, safeSize)
                .stream()
                .map(this::toItem)
                .toList();
        return new PageResult<>(total, items);
    }

    @Override
    public void updateStatus(Long userId, UserStatusRequest request) {
        // 1. 存在性
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        // 2. 已注销的账号不再接受状态变更，注销是不可逆的
        if (user.getStatus() != null && user.getStatus() == User.STATUS_CANCELLED) {
            throw new BizException("账号已注销，不能再改状态");
        }

        int target = request.status();
        if (user.getStatus() != null && user.getStatus() == target) {
            throw new BizException(target == User.STATUS_BANNED ? "该用户已经是封禁状态" : "该用户当前是正常状态");
        }

        // 3. 改状态，业务库只动这一列
        int updated = userMapper.updateStatus(userId, target);
        if (updated == 0) {
            throw new BizException("状态更新失败，用户可能已被删除");
        }

        // 4. 留痕：封禁和解封都记一条
        writeLog(user, target, request.reason());

        log.info("用户状态变更 userId={} status={} 操作人={}", userId, target, AdminContext.getId());
    }

    private void writeLog(User user, int target, String reason) {
        Long adminId = AdminContext.getId();
        if (adminId == null) {
            return;
        }
        try {
            AdminOperationLog entry = new AdminOperationLog();
            entry.setAdminUserId(adminId);
            entry.setModule("user");
            entry.setAction(target == User.STATUS_BANNED ? "user_ban" : "user_unban");
            entry.setTargetId(user.getId());
            entry.setTargetTitle(StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getPhone());
            entry.setDetail(reason);
            entry.setIp(ClientIpResolver.currentIp());
            operationLogMapper.insert(entry);
        } catch (Exception e) {
            log.warn("写操作日志失败 userId={}", user.getId(), e);
        }
    }

    private UserItemResponse toItem(User user) {
        return new UserItemResponse(
                user.getId(),
                user.getNickname(),
                user.getPhone(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt());
    }
}
