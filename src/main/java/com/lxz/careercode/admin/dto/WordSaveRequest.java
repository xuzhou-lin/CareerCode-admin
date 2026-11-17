package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增或修改敏感词。不传 id 就是新增。
 */
public record WordSaveRequest(
        Long id,

        @NotBlank(message = "敏感词不能为空")
        @Size(max = 100, message = "单个词最多 100 个字符")
        String word,

        Integer category,

        Integer level) {
}
