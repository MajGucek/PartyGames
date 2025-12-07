package org.PartyGames.Client.Connections;


import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketConnection extends WebSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnection.class);
    private final List<NetworkMessage> server_messages;
    private volatile boolean is_connected = false;

    public WebSocketConnection(String server_address) {
        super(URI.create(server_address), new Draft_6455());
        server_messages = Collections.synchronizedList(new ArrayList<>());
    }


    public void send(NetworkMessage data) {
        if (!isConnected()) {
            logger.warn("No connection established");
        }
        super.send(data.toString());
    }

    public void start() {
        try {
            connectBlocking();
        }
        catch (Exception e) {
            logger.error("Failed to Connect, Exception: {}", String.valueOf(e));
        }
    }


    public void restart() {
        synchronized (server_messages) {
            server_messages.clear();
        }
        is_connected = false;

        reconnect();
    }


    public void stop() {
        close();
        synchronized (server_messages) {
            server_messages.clear();
        }
        is_connected = false;
    }


    public List<NetworkMessage> consumeMessages() throws WebsocketNotConnectedException {
        if (!isConnected()) {
            is_connected = false;
            restart();
            throw new WebsocketNotConnectedException();
        }

        synchronized (server_messages) {
            List<NetworkMessage> clone = new ArrayList<>(server_messages);
            server_messages.clear();
            return clone;
        }
    }

    @Override
    public void onOpen(ServerHandshake server_handshake) {
        NetworkMessage message = new NetworkMessage();
        message.setMessageType(MessageType.NetworkStatus).setData("Connected to server!");
        server_messages.add(message);
        is_connected = true;
        logger.info("Opened connection!");
    }

    @Override
    public void onMessage(String data) {
        try {
            NetworkMessage message = NetworkMessage.fromString(data);
            server_messages.add(message);
        } catch (ParseException e) {
            logger.error("Parse Error: {}", String.valueOf(e));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        NetworkMessage message = new NetworkMessage();
        if (code == -1) {
            //server_messages.add(Pair.with(MessageType.ConnectionStatus, "Waiting for connection!"));
        } else {
            message.setMessageType(MessageType.NetworkStatus).setData("Connection Closed!");
            server_messages.add(message);
        }
        is_connected = false;
    }

    @Override
    public void onError(Exception e) {
        NetworkMessage message = new NetworkMessage();
        message.setMessageType(MessageType.NetworkStatus).setData("Error: " + e);
        server_messages.add(message);
        is_connected = false;
    }


    public boolean isConnected() {
        return is_connected;
    }
}
