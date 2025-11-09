package org.PartyGames.ServerHandlers;


import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketServerHandler extends WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private final Object message_lock;
    private final ConcurrentHashMap<WebSocket, ConcurrentLinkedQueue<String>> messages;
    public WebSocketServerHandler(int port) {
        super(new InetSocketAddress(port));
        messages = new ConcurrentHashMap<WebSocket, ConcurrentLinkedQueue<String>>();
        message_lock = new Object();
    }

    @Override
    public void onOpen(WebSocket client, ClientHandshake clientHandshake) {
        logger.info("{} entered the room!", client.getRemoteSocketAddress().toString());
        messages.put(client, new ConcurrentLinkedQueue<String>());
    }

    @Override
    public void onClose(WebSocket client, int code, String reason, boolean remote) {
        logger.info("{} has left the room!", client.getRemoteSocketAddress().toString());
        messages.remove(client);
    }

    @Override
    public void onMessage(WebSocket client, String message) {
        synchronized (message_lock) {
            ConcurrentLinkedQueue<String> queue = messages.get(client);
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
                messages.put(client, queue);
            }
            queue.add(message);
        }
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


    public Map<WebSocket, List<NetworkMessage>> consumeMessages() {
        synchronized (message_lock) {
            // synchronized is a blocking block {}
            // So, take snapshot, than remove everything out of the messages.
            Map<WebSocket, List<NetworkMessage>> snapshot = new HashMap<WebSocket, List<NetworkMessage>>();

            messages.forEach((client, queue) -> {
                List<NetworkMessage> actions = new ArrayList<NetworkMessage>();
                queue.forEach((action) -> {
                    try {
                        NetworkMessage message = NetworkMessageBuilder.parseNetworkMessage(action);
                        actions.add(message);
                    } catch (ParseException _) {}
                });
                snapshot.put(client, actions);
                queue.clear();
            });
            return snapshot;
        }
    }

}
