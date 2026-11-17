package com.lxz.careercode.admin.entity.biz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签。后台只做维护，不参与发帖时的绑定逻辑。
 */
@Data
@TableName("careercode_post.t_tag")
public class Tag {

    /** 1 公司 2 岗位 3 技术栈 4 学校 5 年份 6 求职阶段 7 自定义 */
    public static final int TYPE_COMPANY = 1;
    public static final int TYPE_POSITION = 2;
    public static final int TYPE_TECH = 3;
    public static final int TYPE_SCHOOL = 4;
    public static final int TYPE_YEAR = 5;
    public static final int TYPE_STAGE = 6;
    public static final int TYPE_CUSTOM = 7;

    /** 状态：正常 */
    public static final int STATUS_NORMAL = 0;

    /** 状态：禁用，发帖时不再可选 */
    public static final int STATUS_DISABLED = 1;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer type;

    /** 关联主数据 ID，公司/岗位这类标签会指向主表 */
    private Long refId;

    /** 被帖子引用次数，删除前用它判断能不能删 */
    private Integer useCount;

    private Integer status;

    private LocalDateTime createdAt;
}
