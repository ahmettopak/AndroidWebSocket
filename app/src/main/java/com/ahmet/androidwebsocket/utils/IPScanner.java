package com.ahmet.androidwebsocket.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/26/2024
 */


public class IPScanner {
    private static final int TIMEOUT = 1000; // 1 saniye
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final List<String> reachableHosts = new ArrayList<>();
    private IPScanListener listener;

    public IPScanner(IPScanListener listener) {
        this.listener = listener;
    }

    public void scanIPRange(String subnet) {
        for (int i = 1; i < 255; i++) {
            final String host = subnet + "." + i;
            executorService.submit(() -> {
                try {
                    InetAddress inetAddress = InetAddress.getByName(host);
                    if (inetAddress.isReachable(TIMEOUT)) {
                        synchronized (reachableHosts) {
                            reachableHosts.add(host);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        new Thread(() -> {
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (listener != null) {
                listener.onScanCompleted(reachableHosts);
            }
        }).start();
    }
}