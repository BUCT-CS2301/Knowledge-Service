package com.mapper;

import com.entity.UserLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserLogMapper {
    List<UserLog> getUserLogs(@Param("user_id") int user_id, @Param("start") int start, @Param("pageSize") int pageSize);
    int getUserLogsCount(@Param("user_id") int user_id);
}
