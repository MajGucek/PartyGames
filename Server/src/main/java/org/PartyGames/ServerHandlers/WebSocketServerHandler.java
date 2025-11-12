package org.PartyGames.ServerHandlers;


import org.PartyGames.Client;
import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class that manages the WebSocket connection
 * It wraps a couple of methods and just abstracts away the implementation
 */
public class WebSocketServerHandler extends WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);
    /** The Thread-insensitive message queue */
    private final ConcurrentLinkedQueue<String> messages;
    public WebSocketServerHandler(int port) {
        super(new InetSocketAddress(port));
        messages = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void onOpen(WebSocket web_socket, ClientHandshake clientHandshake) {
        logger.info("{} entered the room!", web_socket.getRemoteSocketAddress().toString());
        NetworkMessageBuilder builder = new NetworkMessageBuilder();
        String uuid = Client.getUUID();
        builder.setMessageType(MessageType.ClientUUID).setText(uuid);
        web_socket.send(builder.exportJSON());
        logger.info("Assigned: {} : {}", web_socket.getRemoteSocketAddress().toString(), uuid);
    }

    @Override
    public void onClose(WebSocket web_socket, int code, String reason, boolean remote) {
        logger.info("{} has left the room!", web_socket.getRemoteSocketAddress().toString());
    }

    @Override
    public void onMessage(WebSocket web_socket, String message) {
        messages.add(message);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        logger.error("onError: {}", String.valueOf(e));
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }


    public List<NetworkMessage> consumeMessages() {
        List<NetworkMessage> snapshot = new ArrayList<>();

        synchronized (messages) {
            // synchronized is a blocking block {}
            // So, take snapshot, than remove everything out of the messages.

            messages.forEach((message) -> {
                try {
                    NetworkMessage data = NetworkMessageBuilder.parseNetworkMessage(message);
                    snapshot.add(data);
                } catch (ParseException e) {
                    logger.error("Parse exception: {}", String.valueOf(e));
                }
            });
            messages.clear();
            return snapshot;
        }
    }

    public void notifyClients(NetworkMessage message) {
        broadcast(NetworkMessageBuilder.parseString(message));
    }

    public void clearBuffer() {
        messages.clear();
    }

}
