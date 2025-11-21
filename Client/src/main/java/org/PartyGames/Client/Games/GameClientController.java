package org.PartyGames.Client.Games;

import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Common.Shared.Games;
import org.PartyGames.Client.Terminal.IOController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(GameClientController.class);
    protected final IOController io_handler;
    protected final WebSocketConnection connection;
    protected final String uuid;

    public GameClientController(IOController io_handler, WebSocketConnection connection, String uuid) {
        this.io_handler = io_handler;
        this.connection = connection;
        this.uuid = uuid;
    }
    public void start() {
        if (io_handler == null) {
            logger.error("IOController was not attached!");
            throw new AssertionError("IOController was not attached!");
        }
        if (connection == null) {
            logger.error("WebSocketConnection was not attached!");
            throw new AssertionError("WebSocketConnection was not attached!");
        }
        if (uuid == null) {
            logger.error("UUID was not attached!");
            throw new AssertionError("UUID was not attached!");
        }
        io_handler.clearScreen();
    }
    public void stop() {
        io_handler.clearScreen();
    }

    public abstract void handleGame(List<NetworkMessage> messages);
    public abstract Games getGame();
}
