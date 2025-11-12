package org.PartyGames.Terminal;


import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.GameHandlers.GameHandler;
import org.PartyGames.GameHandlers.GameFactory;
import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.PartyGames.Shared.Games;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerminalClient {
    private static final Logger logger = LoggerFactory.getLogger(TerminalClient.class);
    private String uuid;
    private long last_reconnect_attempt;
    private final TerminalIOHandler io_handler;
    private final WebSocketConnectionHandler connection;
    private GameHandler game_handler;

    public TerminalClient(WebSocketConnectionHandler connection_handler, TerminalIOHandler io_handler) {
        this.uuid = "";
        last_reconnect_attempt = 0;
        this.io_handler = io_handler;
        this.connection = connection_handler;
        this.game_handler = GameFactory.createGameHandler(Games.Null, io_handler, connection, uuid);

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
        game_handler.start();
    }


    public void update() {
        checkConnectionTryReconnect();
        List<NetworkMessage> messages_for_game = new ArrayList<>();
        List<NetworkMessage> server_messages = connection.consumeMessages();
        if (server_messages == null) {
            checkConnectionTryReconnect();
            return;
        }
        for (NetworkMessage message : server_messages) {
            if (!uuid.isEmpty()) {
                if (!(message.isBroadcast() || message.getUUID().equalsIgnoreCase(uuid))) {
                    continue;
                }
            }
            switch (message.getType()) {
                case MessageType.NetworkStatus -> {
                    logger.info("Network Status: {}", message.getText());
                }
                case MessageType.ClientUUID -> {
                    uuid = message.getText();
                    logger.info("Got UUID: {}", uuid);
                }
                case MessageType.NewGame -> {
                    if (message.getGame() != game_handler.getGame()) {
                        game_handler.stop();
                        logger.info("Going to NewGame to: {}", message.getGame().toString());
                        game_handler = GameFactory.createGameHandler(message.getGame(), io_handler, connection, uuid);
                        game_handler.start();
                    }
                }
                case MessageType.GameStatus -> {
                    if (game_handler.getGame() != message.getGame()) {
                        game_handler.stop();
                        logger.info("Switching Games to: {}", message.getGame().toString());
                        game_handler = GameFactory.createGameHandler(message.getGame(), io_handler, connection, uuid);
                        game_handler.start();
                    } else {
                        // everything is ok, game is set correctly, respond back to server.
                        NetworkMessageBuilder builder = new NetworkMessageBuilder();
                        builder.setUUID(uuid).setMessageType(MessageType.ClientStatus).setText("ACK");
                        connection.send(builder.exportMessage());
                    }
                }
            }
            messages_for_game.add(message);
        }
        game_handler.handleGame(messages_for_game);
    }


    public void shutdown() {
        game_handler.stop();
        connection.stop();
        io_handler.stop();
        logger.info("Shutdown Terminal Client!");
    }
}
