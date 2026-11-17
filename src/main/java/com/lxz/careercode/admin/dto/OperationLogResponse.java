package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;

/**
 * 操作日志的一条记录。
 *
 * @param adminName 操作人显示名，查不到时为空
 */
public record OperationLogResponse(
        Long id,
        Long adminUserId,
        String adminName,
        String module,
        String action,
        Long targetId,
        String targetTitle,
        String detail,
        String ip,
        LocalDateTime createdAt) {
}
