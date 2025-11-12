package org.PartyGames.GameHandlers;

import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Shared.Games;
import org.PartyGames.Terminal.TerminalIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NullGame extends GameHandler {
    protected static final Logger logger = LoggerFactory.getLogger(NullGame.class);
    public NullGame(TerminalIOHandler io_handler, WebSocketConnectionHandler connection, String uuid) {
        super(io_handler, connection, uuid);
        logger.warn("You've instantiated a NullGame Object, Beware!");
    }

    @Override
    public void handleGame(List<NetworkMessage> messages) {
        io_handler.drawText(2, 5, "Waiting for a Connection!", "rgb(255, 255, 255)");
        io_handler.render();
        logger.warn("You're handling the NullGame, Beware!");
    }

    @Override
    public Games getGame() {
        return Games.Null;
    }
}
