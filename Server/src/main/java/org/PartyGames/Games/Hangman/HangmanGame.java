package org.PartyGames.Games.Hangman;

import org.PartyGames.Games.GameStrategy;
import org.PartyGames.Games.Meteor.MeteorGame;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Shared.Games;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HangmanGame extends GameStrategy {
    private static final Logger logger = LoggerFactory.getLogger(HangmanGame.class);

    public HangmanGame(WebSocketServerHandler connection) {
        super(connection);
    }

    @Override
    public void start() {
        logger.info("Entering Hangman");
    }

    @Override
    public void handleGame(List<NetworkMessage> messages) {
        //logger.info("Handling HangmanGame");
    }

    @Override
    public Games getGame() {
        return Games.Hangman;
    }
}
