package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.OverviewResponse;
import com.lxz.careercode.admin.dto.ReportStatResponse;
import com.lxz.careercode.admin.dto.TrendResponse;

/**
 * 看板数字。
 */
public interface StatService {

    /** 待审、待处理举报、今日发帖、正常用户数，外加近七日发帖趋势 */
    OverviewResponse overview();

    /**
     * 趋势图：发帖 / 评论 / 内推 / 互助，近 7 或 30 天。
     *
     * @param dim  取值 post、comment、referral、help，其他值按 post 处理
     * @param days 只接受 7 和 30，其他值按 7 处理
     */
    TrendResponse trend(String dim, int days);

    /** 举报统计：状态分布 + 近 N 天新增 */
    ReportStatResponse reportStat(int days);
}
