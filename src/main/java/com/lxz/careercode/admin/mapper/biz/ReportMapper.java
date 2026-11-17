package com.lxz.careercode.admin.mapper.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.dto.DailyCount;
import com.lxz.careercode.admin.dto.StatusCount;
import com.lxz.careercode.admin.entity.biz.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 举报表数据访问。
 * <p>
 * 处理动作直接用 BaseMapper 的 updateById，只有做不了的事才写 SQL。
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {

    /** 按状态分组计数，举报分布用。t_report 没有软删列，不加过滤 */
    @Select("SELECT status AS status, COUNT(*) AS cnt FROM careercode_social.t_report GROUP BY status")
    List<StatusCount> countByStatus();

    /** 近 N 天每天新增举报 */
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') AS date, COUNT(*) AS cnt "
            + "FROM careercode_social.t_report "
            + "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) "
            + "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d')")
    List<DailyCount> countDaily(@Param("days") int days);

    /**
     * 举报列表，待处理的排前面，同状态按时间倒序。
     *
     * @param status 状态过滤，null 表示全部
     * @param offset 偏移量
     * @param size   每页条数
     */
    @Select("<script>"
            + "SELECT r.*, u.nickname AS reporter_nickname "
            + "FROM careercode_social.t_report r LEFT JOIN careercode.t_user u ON u.id = r.reporter_id "
            + "<where><if test='status != null'>AND r.status = #{status} </if></where>"
            + "ORDER BY r.status ASC, r.created_at DESC LIMIT #{offset}, #{size}"
            + "</script>")
    List<Report> selectPage(@Param("status") Integer status,
                            @Param("offset") int offset,
                            @Param("size") int size);
}
