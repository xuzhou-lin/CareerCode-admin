package com.lxz.careercode.admin.dto;

/**
 * 按天统计的一个点。
 *
 * @param date 格式 yyyy-MM-dd
 * @param cnt  当天的数量
 */
public record DailyCount(String date, long cnt) {
}
