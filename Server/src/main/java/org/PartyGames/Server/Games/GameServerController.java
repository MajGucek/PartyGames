package org.PartyGames.Server.Games;

import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.Connections.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public abstract class GameServerController {
    private static final Logger log = LoggerFactory.getLogger(GameServerController.class);
    protected WebSocketServer connection;
    protected int TPS;
    protected boolean is_finished;
    public GameServerController() {
        this.connection = null;
        this.TPS = 0;
        is_finished = false;
    }

    public GameServerController attachWebSocketServer(WebSocketServer connection) {
        this.connection = connection;
        return this;
    }
    public GameServerController attachTPS(int TPS) {
        log.info("Set TPS to: {}, for: {}", TPS, GameServerController.class);
        this.TPS = TPS;
        return this;
    }

    public boolean isFinished() { return is_finished; }

    public void start() { }
    public void stop() { is_finished = true; }

    public abstract void handleGame(List<NetworkMessage> messages, int tick);

}
