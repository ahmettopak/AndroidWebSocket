package com.ahmet.androidwebsocket.log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/11/2024
 */

public class Logger {
    private List<LogMessage> logMessages;
    private LogMessageAdapter adapter;

    public Logger(LogMessageAdapter adapter) {
        this.logMessages = new ArrayList<>();
        this.adapter = adapter;
    }

    public void log(String message, LogType type) {
        LogMessage logMessage = new LogMessage(message, type);
        logMessages.add(logMessage);
        adapter.notifyDataSetChanged();
    }
}