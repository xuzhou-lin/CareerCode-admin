package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;

/**
 * 一条审核历史。详情页展示「谁在什么时候通过了还是驳回了」。
 *
 * @param action 1 通过 2 驳回
 */
public record AuditRecordItem(
        Long adminUserId,
        String adminName,
        Integer action,
        String reason,
        LocalDateTime createdAt) {
}
