package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.AdminCreateRequest;
import com.lxz.careercode.admin.dto.AdminItemResponse;
import com.lxz.careercode.admin.dto.AdminPasswordRequest;
import com.lxz.careercode.admin.dto.PageResult;

/**
 * 管理员账号管理。目前只有一种角色（超管），所以没有角色变更接口。
 */
public interface AdminAccountService {

    /** 管理员列表 */
    PageResult<AdminItemResponse> list(int page, int size);

    /** 新增管理员，登录名唯一 */
    void create(AdminCreateRequest request);

    /** 重置密码 */
    void resetPassword(Long adminId, AdminPasswordRequest request);

    /**
     * 启用或禁用。
     *
     * @param status 0 正常，1 禁用
     */
    void updateStatus(Long adminId, Integer status);
}
