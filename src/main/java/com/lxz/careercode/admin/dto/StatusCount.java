package com.lxz.careercode.admin.dto;

/**
 * 按状态分组的一个计数点。
 *
 * @param status 状态码，含义由各业务自己定义
 * @param cnt    该状态的数量
 */
public record StatusCount(Integer status, long cnt) {
}
