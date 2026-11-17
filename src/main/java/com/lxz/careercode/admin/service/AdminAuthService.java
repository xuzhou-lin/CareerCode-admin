package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.AdminInfoResponse;
import com.lxz.careercode.admin.dto.AdminInitRequest;
import com.lxz.careercode.admin.dto.AdminLoginRequest;
import com.lxz.careercode.admin.dto.AdminLoginResponse;

/**
 * 后台身份：登录、初始化第一个管理员、查当前身份。
 */
public interface AdminAuthService {

    /**
     * 登录。账号不存在、密码错误都返回同一个提示，防止被枚举。
     *
     * @param ip        来源 IP，写进登录日志
     * @param userAgent 客户端标识，写进登录日志
     */
    AdminLoginResponse login(AdminLoginRequest request, String ip, String userAgent);

    /** 当前管理员信息 */
    AdminInfoResponse me(Long adminId);

    /** 初始化第一个管理员，只在账号表为空时可用，作为自动初始化失效后的兜底 */
    void init(AdminInitRequest request);
}
