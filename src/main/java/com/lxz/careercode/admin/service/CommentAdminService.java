package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.CommentItemResponse;
import com.lxz.careercode.admin.dto.PageResult;

/**
 * 评论管理。评论发出来就是正常状态，这里只做事后隐藏。
 */
public interface CommentAdminService {

    /**
     * 评论列表。
     *
     * @param status 1 正常 2 已隐藏，null 表示全部
     */
    PageResult<CommentItemResponse> list(Integer status, String keyword, int page, int size);

    /**
     * 隐藏或恢复。
     *
     * @param status 1 恢复显示，2 隐藏
     */
    void updateStatus(Long commentId, Integer status);
}
