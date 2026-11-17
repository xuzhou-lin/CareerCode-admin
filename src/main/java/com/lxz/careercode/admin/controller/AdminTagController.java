package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.TagItemResponse;
import com.lxz.careercode.admin.dto.TagSaveRequest;
import com.lxz.careercode.admin.service.TagAdminService;
import com.lxz.careercode.admin.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签管理接口。
 */
@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagAdminService tagAdminService;

    /** 标签列表，可按类型和技术栈关键字筛 */
    @GetMapping
    public Result<PageResult<TagItemResponse>> list(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        return Result.success(tagAdminService.list(type, keyword, page, size));
    }

    /** 新增或改名：body 带 id 就是改 */
    @PostMapping
    public Result<Void> save(@Valid @RequestBody TagSaveRequest request) {
        tagAdminService.save(request);
        return Result.success();
    }

    /** 启用或禁用，禁用的标签发帖时不再出现 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id,
                                     @RequestParam Integer status) {
        tagAdminService.updateStatus(id, status);
        return Result.success();
    }

    /** 删除，被帖子引用过的会拒绝 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        tagAdminService.delete(id);
        return Result.success();
    }
}
