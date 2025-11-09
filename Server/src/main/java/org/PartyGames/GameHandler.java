package org.PartyGames;

import org.PartyGames.Games.GameStrategy;
import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.PartyGames.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Shared.Games;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameHandler.class);
    private final WebSocketServerHandler connection;
    private GameStrategy game;
    private final NotifierService notifier;

    public GameHandler(int port) {
        connection = new WebSocketServerHandler(port);
        game = GameFactory.createGame(Games.Lobby, connection);
        notifier = new NotifierService();
    }
    private void reinstateNotification(Games game) {
        notifier.shutdown();
        NetworkMessageBuilder message_builder = new NetworkMessageBuilder();
        message_builder.setMessageType(MessageType.NewGame).setGame(game);
        notifier.addNotification(() -> {
            connection.broadcast(message_builder.exportJSON());
        }, 1);
    }

    public void start() {
        connection.start();
        logger.info("Server started on port: {}", connection.getPort());

        game.start();
        reinstateNotification(game.getGame());
    }
    public void update() {
        game.handleGame();
        if (game.isFinished()) {
            game.stop();
            logger.info("Finished with a game!");

            game = GameFactory.createGame(GameFactory.getRandomGame(), connection);
            game.start();
            reinstateNotification(game.getGame());
        }
    }
    public void shutdown() {
        notifier.shutdown();
        try {
            connection.stop(0, "Shutdown requested!");
        } catch (InterruptedException e) {
            logger.error("Connection stop failed!{}", String.valueOf(e));
        }
        game.stop();
        logger.info("Shutting down");
    }

}
