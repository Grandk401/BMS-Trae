/**
 * 用户 Mapper 接口
 */
package com.bms.mapper;

import com.bms.dto.UserSearchDTO;
import com.bms.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Integer id);

    @Insert("INSERT INTO user(username, password, role) VALUES(#{username}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET role = #{role} WHERE id = #{id}")
    int updateRole(Integer id, String role);

    @Update("UPDATE user SET enabled = #{enabled} WHERE id = #{id}")
    int updateEnabled(Integer id, Boolean enabled);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 动态搜索用户列表
     *
     * @param dto 搜索条件
     * @return 用户列表
     */
    List<User> searchUsers(UserSearchDTO dto);
}
