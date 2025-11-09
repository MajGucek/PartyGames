package org.PartyGames.GameHandlers;

import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Shared.Games;
import org.PartyGames.Terminal.TerminalIOHandler;

import java.util.List;

public abstract class GameHandler {
    protected final TerminalIOHandler io_handler;
    protected final WebSocketConnectionHandler connection;

    public GameHandler(TerminalIOHandler io_handler, WebSocketConnectionHandler connection) {
        this.io_handler = io_handler;
        this.connection = connection;
    }
    public void startGame() {}
    public void stopGame() {}
    public abstract void handleGame(List<NetworkMessage> messages);
    public abstract Games getGame();
}
