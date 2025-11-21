package org.PartyGames.Server.Games;


import org.PartyGames.Server.Games.Lobby.Lobby;
import org.PartyGames.Server.Connections.WebSocketServer;
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
        if (playable.length == 0) {
            logger.error("No other Games to create!");
            throw new AssertionError("No playable games available!");
        }

        Games rnd = playable[random.nextInt(playable.length)];
        logger.info("Creating game: {}", rnd.toString());
        return rnd;
    }
    /**
     * Create a game
     * @param state The Games enum value
     * @param connection A WebSocket connection
     * @return A new GameStrategy concrete Class
     */
    public static GameServerController createGame(Games state, WebSocketServer connection) {
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
