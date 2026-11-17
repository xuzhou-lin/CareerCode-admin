package com.lxz.careercode.admin.dto;

import java.util.List;

/**
 * 概览数据。
 *
 * @param weekTrend 近七日发帖量，缺数据的日子已补 0
 */
public record OverviewResponse(
        long pendingPosts,
        long pendingReports,
        long todayPosts,
        long totalUsers,
        List<DailyCount> weekTrend) {
}
