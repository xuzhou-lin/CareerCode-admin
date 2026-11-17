package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;

/**
 * 管理员列表的一行。
 *
 * @param role 1 超管（当前只有这一种），留给以后区分审核/运营
 */
public record AdminItemResponse(
        Long id,
        String username,
        String nickname,
        Integer role,
        Integer status,
        LocalDateTime lastLoginAt,
        String lastLoginIp,
        LocalDateTime createdAt) {
}
