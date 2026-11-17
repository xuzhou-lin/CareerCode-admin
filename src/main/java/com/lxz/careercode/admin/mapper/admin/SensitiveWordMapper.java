package com.lxz.careercode.admin.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.entity.admin.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 敏感词（careercode_admin 库）。
 */
@Mapper
public interface SensitiveWordMapper extends BaseMapper<SensitiveWord> {

    @Select("<script>"
            + "SELECT * FROM t_sensitive_word "
            + "<where>"
            + "<if test='category != null'>AND category = #{category} </if>"
            + "<if test='status != null'>AND status = #{status} </if>"
            + "<if test=\"keyword != null and keyword != ''\">AND word LIKE CONCAT('%', #{keyword}, '%') </if>"
            + "</where>"
            + "ORDER BY id DESC LIMIT #{offset}, #{size}"
            + "</script>")
    List<SensitiveWord> selectPage(@Param("category") Integer category,
                                   @Param("status") Integer status,
                                   @Param("keyword") String keyword,
                                   @Param("offset") int offset,
                                   @Param("size") int size);

    @Select("<script>"
            + "SELECT COUNT(*) FROM t_sensitive_word "
            + "<where>"
            + "<if test='category != null'>AND category = #{category} </if>"
            + "<if test='status != null'>AND status = #{status} </if>"
            + "<if test=\"keyword != null and keyword != ''\">AND word LIKE CONCAT('%', #{keyword}, '%') </if>"
            + "</where>"
            + "</script>")
    long countPage(@Param("category") Integer category,
                   @Param("status") Integer status,
                   @Param("keyword") String keyword);

    /** 批量导入前先查重，已存在的跳过 */
    @Select("SELECT COUNT(*) FROM t_sensitive_word WHERE word = #{word}")
    long countByWord(@Param("word") String word);

    /** 取所有启用中的词，审核详情用来做命中比对 */
    @Select("SELECT word FROM t_sensitive_word WHERE status = 0")
    List<String> selectEnabledWords();

    /** 全部词，批量导入时一次查出来做去重，避免逐个 count */
    @Select("SELECT word FROM t_sensitive_word")
    List<String> selectAllWords();
}
