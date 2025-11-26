package org.PartyGames.Client;


import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Client.Games.Factory.GameClientControllerFactory;
import org.PartyGames.Client.Terminal.IOController;
import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    private String uuid;
    private long last_reconnect_attempt;
    private final IOController io_handler;
    private final WebSocketConnection connection;
    private GameClientController game_handler;
    GameClientControllerFactory game_controller_factory;

    public ClientController(WebSocketConnection connection_handler, IOController io_handler) {
        this.uuid = "";
        last_reconnect_attempt = 0;
        this.io_handler = io_handler;
        this.connection = connection_handler;
        this.game_handler = null;
        this.game_controller_factory = new GameClientControllerFactory();
    }

    private void checkConnectionTryReconnect()  {
        if (!connection.isConnected()) {
            long now = System.currentTimeMillis();
            if (now - last_reconnect_attempt >= 2000) {
                connection.restart();
                last_reconnect_attempt = now;
            }
        }
    }


    public void start() {
        connection.start();
        io_handler.start();
        this.game_handler = game_controller_factory.createGameClientController("NullGame", io_handler, connection, uuid);
        game_handler.start();
        logger.info("Width: {}", io_handler.getCharWidth());
        logger.info("Height: {}", io_handler.getCharHeight());
    }


    public void update() {
        checkConnectionTryReconnect();
        List<NetworkMessage> messages_for_game = new ArrayList<>();
        List<NetworkMessage> server_messages = connection.consumeMessages();
        if (server_messages == null) {
            checkConnectionTryReconnect();
            return;
        }
        GameClientControllerFactory game_controller_factory = new GameClientControllerFactory();
        for (NetworkMessage action : server_messages) {
            if (!uuid.isEmpty()) {
                String addr = action.getAddress();
                if (addr == null) { continue; }
                if (!(action.isBroadcast() || action.getAddress().equalsIgnoreCase(uuid))) {
                    continue;
                }
            }
            MessageType message_type = action.getMessageType();
            switch (message_type) {
                case MessageType.NetworkStatus -> {
                    logger.info("Network Status: {}", action.getData());
                }
                case MessageType.ClientUUID -> {
                    String data = action.getData();
                    if (data == null) { continue; }
                    uuid = data;
                    logger.info("Got UUID: {}", uuid);
                }
                case MessageType.NewGame -> {
                    String new_game_name = action.getData();
                    if (new_game_name == null) { continue; }

                    if (!new_game_name.equals(game_handler.getClass().getSimpleName())) {
                        game_handler.stop();
                        logger.info("Going to NewGame to: {}", new_game_name);
                        game_handler = game_controller_factory.createGameClientController(new_game_name, io_handler, connection, uuid);
                        game_handler.start();
                    }
                }
                case MessageType.GameStatus -> {
                    String message_game = action.getData();
                    if (message_game == null) { continue; }

                    if (!message_game.equals(game_handler.getClass().getSimpleName())) {
                        game_handler.stop();
                        logger.info("Switching Games to: {}", message_game);
                        game_handler = game_controller_factory.createGameClientController(message_game, io_handler, connection, uuid);
                        game_handler.start();
                    } else {
                        // everything is ok, game is set correctly, respond back to server.
                        NetworkMessage message = new NetworkMessage();
                        message.setAddress(uuid).setMessageType(MessageType.ClientStatus).setData("ACK");
                        connection.send(message);
                    }
                }
            }
            messages_for_game.add(action);
        }
        game_handler.handleGame(messages_for_game);
    }


    public void shutdown() {
        logger.info("Requested shutdown");
        game_handler.stop();
        logger.info("game_handler stopped");
        connection.stop();
        logger.info("connection stopped");
        io_handler.stop();
        logger.info("io_handler stopped");
    }
}
