package com.lxz.careercode.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxz.careercode.admin.dto.DailyCount;
import com.lxz.careercode.admin.dto.OverviewResponse;
import com.lxz.careercode.admin.dto.ReportStatResponse;
import com.lxz.careercode.admin.dto.StatusCount;
import com.lxz.careercode.admin.dto.TrendResponse;
import com.lxz.careercode.admin.entity.biz.Post;
import com.lxz.careercode.admin.entity.biz.Report;
import com.lxz.careercode.admin.entity.biz.User;
import com.lxz.careercode.admin.mapper.biz.CommentMapper;
import com.lxz.careercode.admin.mapper.biz.PostMapper;
import com.lxz.careercode.admin.mapper.biz.ReportMapper;
import com.lxz.careercode.admin.mapper.biz.UserMapper;
import com.lxz.careercode.admin.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    /** 概览页的趋势固定七天 */
    private static final int OVERVIEW_DAYS = 7;

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final ReportMapper reportMapper;
    private final UserMapper userMapper;

    @Override
    public OverviewResponse overview() {
        long pendingPosts = postMapper.countForAudit(Post.STATUS_REVIEWING, null, null);
        long pendingReports = reportMapper.selectCount(
                new LambdaQueryWrapper<Report>().eq(Report::getStatus, Report.STATUS_PENDING));
        long todayPosts = postMapper.countToday();
        long totalUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, User.STATUS_NORMAL));

        List<DailyCount> trend = fillTrend(OVERVIEW_DAYS,
                toMap(postMapper.countDaily(null, OVERVIEW_DAYS - 1)));

        return new OverviewResponse(pendingPosts, pendingReports, todayPosts, totalUsers, trend);
    }

    @Override
    public TrendResponse trend(String dim, int days) {
        String key = normalizeDim(dim);
        int safeDays = normalizeDays(days);

        Map<String, Long> byDate;
        if ("comment".equals(key)) {
            byDate = toMap(commentMapper.countDaily(safeDays - 1));
        } else {
            byDate = toMap(postMapper.countDaily(dimToPostType(key), safeDays - 1));
        }

        List<DailyCount> points = fillTrend(safeDays, byDate);
        long total = points.stream().mapToLong(DailyCount::cnt).sum();
        return new TrendResponse(key, safeDays, total, points);
    }

    @Override
    public ReportStatResponse reportStat(int days) {
        int safeDays = normalizeDays(days);

        Map<Integer, Long> byStatus = reportMapper.countByStatus().stream()
                .filter(s -> s.status() != null)
                .collect(Collectors.toMap(StatusCount::status, StatusCount::cnt));

        long pending = byStatus.getOrDefault(Report.STATUS_PENDING, 0L);
        long accepted = byStatus.getOrDefault(Report.STATUS_ACCEPTED, 0L);
        long rejected = byStatus.getOrDefault(Report.STATUS_REJECTED, 0L);
        long done = byStatus.getOrDefault(Report.STATUS_DONE, 0L);

        return new ReportStatResponse(
                pending + accepted + rejected + done,
                pending, accepted, rejected, done,
                fillTrend(safeDays, toMap(reportMapper.countDaily(safeDays - 1))));
    }

    /** 只认 7 和 30，其他值统一按 7 天处理，避免被传个 365 拖垮数据库 */
    private int normalizeDays(int days) {
        return days == 30 ? 30 : 7;
    }

    private String normalizeDim(String dim) {
        if (dim == null) {
            return "post";
        }
        return switch (dim) {
            case "comment", "referral", "help" -> dim;
            default -> "post";
        };
    }

    /** 发帖维度落回 t_post.type：面经 1、内推 2、互助 3 */
    private Integer dimToPostType(String dim) {
        return switch (dim) {
            case "referral" -> 2;
            case "help" -> 3;
            default -> 1;
        };
    }

    private Map<String, Long> toMap(List<DailyCount> rows) {
        return rows.stream()
                .filter(r -> r.date() != null)
                .collect(Collectors.toMap(DailyCount::date, DailyCount::cnt, Long::sum));
    }

    /** SQL 只返回有数据的日子，折线要连续，缺的补 0 */
    private List<DailyCount> fillTrend(int days, Map<String, Long> byDate) {
        List<DailyCount> points = new ArrayList<>(days);
        for (int i = days - 1; i >= 0; i--) {
            String date = LocalDate.now().minusDays(i).toString();
            points.add(new DailyCount(date, byDate.getOrDefault(date, 0L)));
        }
        return points;
    }
}
