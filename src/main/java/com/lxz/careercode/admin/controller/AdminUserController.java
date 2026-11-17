package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.UserItemResponse;
import com.lxz.careercode.admin.dto.UserStatusRequest;
import com.lxz.careercode.admin.service.UserAdminService;
import com.lxz.careercode.admin.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理接口。
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserAdminService userAdminService;

    /** 用户列表，nickname 和手机号都能搜 */
    @GetMapping
    public Result<PageResult<UserItemResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(userAdminService.list(keyword, page, size));
    }

    /** 封禁（status=1）或解封（status=0），原因会写进操作日志 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id,
                                     @Valid @RequestBody UserStatusRequest request) {
        userAdminService.updateStatus(id, request);
        return Result.success();
    }
}
