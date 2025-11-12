package org.PartyGames;

import org.PartyGames.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Games.*;
import org.PartyGames.Shared.Games;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * The Class responsible for creating Games
 * Register your game in here
 */
public class GameFactory {
    public static final Logger logger = LoggerFactory.getLogger(GameFactory.class);
    /** Get a random Game, that != Lobby */
    public static Games getRandomGame() {
        logger.warn("Please implement this function!");
        return Games.Lobby;
    }
    /**
     * Create a game
     * @param state The Games enum value
     * @param connection A WebSocket connection
     * @return A new GameStratey concrete Class
     */
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
