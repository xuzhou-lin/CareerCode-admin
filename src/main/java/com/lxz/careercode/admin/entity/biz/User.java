package com.lxz.careercode.admin.entity.biz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户账号。
 * <p>
 * 只映射后台需要的列：判断管理员（role）、判断账号状态（status）、展示作者（nickname）。
 * 不映射 password_hash 等敏感字段，少一个字段就少一处泄露风险。
 */
@Data
@TableName("t_user")
public class User {

    /** 状态：正常 */
    public static final int STATUS_NORMAL = 0;

    /** 状态：封禁 */
    public static final int STATUS_BANNED = 1;

    /** 状态：已注销 */
    public static final int STATUS_CANCELLED = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    private String email;

    private String nickname;

    /** 0 正常 1 封禁 2 已注销 */
    private Integer status;

    private LocalDateTime createdAt;
}
