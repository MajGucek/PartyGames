package org.PartyGames.GameHandlers.Hangman;

import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.GameHandlers.GameHandler;
import org.PartyGames.GameHandlers.Lobby.Lobby;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Shared.Games;
import org.PartyGames.Terminal.TerminalIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HangmanGame extends GameHandler {
    private static final Logger logger = LoggerFactory.getLogger(HangmanGame.class);
    public HangmanGame(TerminalIOHandler io_handler, WebSocketConnectionHandler connection, String uuid) {
        super(io_handler, connection, uuid);
    }

    @Override
    public void start() {
        logger.info("Entered Hangman");
    }

    @Override
    public void stop() {
        logger.info("Exited Hangman");
    }

    @Override
    public void handleGame(List<NetworkMessage> messages) {

    }

    @Override
    public Games getGame() {
        return Games.Hangman;
    }
}
