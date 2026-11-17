package com.lxz.careercode.admin.dto;

import java.time.LocalDateTime;

/**
 * 用户管理列表的一行。
 *
 * @param status 0 正常 1 封禁 2 已注销
 */
public record UserItemResponse(
        Long id,
        String nickname,
        String phone,
        String email,
        Integer status,
        LocalDateTime createdAt) {
}
