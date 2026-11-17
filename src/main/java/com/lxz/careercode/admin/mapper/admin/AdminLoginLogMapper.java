package com.lxz.careercode.admin.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.entity.admin.AdminLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员登录日志（careercode_admin 库）。
 */
@Mapper
public interface AdminLoginLogMapper extends BaseMapper<AdminLoginLog> {
}
