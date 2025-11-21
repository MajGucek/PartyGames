package org.PartyGames.Client.Terminal;


import org.PartyGames.Client.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Client.GameHandlers.GameHandler;
import org.PartyGames.Client.GameHandlers.GameFactory;
import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Common.Shared.Games;

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
                    Games message_game = action.getGame();
                    if (message_game == null) { continue; }

                    if (message_game != game_handler.getGame()) {
                        game_handler.stop();
                        logger.info("Going to NewGame to: {}", message_game);
                        game_handler = GameFactory.createGameHandler(message_game, io_handler, connection, uuid);
                        game_handler.start();
                    }
                }
                case MessageType.GameStatus -> {
                    Games message_game = action.getGame();
                    if (message_game == null) { continue; }

                    if (message_game != game_handler.getGame()) {
                        game_handler.stop();
                        logger.info("Switching Games to: {}", message_game);
                        game_handler = GameFactory.createGameHandler(message_game, io_handler, connection, uuid);
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
        game_handler.stop();
        connection.stop();
        io_handler.stop();
        logger.info("Shutdown Terminal Client!");
    }
}
