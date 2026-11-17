package com.lxz.careercode.admin.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容审核记录。一篇帖子每审一次留一行，
 * 用来补 t_post.audit_remark 只能存最后一次的短板。
 */
@Data
@TableName("t_admin_audit_record")
public class AdminAuditRecord {

    /** 审核结果：通过 */
    public static final int ACTION_PASS = 1;

    /** 审核结果：驳回 */
    public static final int ACTION_REJECT = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 逻辑关联 careercode_post.t_post，不是物理外键 */
    private Long postId;

    /** 审核时的标题快照，帖子删了也能查 */
    private String postTitle;

    private Long adminUserId;

    /** 1 通过 2 驳回 */
    private Integer action;

    private String reason;

    private LocalDateTime createdAt;
}
