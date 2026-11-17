package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;

/**
 * 标签管理列表的一行。
 *
 * @param typeName 类型中文名，如「技术栈」
 * @param useCount 被多少帖子引用，为 0 才允许删除
 */
public record TagItemResponse(
        Long id,
        String name,
        Integer type,
        String typeName,
        Integer useCount,
        Integer status,
        LocalDateTime createdAt) {
}
