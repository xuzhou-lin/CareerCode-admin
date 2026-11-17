package com.lxz.careercode.admin.entity.biz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子/内容主表。
 * <p>
 * 后台只关心审核相关的列，其余（封面、公司、热度分等）不映射。
 */
@Data
@TableName("careercode_post.t_post")
public class Post {

    /** 草稿 */
    public static final int STATUS_DRAFT = 0;

    /** 审核中 */
    public static final int STATUS_REVIEWING = 1;

    /** 已发布 */
    public static final int STATUS_PUBLISHED = 2;

    /** 审核驳回 */
    public static final int STATUS_REJECTED = 3;

    /** 已下架 */
    public static final int STATUS_OFFLINE = 4;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 1 面经 2 内推 3 技术互助 */
    private Integer type;

    private String title;

    private String summary;

    private String content;

    /** 1 Markdown 2 HTML 3 富文本 JSON */
    private Integer contentFormat;

    private Integer status;

    /** 审核意见，驳回时必填 */
    private String auditRemark;

    private Integer isAnonymous;

    private String anonymousName;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /** 联查出来的作者昵称，不是 t_post 的列 */
    @TableField(exist = false)
    private String authorNickname;
}
