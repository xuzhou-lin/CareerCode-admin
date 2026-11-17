package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.TagItemResponse;
import com.lxz.careercode.admin.dto.TagSaveRequest;

/**
 * 标签管理：查询、新增、改名、启停、删除。
 */
public interface TagAdminService {

    /**
     * 标签列表。
     *
     * @param type    标签类型，null 表示全部
     * @param keyword 标签名模糊匹配
     */
    PageResult<TagItemResponse> list(Integer type, String keyword, int page, int size);

    /** 新增或改名，带 id 就是改 */
    void save(TagSaveRequest request);

    /** 启用/禁用。禁用的标签发帖时不再可选，但不影响已绑定的帖子 */
    void updateStatus(Long tagId, Integer status);

    /** 删除。被帖子引用过的标签不允许删，只能禁用 */
    void delete(Long tagId);
}
