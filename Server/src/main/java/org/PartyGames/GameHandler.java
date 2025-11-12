package org.PartyGames;

import org.PartyGames.Games.GameStrategy;
import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.PartyGames.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Shared.Games;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Main Class that is responsible for switching Games and game looping
 */
public class GameHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameHandler.class);
    private final WebSocketServerHandler connection;
    /** A Game instance, notice how it's the Abstract class, because of the Factory*/
    private GameStrategy game;
    /** The last time I've sent a GameState message */
    private long last_time_sent_game;

    public GameHandler(int port) {
        connection = new WebSocketServerHandler(port);
        game = GameFactory.createGame(Games.Lobby, connection);
        last_time_sent_game = 0;
    }
    /** Helper method to notify Clients of the current Game going on */
    private void notifyOfGameStrategy(Games game) {
        NetworkMessageBuilder message_builder = new NetworkMessageBuilder();
        message_builder.setToBroadcast().setMessageType(MessageType.GameStatus).setGame(game).setText("Routine Game type message");
        long now = System.currentTimeMillis();
        if (now - last_time_sent_game >= 2000) {
            connection.notifyClients(message_builder.exportMessage());
            last_time_sent_game = now;
        }
    }

    public void start() {
        connection.start();
        logger.info("Server started on port: {}", connection.getPort());
        NetworkMessageBuilder builder = new NetworkMessageBuilder();
        builder.setToBroadcast().setMessageType(MessageType.NewGame).setGame(game.getGame());
        connection.notifyClients(builder.exportMessage());
        game.start();
        notifyOfGameStrategy(game.getGame());
    }
    /** The main Loop of the GameHandler */
    public void update() {
        if (game.isFinished()) {
            connection.consumeMessages();
            connection.clearBuffer();
            logger.info("Finished with a game!");

            NetworkMessageBuilder builder = new NetworkMessageBuilder();
            builder.setToBroadcast().setMessageType(MessageType.NewGame).setGame(game.getGame());
            connection.notifyClients(builder.exportMessage());

            game = GameFactory.createGame(GameFactory.getRandomGame(), connection);
            if (game == null) {
                logger.error("Error, null game");
            }
            logger.info("Created Game: {}", game.getGame());
            game.start();
        }

        game.handleGame(connection.consumeMessages());
        notifyOfGameStrategy(game.getGame());

    }
    public void shutdown() {
        try {
            connection.stop(0, "Shutdown requested!");
        } catch (InterruptedException e) {
            logger.error("Connection stop failed!{}", String.valueOf(e));
        }
        game.stop();
        logger.info("Shutting down");
    }

}
