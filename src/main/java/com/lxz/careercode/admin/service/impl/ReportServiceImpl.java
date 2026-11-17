package com.lxz.careercode.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxz.careercode.admin.common.AdminContext;
import com.lxz.careercode.admin.common.BizException;
import com.lxz.careercode.admin.common.ClientIpResolver;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.entity.admin.AdminOperationLog;
import com.lxz.careercode.admin.entity.biz.Report;
import com.lxz.careercode.admin.mapper.admin.AdminOperationLogMapper;
import com.lxz.careercode.admin.mapper.biz.ReportMapper;
import com.lxz.careercode.admin.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    /** 允许的处理动作：1 已受理、2 已驳回、3 已处理 */
    private static final Set<Integer> ALLOWED_ACTIONS = Set.of(
            Report.STATUS_ACCEPTED, Report.STATUS_REJECTED, Report.STATUS_DONE);

    private final ReportMapper reportMapper;
    private final AdminOperationLogMapper operationLogMapper;

    @Override
    public PageResult<Report> list(Integer status, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;

        List<Report> records = reportMapper.selectPage(status, offset, safeSize);
        // 总数要和列表用同一套过滤条件，否则分页会出现"第 2 页是空的"
        LambdaQueryWrapper<Report> counter = new LambdaQueryWrapper<>();
        if (status != null) {
            counter.eq(Report::getStatus, status);
        }
        long total = reportMapper.selectCount(counter);
        return new PageResult<>(total, records);
    }

    @Override
    public void handle(Long id, Integer action, String note) {
        if (action == null || !ALLOWED_ACTIONS.contains(action)) {
            throw new BizException("处理动作不合法");
        }

        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new BizException("举报记录不存在");
        }
        if (report.getStatus() != null && report.getStatus() != Report.STATUS_PENDING) {
            throw new BizException("这条举报已经处理过了");
        }

        Report update = new Report();
        update.setId(id);
        update.setStatus(action);
        update.setHandlerId(AdminContext.requiredId());
        update.setHandleResult(StringUtils.hasText(note) ? note.trim() : null);
        update.setHandledAt(LocalDateTime.now());
        reportMapper.updateById(update);

        // 举报单在业务库、日志在管理库，跨库没有事务，改完再记日志，失败只告警
        writeLog(report, action, note);

        log.info("举报处理 reportId={} action={} 操作人={}", id, action, AdminContext.getId());
    }

    private void writeLog(Report report, Integer action, String note) {
        Long adminId = AdminContext.getId();
        if (adminId == null) {
            return;
        }
        try {
            AdminOperationLog entry = new AdminOperationLog();
            entry.setAdminUserId(adminId);
            entry.setModule("report");
            entry.setAction(actionName(action));
            entry.setTargetId(report.getId());
            entry.setTargetTitle("举报 #" + report.getId());
            entry.setDetail(StringUtils.hasText(note) ? note.trim() : null);
            entry.setIp(ClientIpResolver.currentIp());
            operationLogMapper.insert(entry);
        } catch (Exception e) {
            log.warn("写操作日志失败 reportId={}", report.getId(), e);
        }
    }

    private String actionName(Integer action) {
        if (action == Report.STATUS_ACCEPTED) {
            return "report_accept";
        }
        if (action == Report.STATUS_REJECTED) {
            return "report_reject";
        }
        return "report_done";
    }
}
