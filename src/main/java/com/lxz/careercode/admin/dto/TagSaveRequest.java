package com.lxz.careercode.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 新增或修改标签。不传 id 就是新增。
 *
 * @param type 1 公司 2 岗位 3 技术栈 4 学校 5 年份 6 求职阶段 7 自定义
 */
public record TagSaveRequest(
        Long id,

        @NotBlank(message = "标签名不能为空")
        @Size(max = 50, message = "标签名最多 50 个字符")
        String name,

        @NotNull(message = "必须指定标签类型")
        Integer type,

        Long refId,

        /** 0 正常 1 禁用，不传默认正常 */
        Integer status) {
}
