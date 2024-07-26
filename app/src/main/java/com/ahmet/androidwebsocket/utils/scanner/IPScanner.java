package com.ahmet.androidwebsocket.utils.scanner;

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
    private boolean isScanning = false;

    public IPScanner(IPScanListener listener) {
        this.listener = listener;
    }

    public synchronized void scanIPRange(String subnet) {
        if (isScanning) {
            return; // Prevent starting a new scan if one is already in progress
        }

        isScanning = true;
        if (listener != null) {
            listener.onIPScanStarted(); // Notify that scan has started
        }

        // Clear previous results
        reachableHosts.clear();

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
                    // Log exception or handle it accordingly
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        new Thread(() -> {
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                if (listener != null) {
                    listener.onIPScanCompleted(reachableHosts); // Notify that scan has completed
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onIPScanFailed("Scan interrupted");
                }
            } finally {
                isScanning = false; // Allow new scans to start
            }
        }).start();
    }
}