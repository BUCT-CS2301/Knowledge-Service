package com.service;

import com.entity.UserLog;

import java.util.List;

public interface IUserLogService {
    List<UserLog> getUserLogs(int user_id, int page, int pageSize);
}
