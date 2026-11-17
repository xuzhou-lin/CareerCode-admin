package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;

/**
 * 评论管理列表的一行。
 *
 * @param status 1 正常 2 已隐藏
 */
public record CommentItemResponse(
        Long id,
        Long postId,
        String postTitle,
        String content,
        String authorNickname,
        Integer status,
        LocalDateTime createdAt) {
}
