package com.lxz.careercode.admin.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 敏感词。只用于审核辅助（比对标题正文、命中打标），不做实时拦截。
 */
@Data
@TableName("t_sensitive_word")
public class SensitiveWord {

    /** 分类 */
    public static final int CATEGORY_POLITICS = 1;
    public static final int CATEGORY_PORN = 2;
    public static final int CATEGORY_AD = 3;
    public static final int CATEGORY_ABUSE = 4;
    public static final int CATEGORY_OTHER = 5;

    /** 命中后怎么处理 */
    public static final int LEVEL_TIP = 1;
    public static final int LEVEL_BLOCK = 2;
    public static final int LEVEL_MANUAL = 3;

    /** 状态：启用 */
    public static final int STATUS_ENABLED = 0;

    /** 状态：禁用 */
    public static final int STATUS_DISABLED = 1;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String word;

    private Integer category;

    /** 1 提示 2 拦截 3 转人工 */
    private Integer level;

    private Integer status;

    private Integer hitCount;

    private Long creatorId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
