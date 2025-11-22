package org.PartyGames.Server.Games.Lobby;

import org.PartyGames.Server.Games.GameServerController;
import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.Connections.*;
import org.PartyGames.Common.Shared.Games;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * This is the Lobby game class.
 */
public class Lobby extends GameServerController {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);
    /** Map: Client-ID -> Client-Name */
    private final Map<String, String> client_names;
    /** Map: Client-ID -> if it has voted */
    private final Map<String, Boolean> client_votes;

    /** Call super constructor and initialize both internal Maps */
    public Lobby() {
        super();
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
            if (action.getMessageType().equals(MessageType.Invalid)) { continue; }

            // for convenience get the Address
            String client_id = action.getAddress();
            if (client_id == null) { continue; }
            MessageType message_type = action.getMessageType();

            switch (message_type) {
                case MessageType.ClientName -> {
                    String data = action.getData();
                    if (data == null) { continue; }

                    // Handle for Client name, either the name isn't or is already registered
                    if (client_names.containsValue(data))  {
                        // this name is already registered, deny the client
                        NetworkMessage message = new NetworkMessage();
                        message.setMessageType(MessageType.ClientError)
                                .setAddress(client_id)
                                .setData("Name already taken!");
                        // send to the Client a ClientError message
                        connection.notifyClients(message);
                        logger.warn("Woah, another client trying to use the same name: {}", data);
                    } else {
                        // this name isn't taken, register it.
                        client_names.put(client_id, data);
                        // put the vote to false for the Client-ID
                        client_votes.put(client_id, false);
                        NetworkMessage message = new NetworkMessage();
                        message.setMessageType(MessageType.ClientName)
                                .setAddress(client_id)
                                .setData(data);
                        // send to the Client a ClientName message with content of the name
                        connection.notifyClients(message);
                        logger.info("Success! registered a new name: {}", data);
                    }
                }
                case MessageType.ClientStatus -> {
                    String data = action.getData();
                    if (data == null) { continue; }

                    String name = client_names.get(client_id);
                    if (name != null) {
                        if (data.equals("ACK")) {
                            //logger.info("Client: {}, has acknowledged our game", name);
                        } else {
                            logger.warn("Unexpected! Client sent ClientStatus with: {}", action);
                        }
                    } else {
                        // Client has acknowledged our Game, but hasn't sent their name, or Map ID->Name doesn't exist
                    }
                }
                case MessageType.ClientEvent -> {
                    // Client sent a Vote
                    if (!client_names.containsKey(client_id)) {
                        logger.error("This Client: {} tried to send ClientEvent and wasn't registered", client_id);
                    } else {
                        String data = action.getData();
                        if (data == null) { continue; }

                        if (data.equals("Y")) {
                            client_votes.put(client_id, true);
                        } else if (data.equals("N")) {
                            client_votes.put(client_id, false);
                        }
                    }
                }
            }
        }

        boolean end_lobby = true;
        for (Map.Entry<String, Boolean> votes : client_votes.entrySet()) {
            //String id = votes.getKey();
            Boolean vote = votes.getValue();
            if (!vote) {
                // As soon as 1 client votes against, don't end lobby
                end_lobby = false;
                break;
            }
        }

        if (end_lobby && !client_votes.isEmpty()) {
            NetworkMessage message = new NetworkMessage();
            message.setToBroadcast().setMessageType(MessageType.GameStatus).setData("Ending Lobby").setGame(Games.Lobby);
            connection.notifyClients(message);
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
