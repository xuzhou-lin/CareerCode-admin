package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.common.Result;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.WordBatchRequest;
import com.lxz.careercode.admin.dto.WordItemResponse;
import com.lxz.careercode.admin.dto.WordSaveRequest;
import com.lxz.careercode.admin.service.WordAdminService;
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
 * 敏感词维护接口。
 */
@RestController
@RequestMapping("/api/admin/words")
@RequiredArgsConstructor
public class AdminWordController {

    private final WordAdminService wordAdminService;

    /** 词列表，可按分类、状态、词面筛 */
    @GetMapping
    public Result<PageResult<WordItemResponse>> list(
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        return Result.success(wordAdminService.list(category, status, keyword, page, size));
    }

    /** 新增或修改，body 带 id 就是改 */
    @PostMapping
    public Result<Void> save(@Valid @RequestBody WordSaveRequest request) {
        wordAdminService.save(request);
        return Result.success();
    }

    /** 批量导入，换行或逗号分隔，返回实际新增条数 */
    @PostMapping("/batch")
    public Result<Integer> batchImport(@Valid @RequestBody WordBatchRequest request) {
        return Result.success(wordAdminService.batchImport(request));
    }

    /** 启用或禁用 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id,
                                     @RequestParam Integer status) {
        wordAdminService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        wordAdminService.delete(id);
        return Result.success();
    }
}
