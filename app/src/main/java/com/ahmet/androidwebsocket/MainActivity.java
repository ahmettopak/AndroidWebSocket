package com.ahmet.androidwebsocket;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmet.androidwebsocket.databinding.ActivityMainBinding;
import com.ahmet.androidwebsocket.log.LogAdapter;
import com.ahmet.androidwebsocket.log.LogEntry;
import com.ahmet.androidwebsocket.tinydb.TinyDB;
import com.ahmet.androidwebsocket.websocket.WebSocketManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LogAdapter logAdapter;
    private WebSocketManager webSocketManager;
    private TinyDB tinyDB;
    private final String defaultSocketURL = "ws://192.168.3.2:2005";
    private final String KEY_SOCKET_URL = "SOCKET_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tinyDB = new TinyDB(this);
        setupEdgeToEdge();
        setupLogAdapter();
        loadSocketUrl();

        binding.connectButton.setOnClickListener(v -> toggleWebSocketConnection());
        binding.sendButton.setOnClickListener(v -> sendMessage());
        binding.sendBytesButton.setOnClickListener(v -> sendByteMessage());
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