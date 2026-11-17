package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 重置管理员密码。只能由已登录的管理员操作。
 */
public record AdminPasswordRequest(
        @NotBlank(message = "请输入新密码")
        @Size(min = 8, max = 32, message = "密码长度 8-32 位")
        String password) {
}
