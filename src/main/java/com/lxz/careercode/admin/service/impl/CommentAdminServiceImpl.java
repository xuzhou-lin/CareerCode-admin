package com.lxz.careercode.admin.service.impl;

import com.lxz.careercode.admin.common.AdminContext;
import com.lxz.careercode.admin.common.BizException;
import com.lxz.careercode.admin.common.ClientIpResolver;
import com.lxz.careercode.admin.dto.CommentItemResponse;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.entity.admin.AdminOperationLog;
import com.lxz.careercode.admin.entity.biz.Comment;
import com.lxz.careercode.admin.mapper.admin.AdminOperationLogMapper;
import com.lxz.careercode.admin.mapper.biz.CommentMapper;
import com.lxz.careercode.admin.service.CommentAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentAdminServiceImpl implements CommentAdminService {

    private final CommentMapper commentMapper;
    private final AdminOperationLogMapper operationLogMapper;

    @Override
    public PageResult<CommentItemResponse> list(Integer status, String keyword, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        String trimmed = StringUtils.hasText(keyword) ? keyword.trim() : null;

        long total = commentMapper.countForAdmin(status, trimmed);
        List<CommentItemResponse> items = commentMapper.selectPageForAdmin(status, trimmed, offset, safeSize)
                .stream()
                .map(this::toItem)
                .toList();
        return new PageResult<>(total, items);
    }

    @Override
    public void updateStatus(Long commentId, Integer status) {
        if (status == null || (status != Comment.STATUS_NORMAL && status != Comment.STATUS_HIDDEN)) {
            throw new BizException("状态只能是 1（正常）或 2（隐藏）");
        }

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeletedAt() != null) {
            throw new BizException("评论不存在或已删除");
        }
        if (comment.getStatus() != null && comment.getStatus().equals(status)) {
            throw new BizException(status == Comment.STATUS_HIDDEN ? "这条评论已经是隐藏状态" : "这条评论当前正常显示");
        }

        int updated = commentMapper.updateStatus(commentId, status);
        if (updated == 0) {
            throw new BizException("更新失败，评论可能已被删除");
        }

        writeLog(comment, status);
        log.info("评论状态变更 commentId={} status={} 操作人={}", commentId, status, AdminContext.getId());
    }

    private void writeLog(Comment comment, int status) {
        Long adminId = AdminContext.getId();
        if (adminId == null) {
            return;
        }
        try {
            AdminOperationLog entry = new AdminOperationLog();
            entry.setAdminUserId(adminId);
            entry.setModule("comment");
            entry.setAction(status == Comment.STATUS_HIDDEN ? "comment_hide" : "comment_restore");
            entry.setTargetId(comment.getId());
            entry.setTargetTitle(cut(comment.getContent(), 60));
            entry.setIp(ClientIpResolver.currentIp());
            operationLogMapper.insert(entry);
        } catch (Exception e) {
            log.warn("写操作日志失败 commentId={}", comment.getId(), e);
        }
    }

    private String cut(String text, int max) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return text.length() <= max ? text : text.substring(0, max) + "…";
    }

    private CommentItemResponse toItem(Comment c) {
        return new CommentItemResponse(
                c.getId(),
                c.getPostId(),
                c.getPostTitle(),
                c.getContent(),
                c.getAuthorNickname(),
                c.getStatus(),
                c.getCreatedAt());
    }
}
