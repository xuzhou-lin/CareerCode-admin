package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.common.Result;
import com.lxz.careercode.admin.dto.CommentItemResponse;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.service.CommentAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论管理接口。
 */
@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentAdminService commentAdminService;

    /** 评论列表，按内容关键词筛 */
    @GetMapping
    public Result<PageResult<CommentItemResponse>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(commentAdminService.list(status, keyword, page, size));
    }

    /** 隐藏（status=2）或恢复（status=1） */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id,
                                     @RequestParam Integer status) {
        commentAdminService.updateStatus(id, status);
        return Result.success();
    }
}
