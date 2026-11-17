package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.AuditDetail;
import com.lxz.careercode.admin.dto.AuditItem;
import com.lxz.careercode.admin.dto.PageResult;

/**
 * 内容审核：待审列表、详情、通过/驳回。
 * 通过后不发通知（通知组装在 social 服务里，避免两套文案）。
 */
public interface AuditService {

    /**
     * 审核列表，不含正文。
     *
     * @param status 状态过滤，null 表示全部；审核页通常传 1
     */
    /**
     * 审核列表。
     *
     * @param status 帖子状态，null 表示全部
     * @param type   1 面经 2 内推 3 互助，null 表示全部
     */
    PageResult<AuditItem> list(Integer status, Integer type, String keyword, int page, int size);

    /** 审核详情，带正文和作者 */
    AuditDetail detail(Long id);

    /**
     * 审核。
     *
     * @param pass true 通过（存 2），false 驳回（存 3），驳回必须带 reason
     */
    void audit(Long id, boolean pass, String reason);
}
