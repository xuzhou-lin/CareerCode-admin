package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;

/**
 * 审核列表的一行。不含正文，正文在详情接口里单独取。
 */
public record AuditItem(
        Long id,
        Integer type,
        String title,
        String summary,
        Integer status,
        String authorNickname,
        Boolean anonymous,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount,
        LocalDateTime createdAt) {
}
