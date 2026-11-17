package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 举报处理请求。
 *
 * @param action 1 已受理、2 已驳回、3 已处理
 * @param note   处理说明
 */
public record ReportHandleRequest(
        @NotNull(message = "必须指定处理动作") Integer action,
        @Size(max = 500, message = "处理说明最多 500 个字符") String note) {
}
