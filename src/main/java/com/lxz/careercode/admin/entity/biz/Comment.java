package com.lxz.careercode.admin.entity.biz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论。评论没有「待审核」环节，发出来就是正常状态，后台只做事后管理。
 */
@Data
@TableName("careercode_social.t_comment")
public class Comment {

    /** 正常 */
    public static final int STATUS_NORMAL = 1;

    /** 已隐藏 */
    public static final int STATUS_HIDDEN = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private Long userId;

    private Long parentId;

    private String content;

    private Integer likeCount;

    private Integer replyCount;

    private Integer isAnonymous;

    /** 1 正常 2 已隐藏 */
    private Integer status;

    private String ip;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    /** 关联查询带出来的作者昵称，不属于 t_comment 的列 */
    @TableField(exist = false)
    private String authorNickname;

    /** 关联查询带出来的帖子标题 */
    @TableField(exist = false)
    private String postTitle;
}
