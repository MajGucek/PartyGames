package org.PartyGames.Terminal;


import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.GameHandlers.GameHandler;
import org.PartyGames.GameHandlers.GameHandlerFactory;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Shared.Games;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerminalClient {
    private static final Logger logger = LoggerFactory.getLogger(TerminalClient.class);
    private long last_reconnect_attempt;
    private final TerminalIOHandler io_handler;
    private final WebSocketConnectionHandler connection;
    private GameHandler game_handler;

    public TerminalClient(WebSocketConnectionHandler connection_handler, TerminalIOHandler io_handler) {
        last_reconnect_attempt = 0;
        this.io_handler = io_handler;
        this.connection = connection_handler;
        this.game_handler = GameHandlerFactory.createGameHandler(Games.Null, io_handler, connection);
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
        game_handler.startGame();
    }


    public void update() {
        checkConnectionTryReconnect();
        List<NetworkMessage> messages_for_game = new ArrayList<NetworkMessage>();
        List<NetworkMessage> server_messages = connection.consumeMessages();
        for (NetworkMessage message : server_messages) {
            switch (message.getType()) {
                case NetworkStatus -> {
                    logger.info("Network Status: {}", message.getText());
                }
                case NewGame -> {
                    if (game_handler.getGame() != message.getGame()) {
                        game_handler.stopGame();
                        logger.info("OMG let's switch games to: {}", message.getGame().toString());
                        game_handler = GameHandlerFactory.createGameHandler(message.getGame(), io_handler, connection);
                        game_handler.startGame();
                    }
                }
                case PlayerEvent, PlayerStatus, GameStatus ->  {
                    messages_for_game.add(message);
                }
                default -> {
                    logger.error("Hmmm... Not yet implemented for this Type of message: {}", message.getType());
                }
            }
        }
        game_handler.handleGame(messages_for_game);
    }


    public void shutdown() {
        game_handler.stopGame();
        connection.stop();
        io_handler.stop();
        logger.info("Shutdown Terminal Client!");
    }
}
