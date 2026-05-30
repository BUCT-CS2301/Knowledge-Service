package com.entity;

import lombok.Data;

@Data
public class UserLog {
    private int log_id;
    private int user_id;
    private String type;
    private String description;
    private String time;

    public UserLog() {}

    public UserLog(int user_id, String type, String description, String time) {
        this.user_id = user_id;
        this.type = type;
        this.description = description;
        this.time = time;
    }
}
