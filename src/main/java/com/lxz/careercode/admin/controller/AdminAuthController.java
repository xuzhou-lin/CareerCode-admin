package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.common.AdminContext;
import com.lxz.careercode.admin.common.ClientIpResolver;
import com.lxz.careercode.admin.common.Result;
import com.lxz.careercode.admin.dto.AdminInfoResponse;
import com.lxz.careercode.admin.dto.AdminInitRequest;
import com.lxz.careercode.admin.dto.AdminLoginRequest;
import com.lxz.careercode.admin.dto.AdminLoginResponse;
import com.lxz.careercode.admin.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台身份接口。管理员用自己的账号表登录，与 C 端账号无关。
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    /** 连通性检查，不校验登录 */
    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong");
    }

    /** 管理员登录：账号不存在与密码错误返回同一提示 */
    @PostMapping("/login")
    public Result<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request,
                                            HttpServletRequest servletRequest) {
        return Result.success(adminAuthService.login(
                request,
                ClientIpResolver.resolve(servletRequest),
                servletRequest.getHeader("User-Agent")));
    }

    /** 初始化第一个管理员。启动时已自动建号，这里只在账号被清空后应急用 */
    @PostMapping("/init")
    public Result<Void> init(@Valid @RequestBody AdminInitRequest request) {
        adminAuthService.init(request);
        return Result.success();
    }

    /** 当前管理员，前端进后台时先调这个确认身份 */
    @GetMapping("/me")
    public Result<AdminInfoResponse> me() {
        return Result.success(adminAuthService.me(AdminContext.requiredId()));
    }
}
