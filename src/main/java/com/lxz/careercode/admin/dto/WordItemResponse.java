package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;

/**
 * 敏感词列表的一行。
 *
 * @param categoryName 分类中文名
 * @param level        1 提示 2 拦截 3 转人工
 */
public record WordItemResponse(
        Long id,
        String word,
        Integer category,
        String categoryName,
        Integer level,
        Integer status,
        Integer hitCount,
        LocalDateTime createdAt) {
}
