package org.PartyGames.Server.Connections;


import com.fasterxml.uuid.Generators;
import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class that manages the WebSocket connection
 * It wraps a couple of methods and just abstracts away the implementation
 */
public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    /** The Thread-insensitive message queue */
    private final ConcurrentLinkedQueue<String> messages;
    public WebSocketServer(int port) {
        super(new InetSocketAddress(port));
        messages = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void onOpen(WebSocket websocket, ClientHandshake client_handshake) {
        logger.info("{} entered the room!", websocket.getRemoteSocketAddress().toString());
        NetworkMessage message = new NetworkMessage();
        String uuid = getUUID();
        message.setMessageType(MessageType.ClientUUID).setData(uuid);
        websocket.send(message.toString());
        logger.info("Assigned: {} : {}", websocket.getRemoteSocketAddress().toString(), uuid);
    }

    @Override
    public void onClose(WebSocket websocket, int code, String reason, boolean remote) {
        logger.info("{} has left the room!", websocket.getRemoteSocketAddress().toString());
    }

    @Override
    public void onMessage(WebSocket websocket, String message) {
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

            messages.forEach(message -> {
                try {
                    NetworkMessage data = NetworkMessage.fromString(message);
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
        broadcast(message.toString());
    }

    public void clearBuffer() {
        messages.clear();
    }


    private static String getUUID() {
        return Generators.defaultTimeBasedGenerator().generate().toString();
    }
}
