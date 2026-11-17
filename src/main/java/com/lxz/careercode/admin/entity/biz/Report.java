package com.lxz.careercode.admin.entity.biz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报记录。
 */
@Data
@TableName("careercode_social.t_report")
public class Report {

    /** 待处理 */
    public static final int STATUS_PENDING = 0;

    /** 已受理 */
    public static final int STATUS_ACCEPTED = 1;

    /** 已驳回 */
    public static final int STATUS_REJECTED = 2;

    /** 已处理 */
    public static final int STATUS_DONE = 3;

    /** 1 帖子 2 评论 3 用户 4 私信 */
    public static final int TARGET_POST = 1;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reporterId;

    private Integer targetType;

    private Long targetId;

    /** 1 广告 2 辱骂 3 违法 4 虚假 5 侵权 6 其他 */
    private Integer reasonType;

    private String reason;

    private String evidenceUrls;

    private Integer status;

    private Long handlerId;

    private String handleResult;

    private LocalDateTime handledAt;

    private LocalDateTime createdAt;

    /** 联查出来的举报人昵称，不是表字段 */
    @TableField(exist = false)
    private String reporterNickname;
}
