package org.PartyGames.ConnectionHandlers;


import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketConnectionHandler extends WebSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnectionHandler.class);
    private final List<NetworkMessage> server_messages;
    private volatile boolean is_connected = false;

    public WebSocketConnectionHandler(String server_address) {
        super(URI.create(server_address), new Draft_6455());
        server_messages = Collections.synchronizedList(new ArrayList<>());
    }


    public void send(NetworkMessage data) {
        if (!isConnected()) {
            logger.warn("No connection established");
        }
        super.send(NetworkMessageBuilder.parseString(data));
    }

    public void start() {
        try {
            connect();
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


    public List<NetworkMessage> consumeMessages() {
        if (!isConnected()) { return new ArrayList<>(); }

        synchronized (server_messages) {
            List<NetworkMessage> clone = new ArrayList<NetworkMessage>(server_messages);
            server_messages.clear();
            return clone;
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        //server_messages.add("Uhh... connected successfully!");
        NetworkMessageBuilder builder = new NetworkMessageBuilder();
        builder.setMessageType(MessageType.NetworkStatus).setText("Connected to server!");
        server_messages.add(builder.exportMessage());
        is_connected = true;
    }

    @Override
    public void onMessage(String data) {
        try {
            NetworkMessage message = NetworkMessageBuilder.parseNetworkMessage(data);
            server_messages.add(message);
        }
        catch (ParseException _) {}
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        NetworkMessageBuilder builder = new NetworkMessageBuilder();
        if (code == -1) {
            //server_messages.add(Pair.with(MessageType.ConnectionStatus, "Waiting for connection!"));
        } else {
            builder.setMessageType(MessageType.NetworkStatus).setText("Connection Closed!");
            server_messages.add(builder.exportMessage());
        }
        is_connected = false;
    }

    @Override
    public void onError(Exception e) {
        NetworkMessageBuilder builder = new NetworkMessageBuilder();
        builder.setMessageType(MessageType.NetworkStatus).setText("Error: " + e);
        server_messages.add(builder.exportMessage());
        is_connected = false;
    }

    public boolean isConnected() {
        return is_connected;
    }
}
