package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.common.Result;
import com.lxz.careercode.admin.dto.OperationLogResponse;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.service.LogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志查询接口。
 */
@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final LogQueryService logQueryService;

    /** 操作流水，新的在前 */
    @GetMapping
    public Result<PageResult<OperationLogResponse>> list(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Long adminUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(logQueryService.list(module, adminUserId, page, size));
    }
}
