package org.PartyGames.Client.Games;

import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.ForOverride;
import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Client.Terminal.IOController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;

public abstract class GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(GameClientController.class);
    protected IOController io_controller;
    protected WebSocketConnection connection;
    protected String uuid;
    protected int TPS;

    public GameClientController() {
        this.io_controller = null;
        this.connection = null;
        this.uuid = null;
        this.TPS = 0;
    }

    protected int getTPS() {
        return TPS;
    }

    @DoNotCall
    public GameClientController attachIOController(IOController io_controller) {
        this.io_controller = io_controller;
        return this;
    }
    @DoNotCall
    public GameClientController attachWebSocketConnection(WebSocketConnection connection) {
        this.connection = connection;
        return this;
    }
    @DoNotCall
    public GameClientController attachUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }
    @DoNotCall
    public GameClientController attachTPS(int TPS) {
        this.TPS = TPS;
        return this;
    }


    @OverridingMethodsMustInvokeSuper
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


    @OverridingMethodsMustInvokeSuper
    public void stop() {
        io_controller.clearScreen();
    }

    @ForOverride
    public abstract void handleGame(List<NetworkMessage> messages, int tick);
}
