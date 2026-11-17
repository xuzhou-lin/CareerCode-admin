package com.lxz.careercode.admin.dto;

import java.util.List;

/**
 * 趋势图数据。
 *
 * @param dim    post 发帖 / comment 评论 / referral 内推 / help 互助
 * @param days   7 或 30
 * @param total  区间内总数
 * @param points 按天排列，缺数据的日子已补 0
 */
public record TrendResponse(
        String dim,
        int days,
        long total,
        List<DailyCount> points) {
}
