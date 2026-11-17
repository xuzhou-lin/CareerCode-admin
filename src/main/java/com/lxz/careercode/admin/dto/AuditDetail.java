package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审核详情：在列表字段基础上带上正文、审核意见、作者 ID 和历次审核记录。
 */
public record AuditDetail(
        Long id,
        Integer type,
        String title,
        String summary,
        String content,
        Integer contentFormat,
        Integer status,
        String auditRemark,
        Long authorId,
        String authorNickname,
        Boolean anonymous,
        String anonymousName,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AuditRecordItem> auditHistory) {
}
