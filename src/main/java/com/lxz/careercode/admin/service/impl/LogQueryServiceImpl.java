package com.lxz.careercode.admin.service.impl;

import com.lxz.careercode.admin.dto.OperationLogResponse;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.entity.admin.AdminOperationLog;
import com.lxz.careercode.admin.entity.admin.AdminUser;
import com.lxz.careercode.admin.mapper.admin.AdminOperationLogMapper;
import com.lxz.careercode.admin.mapper.admin.AdminUserMapper;
import com.lxz.careercode.admin.service.LogQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogQueryServiceImpl implements LogQueryService {

    private final AdminOperationLogMapper operationLogMapper;
    private final AdminUserMapper adminUserMapper;

    @Override
    public PageResult<OperationLogResponse> list(String module, Long adminUserId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int offset = (safePage - 1) * safeSize;
        String trimmed = StringUtils.hasText(module) ? module.trim() : null;

        long total = operationLogMapper.countPage(trimmed, adminUserId);
        List<AdminOperationLog> records = operationLogMapper.selectPage(trimmed, adminUserId, offset, safeSize);
        if (records.isEmpty()) {
            return new PageResult<>(total, List.of());
        }

        // 一次查出这页涉及的管理员名字，避免逐条查
        Set<Long> adminIds = records.stream()
                .map(AdminOperationLog::getAdminUserId)
                .collect(Collectors.toSet());
        Map<Long, String> names = adminUserMapper.selectBatchIds(adminIds).stream()
                .collect(Collectors.toMap(AdminUser::getId,
                        a -> StringUtils.hasText(a.getNickname()) ? a.getNickname() : a.getUsername()));

        List<OperationLogResponse> items = records.stream()
                .map(r -> new OperationLogResponse(
                        r.getId(),
                        r.getAdminUserId(),
                        names.get(r.getAdminUserId()),
                        r.getModule(),
                        r.getAction(),
                        r.getTargetId(),
                        r.getTargetTitle(),
                        r.getDetail(),
                        r.getIp(),
                        r.getCreatedAt()))
                .toList();

        return new PageResult<>(total, items);
    }
}
