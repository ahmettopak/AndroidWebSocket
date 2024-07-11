package com.ahmet.androidwebsocket;

import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/11/2024
 */

public class SimpleWebSocket extends WebSocketClient {

    WebSocketListener webSocketListener;
    public SimpleWebSocket(String url , WebSocketListener listener) throws URISyntaxException {
        super(new URI(url));
        this.webSocketListener = listener;

    }

    public void setListener(WebSocketListener listener) {
        this.webSocketListener = listener;
    }

    public void sendData(String data) {
        send(data);
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        webSocketListener.onWebsocketOpen(this , handshakedata);
    }

    @Override
    public void onMessage(String message) {
        webSocketListener.onWebsocketMessage(this , message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        webSocketListener.onWebsocketClose(this , code , reason , remote);
    }

    @Override
    public void onError(Exception ex) {
        webSocketListener.onWebsocketError(this , ex);
    }
}
