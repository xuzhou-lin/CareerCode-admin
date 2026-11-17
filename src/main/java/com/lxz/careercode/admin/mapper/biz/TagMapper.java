package com.lxz.careercode.admin.mapper.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.entity.biz.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签（careercode_post 库）。新增和改名用 BaseMapper，这里只补查询和引用统计。
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /** 标签列表，按类型分组、引用次数倒序 */
    @Select("<script>"
            + "SELECT id, name, type, ref_id, use_count, status, created_at "
            + "FROM careercode_post.t_tag "
            + "<where>"
            + "<if test='type != null'>AND type = #{type} </if>"
            + "<if test=\"keyword != null and keyword != ''\">AND name LIKE CONCAT('%', #{keyword}, '%') </if>"
            + "</where>"
            + "ORDER BY type ASC, use_count DESC LIMIT #{offset}, #{size}"
            + "</script>")
    List<Tag> selectPage(@Param("type") Integer type,
                         @Param("keyword") String keyword,
                         @Param("offset") int offset,
                         @Param("size") int size);

    /** 列表总数，条件和列表一致 */
    @Select("<script>"
            + "SELECT COUNT(*) FROM careercode_post.t_tag "
            + "<where>"
            + "<if test='type != null'>AND type = #{type} </if>"
            + "<if test=\"keyword != null and keyword != ''\">AND name LIKE CONCAT('%', #{keyword}, '%') </if>"
            + "</where>"
            + "</script>")
    long countPage(@Param("type") Integer type, @Param("keyword") String keyword);

    /** 有多少帖子在用这个标签，被引用的标签不允许直接删 */
    @Select("SELECT COUNT(*) FROM careercode_post.t_post_tag WHERE tag_id = #{tagId}")
    long countPostRefs(@Param("tagId") Long tagId);

    /** 同类型下重名校验，改名时把自己排除掉 */
    @Select("<script>"
            + "SELECT COUNT(*) FROM careercode_post.t_tag WHERE type = #{type} AND name = #{name} "
            + "<if test='excludeId != null'>AND id &lt;&gt; #{excludeId} </if>"
            + "</script>")
    long countSameName(@Param("type") Integer type,
                       @Param("name") String name,
                       @Param("excludeId") Long excludeId);
}
