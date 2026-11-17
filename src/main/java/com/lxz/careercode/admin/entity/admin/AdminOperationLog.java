package com.lxz.careercode.admin.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台操作流水。审核、下架、删除、举报处理、封禁、标签、敏感词都往这里记一条。
 */
@Data
@TableName("t_admin_operation_log")
public class AdminOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adminUserId;

    /** post / report / user / tag / word */
    private String module;

    /** audit_pass / audit_reject / offline / delete / report_handle / user_ban ... */
    private String action;

    private Long targetId;

    /** 对象标题快照，对象被删后仍可追溯 */
    private String targetTitle;

    private String detail;

    private String ip;

    private LocalDateTime createdAt;
}
