package com.lxz.careercode.admin.dto;

import java.util.List;

/**
 * 分页结果。字段名和 C 端的 PageResult 保持一致（total + records），
 * 前端不用为后台单独写解析。
 */
public record PageResult<T>(long total, List<T> records) {
}
