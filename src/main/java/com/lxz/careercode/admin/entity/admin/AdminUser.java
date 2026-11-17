package com.lxz.careercode.admin.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台管理员。与前台 t_user 完全独立，不共用账号和登录态。
 */
@Data
@TableName("t_admin_user")
public class AdminUser {

    /** 角色：超管，可管理其他管理员 */
    public static final int ROLE_SUPER = 1;

    /** 角色：审核员 */
    public static final int ROLE_AUDITOR = 2;

    /** 角色：运营 */
    public static final int ROLE_OPERATOR = 3;

    /** 状态：正常 */
    public static final int STATUS_NORMAL = 0;

    /** 状态：禁用 */
    public static final int STATUS_DISABLED = 1;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String passwordHash;

    private String nickname;

    private String phone;

    private String email;

    /** 1 超管 2 审核员 3 运营 */
    private Integer role;

    /** 0 正常 1 禁用 */
    private Integer status;

    private LocalDateTime lastLoginAt;

    private String lastLoginIp;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
