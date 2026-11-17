package com.lxz.careercode.admin.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.entity.admin.AdminOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 后台操作日志（careercode_admin 库）。
 */
@Mapper
public interface AdminOperationLogMapper extends BaseMapper<AdminOperationLog> {

    @Select("<script>"
            + "SELECT * FROM t_admin_operation_log "
            + "<where>"
            + "<if test=\"module != null and module != ''\">AND module = #{module} </if>"
            + "<if test='adminUserId != null'>AND admin_user_id = #{adminUserId} </if>"
            + "</where>"
            + "ORDER BY id DESC LIMIT #{offset}, #{size}"
            + "</script>")
    List<AdminOperationLog> selectPage(@Param("module") String module,
                                       @Param("adminUserId") Long adminUserId,
                                       @Param("offset") int offset,
                                       @Param("size") int size);

    @Select("<script>"
            + "SELECT COUNT(*) FROM t_admin_operation_log "
            + "<where>"
            + "<if test=\"module != null and module != ''\">AND module = #{module} </if>"
            + "<if test='adminUserId != null'>AND admin_user_id = #{adminUserId} </if>"
            + "</where>"
            + "</script>")
    long countPage(@Param("module") String module, @Param("adminUserId") Long adminUserId);
}
