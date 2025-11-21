package org.PartyGames.Server.Games;


import org.PartyGames.Server.Games.Lobby.Lobby;
import org.PartyGames.Server.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Common.Shared.Games;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Random;

/**
 * The Class responsible for creating Games
 * Register your game in here
 */
public class GameFactory {
    public static final Logger logger = LoggerFactory.getLogger(GameFactory.class);
    private static final Random random = new Random();
    /** Get a random Game, that != Lobby */
    public static Games getRandomGame() {
        Games[] values = Games.values();

        Games[] playable = Arrays.stream(values)
                .filter(g -> g != Games.Null && g != Games.Lobby)
                .toArray(Games[]::new);
        return playable[random.nextInt(playable.length)];
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
