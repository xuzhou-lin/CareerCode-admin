package com.lxz.careercode.admin.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.entity.admin.AdminAuditRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 内容审核记录（careercode_admin 库）。
 */
@Mapper
public interface AdminAuditRecordMapper extends BaseMapper<AdminAuditRecord> {

    /** 某篇帖子的全部审核历史，新的在前 */
    @Select("SELECT * FROM t_admin_audit_record WHERE post_id = #{postId} ORDER BY created_at DESC")
    List<AdminAuditRecord> selectByPostId(@Param("postId") Long postId);
}
