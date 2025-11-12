package org.PartyGames.GameHandlers;

import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Shared.Games;
import org.PartyGames.Terminal.TerminalIOHandler;
import org.slf4j.LoggerFactory;

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
    public void startGame() {
        io_handler.clearScreen();
    }
    public void stopGame() {
        io_handler.clearScreen();
    }

    public abstract void handleGame(List<NetworkMessage> messages);
    public abstract Games getGame();
}
