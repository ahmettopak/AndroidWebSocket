package com.ahmet.androidwebsocket.utils.scanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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


public class PortScanner {
    private static final int TIMEOUT = 1000; // 1 second
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final List<Integer> openPorts = new ArrayList<>();
    private final List<Integer> knownPorts = new ArrayList<>();
    private PortScanListener listener;

    public PortScanner(PortScanListener listener) {
        this.listener = listener;
        initializeKnownPorts();
    }

    private void initializeKnownPorts() {
        // Add known ports here
        knownPorts.add(21);   // FTP
        knownPorts.add(22);   // SSH
        knownPorts.add(23);   // Telnet
        knownPorts.add(25);   // SMTP
        knownPorts.add(53);   // DNS
        knownPorts.add(80);   // HTTP
        knownPorts.add(110);  // POP3
        knownPorts.add(143);  // IMAP
        knownPorts.add(443);  // HTTPS
        knownPorts.add(3389); // RDP
        // Add more known ports as needed
    }

    public void scanPortRange(String ip, int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            final int currentPort = port;
            executorService.submit(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, currentPort), TIMEOUT);
                    synchronized (openPorts) {
                        openPorts.add(currentPort);
                    }
                } catch (IOException ignored) {
                }
            });
        }

        finishPortScan();
    }

    public void scanKnownPorts(String ip) {
        for (int port : knownPorts) {
            final int currentPort = port;
            executorService.submit(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, currentPort), TIMEOUT);
                    synchronized (openPorts) {
                        openPorts.add(currentPort);
                    }
                } catch (IOException ignored) {
                }
            });
        }

        finishPortScan();
    }

    public void scanCustomPorts(String ip, List<Integer> customPorts) {
        for (int port : customPorts) {
            final int currentPort = port;
            executorService.submit(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, currentPort), TIMEOUT);
                    synchronized (openPorts) {
                        openPorts.add(currentPort);
                    }
                } catch (IOException ignored) {
                }
            });
        }

        finishPortScan();
    }

    private void finishPortScan() {
        executorService.shutdown();
        new Thread(() -> {
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (listener != null) {
                listener.onPortScanCompleted(openPorts);
            }
        }).start();
    }
}