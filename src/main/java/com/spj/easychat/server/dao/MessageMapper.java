package com.spj.easychat.server.dao;

import com.spj.easychat.common.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<Message> getRecentMessageList();

    List<Message> getRecentMessageListByFromUser(String userName);

}
