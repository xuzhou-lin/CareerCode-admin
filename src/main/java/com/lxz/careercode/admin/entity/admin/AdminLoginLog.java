package com.lxz.careercode.admin.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员登录日志。账号不存在也记一条（adminUserId 为空），用来识别撞库。
 */
@Data
@TableName("t_admin_login_log")
public class AdminLoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账号不存在时为空 */
    private Long adminUserId;

    private String username;

    private String ip;

    private String userAgent;

    /** 0 失败 1 成功 */
    private Integer success;

    private String failReason;

    private LocalDateTime createdAt;
}
