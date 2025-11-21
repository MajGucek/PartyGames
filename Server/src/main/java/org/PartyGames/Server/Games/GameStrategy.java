package org.PartyGames.Server.Games;

import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Common.Shared.Games;
import java.util.List;

/**
 * This is the Base for all the games on the Server side <p>
 * To make a game you must extend this class and implement the two abstract methods: <p>
 * handleGame() and getGame() <p>
 * The implementation for getGame() is trivial, but firstly you must register your in:
 * Common:org.PartyGames.Shared.Games and then click the right upper Maven logo, then:
 * Under Lifecycle, clean and install. That will recompile the Common module.
 */

public abstract class GameStrategy {
    /** This variable represents the state of the server */
    protected WebSocketServerHandler connection;
    protected boolean is_finished;
    /**
     * In the concrete subclasses you must call this super constructor.
     */
    public GameStrategy(WebSocketServerHandler connection) {
        this.connection = connection;
        is_finished = false;
    }
    public boolean isFinished() { return is_finished; }

    /** Optionally @Override this method to add extra behavior */
    public void start() {}
    /** Optionally @Override this method and call the super.stop(), to add extra clean-up behavior */
    public void stop() { is_finished = true; }

    /**
     * Method that gets called every server tick once.
     * @param messages these are the NetworkMessages in-order that have been sent by the clients
     * In this method implement the main logic for you game
     */
    public abstract void handleGame(List<NetworkMessage> messages);

    /**
     * You must implement this method such that getGame() return a Games.GameName
     * @return Concrete subclasses equivalent of the Common:org.PartyGames.Shared.Games enum, look in Lobby, for example
     */
    public abstract Games getGame();
}
