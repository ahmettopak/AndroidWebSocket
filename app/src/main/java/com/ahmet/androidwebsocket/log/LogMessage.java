package com.ahmet.androidwebsocket.log;

import android.graphics.Color;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/11/2024
 */

public class LogMessage {


    private String message;
    private LogType type;

    public LogMessage(String message, LogType type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public LogType getType() {
        return type;
    }
}