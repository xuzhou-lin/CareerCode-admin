package com.lxz.careercode.admin.service;

import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.entity.biz.Report;

/**
 * 举报处理：列表与状态流转。
 * 只改举报单本身，不联动下架内容。
 */
public interface ReportService {

    /**
     * 举报列表，待处理的排在前面。
     *
     * @param status 状态过滤，null 表示全部
     */
    PageResult<Report> list(Integer status, int page, int size);

    /**
     * 处理举报。
     *
     * @param action 1 受理、2 驳回、3 处理完成
     */
    void handle(Long id, Integer action, String note);
}
