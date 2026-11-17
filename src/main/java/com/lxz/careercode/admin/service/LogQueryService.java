package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.OperationLogResponse;
import com.lxz.careercode.admin.dto.PageResult;

/**
 * 操作日志查询。
 */
public interface LogQueryService {

    /**
     * 操作流水，新的在前。
     *
     * @param module      模块过滤，null 或空表示全部
     * @param adminUserId 操作人过滤
     */
    PageResult<OperationLogResponse> list(String module, Long adminUserId, int page, int size);
}
