package com.lxz.careercode.admin.mapper.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.dto.DailyCount;
import com.lxz.careercode.admin.entity.biz.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 评论（careercode_social 库）。后台只做查询和隐藏，不删数据。
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /** 评论列表，带作者昵称和所属帖子标题 */
    @Select("<script>"
            + "SELECT c.id, c.post_id, c.user_id, c.content, c.status, c.created_at, "
            + "u.nickname AS author_nickname, p.title AS post_title "
            + "FROM careercode_social.t_comment c "
            + "LEFT JOIN careercode.t_user u ON u.id = c.user_id "
            + "LEFT JOIN careercode_post.t_post p ON p.id = c.post_id "
            + "WHERE c.deleted_at IS NULL "
            + "<if test='status != null'>AND c.status = #{status} </if>"
            + "<if test=\"keyword != null and keyword != ''\">"
            + "AND c.content LIKE CONCAT('%', #{keyword}, '%') "
            + "</if>"
            + "ORDER BY c.id DESC LIMIT #{offset}, #{size}"
            + "</script>")
    List<Comment> selectPageForAdmin(@Param("status") Integer status,
                                     @Param("keyword") String keyword,
                                     @Param("offset") int offset,
                                     @Param("size") int size);

    @Select("<script>"
            + "SELECT COUNT(*) FROM careercode_social.t_comment c WHERE c.deleted_at IS NULL "
            + "<if test='status != null'>AND c.status = #{status} </if>"
            + "<if test=\"keyword != null and keyword != ''\">"
            + "AND c.content LIKE CONCAT('%', #{keyword}, '%') "
            + "</if>"
            + "</script>")
    long countForAdmin(@Param("status") Integer status, @Param("keyword") String keyword);

    /** 隐藏或恢复，1 正常 2 已隐藏 */
    @Update("UPDATE careercode_social.t_comment SET status = #{status} "
            + "WHERE id = #{id} AND deleted_at IS NULL")
    int updateStatus(@Param("id") Long id, @Param("status") int status);

    /** 近 N 天每天的评论量，趋势图用 */
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') AS date, COUNT(*) AS cnt "
            + "FROM careercode_social.t_comment "
            + "WHERE deleted_at IS NULL AND created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) "
            + "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d')")
    List<DailyCount> countDaily(@Param("days") int days);
}
