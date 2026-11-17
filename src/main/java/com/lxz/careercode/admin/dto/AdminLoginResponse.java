package com.lxz.careercode.admin.dto;

/**
 * 管理员登录返回。
 *
 * @param expireIn 剩余有效期，单位秒
 */
public record AdminLoginResponse(
        String token,
        long expireIn,
        Long adminId,
        String nickname,
        Integer role) {
}
