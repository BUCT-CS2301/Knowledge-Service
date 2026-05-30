package com.service.Impl;

import com.entity.UserLog;
import com.mapper.UserLogMapper;
import com.service.IUserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLogServiceImpl implements IUserLogService {
    @Autowired
    private UserLogMapper userLogMapper;

    @Override
    public List<UserLog> getUserLogs(int user_id, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        return userLogMapper.getUserLogs(user_id, start, pageSize);
    }
}
