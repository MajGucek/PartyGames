package org.PartyGames.Server;

import org.PartyGames.Common.Networking.*;
import org.PartyGames.Common.Shared.Games;
import org.PartyGames.Server.Connections.WebSocketServer;

import org.PartyGames.Server.Games.GameFactory;
import org.PartyGames.Server.Games.GameServerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Main Class that is responsible for switching Games and game looping
 */
public class ServerController {
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private final WebSocketServer connection;
    /** A Game instance, notice how it's the Abstract class, because of the Factory*/
    private GameServerController game;
    /** The last time I've sent a GameState message */
    private long last_time_sent_game;

    public ServerController(int port) {
        connection = new WebSocketServer(port);
        game = GameFactory.createGame(Games.Lobby, connection);
        last_time_sent_game = 0;
    }
    /** Helper method to notify Clients of the current Game going on */
    private void notifyOfGameStrategy(Games game) {
        NetworkMessage message = new NetworkMessage();
        message.setToBroadcast().setMessageType(MessageType.GameStatus).setGame(game).setData("Routine Game type message");
        long now = System.currentTimeMillis();
        if (now - last_time_sent_game >= 2000) {
            connection.notifyClients(message);
            last_time_sent_game = now;
        }
    }

    public void start() {
        connection.start();
        logger.info("Server started on port: {}", connection.getPort());
        NetworkMessage message = new NetworkMessage();
        message.setToBroadcast().setMessageType(MessageType.NewGame).setGame(game.getGame());
        connection.notifyClients(message);
        game.start();
        notifyOfGameStrategy(game.getGame());
    }
    /** The main Loop of the GameHandler */
    public void update() {
        if (game.isFinished()) {
            connection.consumeMessages();
            connection.clearBuffer();
            logger.info("Finished with a game!");

            game = GameFactory.createGame(GameFactory.getRandomGame(), connection);
            if (game == null) {
                logger.error("Error, null game");
            }
            logger.info("Created Game: {}", game.getGame());

            NetworkMessage message = new NetworkMessage();
            message.setToBroadcast().setMessageType(MessageType.NewGame).setGame(game.getGame());
            connection.notifyClients(message);
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
