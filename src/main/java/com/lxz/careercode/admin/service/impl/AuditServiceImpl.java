package com.lxz.careercode.admin.service.impl;

import com.lxz.careercode.admin.common.AdminContext;
import com.lxz.careercode.admin.common.BizException;
import com.lxz.careercode.admin.common.ClientIpResolver;
import com.lxz.careercode.admin.dto.AuditDetail;
import com.lxz.careercode.admin.dto.AuditItem;
import com.lxz.careercode.admin.dto.AuditRecordItem;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.entity.admin.AdminAuditRecord;
import com.lxz.careercode.admin.entity.admin.AdminOperationLog;
import com.lxz.careercode.admin.entity.admin.AdminUser;
import com.lxz.careercode.admin.entity.biz.Post;
import com.lxz.careercode.admin.entity.biz.User;
import com.lxz.careercode.admin.mapper.admin.AdminAuditRecordMapper;
import com.lxz.careercode.admin.mapper.admin.AdminOperationLogMapper;
import com.lxz.careercode.admin.mapper.admin.AdminUserMapper;
import com.lxz.careercode.admin.mapper.biz.PostMapper;
import com.lxz.careercode.admin.mapper.biz.UserMapper;
import com.lxz.careercode.admin.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final AdminAuditRecordMapper auditRecordMapper;
    private final AdminOperationLogMapper operationLogMapper;
    private final AdminUserMapper adminUserMapper;

    @Override
    public PageResult<AuditItem> list(Integer status, Integer type, String keyword, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        String trimmed = StringUtils.hasText(keyword) ? keyword.trim() : null;

        long total = postMapper.countForAudit(status, type, trimmed);
        List<AuditItem> items = postMapper.selectPageForAudit(status, type, trimmed, offset, safeSize)
                .stream()
                .map(this::toItem)
                .toList();

        return new PageResult<>(total, items);
    }

    @Override
    public AuditDetail detail(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getDeletedAt() != null) {
            throw new BizException("内容不存在或已删除");
        }
        User author = userMapper.selectById(post.getUserId());

        return new AuditDetail(
                post.getId(),
                post.getType(),
                post.getTitle(),
                post.getSummary(),
                post.getContent(),
                post.getContentFormat(),
                post.getStatus(),
                post.getAuditRemark(),
                post.getUserId(),
                author == null ? null : author.getNickname(),
                post.getIsAnonymous() != null && post.getIsAnonymous() == 1,
                post.getAnonymousName(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                loadHistory(id));
    }

    @Override
    public void audit(Long id, boolean pass, String reason) {
        // 1. 基础校验
        Post post = postMapper.selectById(id);
        if (post == null || post.getDeletedAt() != null) {
            throw new BizException("内容不存在或已删除");
        }
        if (post.getStatus() == null || post.getStatus() != Post.STATUS_REVIEWING) {
            throw new BizException("这条内容当前不在审核中，不能重复审核");
        }
        if (!pass && !StringUtils.hasText(reason)) {
            throw new BizException("驳回必须填写原因");
        }

        // 2. 先改业务状态。两个库不在同一个事务里，状态是主数据，必须先落地
        int target = pass ? Post.STATUS_PUBLISHED : Post.STATUS_REJECTED;
        String remark = StringUtils.hasText(reason) ? reason.trim() : null;
        // SQL 带 status = 1 条件，返回 0 说明期间已被其他管理员处理
        int updated = postMapper.audit(id, target, remark);
        if (updated == 0) {
            throw new BizException("这条内容已被其他管理员处理");
        }

        // 3. 再写审核记录和操作日志。写失败只记警告，不能因为日志问题回滚审核结果
        recordAudit(post, pass, remark);

        log.info("审核完成 postId={} 结果={} 审核人={}", id, pass ? "通过" : "驳回", AdminContext.getId());
    }

    /** 历次审核记录，新的在前；管理员名字一次批量查出来 */
    private List<AuditRecordItem> loadHistory(Long postId) {
        List<AdminAuditRecord> records = auditRecordMapper.selectByPostId(postId);
        if (records.isEmpty()) {
            return List.of();
        }

        Set<Long> adminIds = records.stream()
                .map(AdminAuditRecord::getAdminUserId)
                .collect(Collectors.toSet());
        Map<Long, String> names = adminUserMapper.selectBatchIds(adminIds).stream()
                .collect(Collectors.toMap(AdminUser::getId,
                        a -> StringUtils.hasText(a.getNickname()) ? a.getNickname() : a.getUsername()));

        return records.stream()
                .map(r -> new AuditRecordItem(
                        r.getAdminUserId(),
                        names.get(r.getAdminUserId()),
                        r.getAction(),
                        r.getReason(),
                        r.getCreatedAt()))
                .toList();
    }

    private void recordAudit(Post post, boolean pass, String reason) {
        Long adminId = AdminContext.getId();
        if (adminId == null) {
            return;
        }

        try {
            AdminAuditRecord record = new AdminAuditRecord();
            record.setPostId(post.getId());
            record.setPostTitle(post.getTitle());
            record.setAdminUserId(adminId);
            record.setAction(pass ? AdminAuditRecord.ACTION_PASS : AdminAuditRecord.ACTION_REJECT);
            record.setReason(reason);
            auditRecordMapper.insert(record);
        } catch (Exception e) {
            log.warn("写审核记录失败 postId={}", post.getId(), e);
        }

        try {
            AdminOperationLog entry = new AdminOperationLog();
            entry.setAdminUserId(adminId);
            entry.setModule("post");
            entry.setAction(pass ? "audit_pass" : "audit_reject");
            entry.setTargetId(post.getId());
            entry.setTargetTitle(post.getTitle());
            entry.setDetail(reason);
            entry.setIp(ClientIpResolver.currentIp());
            operationLogMapper.insert(entry);
        } catch (Exception e) {
            log.warn("写操作日志失败 postId={}", post.getId(), e);
        }
    }

    private AuditItem toItem(Post post) {
        return new AuditItem(
                post.getId(),
                post.getType(),
                post.getTitle(),
                post.getSummary(),
                post.getStatus(),
                post.getAuthorNickname(),
                post.getIsAnonymous() != null && post.getIsAnonymous() == 1,
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt());
    }
}
