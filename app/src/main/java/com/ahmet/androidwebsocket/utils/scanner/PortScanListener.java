package com.ahmet.androidwebsocket.utils.scanner;

import java.util.List;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/26/2024
 */

public interface PortScanListener {

    void onPortScanStarted();
    void onPortScanFailed(String error);
    void onPortScanCompleted(List<Integer> openPorts);
}