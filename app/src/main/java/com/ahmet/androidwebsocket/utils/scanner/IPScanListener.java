package com.ahmet.androidwebsocket.utils.scanner;

import java.util.List;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/26/2024
 */

public interface IPScanListener {
    void onIPScanStarted();
    void onIPScanFailed(String error);
    void onIPScanCompleted(List<String> reachableHosts);
}