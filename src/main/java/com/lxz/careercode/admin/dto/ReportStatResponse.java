package com.lxz.careercode.admin.dto;

import java.util.List;

/**
 * 举报处理统计。
 *
 * @param total    举报总数
 * @param pending  待处理
 * @param accepted 已受理
 * @param rejected 已驳回
 * @param done     已处理
 * @param points   近 N 天每天新增举报
 */
public record ReportStatResponse(
        long total,
        long pending,
        long accepted,
        long rejected,
        long done,
        List<DailyCount> points) {
}
