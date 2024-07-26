package com.ahmet.androidwebsocket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmet.androidwebsocket.databinding.ActivityMainBinding;
import com.ahmet.androidwebsocket.log.LogAdapter;
import com.ahmet.androidwebsocket.log.LogEntry;
import com.ahmet.androidwebsocket.tinydb.TinyDB;
import com.ahmet.androidwebsocket.utils.scanner.IPScanListener;
import com.ahmet.androidwebsocket.utils.scanner.IPScannerManager;
import com.ahmet.androidwebsocket.utils.scanner.PortScanListener;
import com.ahmet.androidwebsocket.websocket.WebSocketManager;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LogAdapter logAdapter;
    private LogAdapter portAdapter;
    private LogAdapter ipAdapter;
    private WebSocketManager webSocketManager;
    private TinyDB tinyDB;
    private final String defaultSocketURL = "ws://192.168.3.2:2005";
    private final String KEY_SOCKET_URL = "SOCKET_URL";

    IPScannerManager ipScannerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tinyDB = new TinyDB(this);
        setupEdgeToEdge();
        setupLogAdapter();
        setupIpAdapter();
        loadSocketUrl();

        binding.connectButton.setOnClickListener(v -> toggleWebSocketConnection());
        binding.sendButton.setOnClickListener(v -> sendMessage());
        binding.sendBytesButton.setOnClickListener(v -> sendByteMessage());

        ipScannerManager = new IPScannerManager(new IPScanListener() {
            @Override
            public void onIPScanStarted() {
                ipAdapter.log(LogEntry.LogType.INFO, "IP Scan started");
            }

            @Override
            public void onIPScanFailed(String error) {
                ipAdapter.log(LogEntry.LogType.ERROR, "IP Scan failed: " + error);
            }

            @Override
            public void onIPScanCompleted(List<String> reachableHosts) {
                ipAdapter.log(LogEntry.LogType.INFO, "IP Scan completed. Reachable hosts: " + reachableHosts);
            }
        }, new PortScanListener() {
            @Override
            public void onPortScanStarted() {
                portAdapter.log(LogEntry.LogType.INFO, "Port Scan started");
            }

            @Override
            public void onPortScanFailed(String error) {
                portAdapter.log(LogEntry.LogType.ERROR, "Port Scan failed: " + error);
            }

            @Override
            public void onPortScanCompleted(List<Integer> openPorts) {
                List<String> ports = new ArrayList<>();
                for (Integer port : openPorts) {
                    ports.add(String.valueOf(port));
                }
                portAdapter.log(LogEntry.LogType.INFO, "Port Scan completed. Open ports: " + ports);
            }
        });


        binding.ipScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subnet = binding.subnetEditText.getText().toString().trim();

                if (binding.useDeviceSubnetCheckBox.isChecked()) {
                    // Retrieve the device's subnet here
                    subnet = getDeviceSubnet();
                }
                else{
                    if (subnet.isEmpty()) {
                        ipAdapter.log(LogEntry.LogType.ERROR, "Subnet cannot be empty");
                        return;
                    }
                }

                try {
                    ipScannerManager.scanNetwork(subnet);
                } catch (IllegalArgumentException e) {
                    ipAdapter.log(LogEntry.LogType.ERROR, "Invalid subnet format: " + e.getMessage());
                } catch (Exception e) {
                    ipAdapter.log(LogEntry.LogType.ERROR, "Failed to start IP scan: " + e.getMessage());
                }
            }
        });

        binding.portScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = binding.ipEditText.getText().toString().trim();
                if (ip.isEmpty()) {
                    portAdapter.log(LogEntry.LogType.ERROR, "IP address cannot be empty");
                    return;
                }

                try {
                    // Validate IP address format (e.g., "192.168.1.1")
                    InetAddress.getByName(ip);

                    if (binding.searchKnowPortsCheckBox.isChecked()) {
                        ipScannerManager.scanKnownPorts(ip);
                    } else {
                        int startPort;
                        int endPort;

                        try {
                            startPort = Integer.parseInt(binding.startPortEditText.getText().toString().trim());
                            endPort = Integer.parseInt(binding.endPortEditText.getText().toString().trim());

                            if (startPort < 0 || endPort > 65535 || startPort > endPort) {
                                throw new IllegalArgumentException("Port numbers must be between 0 and 65535, and start port must be less than or equal to end port.");
                            }

                            ipScannerManager.scanPorts(ip, startPort, endPort);
                        } catch (NumberFormatException e) {
                            portAdapter.log(LogEntry.LogType.ERROR, "Invalid port number: " + e.getMessage());
                        } catch (IllegalArgumentException e) {
                            portAdapter.log(LogEntry.LogType.ERROR, e.getMessage());
                        }
                    }
                } catch (UnknownHostException e) {
                    portAdapter.log(LogEntry.LogType.ERROR, "Invalid IP address format: " + e.getMessage());
                } catch (Exception e) {
                    portAdapter.log(LogEntry.LogType.ERROR, "Failed to start port scan: " + e.getMessage());
                }
            }
        });
    }

    private String getDeviceSubnet() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Network activeNetwork = cm.getActiveNetwork();
            LinkProperties linkProperties = cm.getLinkProperties(activeNetwork);
            if (linkProperties != null) {
                for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
                    InetAddress inetAddress = linkAddress.getAddress();
                    if (inetAddress instanceof java.net.Inet4Address) {
                        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
                        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                            InetAddress address = interfaceAddress.getAddress();
                            if (address.isSiteLocalAddress()) {
                                byte[] ip = address.getAddress();
                                ip[3] = 0; // zero out the last octet
                                return (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." + (ip[2] & 0xFF);
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setupEdgeToEdge() {
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupLogAdapter() {
        logAdapter = new LogAdapter(this, new ArrayList<>(), binding.logListView);
        binding.logListView.setAdapter(logAdapter);
    }
    private void setupIpAdapter() {
        ipAdapter = new LogAdapter(this, new ArrayList<>(), binding.ipListView);
        binding.ipListView.setAdapter(ipAdapter);

        portAdapter = new LogAdapter(this, new ArrayList<>(), binding.portListView);
        binding.portListView.setAdapter(portAdapter);
    }
    private void loadSocketUrl() {
        String url = tinyDB.getString(KEY_SOCKET_URL, defaultSocketURL);
        binding.urlEditText.setText(url);
    }

    private void saveSocketUrl(String url) {
        tinyDB.putString(KEY_SOCKET_URL, url);
    }

    private void toggleWebSocketConnection() {
        if (webSocketManager == null || !webSocketManager.isSocketOpen()) {
            connectWebSocket();
        } else {
            disconnectWebSocket();
        }
    }

    private void connectWebSocket() {
        String url = binding.urlEditText.getText().toString();
        if (url.isEmpty()) {
            logAdapter.log(LogEntry.LogType.ERROR, "WebSocket URL is empty");
            return;
        }

        // Perform URL validation
        if (!isValidWebSocketUrl(url)) {
            logAdapter.log(LogEntry.LogType.ERROR, "Invalid WebSocket URL");
            return;
        }

        saveSocketUrl(url);

        try {
            webSocketManager = new WebSocketManager(url, logAdapter);
            webSocketManager.connect();
            binding.connectButton.setText("Disconnect");
        } catch (Exception e) {
            logAdapter.log(LogEntry.LogType.ERROR, "Failed to create WebSocketManager: " + e.getMessage());
        }
    }

    private void disconnectWebSocket() {
        if (webSocketManager != null && webSocketManager.isSocketOpen()) {
            webSocketManager.disconnect();
            logAdapter.log(LogEntry.LogType.INFO, "WebSocket disconnected");
            binding.connectButton.setText("Connect");
        }
    }

    private boolean isValidWebSocketUrl(String url) {
        // Simple URL validation logic
        return url.startsWith("ws://") || url.startsWith("wss://");
    }

    private void sendMessage() {
        if (webSocketManager == null || !webSocketManager.isSocketOpen()) {
            logAdapter.log(LogEntry.LogType.ERROR, "WebSocket is not connected");
            return;
        }

        String message = binding.messageEditText.getText().toString();
        if (message.isEmpty()) {
            logAdapter.log(LogEntry.LogType.ERROR, "Message is empty");
            return;
        }

        try {
            webSocketManager.sendMessage(message);
        } catch (Exception e) {
            logAdapter.log(LogEntry.LogType.ERROR, "Failed to send message: " + e.getMessage());
        }
    }

    private void sendByteMessage() {
        if (webSocketManager == null || !webSocketManager.isSocketOpen()) {
            logAdapter.log(LogEntry.LogType.ERROR, "WebSocket is not connected");
            return;
        }

        String message = binding.messageEditText.getText().toString();
        if (message.isEmpty()) {
            logAdapter.log(LogEntry.LogType.ERROR, "Message is empty");
            return;
        }

        try {
            webSocketManager.sendByteMessage(message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logAdapter.log(LogEntry.LogType.ERROR, "Failed to send byte message: " + e.getMessage());
        }
    }
}