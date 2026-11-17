package com.lxz.careercode.admin.mapper.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.entity.biz.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户表（careercode 库）。后台只查询和改状态，不动资料。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** 按昵称或手机号搜，不带条件就是全部 */
    @Select("<script>"
            + "SELECT id, phone, email, nickname, status, created_at "
            + "FROM careercode.t_user WHERE deleted_at IS NULL "
            + "<if test=\"keyword != null and keyword != ''\">"
            + "AND (nickname LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%')) "
            + "</if>"
            + "ORDER BY id DESC LIMIT #{offset}, #{size}"
            + "</script>")
    List<User> selectPageForAdmin(@Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    /** 搜索结果的条数，和列表用同一套条件 */
    @Select("<script>"
            + "SELECT COUNT(*) FROM careercode.t_user WHERE deleted_at IS NULL "
            + "<if test=\"keyword != null and keyword != ''\">"
            + "AND (nickname LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%')) "
            + "</if>"
            + "</script>")
    long countForAdmin(@Param("keyword") String keyword);

    /** 封禁/解封：只改 status，0 正常 1 封禁 */
    @Update("UPDATE careercode.t_user SET status = #{status}, updated_at = NOW() "
            + "WHERE id = #{id} AND deleted_at IS NULL")
    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
