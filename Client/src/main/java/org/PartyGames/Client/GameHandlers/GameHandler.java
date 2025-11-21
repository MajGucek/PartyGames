package org.PartyGames.Client.GameHandlers;

import org.PartyGames.Client.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Common.Shared.Games;
import org.PartyGames.Client.Terminal.TerminalIOHandler;

import java.util.List;

public abstract class GameHandler {
    protected final TerminalIOHandler io_handler;
    protected final WebSocketConnectionHandler connection;
    protected final String uuid;

    public GameHandler(TerminalIOHandler io_handler, WebSocketConnectionHandler connection, String uuid) {
        this.io_handler = io_handler;
        this.connection = connection;
        this.uuid = uuid;
    }
    public void start() {
        io_handler.clearScreen();
    }
    public void stop() {
        io_handler.clearScreen();
    }

    public abstract void handleGame(List<NetworkMessage> messages);
    public abstract Games getGame();
}
