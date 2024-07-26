package com.ahmet.androidwebsocket.utils;

import java.util.List;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/26/2024
 */

public interface IPScanListener {
    void onScanCompleted(List<String> reachableHosts);
}