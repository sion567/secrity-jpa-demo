package com.example.demo.mapper;


import org.apache.ibatis.annotations.Select;
import com.example.demo.entity.User;
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    @Select("select * from user where mobile = #{mobile}")
    User selectByMobile(String mobile);
}
