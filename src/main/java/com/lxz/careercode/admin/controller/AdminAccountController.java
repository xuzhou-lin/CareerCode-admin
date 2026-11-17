package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.common.Result;
import com.lxz.careercode.admin.dto.AdminCreateRequest;
import com.lxz.careercode.admin.dto.AdminItemResponse;
import com.lxz.careercode.admin.dto.AdminPasswordRequest;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.service.AdminAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员账号管理接口。现阶段所有管理员权限相同。
 */
@RestController
@RequestMapping("/api/admin/admins")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    /** 管理员列表 */
    @GetMapping
    public Result<PageResult<AdminItemResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(adminAccountService.list(page, size));
    }

    /** 新增管理员，登录名唯一，角色固定超管 */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody AdminCreateRequest request) {
        adminAccountService.create(request);
        return Result.success();
    }

    /** 重置密码 */
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable("id") Long id,
                                      @Valid @RequestBody AdminPasswordRequest request) {
        adminAccountService.resetPassword(id, request);
        return Result.success();
    }

    /** 启用（0）或禁用（1），不能禁用自己，也不能禁用最后一个 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id,
                                     @RequestParam Integer status) {
        adminAccountService.updateStatus(id, status);
        return Result.success();
    }
}
