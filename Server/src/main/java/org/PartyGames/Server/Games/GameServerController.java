package org.PartyGames.Server.Games;

import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.Connections.WebSocketServer;
import java.util.List;


public abstract class GameServerController {
    /** This variable represents the state of the server */
    protected WebSocketServer connection;
    protected boolean is_finished;
    /**
     * In the concrete subclasses you must call this super constructor.
     */
    public GameServerController() {
        this.connection = null;
        is_finished = false;
    }

    public GameServerController attachWebSocketServer(WebSocketServer connection) {
        this.connection = connection;
        return this;
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

}
