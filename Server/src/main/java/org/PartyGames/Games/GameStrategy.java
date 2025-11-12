package org.PartyGames.Games;

import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Shared.Games;
import java.util.List;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameStrategy {
    private static final Logger log = LoggerFactory.getLogger(GameStrategy.class);
    protected WebSocketServerHandler connection;
    protected boolean is_finished;
    public GameStrategy(WebSocketServerHandler connection) {
        this.connection = connection;
        is_finished = false;
    }
    public boolean isFinished() { return is_finished; }
    public void start() {
    }
    public void stop() { is_finished = true; }
    protected String getID(WebSocket websocket) {
        return websocket.getRemoteSocketAddress().toString();
    }

    public abstract void handleGame(List<NetworkMessage> messages);
    public abstract Games getGame();
}
