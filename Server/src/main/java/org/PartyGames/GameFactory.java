package org.PartyGames;

import org.PartyGames.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Games.*;
import org.PartyGames.Shared.Games;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class GameFactory {
    public static final Logger logger = LoggerFactory.getLogger(GameFactory.class);
    public static Games getRandomGame() {
        logger.warn("Please implement this function!");
        return Games.Lobby;
    }
    public static GameStrategy createGame(Games state, WebSocketServerHandler connection) {
        switch (state) {
            case Lobby -> {
                return new Lobby(connection);
            }
            default -> {
                logger.error("WOW buddy, slow down, please DO register {}, in the GameFactory.createGame() method!", state);
                return null;
            }
        }
    }
}
