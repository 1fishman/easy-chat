package com.spj.easychat.server.dao;

import com.spj.easychat.common.entity.CommonMessage;
import com.spj.easychat.common.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface MessageMapper {
    List<CommonMessage> getRecentMessageList( @Param("userName") String userName);

    List<CommonMessage> getRecentMessageListByFromUser(String userName);

    void insertMessage(CommonMessage commonMessage);

}
