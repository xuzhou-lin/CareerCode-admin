package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理员登录请求。
 */
public record AdminLoginRequest(
        @NotBlank(message = "请输入账号") String username,
        @NotBlank(message = "请输入密码") String password) {
}
