package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.dto.AuditDetail;
import com.lxz.careercode.admin.dto.AuditItem;
import com.lxz.careercode.admin.dto.AuditRequest;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.service.AuditService;
import com.lxz.careercode.admin.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内容审核接口。
 */
@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {

    private final AuditService auditService;

    /**
     * 审核列表。
     *
     * @param status  状态过滤，不传表示全部；审核页通常传 1（只看待审）
     * @param type    1 面经 2 内推 3 互助，不传表示全部类型
     * @param keyword 标题/摘要关键词
     */
    @GetMapping
    public Result<PageResult<AuditItem>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(auditService.list(status, type, keyword, page, size));
    }

    /** 审核详情，带正文 */
    @GetMapping("/{id}")
    public Result<AuditDetail> detail(@PathVariable("id") Long id) {
        return Result.success(auditService.detail(id));
    }

    /**
     * 通过 / 驳回。
     * <p>
     * 通过 → status 2，驳回 → status 3 并记录原因。
     */
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable("id") Long id, @Valid @RequestBody AuditRequest request) {
        auditService.audit(id, Boolean.TRUE.equals(request.pass()), request.reason());
        return Result.success();
    }
}
