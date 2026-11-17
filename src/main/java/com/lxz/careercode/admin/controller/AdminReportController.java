package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.ReportHandleRequest;
import com.lxz.careercode.admin.entity.biz.Report;
import com.lxz.careercode.admin.service.ReportService;
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
 * 举报处理接口。
 */
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    /**
     * 举报列表，待处理的排最前面。
     *
     * @param status 状态过滤，不传表示全部；0 只看待处理
     */
    @GetMapping
    public Result<PageResult<Report>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(reportService.list(status, page, size));
    }

    /**
     * 处理举报。
     *
     * @param id   举报单 id
     * @param body action：1 受理、2 驳回、3 处理完成
     */
    @PostMapping("/{id}/handle")
    public Result<Void> handle(@PathVariable("id") Long id,
                               @Valid @RequestBody ReportHandleRequest body) {
        reportService.handle(id, body.action(), body.note());
        return Result.success();
    }
}
