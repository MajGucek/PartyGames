package org.PartyGames.Games;

import org.PartyGames.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Shared.Games;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameStrategy {
    protected static final Logger logger = LoggerFactory.getLogger(GameStrategy.class);
    protected WebSocketServerHandler connection;
    protected boolean is_finished;
    public GameStrategy(WebSocketServerHandler connection) {
        this.connection = connection;
        is_finished = false;
    }
    public boolean isFinished() { return is_finished; }
    public void start() {}
    public void stop() { is_finished = true; }
    public abstract void handleGame();
    public abstract Games getGame();
}
