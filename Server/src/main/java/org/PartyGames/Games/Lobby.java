package org.PartyGames.Games;

import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.PartyGames.ServerHandlers.*;
import org.PartyGames.Shared.Games;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class Lobby extends GameStrategy {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);
    private final Map<String, String> client_names;
    private final Map<String, Boolean> client_votes;

    public Lobby(WebSocketServerHandler connection) {
        super(connection);
        client_names = new HashMap<>();
        client_votes = new HashMap<>();
    }

    @Override
    public void start() {
        super.start();
        logger.info("Now in Lobby");
    }

    @Override
    public void handleGame(List<NetworkMessage> messages) {
        for (NetworkMessage action : messages) {
            String client_id = action.getUUID();
            switch (action.getType()) {
                case MessageType.ClientName -> {
                    if (client_names.containsValue(action.getText()))  {
                        // this name is already taken
                        NetworkMessageBuilder builder = new NetworkMessageBuilder();
                        builder.setMessageType(MessageType.ClientError)
                                .setUUID(client_id)
                                .setText("Name already taken!");
                        connection.notifyClients(builder.exportMessage());
                        logger.warn("Woah, another client trying to use the same name: {}", action.getText());
                    } else {
                        client_names.put(client_id, action.getText());
                        client_votes.put(client_names.get(client_id), false);
                        NetworkMessageBuilder builder = new NetworkMessageBuilder();
                        builder.setMessageType(MessageType.ClientName)
                                .setUUID(client_id)
                                .setText(action.getText());
                        connection.notifyClients(builder.exportMessage());
                        logger.info("Success! registered a new name: {}", action.getText());
                    }
                }
                case MessageType.ClientStatus -> {
                    String name = client_names.get(client_id);
                    if (name != null) {
                        logger.info("Client: {}, has acknowledged our game", name);
                    } else {
                        logger.error("This Client: {} is not registered in client_names", client_id);
                    }
                }
                case MessageType.ClientEvent -> {
                    if (!client_names.containsKey(client_id)) {
                        logger.error("This Client: {} tried to send ClientEvent and wasn't registered", client_id);
                    } else {
                        if (action.getText().equals("Y")) {
                            client_votes.put(client_names.get(client_id), true);
                        } else if (action.getText().equals("N")) {
                            client_votes.put(client_names.get(client_id), false);
                        }
                    }
                }
            }
        }

        boolean end_lobby = true;
        for (Map.Entry<String, Boolean> votes : client_votes.entrySet()) {
            String name = votes.getKey();
            Boolean vote = votes.getValue();
            if (!vote) {
                // As soon as 1 client votes against, don't end lobby
                end_lobby = false;
            }
        }
        //logger.info("Lobby count: {}", client_votes.size());

        if (end_lobby && !client_votes.isEmpty()) {
            NetworkMessageBuilder builder = new NetworkMessageBuilder();
            builder.setToBroadcast().setMessageType(MessageType.GameStatus).setText("Ending Lobby").setGame(Games.Lobby);
            connection.notifyClients(builder.exportMessage());
            stop();
        }


    }

    @Override
    public void stop() {
        super.stop();
        /* Below is extra behavior, you don't need to overload stop, unless you need extra cleanup */
        logger.info("Exiting Lobby state");
    }

    @Override
    public Games getGame() {
        /* Make sure to register your game in Common:org.PartyGames.Shared.Games */
        return Games.Lobby;

    }
}
