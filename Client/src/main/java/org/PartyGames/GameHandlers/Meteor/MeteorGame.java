package org.PartyGames.GameHandlers.Meteor;

import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.GameHandlers.GameHandler;
import org.PartyGames.GameHandlers.Lobby.Lobby;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Shared.Games;
import org.PartyGames.Terminal.TerminalIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MeteorGame extends GameHandler {
    private static final Logger logger = LoggerFactory.getLogger(MeteorGame.class);
    public MeteorGame(TerminalIOHandler io_handler, WebSocketConnectionHandler connection, String uuid) {
        super(io_handler, connection, uuid);
    }

    @Override
    public void start() {
        logger.info("Entered MeteorGame");
    }

    @Override
    public void stop() {
        logger.info("Exited MeteorGame");
    }

    @Override
    public void handleGame(List<NetworkMessage> messages) {

    }

    @Override
    public Games getGame() {
        return Games.Meteor;
    }
}
