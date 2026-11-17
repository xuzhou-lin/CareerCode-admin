package com.lxz.careercode.admin.controller;

import com.lxz.careercode.admin.common.Result;
import com.lxz.careercode.admin.dto.OverviewResponse;
import com.lxz.careercode.admin.dto.ReportStatResponse;
import com.lxz.careercode.admin.dto.TrendResponse;
import com.lxz.careercode.admin.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 看板接口。
 */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatController {

    private final StatService statService;

    /** 待审帖子、待处理举报、今日发帖、正常用户，近七日趋势 */
    @GetMapping
    public Result<OverviewResponse> overview() {
        return Result.success(statService.overview());
    }

    /** 趋势图：dim 取 post/comment/referral/help，days 取 7 或 30 */
    @GetMapping("/trend")
    public Result<TrendResponse> trend(@RequestParam(defaultValue = "post") String dim,
                                       @RequestParam(defaultValue = "7") int days) {
        return Result.success(statService.trend(dim, days));
    }

    /** 举报统计：状态分布 + 近 N 天新增 */
    @GetMapping("/reports")
    public Result<ReportStatResponse> reportStat(@RequestParam(defaultValue = "7") int days) {
        return Result.success(statService.reportStat(days));
    }
}
