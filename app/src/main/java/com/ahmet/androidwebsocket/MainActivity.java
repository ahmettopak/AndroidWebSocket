package com.ahmet.androidwebsocket;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmet.androidwebsocket.databinding.ActivityMainBinding;
import com.ahmet.androidwebsocket.log.LogMessage;
import com.ahmet.androidwebsocket.log.LogMessageAdapter;
import com.ahmet.androidwebsocket.log.LogType;
import com.ahmet.androidwebsocket.log.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements WebSocketListener {

    private ActivityMainBinding binding;
    private ArrayAdapter<String> logAdapter;
    private List<String> logList = new ArrayList<>();
    private SimpleWebSocket webSocket;

    private static final String WEBSOCKET_URL = "ws://192.168.3.2:2005";
    private static final int CONNECTION_TIMEOUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupEdgeToEdge();
        setupLogAdapter();
        setupWebSocket();

        binding.sendButton.setOnClickListener(v -> sendMessage());
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
        logAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, logList);
        binding.logListView.setAdapter(logAdapter);
    }

    private void setupWebSocket() {
        try {
            webSocket = new SimpleWebSocket(WEBSOCKET_URL, this);
            webSocket.setConnectionLostTimeout(CONNECTION_TIMEOUT);
            webSocket.connect();
        } catch (URISyntaxException e) {
            log("WebSocket URI Syntax Error: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String message = binding.messageEditTextText.getText().toString();
        if (webSocket.isOpen() && !message.isEmpty()) {
            webSocket.send(message);
        } else {
            webSocket.connect();
        }
    }

    private void log(String message) {
        logList.add(message);
        runOnUiThread(() -> {
            logAdapter.notifyDataSetChanged();
            binding.logListView.smoothScrollToPosition(logList.size() - 1);
        });
    }

    @Override
    public void onWebsocketOpen(WebSocket conn, Handshakedata d) {
        log("WebSocket Opened");
    }

    @Override
    public void onWebsocketClose(WebSocket ws, int code, String reason, boolean remote) {
        log("WebSocket Closed: Code=" + code + ", Reason=" + reason + ", Remote=" + remote);
    }

    @Override
    public void onWebsocketClosing(WebSocket ws, int code, String reason, boolean remote) {

    }

    @Override
    public void onWebsocketMessage(WebSocket conn, String message) {
        log("WebSocket Message: " + message);
    }

    @Override
    public void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {
        log("WebSocket Binary Message");
    }

    @Override
    public void onWebsocketError(WebSocket conn, Exception ex) {
        log("WebSocket Error: " + ex.getMessage());
    }

    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {
        log("WebSocket Pong");
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata f) {
        log("WebSocket Ping");
    }

    @Override
    public PingFrame onPreparePing(WebSocket conn) {
        log("WebSocket Prepare Ping");
        return null;
    }

    @Override
    public void onWriteDemand(WebSocket conn) {
        log("WebSocket Write Demand");
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        return null;
    }

    @Override
    public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response) {
        log("WebSocket Handshake Received As Client");
    }

    @Override
    public void onWebsocketHandshakeSentAsClient(WebSocket conn, ClientHandshake request) {
        log("WebSocket Handshake Sent As Client");
    }

    @Override
    public void onWebsocketCloseInitiated(WebSocket ws, int code, String reason) {
        log("WebSocket Close Initiated: Code=" + code + ", Reason=" + reason);
    }

    @Override
    public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
        log("WebSocket Local Socket Address");
        return null;
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
        log("WebSocket Remote Socket Address");
        return null;
    }
}
