package com.ahmet.androidwebsocket.log;

import android.graphics.Color;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/11/2024
 */

public enum LogType {
    INFO(Color.GREEN),
    WARNING(Color.YELLOW),
    ERROR(Color.RED);

    private int color;

    LogType(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}