package com.chaos.schoollib.mapper;

import com.chaos.schoollib.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return User 对象
     */
    // 为简单起见，直接返回 User
    User findByUsername(@Param("username") String username);

    /**
     * 插入一个新用户
     * @param user 用户对象
     * @return 受影响的行数
     */
    int insert(User user);
}