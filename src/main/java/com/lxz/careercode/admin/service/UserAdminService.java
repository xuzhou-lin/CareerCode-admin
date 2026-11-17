package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.UserItemResponse;
import com.lxz.careercode.admin.dto.UserStatusRequest;

/**
 * 用户管理：查询与封禁/解封。
 */
public interface UserAdminService {

    /**
     * 用户列表。
     *
     * @param keyword 昵称或手机号模糊匹配，空则不过滤
     */
    PageResult<UserItemResponse> list(String keyword, int page, int size);

    /**
     * 封禁或解封。
     *
     * @param status 0 解封，1 封禁
     */
    void updateStatus(Long userId, UserStatusRequest request);
}
