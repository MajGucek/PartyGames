package org.PartyGames.Client;


import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Client.Games.Factory.GameClientControllerFactory;
import org.PartyGames.Client.Terminal.IOController;
import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;

import java.util.ArrayList;
import java.util.List;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    private final int TPS;
    private String uuid;
    private long last_reconnect_attempt;
    private final IOController io_controller;
    private final WebSocketConnection connection;
    private GameClientController game_controller;
    private final GameClientControllerFactory game_controller_factory;
    private long current_tick;

    public ClientController(WebSocketConnection connection, IOController io_controller, int TPS) {
        this.TPS = TPS;
        this.uuid = "";
        last_reconnect_attempt = 0;
        this.io_controller = io_controller;
        this.connection = connection;
        this.game_controller = null;
        this.game_controller_factory = new GameClientControllerFactory();
        this.current_tick = 0;
    }

    private void checkConnectionTryReconnect() {
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
        io_controller.start();
        this.game_controller = game_controller_factory.createGameClientController(
                "NullGame",
                io_controller,
                connection,
                uuid,
                TPS
        );
        logger.info("Width: {}", io_controller.getCharWidth());
        logger.info("Height: {}", io_controller.getCharHeight());
        game_controller.start();
    }


    public void update() {
        current_tick++;
        List<NetworkMessage> messages_for_game = new ArrayList<>();
        try {
            List<NetworkMessage> server_messages = connection.consumeMessages();
            GameClientControllerFactory game_controller_factory = new GameClientControllerFactory();
            for (NetworkMessage action : server_messages) {
                if (!uuid.isEmpty()) {
                    String addr = action.getAddress();
                    if (addr == null) {
                        continue;
                    }
                    if (!(action.isBroadcast() || action.getAddress().equalsIgnoreCase(uuid))) {
                        continue;
                    }
                }
                MessageType message_type = action.getMessageType();
                switch (message_type) {
                    case MessageType.NetworkStatus -> logger.info("Network Status: {}", action.getData());

                    case MessageType.ClientUUID -> {
                        String data = action.getData();
                        if (data == null) { continue; }
                        uuid = data;
                        logger.info("Got UUID: {}", uuid);
                    }
                    case MessageType.NewGame -> {
                        String new_game_name = action.getData();
                        if (new_game_name == null) { continue; }

                        if (!new_game_name.equals(game_controller.getClass().getSimpleName())) {
                            game_controller.stop();
                            logger.info("Going to NewGame to: {}", new_game_name);
                            game_controller = game_controller_factory.createGameClientController(
                                    new_game_name,
                                    io_controller,
                                    connection,
                                    uuid,
                                    TPS
                            );
                            game_controller.start();
                        }
                    }
                    case MessageType.GameStatus -> {
                        String message_game = action.getData();
                        if (message_game == null) { continue; }

                        if (!message_game.equals(game_controller.getClass().getSimpleName())) {
                            game_controller.stop();
                            logger.info("Switching Games to: {}", message_game);
                            game_controller = game_controller_factory.createGameClientController(
                                    message_game,
                                    io_controller,
                                    connection,
                                    uuid,
                                    TPS
                            );
                            game_controller.start();
                        }
                    }
                    default -> messages_for_game.add(action);
                }
            }
        } catch (WebsocketNotConnectedException e) {
            checkConnectionTryReconnect();
        }

        game_controller.handleGame(messages_for_game, (int) (current_tick % TPS));
    }


    public void shutdown() {
        logger.info("Requested shutdown");
        game_controller.stop();
        logger.info("game_controller stopped");
        connection.stop();
        logger.info("connection stopped");
        io_controller.stop();
        logger.info("io_controller stopped");
    }
}
