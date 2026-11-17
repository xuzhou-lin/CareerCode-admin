package com.lxz.careercode.admin.dto;

/**
 * 当前管理员信息，前端用来显示右上角和判断角色。
 */
public record AdminInfoResponse(
        Long id,
        String username,
        String nickname,
        Integer role) {
}
