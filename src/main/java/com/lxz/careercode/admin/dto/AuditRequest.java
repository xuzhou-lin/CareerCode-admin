package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 审核请求。
 *
 * @param pass   true 通过（status=2），false 驳回（status=3）
 * @param reason 审核意见，驳回时必填
 */
public record AuditRequest(
        @NotNull(message = "必须指定通过或驳回") Boolean pass,
        @Size(max = 255, message = "审核意见最多 255 个字符") String reason) {
}
