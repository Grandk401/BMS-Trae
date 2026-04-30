/**
 * 用户 Mapper 接口
 */
package com.bms.mapper;

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

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Integer id);
}
