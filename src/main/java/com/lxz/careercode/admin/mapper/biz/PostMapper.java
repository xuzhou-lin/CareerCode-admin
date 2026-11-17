package com.lxz.careercode.admin.mapper.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.dto.DailyCount;
import com.lxz.careercode.admin.entity.biz.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 帖子表数据访问。
 * <p>
 * 后台只用三个动作：分页看待审、看单篇详情、改审核状态。
 * 所有查询都带 `deleted_at IS NULL`，避免把软删的帖子翻出来。
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 审核列表。故意不查 content：正文是 LONGTEXT，列表一次拖 20 条会很慢。
     *
     * @param status  状态过滤，null 表示全部
     * @param keyword 标题/摘要模糊匹配，null 或空表示不过滤
     * @param offset  偏移量
     * @param size    每页条数
     */
    @Select("<script>"
            + "SELECT p.id, p.user_id, p.type, p.title, p.summary, p.status, "
            + "p.is_anonymous, p.anonymous_name, p.view_count, p.like_count, p.comment_count, "
            + "p.created_at, u.nickname AS author_nickname "
            + "FROM careercode_post.t_post p LEFT JOIN careercode.t_user u ON u.id = p.user_id "
            + "WHERE p.deleted_at IS NULL "
            + "<if test='status != null'>AND p.status = #{status} </if>"
            + "<if test='type != null'>AND p.type = #{type} </if>"
            + "<if test=\"keyword != null and keyword != ''\">"
            + "AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.summary LIKE CONCAT('%', #{keyword}, '%')) "
            + "</if>"
            + "ORDER BY p.created_at DESC LIMIT #{offset}, #{size}"
            + "</script>")
    List<Post> selectPageForAudit(@Param("status") Integer status,
                                  @Param("type") Integer type,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    /** 审核列表总数 */
    @Select("<script>"
            + "SELECT COUNT(*) FROM careercode_post.t_post p WHERE p.deleted_at IS NULL "
            + "<if test='status != null'>AND p.status = #{status} </if>"
            + "<if test='type != null'>AND p.type = #{type} </if>"
            + "<if test=\"keyword != null and keyword != ''\">"
            + "AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.summary LIKE CONCAT('%', #{keyword}, '%')) "
            + "</if>"
            + "</script>")
    long countForAudit(@Param("status") Integer status,
                       @Param("type") Integer type,
                       @Param("keyword") String keyword);

    /**
     * 审核动作。
     * <p>
     * 带 `status = 1` 条件是为了防重复审核：两个人同时点通过时，
     * 只有第一个会更新成功，第二个影响 0 行，Service 据此报“已被处理”。
     *
     * @param id     帖子 id
     * @param status 目标状态（2 通过 / 3 驳回）
     * @param remark 审核意见
     * @return 影响行数，0 表示状态已被别人改过
     */
    @Update("UPDATE careercode_post.t_post SET status = #{status}, audit_remark = #{remark}, updated_at = NOW() "
            + "WHERE id = #{id} AND status = 1 AND deleted_at IS NULL")
    int audit(@Param("id") Long id, @Param("status") int status, @Param("remark") String remark);

    /** 今日新增帖子数，看板用 */
    @Select("SELECT COUNT(*) FROM careercode_post.t_post WHERE deleted_at IS NULL "
            + "AND created_at >= CURDATE()")
    long countToday();

    /**
     * 近 N 天每天的发帖量。只返回有数据的日子，缺的由 Service 补 0。
     *
     * @param type 1 面经 2 内推 3 互助，null 表示全部
     * @param days 往前推几天，含今天
     */
    @Select("<script>"
            + "SELECT DATE_FORMAT(created_at, '%Y-%m-%d') AS date, COUNT(*) AS cnt "
            + "FROM careercode_post.t_post "
            + "WHERE deleted_at IS NULL AND created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) "
            + "<if test='type != null'>AND type = #{type} </if>"
            + "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d')"
            + "</script>")
    List<DailyCount> countDaily(@Param("type") Integer type, @Param("days") int days);
}
