package com.lxz.careercode.admin.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxz.careercode.admin.entity.admin.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 管理员账号（careercode_admin 库）。
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    /** 按登录名查有效账号，软删的不返回 */
    @Select("SELECT * FROM t_admin_user WHERE username = #{username} AND deleted_at IS NULL LIMIT 1")
    AdminUser selectByUsername(@Param("username") String username);

    /** 登录成功后更新最后登录信息，失败不影响登录 */
    @Update("UPDATE t_admin_user SET last_login_at = NOW(), last_login_ip = #{ip} WHERE id = #{id}")
    int updateLastLogin(@Param("id") Long id, @Param("ip") String ip);

    /** 管理员列表，新的在前 */
    @Select("SELECT * FROM t_admin_user WHERE deleted_at IS NULL ORDER BY id LIMIT #{offset}, #{size}")
    List<AdminUser> selectPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM t_admin_user WHERE deleted_at IS NULL")
    long countAll();

    /** 还能正常登录的管理员数量，用来拦住「禁用最后一个」 */
    @Select("SELECT COUNT(*) FROM t_admin_user WHERE deleted_at IS NULL AND status = 0")
    long countEnabled();

    @Update("UPDATE t_admin_user SET password_hash = #{hash}, updated_at = NOW() "
            + "WHERE id = #{id} AND deleted_at IS NULL")
    int updatePassword(@Param("id") Long id, @Param("hash") String hash);

    @Update("UPDATE t_admin_user SET status = #{status}, updated_at = NOW() "
            + "WHERE id = #{id} AND deleted_at IS NULL")
    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
