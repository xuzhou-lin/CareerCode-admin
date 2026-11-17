package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.WordBatchRequest;
import com.lxz.careercode.admin.dto.WordItemResponse;
import com.lxz.careercode.admin.dto.WordSaveRequest;

/**
 * 敏感词维护。只做词库管理和审核时比对，不做实时拦截。
 */
public interface WordAdminService {

    /**
     * 词列表。
     *
     * @param category 分类，null 表示全部
     * @param status   0 启用 1 禁用，null 表示全部
     */
    PageResult<WordItemResponse> list(Integer category, Integer status, String keyword, int page, int size);

    /** 新增或修改，不传 id 就是新增 */
    void save(WordSaveRequest request);

    /**
     * 批量导入，换行或逗号分隔，已存在的跳过。
     *
     * @return 实际新增的条数
     */
    int batchImport(WordBatchRequest request);

    /** 启用/禁用 */
    void updateStatus(Long wordId, Integer status);

    void delete(Long wordId);
}
