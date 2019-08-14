package com.spj.easychat.server.dao;

import com.spj.easychat.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserByName(String name);

    void addUser(User user);

}
