package org.PartyGames.Client.Games;

import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Client.Terminal.IOController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(GameClientController.class);
    protected IOController io_controller;
    protected WebSocketConnection connection;
    protected String uuid;

    public GameClientController() {
        this.io_controller = null;
        this.connection = null;
        this.uuid = null;
    }

    public GameClientController attachIOController(IOController io_controller) {
        this.io_controller = io_controller;
        return this;
    }
    public GameClientController attachWebSocketConnection(WebSocketConnection connection) {
        this.connection = connection;
        return this;
    }
    public GameClientController attachUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void start() {
        if (io_controller == null) {
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
        io_controller.clearScreen();
    }
    public void stop() {
        io_controller.clearScreen();
    }

    public abstract void handleGame(List<NetworkMessage> messages);
}
