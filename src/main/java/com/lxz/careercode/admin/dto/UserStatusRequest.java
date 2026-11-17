package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 修改用户状态。
 *
 * @param status 0 解封，1 封禁
 * @param reason 操作原因，会写进操作日志
 */
public record UserStatusRequest(
        @NotNull(message = "必须指定目标状态")
        @Min(value = 0, message = "状态只能是 0 或 1")
        @Max(value = 1, message = "状态只能是 0 或 1")
        Integer status,

        @Size(max = 255, message = "原因最多 255 个字符")
        String reason) {
}
