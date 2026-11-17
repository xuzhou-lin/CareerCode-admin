package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 批量导入敏感词。一行一个或用逗号分隔，重复的自动跳过。
 */
public record WordBatchRequest(
        @NotBlank(message = "导入内容不能为空") String text,
        Integer category,
        Integer level) {
}
