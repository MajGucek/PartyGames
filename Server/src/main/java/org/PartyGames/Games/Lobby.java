package org.PartyGames.Games;

import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.PartyGames.ServerHandlers.*;
import org.PartyGames.Shared.Games;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * This is the Lobby game class.
 */
public class Lobby extends GameStrategy {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);
    /** Map: Client-ID -> Client-Name */
    private final Map<String, String> client_names;
    /** Map: Client-ID -> if it has voted */
    private final Map<String, Boolean> client_votes;

    /** Call super constructor and initialize both internal Maps */
    public Lobby(WebSocketServerHandler connection) {
        super(connection);
        client_names = new HashMap<>();
        client_votes = new HashMap<>();
    }

    /** Overridden start method that just logs That its in Lobby */
    @Override
    public void start() {
        super.start();
        logger.info("Now in Lobby");
    }

    /**
     * @param messages these are the NetworkMessages in-order that have been sent by the clients
     * This method implements the main logic for Lobby game.
     */
    @Override
    public void handleGame(List<NetworkMessage> messages) {
        // Iterate over messages
        for (NetworkMessage action : messages) {
            // for convenience copy this UUID
            String client_id = action.getUUID();

            // Switch the MessageType, check in Common:org.PartyGames.Networking.MessageType
            switch (action.getType()) {
                case MessageType.ClientName -> {
                    // Handle for Client name, either the name isn't or is already registered
                    if (client_names.containsValue(action.getText()))  {
                        // this name is already registered, deny the client
                        NetworkMessageBuilder builder = new NetworkMessageBuilder();
                        builder.setMessageType(MessageType.ClientError)
                                .setUUID(client_id)
                                .setText("Name already taken!");
                        // send to the Client a ClientError message
                        connection.notifyClients(builder.exportMessage());
                        logger.warn("Woah, another client trying to use the same name: {}", action.getText());
                    } else {
                        // this name isn't taken, register it.
                        client_names.put(client_id, action.getText());
                        // put the vote to false for the Client-ID
                        client_votes.put(client_id, false);
                        NetworkMessageBuilder builder = new NetworkMessageBuilder();
                        builder.setMessageType(MessageType.ClientName)
                                .setUUID(client_id)
                                .setText(action.getText());
                        // send to the Client a ClientName message with content of the name
                        connection.notifyClients(builder.exportMessage());
                        logger.info("Success! registered a new name: {}", action.getText());
                    }
                }
                case MessageType.ClientStatus -> {
                    // Client-side isn't implemented yet
                    String name = client_names.get(client_id);
                    if (name != null) {
                        logger.info("Client: {}, has acknowledged our game", name);
                    } else {
                        logger.error("This Client: {} is not registered in client_names", client_id);
                    }
                }
                case MessageType.ClientEvent -> {
                    // Client sent a Vote
                    if (!client_names.containsKey(client_id)) {
                        logger.error("This Client: {} tried to send ClientEvent and wasn't registered", client_id);
                    } else {
                        if (action.getText().equals("Y")) {
                            client_votes.put(client_id, true);
                        } else if (action.getText().equals("N")) {
                            client_votes.put(client_id, false);
                        }
                    }
                }
            }
        }

        boolean end_lobby = true;
        for (Map.Entry<String, Boolean> votes : client_votes.entrySet()) {
            String id = votes.getKey();
            Boolean vote = votes.getValue();
            if (!vote) {
                // As soon as 1 client votes against, don't end lobby
                end_lobby = false;
            }
        }

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
