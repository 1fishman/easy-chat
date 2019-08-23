package com.spj.easychat.server.dao;

import com.spj.easychat.common.entity.CommonMessage;
import com.spj.easychat.common.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface MessageMapper {
    List<CommonMessage> getRecentMessageList(@Param("fromUser") String fromUser,@Param("toUser") String toUser, @Param("count") int count);

    List<CommonMessage> getRecentMessageListByFromUser(String userName);

    void insertMessage(CommonMessage commonMessage);
    void insertMessages(@Param("list") List<CommonMessage> list);

}
