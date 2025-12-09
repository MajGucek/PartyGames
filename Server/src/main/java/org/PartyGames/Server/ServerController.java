package org.PartyGames.Server;

import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.Connections.WebSocketServer;

import org.PartyGames.Server.Games.Factory.GameServerControllerFactory;
import org.PartyGames.Server.Games.GameServerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerController {
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private final int TPS;
    private final WebSocketServer connection;
    private GameServerController game_controller;
    private long last_time_sent_game;
    private final GameServerControllerFactory game_server_controller_factory;
    private long current_tick;

    public ServerController(int port, int TPS) {
        connection = new WebSocketServer(port);
        this.TPS = TPS;
        game_server_controller_factory = new GameServerControllerFactory();
        game_controller = game_server_controller_factory.createGameServerController("Lobby", connection, TPS);
        last_time_sent_game = 0;
        this.current_tick = 0;
    }

    private void notifyOfGameStrategy(String game) {
        NetworkMessage message = new NetworkMessage();
        message.setToBroadcast().setMessageType(MessageType.GameStatus).setData(game);
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
        message.setToBroadcast()
                .setMessageType(MessageType.NewGame)
                .setData(game_controller.getClass().getSimpleName());
        connection.notifyClients(message);
        game_controller.start();
        notifyOfGameStrategy(game_controller.getClass().getSimpleName());
    }

    public void update() {
        if (game_controller.isFinished()) {
            connection.consumeMessages();
            connection.clearBuffer();
            logger.info("Finished with a game!");

            game_controller = game_server_controller_factory.createGameServerController(
                    game_server_controller_factory.getRandomGame(),
                    connection,
                    TPS
            );

            logger.info("Created Game: {}", game_controller.getClass().getSimpleName());

            NetworkMessage message = new NetworkMessage();
            message.setToBroadcast()
                    .setMessageType(MessageType.NewGame)
                    .setData(game_controller.getClass().getSimpleName());
            connection.notifyClients(message);
            game_controller.start();
        }

        game_controller.handleGame(connection.consumeMessages(), (int) (current_tick % TPS));
        notifyOfGameStrategy(game_controller.getClass().getSimpleName());
        current_tick++;
    }
    public void shutdown() {
        try {
            connection.stop(0, "Shutdown requested!");
        } catch (InterruptedException e) {
            logger.error("Connection stop failed!{}", String.valueOf(e));
        }
        game_controller.stop();
        logger.info("Shutting down");
    }

}
