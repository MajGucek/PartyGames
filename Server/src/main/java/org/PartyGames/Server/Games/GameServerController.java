package org.PartyGames.Server.Games;

import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.Connections.WebSocketServer;
import java.util.List;


public abstract class GameServerController {
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
        this.TPS = TPS;
        return this;
    }

    public boolean isFinished() { return is_finished; }

    public void start() { }
    public void stop() { is_finished = true; }

    public abstract void handleGame(List<NetworkMessage> messages, int tick);

}
