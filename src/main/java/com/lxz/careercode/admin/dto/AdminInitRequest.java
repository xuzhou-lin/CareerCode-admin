package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 初始化第一个管理员。只在账号表为空时可用，作为部署兜底。
 */
public record AdminInitRequest(
        @NotBlank(message = "请输入登录名")
        @Size(min = 3, max = 50, message = "登录名 3-50 个字符")
        String username,

        @NotBlank(message = "请输入密码")
        @Size(min = 8, max = 32, message = "密码长度 8-32 位")
        String password,

        @Size(max = 50, message = "显示名最多 50 个字符")
        String nickname) {
}
