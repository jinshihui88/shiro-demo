package com.example.shirodemo.mapper;

import com.example.shirodemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return User 查询到的用户信息
     */
    @Select("select * from user where username = #{username}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "phone", property = "phone"),
            @Result(column = "gender", property = "gender"),
            @Result(column = "lock_state", property = "lockState"),
            @Result(column = "last_login_time", property = "lastLoginTime")
    })
    User selectByUsername(String username);
}
