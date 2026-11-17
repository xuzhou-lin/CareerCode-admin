package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 新增管理员。目前只有超管一种角色，不给选。
 */
public record AdminCreateRequest(
        @NotBlank(message = "请输入登录名")
        @Size(min = 3, max = 50, message = "登录名 3-50 个字符")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "登录名只能用字母、数字和下划线")
        String username,

        @NotBlank(message = "请输入初始密码")
        @Size(min = 8, max = 32, message = "密码长度 8-32 位")
        String password,

        @Size(max = 50, message = "显示名最多 50 个字符")
        String nickname) {
}
